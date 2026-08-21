package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = Color.White,
    secondary = CyanAccent,
    onSecondary = Color.Black,
    tertiary = EmeraldSuccess,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextGray,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = PurpleDark,
    onPrimary = Color.White,
    primaryContainer = PurplePrimary,
    onPrimaryContainer = Color.White,
    secondary = CyanAccent,
    onSecondary = Color.Black,
    tertiary = EmeraldSuccess,
    background = LightBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = LightBorder
)

@Composable
fun StudySnapTheme(
    darkTheme: Boolean = true,
    isRtl: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
