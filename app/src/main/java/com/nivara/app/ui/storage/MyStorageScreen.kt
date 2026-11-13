package com.nivara.app.ui.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.nivara.app.data.model.BoxItem
import com.nivara.app.data.repository.StorageRepository
import com.nivara.app.ui.theme.ChampagneGold
import com.nivara.app.ui.theme.DeepForestGreen
import com.nivara.app.ui.theme.SoftBeige
import com.nivara.app.ui.theme.WarmIvory

@Composable
fun MyStorageScreen(
    onNavigateToStore: () -> Unit,
    onNavigateToRetrieve: () -> Unit,
    onBoxClick: (BoxItem) -> Unit
) {
    val firestoreBoxes by StorageRepository.getUserBoxesFlow().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedBoxForQr by remember { mutableStateOf<BoxItem?>(null) }

    val filteredBoxes = firestoreBoxes.filter { box ->
        val matchesSearch = box.title.contains(searchQuery, ignoreCase = true) ||
                box.uniqueBoxId.contains(searchQuery, ignoreCase = true) ||
                box.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == "All" || box.category == selectedCategoryFilter
        matchesSearch && matchesCategory
    }

    // QR Code Dialog
    if (selectedBoxForQr != null) {
        AlertDialog(
            onDismissRequest = { selectedBoxForQr = null },
            confirmButton = {
                Button(onClick = { selectedBoxForQr = null }, colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)) {
                    Text("Close", color = WarmIvory)
                }
            },
            title = { Text("Box QR Code & Tracking", color = DeepForestGreen, fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DeepForestGreen,
                        modifier = Modifier.size(160.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(120.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(selectedBoxForQr?.uniqueBoxId ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepForestGreen)
                    Text("Scan at warehouse gate for instant check-in", fontSize = 12.sp, color = DeepForestGreen.copy(alpha = 0.7f))
                }
            }
        )
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
                Text(
                    text = "My Storage",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen
                )
                Text(
                    text = "${firestoreBoxes.size} Boxes in secure NIVARA vault",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeepForestGreen.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = onNavigateToStore,
                colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = ChampagneGold)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Store More", color = WarmIvory, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by Box ID, title or item...") },
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

        // Box List
        if (filteredBoxes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Inventory2, contentDescription = null, tint = DeepForestGreen.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No boxes stored yet. Book a pickup!", color = DeepForestGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredBoxes) { box ->
                    BoxCardItem(
                        box = box,
                        onClick = { onBoxClick(box) },
                        onQrClick = { selectedBoxForQr = box },
                        onRetrieveClick = onNavigateToRetrieve
                    )
                }
            }
        }
    }
}

@Composable
fun BoxCardItem(box: BoxItem, onClick: () -> Unit, onQrClick: () -> Unit, onRetrieveClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DeepForestGreen.copy(alpha = 0.1f),
                    modifier = Modifier.clickable { onQrClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = box.uniqueBoxId,
                            color = DeepForestGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ChampagneGold.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = box.status,
                        color = DeepForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = box.photoUrl,
                    contentDescription = box.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = box.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepForestGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = box.category, fontSize = 12.sp, color = ChampagneGold, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = box.locationReference, fontSize = 11.sp, color = DeepForestGreen.copy(alpha = 0.6f))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SoftBeige)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${box.itemsInside.size} registered items inside", fontSize = 12.sp, color = DeepForestGreen.copy(alpha = 0.7f))
                TextButton(onClick = onRetrieveClick) {
                    Text("Retrieve Box", color = DeepForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
