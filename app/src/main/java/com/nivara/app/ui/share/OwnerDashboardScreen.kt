package com.nivara.app.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.nivara.app.ui.theme.WarmIvory

@Composable
fun OwnerDashboardScreen() {
    val userProfile by NivaraRepository.userProfile.collectAsState()
    val rentalItems by NivaraRepository.rentalItems.collectAsState()

    val myItems = rentalItems.filter { it.ownerId == "user_me" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "Owner Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DeepForestGreen
        )
        Text(
            text = "Track your rental earnings and listed inventory.",
            style = MaterialTheme.typography.bodyMedium,
            color = DeepForestGreen.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Earnings Summary Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DeepForestGreen,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Total Rental Earnings", color = WarmIvory.copy(alpha = 0.7f), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("₹${userProfile.totalEarnings.toInt()}", color = ChampagneGold, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Listed Items", color = WarmIvory.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text("${myItems.size}", color = WarmIvory, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Column {
                        Text("Platform Commission", color = WarmIvory.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text("20%", color = WarmIvory, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Column {
                        Text("Payout Status", color = WarmIvory.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text("Verified", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("My Listed Items (${myItems.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepForestGreen)
        Spacer(modifier = Modifier.height(12.dp))

        if (myItems.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("You haven't listed any items for rent yet.", color = DeepForestGreen.copy(alpha = 0.7f))
                }
            }
        } else {
            myItems.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.title, fontWeight = FontWeight.Bold, color = DeepForestGreen, fontSize = 14.sp)
                            Surface(shape = RoundedCornerShape(4.dp), color = ChampagneGold.copy(alpha = 0.2f)) {
                                Text(item.status, color = DeepForestGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Rental Price: ₹${item.dailyPrice.toInt()} / day • Deposit: ₹${item.securityDeposit.toInt()}", fontSize = 12.sp, color = DeepForestGreen.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}
