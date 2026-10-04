package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CasinoColorScheme = darkColorScheme(
    primary = CasinoGold,
    onPrimary = Color(0xFF1E1700),
    primaryContainer = CasinoGoldDark,
    onPrimaryContainer = CasinoGoldLight,
    secondary = CasinoNeonCyan,
    onSecondary = Color(0xFF00382E),
    secondaryContainer = CasinoEmeraldCard,
    onSecondaryContainer = CasinoNeonCyan,
    tertiary = CasinoCrimson,
    onTertiary = Color.White,
    background = CasinoEmeraldDark,
    onBackground = TextPrimary,
    surface = CasinoEmeraldMedium,
    onSurface = TextPrimary,
    surfaceVariant = CasinoEmeraldCard,
    onSurfaceVariant = TextSecondary,
    outline = CasinoEmeraldBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CasinoColorScheme,
        typography = Typography,
        content = content
    )
}
