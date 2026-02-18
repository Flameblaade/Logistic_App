package com.example.logistic_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logistic_app.ui.components.MapPlaceholder
import com.example.logistic_app.ui.theme.*

@Composable
fun DispatchScreen(onConfirmDelivery: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBackground)
            .padding(16.dp)
    ) {
        // Top Card Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E7D32)),
                contentAlignment = Alignment.Center
            ) {
                Text("JDC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Juan Dela Cruz", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("DRV-001", color = Color.Gray, fontSize = 12.sp)
            }
        }

        // Active Dispatch Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Current Dispatch", fontWeight = FontWeight.Bold)
                    }
                    Surface(
                        color = OngoingBlue,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Ongoing", 
                            color = Color.White, 
                            fontSize = 10.sp, 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Dispatch ID", color = Color.Gray, fontSize = 12.sp)
                        Text("DSP-2024-001", fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ETA", color = Color.Gray, fontSize = 12.sp)
                        Text("8:00am", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionChip(label = "Delivered", color = DeliveredGreen, onClick = onConfirmDelivery)
                    ActionChip(label = "Stop Over", color = StopOverYellow, onClick = {})
                    ActionChip(label = "Reported Delay", color = ReportedOrange, onClick = {})
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Map Placeholder
        MapPlaceholder(
            modifier = Modifier.weight(1f),
            text = "Mock Route: PPC -> Bacungan"
        )
    }
}

@Composable
fun ActionChip(label: String, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}
