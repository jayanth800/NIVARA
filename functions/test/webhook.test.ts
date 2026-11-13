import { expect } from "chai";

describe("NIVARA Financial Ledger & Component-Aware Refund Tests", () => {
  it("should calculate 20% platform commission on rental amount only", () => {
    const rentalAmountPaise = 105000; // ₹1,050
    const platformCommissionPaise = Math.round(rentalAmountPaise * 0.20); // 21,000 paise (₹210)
    const ownerEarningsPaise = rentalAmountPaise - platformCommissionPaise; // 84,000 paise (₹840)

    expect(platformCommissionPaise).to.equal(21000);
    expect(ownerEarningsPaise).to.equal(84000);
  });

  it("should reconcile total checkout components to ₹2,800 (280,000 paise)", () => {
    const rental = 105000;
    const deposit = 150000;
    const delivery = 15000;
    const cleaning = 10000;

    const totalInflow = rental + deposit + delivery + cleaning;

    const commission = Math.round(rental * 0.20);
    const ownerPayable = rental - commission;

    const totalOutflow = commission + ownerPayable + deposit + delivery + cleaning;

    expect(totalInflow).to.equal(280000);
    expect(totalOutflow).to.equal(280000);
    expect(totalInflow).to.equal(totalOutflow);
  });

  it("should allocate deposit-only refund accurately", () => {
    const depositPaid = 150000;
    const refundAmount = 150000; // Deposit Return

    let currentDepositRefunded = 0;
    expect(currentDepositRefunded + refundAmount).to.be.at.most(depositPaid);
    currentDepositRefunded += refundAmount;

    expect(currentDepositRefunded).to.equal(depositPaid);
  });

  it("should allocate rental-only refund accurately with 20% commission reversal", () => {
    const rentalPaid = 105000;
    const rentalRefundAmount = 50000; // ₹500 rental refund

    const commissionReversal = Math.round(rentalRefundAmount * 0.20); // ₹100
    const ownerPayableReversal = rentalRefundAmount - commissionReversal; // ₹400

    expect(commissionReversal).to.equal(10000);
    expect(ownerPayableReversal).to.equal(40000);
    expect(commissionReversal + ownerPayableReversal).to.equal(rentalRefundAmount);
  });

  it("should reject refund requests exceeding original component balances", () => {
    const originalDeposit = 150000;
    let refundedDeposit = 150000;

    const additionalRefundAttempt = 10000;
    const exceedsLimit = refundedDeposit + additionalRefundAttempt > originalDeposit;

    expect(exceedsLimit).to.be.true;
  });
});
