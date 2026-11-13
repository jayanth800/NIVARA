package com.nivara.app.data.model

data class BoxItem(
    val id: String,
    val uniqueBoxId: String,
    val qrCode: String,
    val category: String,
    val title: String,
    val description: String,
    val storageDate: String,
    val status: String, // "Stored", "In Transit", "Retrieved"
    val locationReference: String, // e.g. "Zone A - Rack 4 - Shelf B"
    val photoUrl: String,
    val itemsInside: List<InventoryItem> = emptyList()
)

data class InventoryItem(
    val id: String,
    val boxId: String,
    val name: String,
    val category: String,
    val description: String,
    val photoUrl: String
)

data class StorageCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val imageUrl: String
)

data class BookingOrder(
    val bookingId: String,
    val category: String,
    val boxCount: Int,
    val pickupDate: String,
    val pickupTime: String,
    val address: String,
    val packingType: String, // "Self-Packing" or "Professional Assistance"
    val specialInstructions: String,
    val estimatedCharges: Double,
    val status: String, // "Pickup Scheduled", "Picked Up", "At Storage Facility", "Stored Successfully"
    val trackingStage: Int // 0 to 6
)

data class RetrievalOrder(
    val retrievalId: String,
    val targetType: String, // "Box" or "Item"
    val targetId: String,
    val targetName: String,
    val deliveryAddress: String,
    val deliveryDate: String,
    val deliveryTime: String,
    val charges: Double,
    val status: String // "Retrieval Requested", "Out for Delivery", "Delivered"
)

data class RentalItem(
    val id: String,
    val ownerId: String,
    val ownerName: String,
    val title: String,
    val description: String,
    val category: String,
    val dailyPrice: Double,
    val weeklyPrice: Double,
    val securityDeposit: Double,
    val availableStart: String,
    val availableEnd: String,
    val condition: String, // "Like New", "Excellent", "Good"
    val photoUrl: String,
    val isAvailable: Boolean,
    val isVerified: Boolean,
    val status: String // "Available for Rent", "Currently Rented", "Verification Pending"
)

data class RentalBooking(
    val bookingId: String,
    val rentalItemId: String,
    val itemTitle: String,
    val itemPhotoUrl: String,
    val renterName: String,
    val startDate: String,
    val endDate: String,
    val rentalDays: Int,
    val rentalAmount: Double,
    val platformCommission: Double, // 20%
    val ownerGrossShare: Double,
    val deliveryCharge: Double,
    val cleaningFee: Double,
    val tax: Double,
    val totalAmount: Double,
    val securityDeposit: Double,
    val status: String // "Booking Confirmed", "Preparing for Dispatch", "Out for Delivery", "Delivered to Renter", "Rental Active", "Return Requested", "Collected", "Inspection Completed", "Returned to Storage"
)

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val membershipPlan: String, // "Gold Storage Plan (3 Medium Boxes)"
    val totalBoxesStored: Int,
    val activeRentalsCount: Int,
    val totalEarnings: Double,
    val savedAddresses: List<String>
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean
)

data class PaymentRecord(
    val paymentId: String,
    val userId: String,
    val bookingId: String,
    val amount: Double,
    val platformFee: Double,
    val securityDeposit: Double,
    val status: String, // "SUCCESS", "PENDING", "REFUNDED"
    val timestamp: String,
    val method: String
)

data class PayoutRecord(
    val payoutId: String,
    val ownerId: String,
    val amount: Double,
    val status: String, // "COMPLETED", "PROCESSING"
    val timestamp: String,
    val bankRef: String
)
