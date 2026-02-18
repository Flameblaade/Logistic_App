package com.example.logistic_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.logistic_app.ui.MainScreen
import com.example.logistic_app.ui.theme.Logistic_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Logistic_AppTheme {
                MainScreen()
            }
        }
    }
}
