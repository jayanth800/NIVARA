package com.nivara.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.nivara.app.data.model.BoxItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object StorageRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getUserBoxesFlow(): Flow<List<BoxItem>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .collection("boxes")
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    trySend(emptyList<BoxItem>())
                    return@EventListener
                }

                val boxes = snapshot?.documents?.mapNotNull { doc: DocumentSnapshot ->
                    val data = doc.data ?: return@mapNotNull null
                    BoxItem(
                        id = doc.id,
                        uniqueBoxId = data["uniqueBoxId"] as? String ?: "NIV-BOX-0000",
                        qrCode = data["qrCode"] as? String ?: "QR_DEFAULT",
                        category = data["category"] as? String ?: "General",
                        title = data["title"] as? String ?: "Storage Box",
                        description = data["description"] as? String ?: "",
                        storageDate = data["storageDate"] as? String ?: "Today",
                        status = data["status"] as? String ?: "Stored",
                        locationReference = data["locationReference"] as? String ?: "Warehouse Alpha",
                        photoUrl = data["photoUrl"] as? String ?: "",
                        itemsInside = emptyList()
                    )
                } ?: emptyList<BoxItem>()

                trySend(boxes)
            })

        awaitClose {
            listener.remove()
        }
    }

    suspend fun saveBoxToFirestore(box: BoxItem): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val boxMap = hashMapOf(
                "uniqueBoxId" to box.uniqueBoxId,
                "qrCode" to box.qrCode,
                "category" to box.category,
                "title" to box.title,
                "description" to box.description,
                "storageDate" to box.storageDate,
                "status" to box.status,
                "locationReference" to box.locationReference,
                "photoUrl" to box.photoUrl,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(uid)
                .collection("boxes")
                .document(box.id)
                .set(boxMap)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
