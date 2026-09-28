package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DistroHeaderBlue,
    onPrimary = Color.White,
    primaryContainer = DistroPrimaryBlue,
    onPrimaryContainer = Color.White,
    secondary = DistroGreenAction,
    onSecondary = Color.White,
    secondaryContainer = DistroGreenLight,
    onSecondaryContainer = DistroGreenDark,
    tertiary = DistroOrangeAction,
    onTertiary = Color.White,
    tertiaryContainer = DistroOrangeLight,
    onTertiaryContainer = DistroOrangeDark,
    background = DistroBackgroundLight,
    onBackground = DistroTextPrimary,
    surface = DistroSurfaceLight,
    onSurface = DistroTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = DistroTextSecondary,
    outline = DistroCardBorder,
    error = DistroRedAlert,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = DistroLightBlue,
    onPrimary = Color.White,
    primaryContainer = DistroHeaderBlue,
    onPrimaryContainer = Color.White,
    secondary = DistroGreenAction,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF14532D),
    onSecondaryContainer = DistroGreenLight,
    tertiary = DistroOrangeAction,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF7C2D12),
    onTertiaryContainer = DistroOrangeLight,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569),
    error = Color(0xFFEF4444),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branded DMS blue & action colors
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DistroHeaderBlue.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
