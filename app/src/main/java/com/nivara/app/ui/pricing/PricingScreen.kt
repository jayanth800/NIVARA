package com.nivara.app.ui.pricing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.SoftBeige
import com.nivara.app.ui.theme.WarmIvory

@Composable
fun PricingScreen(onNavigateToStore: () -> Unit) {
    var showCalculator by remember { mutableStateOf(false) }
    var boxesCount by remember { mutableStateOf(2f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Storage Plans & Pricing",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen
                )
                Text(
                    text = "Transparent monthly & annual storage plans.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeepForestGreen.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = { showCalculator = !showCalculator },
                colors = IconButtonDefaults.iconButtonColors(containerColor = ChampagneGold)
            ) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = "Calculator", tint = DeepForestGreen)
            }
        }

        if (showCalculator) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepForestGreen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Smart Space Calculator", color = WarmIvory, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estimate storage boxes needed: ${boxesCount.toInt()} boxes", color = ChampagneGold, fontSize = 14.sp)
                    Slider(
                        value = boxesCount,
                        onValueChange = { boxesCount = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(thumbColor = ChampagneGold, activeTrackColor = ChampagneGold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estimated Cost: ₹${boxesCount.toInt() * 800} / month (Free Pickup)", color = WarmIvory, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Plan Cards
        PricingCard(
            title = "Small Box Plan",
            price = "₹599",
            period = "per month",
            features = listOf("1 Heavy-Duty Tamper-Proof Box", "Free Pickup & Return Delivery", "24/7 Climate Control Vault", "Digital QR Inventory Access"),
            buttonColor = SoftBeige,
            textColor = DeepForestGreen,
            onClick = onNavigateToStore
        )

        Spacer(modifier = Modifier.height(16.dp))

        PricingCard(
            title = "Gold Storage Plan (Most Popular)",
            price = "₹2,199",
            period = "per month (4 Boxes)",
            features = listOf("4 Heavy-Duty Storage Boxes", "Free Priority Pickup & Delivery", "Climate & Humidity Monitoring", "Free P2P Rental Integration", "Dedicated Storage Assistant"),
            buttonColor = ChampagneGold,
            textColor = DeepForestGreen,
            onClick = onNavigateToStore
        )

        Spacer(modifier = Modifier.height(16.dp))

        PricingCard(
            title = "Annual Household Vault",
            price = "₹21,999",
            period = "per year (Up to 10 Boxes)",
            features = listOf("10 Large Storage Boxes", "Unlimited Free Retrievals", "Zero Maintenance Fees", "Full Insurance Cover up to ₹1,00,000", "Family Shared Access"),
            buttonColor = DeepForestGreen,
            textColor = WarmIvory,
            onClick = onNavigateToStore
        )
    }
}

@Composable
fun PricingCard(
    title: String,
    price: String,
    period: String,
    features: List<String>,
    buttonColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepForestGreen)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = DeepForestGreen)
                Spacer(modifier = Modifier.width(4.dp))
                Text(period, fontSize = 12.sp, color = DeepForestGreen.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, fontSize = 13.sp, color = DeepForestGreen.copy(alpha = 0.8f))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Choose Plan", color = textColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}
