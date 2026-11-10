package com.omniroute.wear.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.theme.BorderSubtle
import com.omniroute.wear.ui.theme.SurfaceCard
import com.omniroute.wear.ui.theme.TextPrimary

/**
 * Compact dark pill badge for vehicle types ("MOTO") and status metadata.
 */
@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    iconPrefix: String? = null,
    textColor: Color = TextPrimary,
    backgroundColor: Color = SurfaceCard,
    borderColor: Color = BorderSubtle
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 2.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (iconPrefix != null) {
            Text(
                text = iconPrefix,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(end = 3.dp)
            )
        }
        Text(
            text = text.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}
