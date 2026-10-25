package com.omniroute.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

// ==========================================
// OmniRoute Wear OS - Light Mode Theme
// ==========================================

val OmniWearColorPalette = Colors(
    primary = DeliveryGreen,
    primaryVariant = DeliveryGreenDark,
    secondary = SurfaceCard,
    secondaryVariant = SurfaceCardElevated,
    background = BackgroundPrimary,
    surface = SurfaceCard,
    error = AlertRed,
    onPrimary = BackgroundPrimary,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = BackgroundPrimary
)

@Composable
fun OmniWearTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = OmniWearColorPalette,
        typography = OmniWearTypography,
        content = content
    )
}
