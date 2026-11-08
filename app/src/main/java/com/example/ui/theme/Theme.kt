package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class AppThemeMode(val title: String, val description: String) {
    DARK_YOUTUBE("Dark YouTube", "OLED pitch black with signature YouTube Red"),
    LIGHT("Light Mode", "Crisp white minimalist YouTube interface"),
    MATERIAL_YOU("Material You", "Dynamic colors adapted from system wallpaper")
}

private val DarkColorScheme = darkColorScheme(
    primary = YouTubeRed,
    onPrimary = Color.White,
    primaryContainer = YouTubeRedDark,
    onPrimaryContainer = Color.White,
    secondary = YouTubeBlue,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1E3A5F),
    onSecondaryContainer = YouTubeBlue,
    tertiary = FpsGreen,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkOutline,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = YouTubeRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9DD),
    onPrimaryContainer = YouTubeRedDark,
    secondary = Color(0xFF0066CC),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3E4FF),
    onSecondaryContainer = Color(0xFF001C3B),
    tertiary = Color(0xFF00897B),
    onTertiary = Color.White,
    background = Color(0xFFF9F9F9),
    onBackground = Color(0xFF0F0F0F),
    surface = Color.White,
    onSurface = Color(0xFF0F0F0F),
    surfaceVariant = Color(0xFFEFEFEF),
    onSurfaceVariant = Color(0xFF606060),
    outline = Color(0xFFD6D6D6),
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun TubeGrabTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK_YOUTUBE,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when (themeMode) {
        AppThemeMode.DARK_YOUTUBE -> DarkColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.MATERIAL_YOU -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dynamicDarkColorScheme(context)
            } else {
                DarkColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
