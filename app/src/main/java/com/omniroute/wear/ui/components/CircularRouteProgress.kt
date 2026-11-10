package com.omniroute.wear.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.theme.BorderSubtle
import com.omniroute.wear.ui.theme.DeliveryGreen
import com.omniroute.wear.ui.theme.SurfaceCard
import com.omniroute.wear.ui.theme.TextPrimary

/**
 * Renders the glanceable "4/15 ENTREGAS" pill badge on a dark OLED surface.
 */
@Composable
fun CircularRouteProgress(
    completedStops: Int,
    totalStops: Int,
    modifier: Modifier = Modifier,
    indicatorSize: Dp = 14.dp,
    strokeWidth: Dp = 2.dp
) {
    val safeTotal = if (totalStops <= 0) 1 else totalStops
    val progress = (completedStops.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(14.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$completedStops/$safeTotal ENTREGAS",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            modifier = Modifier.padding(end = 6.dp)
        )

        Box(
            modifier = Modifier.size(indicatorSize),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(indicatorSize),
                indicatorColor = DeliveryGreen,
                trackColor = BorderSubtle,
                strokeWidth = strokeWidth
            )
        }
    }
}
