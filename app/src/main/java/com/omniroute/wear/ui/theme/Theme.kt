package com.omniroute.wear.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

// ==========================================
// OmniRoute Wear OS - Sistema Dinámico de Temas
// ==========================================

@Immutable
data class OmniColors(
    val background: Color,
    val surfaceCard: Color,
    val surfaceSecondary: Color,
    val borderSubtle: Color,
    val borderLight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val primaryGreen: Color,
    val onPrimaryGreen: Color,
    val negativeBorder: Color,
    val negativeText: Color,
    val warningBorder: Color,
    val warningText: Color,
    val mapLand: Color,
    val mapWater: Color,
    val mapRoad: Color,
    val isDark: Boolean
)

val DarkOmniColors = OmniColors(
    background = DarkBackground,                 // Negro Puro Color.Black (#000000)
    surfaceCard = DarkSurfaceCard,              // Gris muy oscuro (#1A1A1A)
    surfaceSecondary = DarkSurfaceSecondary,    // Píldoras secundarias (#1E1E1E)
    borderSubtle = DarkBorderSubtle,            // Bordes sutiles oscuros (#2E2E2E)
    borderLight = Color(0xFF3F3F46),
    textPrimary = DarkTextPrimary,              // Textos principales en blanco (#FFFFFF)
    textSecondary = DarkTextSecondary,          // Textos secundarios en gris suave (#A1A1AA)
    textTertiary = DarkTextTertiary,
    primaryGreen = DeliveryGreen,               // Verde vibrante (#22B061)
    onPrimaryGreen = Color.White,               // Texto/icono blanco
    negativeBorder = AlertRedDark,              // Modo oscuro: borde rojo oscuro (#991B1B)
    negativeText = AlertRed,                    // Modo oscuro: texto rojo (#EF4444)
    warningBorder = WarningAmberDark,
    warningText = WarningAmber,
    mapLand = DarkMapLand,
    mapWater = DarkMapWater,
    mapRoad = DarkMapRoad,
    isDark = true
)

val LightOmniColors = OmniColors(
    background = LightBackground,               // Blanco Puro Color.White (#FFFFFF)
    surfaceCard = LightSurfaceCard,             // Tarjetas blancas
    surfaceSecondary = LightSurfaceSecondary,   // Píldoras secundarias claras
    borderSubtle = LightBorderSubtle,           // Bordes claros (#E0E0E0)
    borderLight = Color(0xFFCBD5E1),
    textPrimary = LightTextPrimary,             // Textos principales en negro puro (#000000)
    textSecondary = LightTextSecondary,         // Textos secundarios en gris medio (#666666)
    textTertiary = LightTextTertiary,
    primaryGreen = DeliveryGreen,               // Verde vibrante (#22B061)
    onPrimaryGreen = Color.White,               // Texto/icono blanco
    negativeBorder = AlertRed,                  // Modo claro: borde rojo brillante (#EF4444)
    negativeText = Color(0xFFDC2626),           // Modo claro: texto rojo (#DC2626)
    warningBorder = WarningAmber,
    warningText = Color(0xFFD97706),
    mapLand = LightMapLand,
    mapWater = LightMapWater,
    mapRoad = LightMapRoad,
    isDark = false
)

val LocalOmniColors = staticCompositionLocalOf { DarkOmniColors }

object OmniTheme {
    val colors: OmniColors
        @Composable
        get() = LocalOmniColors.current
}

/**
 * Tema dinámico para Wear OS reactivo al sistema mediante isSystemInDarkTheme().
 */
@Composable
fun OmniWearTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val omniColors = if (darkTheme) DarkOmniColors else LightOmniColors

    val wearColors = Colors(
        primary = omniColors.primaryGreen,
        primaryVariant = DeliveryGreenDark,
        secondary = omniColors.surfaceSecondary,
        background = omniColors.background,
        surface = omniColors.surfaceCard,
        error = omniColors.negativeText,
        onPrimary = omniColors.onPrimaryGreen,
        onSecondary = omniColors.textPrimary,
        onBackground = omniColors.textPrimary,
        onSurface = omniColors.textPrimary,
        onError = Color.White
    )

    CompositionLocalProvider(
        LocalOmniColors provides omniColors
    ) {
        MaterialTheme(
            colors = wearColors,
            typography = OmniWearTypography,
            content = content
        )
    }
}
