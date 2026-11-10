package com.omniroute.wear.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.theme.OmniTheme

/**
 * Insignia de estado tipo píldora pequeña con borde de color y fondo transparente.
 * Ejemplos: "ENTREGA DE SALUD", "GPS ÓPTIMO", "MOTO".
 */
@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconPrefix: String? = null,
    textColor: Color = OmniTheme.colors.primaryGreen,
    borderColor: Color = OmniTheme.colors.primaryGreen,
    backgroundColor: Color = Color.Transparent
) {
    val pillShape = RoundedCornerShape(percent = 50)

    Row(
        modifier = modifier
            .clip(pillShape)
            .background(backgroundColor)
            .border(BorderStroke(1.dp, borderColor), pillShape)
            .padding(horizontal = 8.dp, vertical = 2.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier
                    .size(10.dp)
                    .padding(end = 3.dp)
            )
        } else if (iconPrefix != null) {
            Text(
                text = iconPrefix,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(end = 3.dp)
            )
        }
        Text(
            text = text.uppercase(),
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}
