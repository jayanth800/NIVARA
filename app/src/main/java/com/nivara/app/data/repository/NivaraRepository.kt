package com.nivara.app.data.repository

import com.nivara.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NivaraRepository {

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Aarav Sharma",
            email = "aarav.sharma@example.com",
            phone = "+91 98765 43210",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=400",
            membershipPlan = "Gold Storage Plan (Annual)",
            totalBoxesStored = 4,
            activeRentalsCount = 1,
            totalEarnings = 14200.0,
            savedAddresses = listOf("A-402, Greenwoods Apartment, MG Road, Bengaluru", "Villa 14, Palm Meadows, Whitefield, Bengaluru")
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _storageCategories = MutableStateFlow(
        listOf(
            StorageCategory("1", "Clothing & Wardrobe", "Seasonal wear, woolens, winter coats & ethnic wear", "Checkroom", "https://images.unsplash.com/photo-1558769132-cb1aea458c5e?auto=format&fit=crop&q=80&w=600"),
            StorageCategory("2", "Travel & Luggage", "Suitcases, duffel bags, trekking rucksacks", "Luggage", "https://images.unsplash.com/photo-1565026057447-bcbe0e7ef976?auto=format&fit=crop&q=80&w=600"),
            StorageCategory("3", "Home Essentials", "Extra bedsheets, duvets, pillows & heavy blankets", "Home", "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?auto=format&fit=crop&q=80&w=600"),
            StorageCategory("4", "Festival Decorations", "Diwali lights, Christmas trees, puja items & banners", "Celebration", "https://images.unsplash.com/photo-1513151233558-d860c5398176?auto=format&fit=crop&q=80&w=600"),
            StorageCategory("5", "Books & Documents", "Academic textbooks, old archives & legal files", "Book", "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&q=80&w=600"),
            StorageCategory("6", "Sports Equipment", "Camping tents, badminton rackets, golf clubs & weights", "Sports", "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&q=80&w=600"),
            StorageCategory("7", "Electronics", "Home theatre systems, spare monitors & consoles", "Devices", "https://images.unsplash.com/photo-1550009158-9ebf69173e03?auto=format&fit=crop&q=80&w=600"),
            StorageCategory("8", "Furniture", "Side tables, chairs, foldable mattresses & racks", "Weekend", "https://images.unsplash.com/photo-1538688525198-9b88f6f53126?auto=format&fit=crop&q=80&w=600"),
            StorageCategory("9", "Baby Products", "Strollers, high chairs, cribs & infant toys", "Child", "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&q=80&w=600"),
            StorageCategory("10", "Other Belongings", "Miscellaneous items & memorabilia", "AllInclusive", "https://images.unsplash.com/photo-1595246140625-573b715d11dc?auto=format&fit=crop&q=80&w=600")
        )
    )
    val storageCategories: StateFlow<List<StorageCategory>> = _storageCategories.asStateFlow()

    private val _storedBoxes = MutableStateFlow(
        listOf(
            BoxItem(
                id = "box_1",
                uniqueBoxId = "NIV-BOX-8842",
                qrCode = "QR_NIV_8842",
                category = "Clothing & Wardrobe",
                title = "Winter & Woolen Wear",
                description = "Heavy woolens, jackets, thermal wear, and winter boots.",
                storageDate = "15 Nov 2024",
                status = "Stored",
                locationReference = "Warehouse Alpha - Zone A - Rack 2",
                photoUrl = "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?auto=format&fit=crop&q=80&w=600",
                itemsInside = listOf(
                    InventoryItem("inv_1", "box_1", "Cashmere Overcoat", "Clothing", "Charcoal grey winter coat", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&q=80&w=400"),
                    InventoryItem("inv_2", "box_1", "Thermal Innerwear Set", "Clothing", "Merino wool thermal set (2 pcs)", "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?auto=format&fit=crop&q=80&w=400"),
                    InventoryItem("inv_3", "box_1", "Leather Snow Boots", "Footwear", "Waterproof brown boots size 10", "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?auto=format&fit=crop&q=80&w=400")
                )
            ),
            BoxItem(
                id = "box_2",
                uniqueBoxId = "NIV-BOX-5510",
                qrCode = "QR_NIV_5510",
                category = "Travel & Luggage",
                title = "Camping & Expedition Tent",
                description = "4-person waterproof dome tent with poles and stakes.",
                storageDate = "02 Jan 2025",
                status = "Stored",
                locationReference = "Warehouse Alpha - Zone B - Rack 4",
                photoUrl = "https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?auto=format&fit=crop&q=80&w=600",
                itemsInside = listOf(
                    InventoryItem("inv_4", "box_2", "Coleman 4-Person Dome Tent", "Camping", "Waterproof trekking tent", "https://images.unsplash.com/photo-1478131143081-80f7f84ca84d?auto=format&fit=crop&q=80&w=400"),
                    InventoryItem("inv_5", "box_2", "Sleeping Bag -10C", "Camping", "Thermal insulated sleeping bag", "https://images.unsplash.com/photo-1520116468816-95b69f847357?auto=format&fit=crop&q=80&w=400")
                )
            ),
            BoxItem(
                id = "box_3",
                uniqueBoxId = "NIV-BOX-9123",
                qrCode = "QR_NIV_9123",
                category = "Festival Decorations",
                title = "Diwali & Festive Decor",
                description = "LED string lights, brass diyas, and artificial marigold garlands.",
                storageDate = "10 Nov 2024",
                status = "Stored",
                locationReference = "Warehouse Alpha - Zone C - Rack 1",
                photoUrl = "https://images.unsplash.com/photo-1513151233558-d860c5398176?auto=format&fit=crop&q=80&w=600",
                itemsInside = listOf(
                    InventoryItem("inv_6", "box_3", "Warm LED Fairy Lights (50m)", "Decor", "Copper wire warm white lights", "https://images.unsplash.com/photo-1543589077-47d81606c1bf?auto=format&fit=crop&q=80&w=400"),
                    InventoryItem("inv_7", "box_3", "Handcrafted Brass Diyas", "Decor", "Set of 12 heavy brass diyas", "https://images.unsplash.com/photo-1604537529428-15bcbeecfe4d?auto=format&fit=crop&q=80&w=400")
                )
            ),
            BoxItem(
                id = "box_4",
                uniqueBoxId = "NIV-BOX-3398",
                qrCode = "QR_NIV_3398",
                category = "Books & Documents",
                title = "College Archives & Tax Files",
                description = "Engineering textbooks, semester notes, and tax assessment files.",
                storageDate = "20 Aug 2024",
                status = "Stored",
                locationReference = "Warehouse Alpha - Zone D - Rack 5",
                photoUrl = "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&q=80&w=600",
                itemsInside = listOf(
                    InventoryItem("inv_8", "box_4", "Advanced Calculus & Algorithms", "Books", "Hardcover reference textbooks", "https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&q=80&w=400"),
                    InventoryItem("inv_9", "box_4", "Tax & Financial Files (2020-23)", "Documents", "Organized binder folders", "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80&w=400")
                )
            )
        )
    )
    val storedBoxes: StateFlow<List<BoxItem>> = _storedBoxes.asStateFlow()

    private val _bookingOrders = MutableStateFlow(
        listOf(
            BookingOrder(
                bookingId = "NIV-BK-9021",
                category = "Home Essentials",
                boxCount = 2,
                pickupDate = "Tomorrow, 10:00 AM",
                pickupTime = "10:00 AM - 12:00 PM",
                address = "A-402, Greenwoods Apartment, MG Road, Bengaluru",
                packingType = "Professional Assistance",
                specialInstructions = "Handle fragile glass items with extra bubble wrap.",
                estimatedCharges = 1200.0,
                status = "Pickup Scheduled",
                trackingStage = 1
            )
        )
    )
    val bookingOrders: StateFlow<List<BookingOrder>> = _bookingOrders.asStateFlow()

    private val _rentalItems = MutableStateFlow(
        listOf(
            RentalItem(
                id = "rent_1",
                ownerId = "user_me",
                ownerName = "Aarav Sharma (You)",
                title = "Coleman 4-Person Waterproof Camping Tent",
                description = "Stored securely with NIVARA. Premium dome tent used only twice. Cleaned & inspected by warehouse staff.",
                category = "Camping and Outdoor Equipment",
                dailyPrice = 350.0,
                weeklyPrice = 2000.0,
                securityDeposit = 1500.0,
                availableStart = "01 Mar 2025",
                availableEnd = "30 Apr 2025",
                condition = "Like New",
                photoUrl = "https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?auto=format&fit=crop&q=80&w=600",
                isAvailable = true,
                isVerified = true,
                status = "Available for Rent"
            ),
            RentalItem(
                id = "rent_2",
                ownerId = "user_other",
                ownerName = "Vikram Malhotra",
                title = "Sony Alpha Mirrorless Camera Kit (50mm Lens)",
                description = "Professional camera stored in climate-controlled NIVARA vault. Pristine condition.",
                category = "Electronics",
                dailyPrice = 1200.0,
                weeklyPrice = 7000.0,
                securityDeposit = 5000.0,
                availableStart = "Immediate",
                availableEnd = "15 May 2025",
                condition = "Excellent",
                photoUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&q=80&w=600",
                isAvailable = true,
                isVerified = true,
                status = "Available for Rent"
            ),
            RentalItem(
                id = "rent_3",
                ownerId = "user_other",
                ownerName = "Priya Nambiar",
                title = "DeLonghi Espresso Coffee Machine",
                description = "Semi-automatic espresso maker with milk frother. Inspected & sanitized.",
                category = "Home Appliances",
                dailyPrice = 500.0,
                weeklyPrice = 3000.0,
                securityDeposit = 2500.0,
                availableStart = "Immediate",
                availableEnd = "01 Jun 2025",
                condition = "Like New",
                photoUrl = "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?auto=format&fit=crop&q=80&w=600",
                isAvailable = true,
                isVerified = true,
                status = "Available for Rent"
            ),
            RentalItem(
                id = "rent_4",
                ownerId = "user_other",
                ownerName = "Rohan Mehta",
                title = "Thule Roof Cargo Box for SUVs",
                description = "Aerodynamic roof carrier box for road trips. Easy installation.",
                category = "Travel Accessories",
                dailyPrice = 800.0,
                weeklyPrice = 4500.0,
                securityDeposit = 4000.0,
                availableStart = "10 Mar 2025",
                availableEnd = "30 May 2025",
                condition = "Good",
                photoUrl = "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&q=80&w=600",
                isAvailable = true,
                isVerified = true,
                status = "Available for Rent"
            )
        )
    )
    val rentalItems: StateFlow<List<RentalItem>> = _rentalItems.asStateFlow()

    private val _rentalBookings = MutableStateFlow(
        listOf(
            RentalBooking(
                bookingId = "SHR-BK-4092",
                rentalItemId = "rent_1",
                itemTitle = "Coleman 4-Person Waterproof Camping Tent",
                itemPhotoUrl = "https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?auto=format&fit=crop&q=80&w=600",
                renterName = "Aarav Sharma (You)",
                startDate = "12 Mar 2025",
                endDate = "15 Mar 2025",
                rentalDays = 3,
                rentalAmount = 1050.0,
                platformCommission = 210.0,
                ownerGrossShare = 840.0,
                deliveryCharge = 150.0,
                cleaningFee = 100.0,
                tax = 45.0,
                totalAmount = 1345.0,
                securityDeposit = 1500.0,
                status = "Booking Confirmed"
            )
        )
    )
    val rentalBookings: StateFlow<List<RentalBooking>> = _rentalBookings.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("notif_1", "Pickup Confirmed", "Your storage pickup is scheduled for tomorrow between 10 AM - 12 PM.", "2 hours ago", false),
            NotificationItem("notif_2", "NIVARA Share Payout", "₹1,600 credited to your bank account for recent tent rental.", "Yesterday", true),
            NotificationItem("notif_3", "Seasonal Reminder", "Spring is here! Time to retrieve your winter clothes or keep them safely stored.", "3 days ago", true)
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun addStoredBox(box: BoxItem) {
        _storedBoxes.value = _storedBoxes.value + box
        _userProfile.value = _userProfile.value.copy(totalBoxesStored = _storedBoxes.value.size)
    }

    fun addBookingOrder(order: BookingOrder) {
        _bookingOrders.value = _bookingOrders.value + order
    }

    fun addRentalItem(item: RentalItem) {
        _rentalItems.value = listOf(item) + _rentalItems.value
    }

    fun addRentalBooking(booking: RentalBooking) {
        _rentalBookings.value = listOf(booking) + _rentalBookings.value
        _userProfile.value = _userProfile.value.copy(
            activeRentalsCount = _userProfile.value.activeRentalsCount + 1,
            totalEarnings = _userProfile.value.totalEarnings + (booking.ownerGrossShare)
        )
    }
}
