package com.example.logistic_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.logistic_app.ui.screens.*
import com.example.logistic_app.ui.theme.EmergencyRed
import com.example.logistic_app.ui.theme.NavyBlue

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showEmergencyOverlay by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (currentRoute != "login" && !showEmergencyOverlay) {
                BottomAppBar(
                    containerColor = NavyBlue,
                    contentColor = Color.White,
                    modifier = Modifier.height(72.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dispatch Tab
                        NavigationItem(
                            icon = Icons.Default.ShoppingCart,
                            label = "Dispatch",
                            isSelected = currentRoute == "dispatch" || currentRoute == "delivery_confirmation",
                            onClick = { navController.navigate("dispatch") }
                        )

                        // Placeholder for FAB
                        Spacer(modifier = Modifier.width(64.dp))

                        // Profile Tab
                        NavigationItem(
                            icon = Icons.Default.Person,
                            label = "Profile",
                            isSelected = currentRoute == "profile",
                            onClick = { navController.navigate("profile") }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute != "login" && !showEmergencyOverlay) {
                FloatingActionButton(
                    onClick = { showEmergencyOverlay = true },
                    containerColor = EmergencyRed,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = 56.dp) // Adjusted to sit on the bar
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Emergency",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(onLoginClick = { navController.navigate("dispatch") })
                }
                composable("dispatch") {
                    DispatchScreen(onConfirmDelivery = { navController.navigate("delivery_confirmation") })
                }
                composable("delivery_confirmation") {
                    DeliveryConfirmationScreen(
                        onBack = { navController.popBackStack() },
                        onSubmit = { navController.navigate("dispatch") }
                    )
                }
                composable("profile") {
                    ProfileScreen()
                }
            }

            if (showEmergencyOverlay) {
                EmergencyScreen(onClose = { showEmergencyOverlay = false })
            }
        }
    }
}

@Composable
fun NavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .height(56.dp)
            .clickable(onClick = onClick, interactionSource = null, indication = null)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            label,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 12.sp
        )
    }
}

// Helper to make Column clickable without ripple for cleaner UI if desired, 
// or use normal Modifier.clickable
@Composable
fun Modifier.clickable(onClick: () -> Unit, interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource?, indication: androidx.compose.foundation.Indication?) = this.then(
    Modifier.clickable(
        interactionSource = interactionSource ?: remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        indication = indication,
        onClick = onClick
    )
)
