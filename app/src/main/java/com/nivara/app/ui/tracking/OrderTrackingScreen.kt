package com.nivara.app.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nivara.app.data.repository.NivaraRepository
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.SoftBeige
import com.nivara.app.ui.theme.WarmIvory

@Composable
fun OrderTrackingScreen() {
    val bookings by NivaraRepository.bookingOrders.collectAsState()
    val activeBooking = bookings.firstOrNull()

    val stages = listOf(
        "Pickup Scheduled",
        "Executive Assigned",
        "Picked Up from Home",
        "In Transit to Warehouse",
        "At Secure Storage Facility",
        "Digital Inventory Registered",
        "Stored Successfully"
    )

    val currentStageIndex = activeBooking?.trackingStage ?: 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "Order & Pickup Tracking",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DeepForestGreen
        )
        Text(
            text = activeBooking?.let { "Booking ID: ${it.bookingId} • ${it.category}" } ?: "No active orders",
            style = MaterialTheme.typography.bodyMedium,
            color = DeepForestGreen.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Live Status", fontSize = 12.sp, color = ChampagneGold, fontWeight = FontWeight.Bold)
                        Text(activeBooking?.status ?: "Processing", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepForestGreen)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                stages.forEachIndexed { index, stage ->
                    val isCompleted = index <= currentStageIndex
                    val isCurrent = index == currentStageIndex

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isCompleted) DeepForestGreen else SoftBeige,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isCompleted) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = WarmIvory, modifier = Modifier.size(16.dp))
                                } else {
                                    Text("${index + 1}", fontSize = 11.sp, color = DeepForestGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stage,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCompleted) DeepForestGreen else DeepForestGreen.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                            if (isCurrent) {
                                Text("In progress...", fontSize = 11.sp, color = ChampagneGold, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
