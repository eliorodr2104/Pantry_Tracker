package com.project.pantrytracker.ui.theme

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
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color(0xffe0e0e0), // Grigio chiaro per il container
    onPrimaryContainer = Color.Black,
    secondary = Color.Black, // Colore inverso: sfondo nero con testi bianchi
    onSecondary = Color.White,
    secondaryContainer = Color(0xff1c1c1e), // Grigio scuro per i container secondari
    onSecondaryContainer = Color(0xFF828282),
    tertiary = Color(0xFF808080), // Grigio per elementi meno importanti
    tertiaryContainer = Color(0xFF454545),
    onTertiary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.Black,
    onSurface = Color.White,
    error = Color.Red,
    onError = Color.White,
    outline = Color(0xFFA3A3A3),
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
    //specialFocusColor = Color(0xff007aff) // Blu per testi speciali
)

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color(0xff1c1c1e), // Grigio scuro per i container
    onPrimaryContainer = Color.White,
    secondary = Color.White, // Invertito: testi bianchi su sfondo nero
    onSecondary = Color.Black,
    secondaryContainer = Color(0xffa0a0a0), // Grigio chiaro per i container secondari
    onSecondaryContainer = Color.Black,
    tertiaryContainer = Color(0xff5f5f5f), // Grigio medio per elementi terziari
    onTertiary = Color(0xFF0f71d3),
    onTertiaryContainer = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    error = Color.Red,
    onError = Color.Black,
    outline = Color.Gray,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
)

@Composable
fun PantryTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}