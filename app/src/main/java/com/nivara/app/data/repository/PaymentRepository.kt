package com.nivara.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.nivara.app.data.model.PaymentRecord
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object PaymentRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getUserPaymentsFlow(): Flow<List<PaymentRecord>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("payments")
            .whereEqualTo("userId", uid)
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    trySend(emptyList<PaymentRecord>())
                    return@EventListener
                }

                val records = snapshot?.documents?.mapNotNull { doc: DocumentSnapshot ->
                    val data = doc.data ?: return@mapNotNull null
                    PaymentRecord(
                        paymentId = doc.id,
                        userId = data["userId"] as? String ?: "",
                        bookingId = data["bookingId"] as? String ?: "",
                        amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
                        platformFee = (data["platformFee"] as? Number)?.toDouble() ?: 0.0,
                        securityDeposit = (data["securityDeposit"] as? Number)?.toDouble() ?: 0.0,
                        status = data["status"] as? String ?: "SUCCESS",
                        timestamp = data["timestamp"] as? String ?: "Today",
                        method = data["method"] as? String ?: "UPI"
                    )
                } ?: emptyList<PaymentRecord>()

                trySend(records)
            })

        awaitClose {
            listener.remove()
        }
    }

    suspend fun recordPayment(payment: PaymentRecord): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val paymentMap = hashMapOf(
                "paymentId" to payment.paymentId,
                "userId" to uid,
                "bookingId" to payment.bookingId,
                "amount" to payment.amount,
                "platformFee" to payment.platformFee,
                "securityDeposit" to payment.securityDeposit,
                "status" to payment.status,
                "timestamp" to payment.timestamp,
                "method" to payment.method,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("payments")
                .document(payment.paymentId)
                .set(paymentMap)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
