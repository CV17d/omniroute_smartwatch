package com.omniroute.wear.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// OmniRoute Wear OS - Tokens Universales
// ==========================================

// Delivery Primary Greens (Ambos Modos: Acción Positiva)
val DeliveryGreen = Color(0xFF22B061)        // Verde vibrante de acción (#22B061)
val DeliveryGreenDark = Color(0xFF16A34A)    // Verde pulsado / acento
val DeliveryGreenLight = Color(0xFFE8F5E9)   // Fondo feedback modo claro
val DeliveryGreenOled = Color(0xFF064E3B)    // Fondo feedback modo oscuro

// Alert & Incident Accents (Negativo & Excepciones)
val AlertRed = Color(0xFFEF4444)            // Rojo brillante
val AlertRedDark = Color(0xFF991B1B)        // Rojo oscuro (borde en modo oscuro)
val AlertRedLight = Color(0xFFFFEBEE)       // Fondo tenue modo claro
val WarningAmber = Color(0xFFF59E0B)        // Ámbar vibrante
val WarningAmberDark = Color(0xFFB45309)

// Modo Oscuro (OLED Black)
val DarkBackground = Color(0xFF000000)      // Negro Puro OLED
val DarkSurfaceCard = Color(0xFF1A1A1A)     // Gris muy oscuro (#1A1A1A)
val DarkSurfaceSecondary = Color(0xFF1E1E1E)// Píldora secundaria gris muy oscura
val DarkBorderSubtle = Color(0xFF2E2E2E)    // Borde sutil modo oscuro
val DarkTextPrimary = Color(0xFFFFFFFF)     // Blanco puro
val DarkTextSecondary = Color(0xFFA1A1AA)   // Gris claro suave
val DarkTextTertiary = Color(0xFF71717A)

// Modo Claro (Light Mode)
val LightBackground = Color(0xFFFFFFFF)     // Blanco Puro
val LightSurfaceCard = Color(0xFFFFFFFF)    // Tarjetas blancas
val LightSurfaceSecondary = Color(0xFFF8F9FA) // Píldoras secundarias claras
val LightBorderSubtle = Color(0xFFE0E0E0)   // Borde claro (#E0E0E0)
val LightTextPrimary = Color(0xFF000000)    // Negro puro
val LightTextSecondary = Color(0xFF666666)  // Gris medio
val LightTextTertiary = Color(0xFF888888)

// Retrocompatibilidad con nombres anteriores
val BackgroundPrimary = DarkBackground
val BackgroundSecondary = Color(0xFF0B0D11)
val SurfaceCard = DarkSurfaceCard
val SurfaceCardElevated = Color(0xFF242B35)
val SurfaceButtonSecondary = DarkSurfaceSecondary
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextTertiary = DarkTextTertiary
val BorderSubtle = DarkBorderSubtle
val BorderStrong = Color(0xFF475569)

// Vector Map (Dark & Light)
val DarkMapLand = Color(0xFF13161C)
val DarkMapWater = Color(0xFF0F1B2B)
val DarkMapRoad = Color(0xFF252B36)

val LightMapLand = Color(0xFFF4F1EA)
val LightMapWater = Color(0xFFB8DCED)
val LightMapRoad = Color(0xFFFFFFFF)

val RoutePolyline = DeliveryGreen
val MapLand = DarkMapLand
val MapWater = DarkMapWater
val MapRoad = DarkMapRoad
