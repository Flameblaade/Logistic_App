package com.example.logistic_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.logistic_app.ui.screens.*
import com.example.logistic_app.ui.viewmodel.EmergencyViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    emergencyViewModel: EmergencyViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginClick = {
                navController.navigate(Screen.Dispatch.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Dispatch.route) {
            DispatchScreen(onConfirmDelivery = {
                navController.navigate(Screen.DeliveryConfirmation.route)
            })
        }
        composable(Screen.DeliveryConfirmation.route) {
            DeliveryConfirmationScreen(
                onBack = { navController.popBackStack() },
                onSubmit = {
                    navController.navigate(Screen.Dispatch.route) {
                        popUpTo(Screen.Dispatch.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(Screen.Emergency.route) {
            EmergencyScreen(
                viewModel = emergencyViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
