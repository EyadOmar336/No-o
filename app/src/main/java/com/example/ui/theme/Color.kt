package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Backgrounds
val DarkBackground = Color(0xFF0B0F19)
val DarkSurface = Color(0xFF131B2E)
val DarkSurfaceVariant = Color(0xFF1E293B)
val DarkBorder = Color(0xFF2A3859)

// Light Backgrounds
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightBorder = Color(0xFFE2E8F0)

// Brand Primary Gradient Colors
val PurplePrimary = Color(0xFF8B5CF6)
val PurpleDark = Color(0xFF6D28D9)
val IndigoAccent = Color(0xFF4F46E5)
val CyanAccent = Color(0xFF06B6D4)
val EmeraldSuccess = Color(0xFF10B981)
val GoldCrown = Color(0xFFF59E0B)
val RoseError = Color(0xFFF43F5E)

// Text Colors
val TextWhite = Color(0xFFF8FAFC)
val TextGray = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Subject Colors
val SubjectMath = Color(0xFF10B981)
val SubjectPhysics = Color(0xFF3B82F6)
val SubjectChemistry = Color(0xFFEC4899)
val SubjectBiology = Color(0xFF14B8A6)
val SubjectEnglish = Color(0xFF8B5CF6)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF7C3AED), Color(0xFF4F46E5))
)

val ScanButtonGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
)

val ProCrownGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFF59E0B), Color(0xFFFBBF24))
)

val CardGlowGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1E293B), Color(0xFF131B2E))
)
