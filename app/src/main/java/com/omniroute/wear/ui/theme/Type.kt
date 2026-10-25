package com.omniroute.wear.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Typography

// ==========================================
// OmniRoute Wear OS - Glanceable Typography
// ==========================================

val OmniWearTypography = Typography(
    // App Header (e.g., "OMNIROUTE")
    title1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
        color = TextPrimary
    ),
    // Hero Address (e.g., "CALLE 22 # 5-43")
    title2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center,
        color = TextPrimary
    ),
    // Medium Header / Subtitle
    title3 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        color = TextPrimary
    ),
    // Large Massive Button Label (e.g., "¡LLEGUE!", "INICIAR RUTA")
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
        color = Color(0xFFFFFFFF)
    ),
    // Navigation Manoeuvre (e.g., "150m - Continuar abajo")
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        color = TextPrimary
    ),
    // Recipient & Package Details (e.g., "Envia • Apt 302")
    body2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = TextSecondary
    ),
    // Metadata Tags & Badges (e.g., "4/15 ENTREGAS", "MOTO")
    caption1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 0.4.sp,
        color = TextSecondary
    ),
    caption2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        color = TextTertiary
    )
)
