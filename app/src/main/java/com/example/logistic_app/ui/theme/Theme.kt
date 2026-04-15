package com.example.logistic_app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue600,
    onPrimary = Color.White,
    primaryContainer = Blue700,
    onPrimaryContainer = Blue100,
    secondary = Emerald600,
    onSecondary = Color.White,
    secondaryContainer = Emerald700,
    onSecondaryContainer = Emerald100,
    tertiary = Amber500,
    onTertiary = Color.White,
    tertiaryContainer = Amber700,
    onTertiaryContainer = Amber100,
    error = Rose700,
    onError = Color.White,
    errorContainer = Rose700,
    onErrorContainer = Rose400,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate100,
    onSurfaceVariant = Slate200,
    surfaceVariant = Slate700,
    outline = Slate600,
    outlineVariant = Slate700,
    scrim = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = Color.White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue700,
    secondary = Emerald500,
    onSecondary = Color.White,
    secondaryContainer = Emerald100,
    onSecondaryContainer = Emerald700,
    tertiary = Amber500,
    onTertiary = Color.White,
    tertiaryContainer = Amber100,
    onTertiaryContainer = Amber700,
    error = Rose700,
    onError = Color.White,
    errorContainer = Rose400,
    onErrorContainer = Rose700,
    background = Slate100,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    onSurfaceVariant = Slate500,
    surfaceVariant = Slate200,
    outline = Slate500,
    outlineVariant = Slate200,
    scrim = Color.Black
)

@Composable
fun Logistic_AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        // This ensures Poppins is the default font even for Text() calls without a style
        CompositionLocalProvider(
            LocalTextStyle provides Typography.bodyLarge
        ) {
            content()
        }
    }
}
