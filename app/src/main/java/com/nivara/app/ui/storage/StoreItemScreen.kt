package com.nivara.app.ui.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nivara.app.data.model.BookingOrder
import com.nivara.app.data.model.BoxItem
import com.nivara.app.data.repository.NivaraRepository
import com.nivara.app.data.repository.StorageRepository
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.SoftBeige
import com.nivara.app.ui.theme.WarmIvory
import kotlinx.coroutines.launch

@Composable
fun StoreItemScreen(onBookingConfirmed: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf("Clothing & Wardrobe") }
    var boxCount by remember { mutableStateOf("2") }
    var pickupDate by remember { mutableStateOf("Tomorrow, 10:00 AM - 12:00 PM") }
    var address by remember { mutableStateOf("A-402, Greenwoods Apartment, MG Road, Bengaluru") }
    var packingType by remember { mutableStateOf("Professional Assistance") }
    var specialInstructions by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }
    var confirmedId by remember { mutableStateOf("") }

    val categories = listOf(
        "Clothing & Wardrobe", "Travel & Luggage", "Home Essentials",
        "Festival Decorations", "Books & Documents", "Sports Equipment", "Electronics"
    )

    if (showConfirmation) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmIvory)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = DeepForestGreen,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Booking Confirmed!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unique Booking ID: $confirmedId",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Our executive will arrive at your address with tamper-proof storage boxes as scheduled.",
                        textAlign = TextAlign.Center,
                        color = DeepForestGreen.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBookingConfirmed,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("View My Bookings & Storage", color = WarmIvory, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmIvory)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .padding(bottom = 80.dp)
        ) {
            Text(
                text = "Book Storage Pickup",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen
            )
            Text(
                text = "We collect, pack and store your items securely.",
                style = MaterialTheme.typography.bodyMedium,
                color = DeepForestGreen.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Select Storage Category", fontWeight = FontWeight.Bold, color = DeepForestGreen)
            Spacer(modifier = Modifier.height(8.dp))
            // Category dropdown / radio options
            categories.chunked(2).forEach { rowCats ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowCats.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) DeepForestGreen else Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .padding(vertical = 4.dp),
                            onClick = { selectedCategory = cat }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) WarmIvory else DeepForestGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = boxCount,
                onValueChange = { boxCount = it },
                label = { Text("Number of Boxes Needed") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Pickup Address") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pickupDate,
                onValueChange = { pickupDate = it },
                label = { Text("Pickup Date & Time Slot") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Packing Assistance", fontWeight = FontWeight.Bold, color = DeepForestGreen)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Self-Packing", "Professional Assistance").forEach { type ->
                    val isSelected = packingType == type
                    Button(
                        onClick = { packingType = type },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) DeepForestGreen else SoftBeige
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = type,
                            color = if (isSelected) WarmIvory else DeepForestGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = specialInstructions,
                onValueChange = { specialInstructions = it },
                label = { Text("Special Handling Instructions (Optional)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SoftBeige,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Monthly Storage:", color = DeepForestGreen, fontWeight = FontWeight.Medium)
                        Text("₹800 / box", color = DeepForestGreen, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pickup & Transport:", color = DeepForestGreen, fontWeight = FontWeight.Medium)
                        Text("FREE", color = ChampagneGold, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    confirmedId = "NIV-BK-${(1000..9999).random()}"
                    NivaraRepository.addBookingOrder(
                        BookingOrder(
                            bookingId = confirmedId,
                            category = selectedCategory,
                            boxCount = boxCount.toIntOrNull() ?: 1,
                            pickupDate = pickupDate,
                            pickupTime = "10:00 AM - 12:00 PM",
                            address = address,
                            packingType = packingType,
                            specialInstructions = specialInstructions,
                            estimatedCharges = (boxCount.toIntOrNull() ?: 1) * 800.0,
                            status = "Pickup Scheduled",
                            trackingStage = 1
                        )
                    )
                    val newBox = BoxItem(
                        id = "box_${System.currentTimeMillis()}",
                        uniqueBoxId = "NIV-BOX-${(1000..9999).random()}",
                        qrCode = "QR_${System.currentTimeMillis()}",
                        category = selectedCategory,
                        title = "$selectedCategory Stored Box",
                        description = specialInstructions.ifEmpty { "Stored securely with NIVARA." },
                        storageDate = "Today",
                        status = "Stored",
                        locationReference = "Warehouse Alpha - Zone E",
                        photoUrl = "https://images.unsplash.com/photo-1558769132-cb1aea458c5e?auto=format&fit=crop&q=80&w=600"
                    )
                    NivaraRepository.addStoredBox(newBox)
                    coroutineScope.launch {
                        StorageRepository.saveBoxToFirestore(newBox)
                    }
                    showConfirmation = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = ChampagneGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Storage Booking", color = WarmIvory, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
