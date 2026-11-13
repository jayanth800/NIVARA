package com.nivara.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.nivara.app.data.model.RentalBooking
import com.nivara.app.data.model.RentalItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object ShareRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getMarketplaceListingsFlow(): Flow<List<RentalItem>> = callbackFlow {
        val listener = firestore.collection("marketplace_listings")
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    trySend(emptyList<RentalItem>())
                    return@EventListener
                }

                val items = snapshot?.documents?.mapNotNull { doc: DocumentSnapshot ->
                    val data = doc.data ?: return@mapNotNull null
                    RentalItem(
                        id = doc.id,
                        ownerId = data["ownerId"] as? String ?: "",
                        ownerName = data["ownerName"] as? String ?: "Nivara User",
                        title = data["title"] as? String ?: "",
                        description = data["description"] as? String ?: "",
                        category = data["category"] as? String ?: "",
                        dailyPrice = (data["dailyPrice"] as? Number)?.toDouble() ?: 0.0,
                        weeklyPrice = (data["weeklyPrice"] as? Number)?.toDouble() ?: 0.0,
                        securityDeposit = (data["securityDeposit"] as? Number)?.toDouble() ?: 0.0,
                        availableStart = data["availableStart"] as? String ?: "Immediate",
                        availableEnd = data["availableEnd"] as? String ?: "30 Jun 2025",
                        condition = data["condition"] as? String ?: "Like New",
                        photoUrl = data["photoUrl"] as? String ?: "",
                        isAvailable = data["isAvailable"] as? Boolean ?: true,
                        isVerified = data["isVerified"] as? Boolean ?: true,
                        status = data["status"] as? String ?: "Available for Rent"
                    )
                } ?: emptyList<RentalItem>()

                trySend(items)
            })

        awaitClose {
            listener.remove()
        }
    }

    suspend fun saveListingToFirestore(item: RentalItem): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val listingMap = hashMapOf(
                "ownerId" to uid,
                "ownerName" to item.ownerName,
                "title" to item.title,
                "description" to item.description,
                "category" to item.category,
                "dailyPrice" to item.dailyPrice,
                "weeklyPrice" to item.weeklyPrice,
                "securityDeposit" to item.securityDeposit,
                "availableStart" to item.availableStart,
                "availableEnd" to item.availableEnd,
                "condition" to item.condition,
                "photoUrl" to item.photoUrl,
                "isAvailable" to item.isAvailable,
                "isVerified" to item.isVerified,
                "status" to item.status,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("marketplace_listings")
                .document(item.id)
                .set(listingMap)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRentalBooking(booking: RentalBooking): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            val existingBookings = firestore.collection("rental_bookings")
                .whereEqualTo("rentalItemId", booking.rentalItemId)
                .whereEqualTo("status", "Booking Confirmed")
                .get()
                .await()

            for (doc in existingBookings.documents) {
                val start = doc.getString("startDate")
                val end = doc.getString("endDate")
                if (start == booking.startDate || end == booking.endDate) {
                    return Result.failure(Exception("Selected dates overlap with an existing confirmed booking!"))
                }
            }

            val bookingMap = hashMapOf(
                "bookingId" to booking.bookingId,
                "rentalItemId" to booking.rentalItemId,
                "itemTitle" to booking.itemTitle,
                "itemPhotoUrl" to booking.itemPhotoUrl,
                "renterId" to uid,
                "renterName" to booking.renterName,
                "startDate" to booking.startDate,
                "endDate" to booking.endDate,
                "rentalDays" to booking.rentalDays,
                "rentalAmount" to booking.rentalAmount,
                "platformCommission" to booking.platformCommission,
                "ownerGrossShare" to booking.ownerGrossShare,
                "totalAmount" to booking.totalAmount,
                "securityDeposit" to booking.securityDeposit,
                "status" to booking.status,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("rental_bookings")
                .document(booking.bookingId)
                .set(bookingMap)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
