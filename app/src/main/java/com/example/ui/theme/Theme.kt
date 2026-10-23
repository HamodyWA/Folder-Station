package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = BrandNavyElevated,
    onPrimaryContainer = NeonCyan,
    secondary = GlowingViolet,
    onSecondary = Color.White,
    secondaryContainer = BrandNavyElevated,
    onSecondaryContainer = Color.White,
    tertiary = NeonCyan,
    background = BrandNavyDark,
    onBackground = TextPrimaryDark,
    surface = BrandNavyCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = BrandNavyElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = BrandNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = GlowingViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF5B21B6),
    tertiary = NeonCyan,
    background = BrandLightBg,
    onBackground = TextPrimaryLight,
    surface = BrandLightCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = TextSecondaryLight,
    outline = BrandLightBorder
)

@Composable
fun MyApplicationTheme(
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
