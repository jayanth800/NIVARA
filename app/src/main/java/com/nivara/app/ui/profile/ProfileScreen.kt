package com.nivara.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nivara.app.data.repository.NivaraRepository
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.SoftBeige
import com.nivara.app.ui.theme.WarmIvory

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val userProfile by NivaraRepository.userProfile.collectAsState()
    val notifications by NivaraRepository.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "Profile & Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DeepForestGreen
        )

        Spacer(modifier = Modifier.height(20.dp))

        // User Info Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DeepForestGreen,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = userProfile.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(userProfile.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = WarmIvory)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(userProfile.email, fontSize = 13.sp, color = WarmIvory.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(userProfile.phone, fontSize = 13.sp, color = ChampagneGold, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Storage & Membership", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepForestGreen)
        Spacer(modifier = Modifier.height(12.dp))

        ProfileMenuItem(title = "Active Plan: ${userProfile.membershipPlan}", icon = Icons.Default.WorkspacePremium, onClick = {})
        ProfileMenuItem(title = "Warehouse Climate & Humidity Monitor", icon = Icons.Default.Thermostat, onClick = {})
        ProfileMenuItem(title = "Saved Addresses (${userProfile.savedAddresses.size})", icon = Icons.Default.LocationOn, onClick = {})
        ProfileMenuItem(title = "Payment History & Invoices", icon = Icons.Default.ReceiptLong, onClick = {})

        Spacer(modifier = Modifier.height(24.dp))

        Text("Notifications (${notifications.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepForestGreen)
        Spacer(modifier = Modifier.height(12.dp))

        notifications.forEach { notif ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = ChampagneGold)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(notif.title, fontWeight = FontWeight.Bold, color = DeepForestGreen, fontSize = 14.sp)
                        Text(notif.message, color = DeepForestGreen.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Support & Legal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepForestGreen)
        Spacer(modifier = Modifier.height(12.dp))

        ProfileMenuItem(title = "Help & Support Center", icon = Icons.Default.HelpOutline, onClick = {})
        ProfileMenuItem(title = "Privacy Policy & Terms", icon = Icons.Default.Gavel, onClick = {})

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileMenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = DeepForestGreen)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.SemiBold, color = DeepForestGreen, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = DeepForestGreen.copy(alpha = 0.5f))
        }
    }
}
