package com.nivara.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nivara.app.data.model.StorageCategory
import com.nivara.app.data.repository.NivaraRepository
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.SoftBeige
import com.nivara.app.ui.theme.WarmIvory

@Composable
fun HomeScreen(
    onNavigateToStore: () -> Unit,
    onNavigateToRetrieve: () -> Unit,
    onNavigateToMyStorage: () -> Unit,
    onNavigateToShare: () -> Unit,
    onNavigateToPricing: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToAssistant: () -> Unit
) {
    val userProfile by NivaraRepository.userProfile.collectAsState()
    val categories by NivaraRepository.storageCategories.collectAsState()
    val storedBoxes by NivaraRepository.storedBoxes.collectAsState()
    val bookings by NivaraRepository.bookingOrders.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .verticalScroll(scrollState)
            .padding(bottom = 100.dp)
    ) {
        // Top Bar / Greeting
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DeepForestGreen, DeepForestGreen.copy(alpha = 0.9f))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = userProfile.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Welcome back,",
                                color = WarmIvory.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                            Text(
                                text = userProfile.name,
                                color = WarmIvory,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onNavigateToAssistant,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = ChampagneGold)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Assistant",
                            tint = DeepForestGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Search Bar
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search stored items, boxes, or rentals...", color = TextSecondaryDark()) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ChampagneGold) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // Storage Summary Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DeepForestGreen,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Storage Usage", color = WarmIvory.copy(alpha = 0.7f), fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${storedBoxes.size} Boxes Stored", color = WarmIvory, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ChampagneGold
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = DeepForestGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { 0.65f },
                        color = ChampagneGold,
                        trackColor = WarmIvory.copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("65% of Plan Capacity", color = WarmIvory.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text("Gold Plan (Annual)", color = ChampagneGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepForestGreen)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem("Store Items", Icons.Default.AddBox, DeepForestGreen, onNavigateToStore)
                QuickActionItem("Retrieve", Icons.Default.LocalShipping, ChampagneGold, onNavigateToRetrieve)
                QuickActionItem("My Storage", Icons.Default.Inventory, DeepForestGreen, onNavigateToMyStorage)
                QuickActionItem("NIVARA Share", Icons.Default.Storefront, ChampagneGold, onNavigateToShare)
            }

            // Upcoming Pickups / Orders Alert
            if (bookings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SoftBeige,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTracking() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = DeepForestGreen,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Upcoming Pickup Scheduled", fontWeight = FontWeight.Bold, color = DeepForestGreen, fontSize = 14.sp)
                            Text("${bookings[0].category} (${bookings[0].boxCount} boxes) • ${bookings[0].pickupDate}", color = DeepForestGreen.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = DeepForestGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Popular Storage Categories
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Storage Categories", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DeepForestGreen)
                TextButton(onClick = onNavigateToMyStorage) {
                    Text("View All", color = ChampagneGold, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.take(6).forEach { cat ->
                    CategoryCard(cat, onNavigateToStore)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Promotional Banner for NIVARA Share
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DeepForestGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToShare() }
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ChampagneGold
                        ) {
                            Text(
                                text = "NIVARA SHARE",
                                color = DeepForestGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Earn Money From Stored Items", fontWeight = FontWeight.Bold, color = WarmIvory, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Rent out your camping gear, tools & electronics when not in use.", color = WarmIvory.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = color,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = title, tint = WarmIvory, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DeepForestGreen)
    }
}

@Composable
fun CategoryCard(category: StorageCategory, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        modifier = Modifier
            .width(130.dp)
            .height(150.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = category.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepForestGreen,
                maxLines = 2,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

private fun TextSecondaryDark(): Color = Color(0xFFB8C5BE)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    com.nivara.app.ui.theme.NIVARATheme {
        HomeScreen(
            onNavigateToStore = {},
            onNavigateToRetrieve = {},
            onNavigateToMyStorage = {},
            onNavigateToShare = {},
            onNavigateToPricing = {},
            onNavigateToTracking = {},
            onNavigateToAssistant = {}
        )
    }
}

