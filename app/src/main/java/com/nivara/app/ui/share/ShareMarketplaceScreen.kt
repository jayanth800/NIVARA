package com.nivara.app.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nivara.app.data.model.RentalItem
import com.nivara.app.data.repository.NivaraRepository
import com.nivara.app.data.repository.ShareRepository
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.WarmIvory

@Composable
fun ShareMarketplaceScreen(
    onNavigateToListMyItem: () -> Unit,
    onNavigateToOwnerDashboard: () -> Unit,
    onItemClick: (RentalItem) -> Unit
) {
    val seedRentalItems by NivaraRepository.rentalItems.collectAsState()
    val firestoreListings by ShareRepository.getMarketplaceListingsFlow().collectAsState(initial = emptyList())

    val combinedRentalItems = (firestoreListings + seedRentalItems).distinctBy { it.id }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredItems = combinedRentalItems.filter { item ->
        val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory)
            .padding(20.dp)
            .padding(bottom = 80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ChampagneGold
                ) {
                    Text(
                        text = "PEER-TO-PEER MARKETPLACE",
                        color = DeepForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "NIVARA SHARE",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onNavigateToOwnerDashboard,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = SoftBeige())
                ) {
                    Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard", tint = DeepForestGreen)
                }

                Button(
                    onClick = onNavigateToListMyItem,
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("List Item", color = WarmIvory, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search tents, cameras, appliances...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DeepForestGreen) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = DeepForestGreen,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Rental Items
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No rental items found", color = DeepForestGreen, fontWeight = FontWeight.SemiBold)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredItems) { item ->
                    RentalItemCard(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}

@Composable
fun RentalItemCard(item: RentalItem, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = item.photoUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = DeepForestGreen,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = item.condition,
                        color = WarmIvory,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepForestGreen,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${item.dailyPrice.toInt()} / day",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ChampagneGold
                    )
                    Text(
                        text = "Verified",
                        fontSize = 10.sp,
                        color = DeepForestGreen.copy(alpha = 0.6f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun SoftBeige(): Color = Color(0xFFE8DFD0)
