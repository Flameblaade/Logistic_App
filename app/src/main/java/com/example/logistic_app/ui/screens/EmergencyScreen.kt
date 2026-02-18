package com.example.logistic_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logistic_app.ui.components.MapPlaceholder
import com.example.logistic_app.ui.components.PhotoUploadBox
import com.example.logistic_app.ui.theme.EmergencyRed
import com.example.logistic_app.ui.theme.LightGrayBackground

enum class EmergencyStep { SELECTION, DETAILS, SUCCESS }

@Composable
fun EmergencyScreen(onClose: () -> Unit) {
    var currentStep by remember { mutableStateOf(EmergencyStep.SELECTION) }
    var selectedType by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBackground)
            .padding(16.dp)
    ) {
        when (currentStep) {
            EmergencyStep.SELECTION -> {
                EmergencySelection(
                    onSelect = { 
                        selectedType = it
                        currentStep = EmergencyStep.DETAILS 
                    },
                    onClose = onClose
                )
            }
            EmergencyStep.DETAILS -> {
                EmergencyDetails(
                    type = selectedType,
                    onReport = { currentStep = EmergencyStep.SUCCESS },
                    onBack = { currentStep = EmergencyStep.SELECTION }
                )
            }
            EmergencyStep.SUCCESS -> {
                EmergencySuccess(onDone = onClose)
            }
        }
    }
}

@Composable
fun EmergencySelection(onSelect: (String) -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmergencyOption(
            title = "Vehicle Breakdown",
            color = Color(0xFFC0CA33),
            onClick = { onSelect("Vehicle Breakdown") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        EmergencyOption(
            title = "Non-Military Encounter",
            color = Color(0xFF2E7D32),
            onClick = { onSelect("Non-Military Encounter") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        EmergencyOption(
            title = "T.I.C.",
            color = EmergencyRed,
            onClick = { onSelect("T.I.C.") }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text("↑\nPlease choose your current situation", color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(64.dp).background(Color.White, CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = EmergencyRed, modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
fun EmergencyDetails(type: String, onReport: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Report: $type", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MapPlaceholder(modifier = Modifier.height(150.dp), text = "Emergency Location")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    placeholder = { Text("Describe the situation...") },
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                PhotoUploadBox()
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                Text("Back")
            }
            Button(
                onClick = onReport, 
                modifier = Modifier.weight(1f), 
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
            ) {
                Text("Send Report")
            }
        }
    }
}

@Composable
fun EmergencySuccess(onDone: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Report Sent Successfully", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Support is on the way.", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Text("Done")
        }
    }
}

@Composable
fun EmergencyOption(title: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Text(title, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
