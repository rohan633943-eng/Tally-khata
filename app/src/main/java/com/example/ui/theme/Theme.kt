package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color.Black,
    primaryContainer = TallyGreenDark,
    onPrimaryContainer = Color.White,
    secondary = TallyGold,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFC0D1C7)
)

private val LightColorScheme = lightColorScheme(
    primary = TallyGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = TallyGreenContainer,
    onPrimaryContainer = TallyGreenDark,
    secondary = TallyGold,
    onSecondary = Color.Black,
    background = Color(0xFFF7FAF8),
    onBackground = Color(0xFF191C1A),
    surface = Color.White,
    onSurface = Color(0xFF191C1A),
    surfaceVariant = Color(0xFFF0F4F1),
    onSurfaceVariant = Color(0xFF404943)
)

@Composable
fun TallyKhataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
