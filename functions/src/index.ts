import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import * as crypto from "crypto";

if (!admin.apps.length) {
  admin.initializeApp();
}

export const handleRazorpayWebhook = functions
  .runWith({ secrets: ["RAZORPAY_WEBHOOK_SECRET"] })
  .https.onRequest(async (req, res) => {
    try {
      if (req.method !== "POST") {
        res.status(405).send("Method Not Allowed");
        return;
      }

      const signature = req.headers["x-razorpay-signature"] as string;
      const eventId = req.get("x-razorpay-event-id");
      const secret = process.env.RAZORPAY_WEBHOOK_SECRET;

      if (!secret) {
        functions.logger.error("RAZORPAY_WEBHOOK_SECRET not configured in Secret Manager");
        res.status(500).send("Server configuration error");
        return;
      }

      if (!signature || !/^[a-f0-9]{64}$/i.test(signature) ||
          !eventId || typeof eventId !== "string" || !/^[A-Za-z0-9_-]{1,128}$/.test(eventId)) {
        res.status(400).send("Invalid or missing x-razorpay-signature or x-razorpay-event-id header");
        return;
      }

      const expectedSignature = crypto
        .createHmac("sha256", secret)
        .update(req.rawBody)
        .digest("hex");

      const sigBuf = Buffer.from(signature, "utf8");
      const expBuf = Buffer.from(expectedSignature, "utf8");
      if (sigBuf.length !== expBuf.length || !crypto.timingSafeEqual(sigBuf, expBuf)) {
        functions.logger.error("Razorpay webhook signature mismatch");
        res.status(400).send("Invalid webhook signature");
        return;
      }

      const event = req.body;
      const db = admin.firestore();

      if (event.event !== "payment.captured" && event.event !== "refund.processed") {
        functions.logger.info(`Logging unmonitored event type: ${event.event}`);
        await db.collection("unmonitored_webhook_events").doc(eventId).set({
          eventType: event.event,
          receivedAt: admin.firestore.FieldValue.serverTimestamp()
        });
        res.status(200).send({ status: "logged_unmonitored_event" });
        return;
      }

      await db.runTransaction(async (transaction) => {
        // READ 1: Processed Webhook Event Idempotency Check
        const eventRef = db.collection("processed_webhook_events").doc(eventId);
        const eventSnap = await transaction.get(eventRef);

        if (eventSnap.exists) {
          functions.logger.info(`Event replay ignored for: ${eventId}`);
          return;
        }

        if (event.event === "payment.captured") {
          const paymentEntity = event.payload?.payment?.entity;
          if (!paymentEntity || paymentEntity.status !== "captured" || !paymentEntity.id || !paymentEntity.order_id) {
            throw new Error("Invalid captured payment payload");
          }

          const bookingId = paymentEntity.notes?.bookingId;
          if (typeof bookingId !== "string" || !/^[A-Za-z0-9_-]{1,128}$/.test(bookingId)) {
            throw new Error("Invalid booking ID format");
          }

          const bookingRef = db.collection("rental_bookings").doc(bookingId);
          const paymentRef = db.collection("payments").doc(`PAY_${paymentEntity.id}`);

          // READ 2 & 3: Document Reads BEFORE Writes
          const bookingSnap = await transaction.get(bookingRef);
          const paymentSnap = await transaction.get(paymentRef);

          if (!bookingSnap.exists) {
            throw new Error("Target booking does not exist!");
          }

          const booking = bookingSnap.data()!;

          if (!booking.renterId || !booking.ownerId || booking.renterId === booking.ownerId) {
            throw new Error("Invalid or identical renter/owner booking identity!");
          }

          if (paymentSnap.exists) {
            const existing = paymentSnap.data()!;
            if (existing.gatewayTransactionId !== paymentEntity.id || existing.bookingId !== bookingId) {
              throw new Error("Conflicting existing payment record!");
            }
            transaction.create(eventRef, {
              processedAt: admin.firestore.FieldValue.serverTimestamp(),
              paymentId: paymentRef.id,
              outcome: "DUPLICATE_PAYMENT"
            });
            return;
          }

          const rental = booking.rentalAmountPaise;
          const deposit = booking.securityDepositPaise;
          const delivery = booking.deliveryFeePaise || 0;
          const cleaning = booking.cleaningFeePaise || 0;

          if (!Number.isSafeInteger(rental) || rental < 0 ||
              !Number.isSafeInteger(deposit) || deposit < 0 ||
              !Number.isSafeInteger(delivery) || delivery < 0 ||
              !Number.isSafeInteger(cleaning) || cleaning < 0) {
            throw new Error("Invalid non-integer or negative checkout component!");
          }

          const expectedTotal = rental + deposit + delivery + cleaning;
          if (!Number.isSafeInteger(expectedTotal) || paymentEntity.amount !== expectedTotal || booking.totalAmountPaise !== expectedTotal) {
            throw new Error(`Checkout component sum mismatch! Expected ${expectedTotal}, got ${paymentEntity.amount}`);
          }

          if (booking.razorpayOrderId !== paymentEntity.order_id) {
            throw new Error("Razorpay order ID mismatch!");
          }
          if (paymentEntity.currency !== "INR") {
            throw new Error("Currency mismatch!");
          }
          if (booking.paymentStatus !== "PENDING" || booking.status !== "PENDING_PAYMENT") {
            throw new Error("Booking is not awaiting payment!");
          }

          const commission = Math.round(rental * 0.20); // 20% of rental
          const ownerEarnings = rental - commission;   // 80% of rental

          // WRITES
          transaction.create(paymentRef, {
            gatewayTransactionId: paymentEntity.id,
            bookingId,
            userId: booking.renterId,
            ownerUid: booking.ownerId,
            currency: "INR",
            rentalAmountPaise: rental,
            platformCommissionPaise: commission,
            ownerGrossSharePaise: ownerEarnings,
            securityDepositPaise: deposit,
            deliveryFeePaise: delivery,
            cleaningFeePaise: cleaning,
            totalAmountPaise: expectedTotal,
            refundedAmountPaise: 0,
            refundedDepositPaise: 0,
            refundedRentalPaise: 0,
            refundedDeliveryPaise: 0,
            refundedCleaningPaise: 0,
            settledPayoutAmountPaise: 0,
            payoutStatus: "UNSETTLED",
            status: "VERIFIED",
            createdAt: admin.firestore.FieldValue.serverTimestamp()
          });

          // Balanced Double-Entry Financial Ledger (/financial_ledger)
          transaction.create(db.collection("financial_ledger").doc(`LEDGER_${paymentEntity.id}_CASH`), {
            bookingId, gatewayTransactionId: paymentEntity.id, account: "Payment Clearing", entryType: "CASH_INFLOW",
            amountPaise: expectedTotal, direction: "DEBIT", userId: booking.renterId, ownerId: booking.ownerId, createdAt: admin.firestore.FieldValue.serverTimestamp()
          });

          transaction.create(db.collection("financial_ledger").doc(`LEDGER_${paymentEntity.id}_COMMISSION`), {
            bookingId, gatewayTransactionId: paymentEntity.id, account: "NIVARA Commission Revenue", entryType: "PLATFORM_COMMISSION",
            amountPaise: commission, direction: "CREDIT", userId: booking.renterId, createdAt: admin.firestore.FieldValue.serverTimestamp()
          });

          transaction.create(db.collection("financial_ledger").doc(`LEDGER_${paymentEntity.id}_OWNER_PAYABLE`), {
            bookingId, gatewayTransactionId: paymentEntity.id, account: "Owner Payable", entryType: "OWNER_PAYABLE",
            amountPaise: ownerEarnings, direction: "CREDIT", ownerId: booking.ownerId, createdAt: admin.firestore.FieldValue.serverTimestamp()
          });

          transaction.create(db.collection("financial_ledger").doc(`LEDGER_${paymentEntity.id}_DEPOSIT`), {
            bookingId, gatewayTransactionId: paymentEntity.id, account: "Refundable Deposit Liability", entryType: "SECURITY_DEPOSIT",
            amountPaise: deposit, direction: "CREDIT", userId: booking.renterId, createdAt: admin.firestore.FieldValue.serverTimestamp()
          });

          transaction.create(db.collection("financial_ledger").doc(`LEDGER_${paymentEntity.id}_DELIVERY`), {
            bookingId, gatewayTransactionId: paymentEntity.id, account: "Delivery Payable", entryType: "DELIVERY_PAYABLE",
            amountPaise: delivery, direction: "CREDIT", userId: booking.renterId, createdAt: admin.firestore.FieldValue.serverTimestamp()
          });

          transaction.create(db.collection("financial_ledger").doc(`LEDGER_${paymentEntity.id}_CLEANING`), {
            bookingId, gatewayTransactionId: paymentEntity.id, account: "Cleaning Payable", entryType: "CLEANING_PAYABLE",
            amountPaise: cleaning, direction: "CREDIT", userId: booking.renterId, createdAt: admin.firestore.FieldValue.serverTimestamp()
          });

          transaction.update(bookingRef, {
            status: "Booking Confirmed",
            paymentStatus: "VERIFIED",
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
          });

          transaction.create(eventRef, {
            processedAt: admin.firestore.FieldValue.serverTimestamp(),
            paymentId: paymentRef.id,
            outcome: "PROCESSED"
          });
        } else if (event.event === "refund.processed") {
          const refundEntity = event.payload?.refund?.entity;
          if (!refundEntity || !refundEntity.id || !refundEntity.payment_id) {
            throw new Error("Invalid refund payload structure");
          }

          if (refundEntity.status !== "processed") {
            throw new Error(`Refund entity status is not processed: ${refundEntity.status}`);
          }

          const refundId = refundEntity.id;
          const paymentId = `PAY_${refundEntity.payment_id}`;

          const refundRef = db.collection("refunds").doc(refundId);
          const paymentRef = db.collection("payments").doc(paymentId);
          const authRefundReqRef = db.collection("authorized_refund_requests").doc(refundId);

          // READ ALL REQUIRED DOCUMENTS BEFORE WRITING
          const refundSnap = await transaction.get(refundRef);
          const paymentSnap = await transaction.get(paymentRef);
          const authRefundReqSnap = await transaction.get(authRefundReqRef);

          if (refundSnap.exists) {
            const existingRefund = refundSnap.data()!;
            if (existingRefund.paymentId !== paymentId || existingRefund.amountPaise !== refundEntity.amount) {
              throw new Error("Duplicate refund ID with conflicting payment ID or amount!");
            }
            functions.logger.info(`Refund event already processed: ${refundId}`);
            return;
          }

          // Correction 1 & 2: Mandatory Authorized Server-Side Refund Request Document Check
          if (!authRefundReqSnap.exists) {
            throw new Error("Missing or unauthorized server-side refund request! Refund rejected.");
          }

          const authReq = authRefundReqSnap.data()!;
          if (authReq.status !== "AUTHORIZED") {
            throw new Error(`Authorized refund request is not in AUTHORIZED status: ${authReq.status}`);
          }

          if (authReq.paymentId !== paymentId || authReq.bookingId === undefined) {
            throw new Error("Authorized refund request parameters mismatch!");
          }

          if (!paymentSnap.exists) {
            throw new Error("Target payment record for refund does not exist!");
          }

          const paymentData = paymentSnap.data()!;
          if (paymentData.gatewayTransactionId !== refundEntity.payment_id) {
            throw new Error("Refund payment_id mismatch!");
          }

          if (paymentData.status !== "VERIFIED" && paymentData.status !== "PARTIALLY_REFUNDED") {
            throw new Error(`Payment is not in an eligible status for refund: ${paymentData.status}`);
          }

          const bookingRef = db.collection("rental_bookings").doc(paymentData.bookingId);
          const bookingSnap = await transaction.get(bookingRef);

          if (!bookingSnap.exists) {
            throw new Error("Associated booking does not exist!");
          }

          const booking = bookingSnap.data()!;
          if (booking.renterId !== paymentData.userId || booking.ownerId !== paymentData.ownerUid) {
            throw new Error("Booking ownership mismatch!");
          }

          const currentRefundedTotal = paymentData.refundedAmountPaise || 0;
          const currentRefundedDeposit = paymentData.refundedDepositPaise || 0;
          const currentRefundedRental = paymentData.refundedRentalPaise || 0;
          const currentRefundedDelivery = paymentData.refundedDeliveryPaise || 0;
          const currentRefundedCleaning = paymentData.refundedCleaningPaise || 0;

          if (!Number.isSafeInteger(currentRefundedTotal) || currentRefundedTotal < 0 ||
              !Number.isSafeInteger(currentRefundedDeposit) || currentRefundedDeposit < 0 ||
              !Number.isSafeInteger(currentRefundedRental) || currentRefundedRental < 0 ||
              !Number.isSafeInteger(currentRefundedDelivery) || currentRefundedDelivery < 0 ||
              !Number.isSafeInteger(currentRefundedCleaning) || currentRefundedCleaning < 0) {
            throw new Error("Invalid or unsafe stored cumulative refund counter!");
          }

          if (currentRefundedDeposit + currentRefundedRental + currentRefundedDelivery + currentRefundedCleaning !== currentRefundedTotal) {
            throw new Error("Stored component refund counters do not sum to total cumulative refunded amount!");
          }

          const refundAmount = refundEntity.amount;
          if (!Number.isSafeInteger(refundAmount) || refundAmount <= 0 || refundAmount !== authReq.amountPaise) {
            throw new Error("Invalid or mismatched integer refund amount!");
          }

          const newRefundedTotal = currentRefundedTotal + refundAmount;
          if (!Number.isSafeInteger(newRefundedTotal) || newRefundedTotal > paymentData.totalAmountPaise) {
            throw new Error("Cumulative refund exceeds total captured payment amount!");
          }

          // Correction 3 & 5: Require all four allocations from authorized request
          const allocDeposit = authReq.depositPaise;
          const allocRental = authReq.rentalPaise;
          const allocDelivery = authReq.deliveryPaise;
          const allocCleaning = authReq.cleaningPaise;

          if (!Number.isSafeInteger(allocDeposit) || allocDeposit < 0 ||
              !Number.isSafeInteger(allocRental) || allocRental < 0 ||
              !Number.isSafeInteger(allocDelivery) || allocDelivery < 0 ||
              !Number.isSafeInteger(allocCleaning) || allocCleaning < 0) {
            throw new Error("Invalid, unsafe, or negative component refund allocation!");
          }

          if (allocDeposit + allocRental + allocDelivery + allocCleaning !== refundAmount) {
            throw new Error("Sum of component refund allocations does not equal total refund amount!");
          }

          // Validate each component balance independently
          const newRefundedDeposit = currentRefundedDeposit + allocDeposit;
          const newRefundedRental = currentRefundedRental + allocRental;
          const newRefundedDelivery = currentRefundedDelivery + allocDelivery;
          const newRefundedCleaning = currentRefundedCleaning + allocCleaning;

          if (newRefundedDeposit > paymentData.securityDepositPaise) {
            throw new Error("Deposit refund exceeds original security deposit paid!");
          }
          if (newRefundedRental > paymentData.rentalAmountPaise) {
            throw new Error("Rental refund exceeds original rental amount paid!");
          }
          if (newRefundedDelivery > (paymentData.deliveryFeePaise || 0)) {
            throw new Error("Delivery refund exceeds original delivery fee paid!");
          }
          if (newRefundedCleaning > (paymentData.cleaningFeePaise || 0)) {
            throw new Error("Cleaning refund exceeds original cleaning fee paid!");
          }

          const isFullRefund = newRefundedTotal === paymentData.totalAmountPaise;

          // Correction 6: Consume Authorized Refund Request within same transaction
          transaction.update(authRefundReqRef, {
            status: "CONSUMED",
            consumedAt: admin.firestore.FieldValue.serverTimestamp()
          });

          // RECORD BALANCED REVERSAL LEDGER ENTRIES
          if (allocDeposit > 0) {
            transaction.create(db.collection("financial_ledger").doc(`LEDGER_REFUND_${refundId}_DEPOSIT_DEBIT`), {
              paymentId, refundId, bookingId: paymentData.bookingId, account: "Refundable Deposit Liability",
              entryType: "SECURITY_DEPOSIT_RETURN", amountPaise: allocDeposit, direction: "DEBIT",
              userId: paymentData.userId, ownerId: paymentData.ownerUid, createdAt: admin.firestore.FieldValue.serverTimestamp()
            });

            transaction.create(db.collection("financial_ledger").doc(`LEDGER_REFUND_${refundId}_DEPOSIT_CREDIT`), {
              paymentId, refundId, bookingId: paymentData.bookingId, account: "Payment Clearing",
              entryType: "CASH_OUTFLOW", amountPaise: allocDeposit, direction: "CREDIT",
              userId: paymentData.userId, ownerId: paymentData.ownerUid, createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
          }

          if (allocRental > 0) {
            // Exact final commission reversal reconciliation
            const isFinalRentalRefund = newRefundedRental === paymentData.rentalAmountPaise;
            const prevRentalRefund = currentRefundedRental;
            const prevCommissionReversal = Math.round(prevRentalRefund * 0.20);

            let rentalCommissionReversal: number;
            if (isFinalRentalRefund) {
              rentalCommissionReversal = paymentData.platformCommissionPaise - prevCommissionReversal;
            } else {
              rentalCommissionReversal = Math.round(allocRental * 0.20);
            }
            const ownerPayableReversal = allocRental - rentalCommissionReversal;

            // Multi-Payout Support & Owner Receivable Accounting
            const settledPayouts = paymentData.settledPayoutAmountPaise || 0;
            const isOwnerPaidOut = settledPayouts > 0 || paymentData.payoutStatus === "COMPLETED";

            const ownerAccount = isOwnerPaidOut ? "Owner Receivable" : "Owner Payable";
            const ownerEntryType = isOwnerPaidOut ? "OWNER_RECEIVABLE_ADJUSTMENT" : "RENTAL_REFUND_REVERSAL";

            transaction.create(db.collection("financial_ledger").doc(`LEDGER_REFUND_${refundId}_OWNER_DEBIT`), {
              paymentId, refundId, bookingId: paymentData.bookingId, account: ownerAccount,
              entryType: ownerEntryType, amountPaise: ownerPayableReversal, direction: "DEBIT",
              userId: paymentData.userId, ownerId: paymentData.ownerUid, createdAt: admin.firestore.FieldValue.serverTimestamp()
            });

            transaction.create(db.collection("financial_ledger").doc(`LEDGER_REFUND_${refundId}_COMMISSION_DEBIT`), {
              paymentId, refundId, bookingId: paymentData.bookingId, account: "NIVARA Commission Revenue",
              entryType: "COMMISSION_REFUND_REVERSAL", amountPaise: rentalCommissionReversal, direction: "DEBIT",
              userId: paymentData.userId, ownerId: paymentData.ownerUid, createdAt: admin.firestore.FieldValue.serverTimestamp()
            });

            transaction.create(db.collection("financial_ledger").doc(`LEDGER_REFUND_${refundId}_RENTAL_CREDIT`), {
              paymentId, refundId, bookingId: paymentData.bookingId, account: "Payment Clearing",
              entryType: "CASH_OUTFLOW", amountPaise: allocRental, direction: "CREDIT",
              userId: paymentData.userId, ownerId: paymentData.ownerUid, createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
          }

          if (allocDelivery > 0) {
            transaction.create(db.collection("financial_ledger").doc(`LEDGER_REFUND_${refundId}_DELIVERY_DEBIT`), {
              paymentId, refundId, bookingId: paymentData.bookingId, account: "Delivery Payable",
              entryType: "DELIVERY_REFUND_REVERSAL", amountPaise: allocDelivery, direction: "DEBIT",
              userId: paymentData.userId, ownerId: paymentData.ownerUid, createdAt: admin.firestore.FieldValue.serverTimestamp()
            });

            transaction.create(db.collection("financial_ledger").doc(`LEDGER_REFUND_${refundId}_DELIVERY_CREDIT`), {
              paymentId, refundId, bookingId: paymentData.bookingId, account: "Payment Clearing",
              entryType: "CASH_OUTFLOW", amountPaise: allocDelivery, direction: "CREDIT",
              userId: paymentData.userId, ownerId: paymentData.ownerUid, createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
          }

          if (allocCleaning > 0) {
            transaction.create(db.collection("financial_ledger").doc(`LEDGER_REFUND_${refundId}_CLEANING_DEBIT`), {
              paymentId, refundId, bookingId: paymentData.bookingId, account: "Cleaning Payable",
              entryType: "CLEANING_REFUND_REVERSAL", amountPaise: allocCleaning, direction: "DEBIT",
              userId: paymentData.userId, ownerId: paymentData.ownerUid, createdAt: admin.firestore.FieldValue.serverTimestamp()
            });

            transaction.create(db.collection("financial_ledger").doc(`LEDGER_REFUND_${refundId}_CLEANING_CREDIT`), {
              paymentId, refundId, bookingId: paymentData.bookingId, account: "Payment Clearing",
              entryType: "CASH_OUTFLOW", amountPaise: allocCleaning, direction: "CREDIT",
              userId: paymentData.userId, ownerId: paymentData.ownerUid, createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
          }

          // Persist Full Component Allocation in Refund Document
          transaction.create(refundRef, {
            refundId,
            paymentId,
            gatewayStatus: refundEntity.status,
            amountPaise: refundAmount,
            allocatedDepositRefund: allocDeposit,
            allocatedRentalRefund: allocRental,
            allocatedDeliveryRefund: allocDelivery,
            allocatedCleaningRefund: allocCleaning,
            createdAt: admin.firestore.FieldValue.serverTimestamp()
          });

          // Update Cumulative Counters on Payment Document
          transaction.update(paymentRef, {
            refundedAmountPaise: newRefundedTotal,
            refundedDepositPaise: newRefundedDeposit,
            refundedRentalPaise: newRefundedRental,
            refundedDeliveryPaise: newRefundedDelivery,
            refundedCleaningPaise: newRefundedCleaning,
            status: isFullRefund ? "REFUNDED" : "PARTIALLY_REFUNDED",
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
          });

          // Update Booking Document
          transaction.update(bookingRef, {
            paymentStatus: isFullRefund ? "REFUNDED" : "PARTIALLY_REFUNDED",
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
          });

          // Record Webhook Event Record
          transaction.create(eventRef, {
            processedAt: admin.firestore.FieldValue.serverTimestamp(),
            refundId: refundId,
            outcome: "REFUND_PROCESSED"
          });
        }
      });

      res.status(200).send({ status: "success" });
    } catch (err: any) {
      functions.logger.error("Razorpay webhook error:", err.message);
      res.status(500).send("Webhook handler failure");
    }
  });
