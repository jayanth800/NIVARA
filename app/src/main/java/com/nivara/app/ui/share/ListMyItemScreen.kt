package com.nivara.app.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nivara.app.data.model.RentalItem
import com.nivara.app.data.repository.NivaraRepository
import com.nivara.app.data.repository.ShareRepository
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.WarmIvory
import kotlinx.coroutines.launch

@Composable
fun ListMyItemScreen(onItemListed: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val storedBoxes by NivaraRepository.storedBoxes.collectAsState()
    var selectedBoxId by remember { mutableStateOf(storedBoxes.firstOrNull()?.id ?: "") }
    var title by remember { mutableStateOf("Coleman 4-Person Waterproof Camping Tent") }
    var description by remember { mutableStateOf("Stored securely with NIVARA. Premium dome tent used only twice.") }
    var category by remember { mutableStateOf("Camping and Outdoor Equipment") }
    var dailyPrice by remember { mutableStateOf("350") }
    var weeklyPrice by remember { mutableStateOf("2000") }
    var securityDeposit by remember { mutableStateOf("1500") }
    var condition by remember { mutableStateOf("Like New") }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
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
                        text = "Listing Submitted!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your item has been submitted for NIVARA warehouse verification and will be published on NIVARA Share shortly.",
                        textAlign = TextAlign.Center,
                        color = DeepForestGreen.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onItemListed,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("View Marketplace", color = WarmIvory, fontWeight = FontWeight.Bold)
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
                text = "List Item for Rent",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen
            )
            Text(
                text = "Earn passive income by renting out stored belongings.",
                style = MaterialTheme.typography.bodyMedium,
                color = DeepForestGreen.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Select Stored Item / Box", fontWeight = FontWeight.Bold, color = DeepForestGreen)
            Spacer(modifier = Modifier.height(8.dp))

            storedBoxes.forEach { box ->
                val isSelected = selectedBoxId == box.id
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) DeepForestGreen.copy(alpha = 0.1f) else Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = {
                        selectedBoxId = box.id
                        title = box.title
                        description = box.description
                        category = box.category
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(box.title, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                        Text("${box.uniqueBoxId} • ${box.category}", fontSize = 12.sp, color = DeepForestGreen.copy(alpha = 0.7f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Item Title") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Condition Details") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dailyPrice,
                    onValueChange = { dailyPrice = it },
                    label = { Text("Daily Price (₹)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = weeklyPrice,
                    onValueChange = { weeklyPrice = it },
                    label = { Text("Weekly Price (₹)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = securityDeposit,
                onValueChange = { securityDeposit = it },
                label = { Text("Refundable Security Deposit (₹)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val newItem = RentalItem(
                        id = "rent_${System.currentTimeMillis()}",
                        ownerId = "user_me",
                        ownerName = "Aarav Sharma (You)",
                        title = title,
                        description = description,
                        category = category,
                        dailyPrice = dailyPrice.toDoubleOrNull() ?: 300.0,
                        weeklyPrice = weeklyPrice.toDoubleOrNull() ?: 1800.0,
                        securityDeposit = securityDeposit.toDoubleOrNull() ?: 1000.0,
                        availableStart = "Immediate",
                        availableEnd = "30 Jun 2025",
                        condition = condition,
                        photoUrl = "https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?auto=format&fit=crop&q=80&w=600",
                        isAvailable = true,
                        isVerified = true,
                        status = "Available for Rent"
                    )
                    NivaraRepository.addRentalItem(newItem)
                    coroutineScope.launch {
                        ShareRepository.saveListingToFirestore(newItem)
                    }
                    showSuccess = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = ChampagneGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Listing for Verification", color = WarmIvory, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
