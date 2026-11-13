package com.nivara.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
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
fun AiAssistantScreen(onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                Pair("assistant", "Hello! I am your NIVARA AI Storage Assistant. Tell me what belongings you want to store (e.g. winter clothes, 3 suitcases, festival decor), and I will recommend the ideal box size and storage plan!")
            )
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "AI Storage Assistant",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen
                )
                Text(
                    text = "Powered by NIVARA Smart Engine",
                    fontSize = 12.sp,
                    color = ChampagneGold,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chat list
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            chatMessages.forEach { (sender, message) ->
                val isUser = sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isUser) DeepForestGreen else Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = message,
                            color = if (isUser) WarmIvory else DeepForestGreen,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Ask about boxes, climate control...") },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = DeepForestGreen,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (query.isNotBlank()) {
                        val userQ = query
                        val reply = "Based on your items ($userQ), I recommend 2 Medium tamper-proof boxes under our Gold Storage Plan. Temperature is maintained at 22C."
                        chatMessages = chatMessages + Pair("user", userQ) + Pair("assistant", reply)
                        query = ""
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = DeepForestGreen),
                modifier = Modifier.size(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = ChampagneGold)
            }
        }
    }
}
