package com.omniroute.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

// ==========================================
// OmniRoute Wear OS - Modern OLED Dark Theme
// ==========================================

val OmniWearColorPalette = Colors(
    primary = DeliveryGreen,
    primaryVariant = DeliveryGreenDark,
    secondary = SurfaceButtonSecondary,
    secondaryVariant = SurfaceCardElevated,
    background = Color.Black,
    surface = SurfaceCard,
    error = AlertRed,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
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
