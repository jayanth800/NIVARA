package com.nivara.app.ui.storage

import androidx.compose.foundation.BorderStroke
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
import com.nivara.app.data.repository.NivaraRepository
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.SoftBeige
import com.nivara.app.ui.theme.WarmIvory

@Composable
fun RetrieveItemScreen(onRetrievalConfirmed: () -> Unit) {
    val storedBoxes by NivaraRepository.storedBoxes.collectAsState()
    var selectedTargetId by remember { mutableStateOf(storedBoxes.firstOrNull()?.id ?: "") }
    var retrievalType by remember { mutableStateOf("Entire Box") } // "Entire Box" or "Individual Item"
    var deliveryAddress by remember { mutableStateOf("A-402, Greenwoods Apartment, MG Road, Bengaluru") }
    var deliveryDate by remember { mutableStateOf("Tomorrow, 02:00 PM - 04:00 PM") }
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
                        text = "Retrieval Requested!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your items are being retrieved from warehouse vault and prepared for dispatch.",
                        textAlign = TextAlign.Center,
                        color = DeepForestGreen.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onRetrievalConfirmed,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Track Delivery", color = WarmIvory, fontWeight = FontWeight.Bold)
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
                text = "Retrieve Belongings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen
            )
            Text(
                text = "Request delivery of an entire box or individual stored items.",
                style = MaterialTheme.typography.bodyMedium,
                color = DeepForestGreen.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Retrieval Type", fontWeight = FontWeight.Bold, color = DeepForestGreen)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Entire Box", "Individual Item").forEach { type ->
                    val isSelected = retrievalType == type
                    Button(
                        onClick = { retrievalType = type },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) DeepForestGreen else SoftBeige
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = type,
                            color = if (isSelected) WarmIvory else DeepForestGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Select Box / Item", fontWeight = FontWeight.Bold, color = DeepForestGreen)
            Spacer(modifier = Modifier.height(8.dp))

            storedBoxes.forEach { box ->
                val isSelected = selectedTargetId == box.id
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) DeepForestGreen.copy(alpha = 0.1f) else Color.White,
                    border = BorderStroke(if (isSelected) 2.dp else 0.dp, DeepForestGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { selectedTargetId = box.id }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(box.title, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                            Text("${box.uniqueBoxId} • ${box.category}", fontSize = 12.sp, color = DeepForestGreen.copy(alpha = 0.7f))
                        }
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DeepForestGreen)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = deliveryAddress,
                onValueChange = { deliveryAddress = it },
                label = { Text("Delivery Address") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = deliveryDate,
                onValueChange = { deliveryDate = it },
                label = { Text("Preferred Delivery Date & Time") },
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
                        Text("Retrieval & Express Delivery:", color = DeepForestGreen, fontWeight = FontWeight.Medium)
                        Text("₹350", color = DeepForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showSuccess = true },
                colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = ChampagneGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Retrieval Request", color = WarmIvory, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
