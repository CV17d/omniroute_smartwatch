package com.omniroute.wear.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.omniroute.wear.ui.theme.DeliveryGreenDark
import com.omniroute.wear.ui.theme.OmniTheme

/**
 * Mapa vectorial reactivo a Modo Oscuro / Modo Claro para Wear OS.
 */
@Composable
fun WearMiniMap(
    modifier: Modifier = Modifier
) {
    val colors = OmniTheme.colors
    val mapLand = colors.mapLand
    val mapWater = colors.mapWater
    val mapRoad = colors.mapRoad
    val routeGreen = colors.primaryGreen

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(86.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(mapLand)
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Water Bodies
            val waterPath1 = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.28f, 0f)
                quadraticBezierTo(w * 0.15f, h * 0.35f, 0f, h * 0.65f)
                close()
            }
            drawPath(waterPath1, color = mapWater)

            val waterPath2 = Path().apply {
                moveTo(w * 0.85f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.45f)
                quadraticBezierTo(w * 0.88f, h * 0.20f, w * 0.85f, 0f)
                close()
            }
            drawPath(waterPath2, color = mapWater)

            // Street lines
            drawLine(mapRoad, Offset(w * 0.2f, h * 0.9f), Offset(w * 0.85f, h * 0.3f), strokeWidth = 4f)
            drawLine(mapRoad, Offset(w * 0.35f, h), Offset(w * 0.95f, h * 0.45f), strokeWidth = 4f)
            drawLine(mapRoad, Offset(w * 0.1f, h * 0.5f), Offset(w * 0.65f, h * 0.1f), strokeWidth = 3f)

            // Vibrant Green Route Polyline
            val routePath = Path().apply {
                moveTo(w * 0.48f, h * 0.78f)
                lineTo(w * 0.42f, h * 0.62f)
                lineTo(w * 0.35f, h * 0.45f)
                lineTo(w * 0.32f, h * 0.25f)
                quadraticBezierTo(w * 0.36f, h * 0.18f, w * 0.42f, h * 0.28f)
                lineTo(w * 0.55f, h * 0.46f)
                lineTo(w * 0.72f, h * 0.38f)
                quadraticBezierTo(w * 0.80f, h * 0.26f, w * 0.72f, h * 0.20f)
            }
            drawPath(
                routePath,
                color = routeGreen,
                style = Stroke(width = 6.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Waypoints
            val waypoints = listOf(
                Offset(w * 0.32f, h * 0.25f),
                Offset(w * 0.36f, h * 0.46f),
                Offset(w * 0.42f, h * 0.62f),
                Offset(w * 0.55f, h * 0.46f),
                Offset(w * 0.72f, h * 0.38f),
                Offset(w * 0.72f, h * 0.20f)
            )
            for (pt in waypoints) {
                drawCircle(color = routeGreen, radius = 5.5f, center = pt)
                drawCircle(color = Color.White, radius = 3f, center = pt)
            }

            // Courier Active Pin
            val pin = Offset(w * 0.48f, h * 0.78f)
            drawCircle(color = routeGreen.copy(alpha = 0.35f), radius = 13f, center = pin)
            drawCircle(color = DeliveryGreenDark, radius = 8f, center = pin)
            drawCircle(color = Color.White, radius = 4f, center = pin)
        }
    }
}
