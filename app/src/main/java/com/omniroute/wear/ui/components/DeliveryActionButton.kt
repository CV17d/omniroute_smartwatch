package com.omniroute.wear.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.theme.AlertRed
import com.omniroute.wear.ui.theme.BorderSubtle
import com.omniroute.wear.ui.theme.DeliveryGreen
import com.omniroute.wear.ui.theme.SurfaceCard
import com.omniroute.wear.ui.theme.TextPrimary
import com.omniroute.wear.ui.theme.TextSecondary

sealed class ActionButtonStyle {
    data class PrimaryGreen(val label: String, val icon: ImageVector? = null) : ActionButtonStyle()
    data class DeliveredLight(val label: String = "ENTREGADO") : ActionButtonStyle()
    data class AbsentLight(val title: String = "NO ENTREGADO", val subtitle: String = "AUSENTE") : ActionButtonStyle()
    data class IncidentLight(val label: String = "INCIDENCIA") : ActionButtonStyle()
    data class Outlined(val label: String, val tint: Color = TextSecondary) : ActionButtonStyle()
}

/**
 * Massive touch-friendly action button engineered for round Wear OS displays.
 * Incorporates horizontal insets to keep touch areas well clear of curved bezels.
 */
@Composable
fun DeliveryActionButton(
    style: ActionButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(26.dp)

    when (style) {
        is ActionButtonStyle.PrimaryGreen -> {
            Button(
                onClick = { onClick() },
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 14.dp),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = DeliveryGreen,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (style.icon != null) {
                        Icon(
                            imageVector = style.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                    Text(
                        text = style.label.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
                }
            }
        }

        is ActionButtonStyle.DeliveredLight -> {
            Button(
                onClick = { onClick() },
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 14.dp),
                shape = shape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = BorderSubtle,
                    borderWidth = 1.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SurfaceCard,
                    contentColor = TextPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "✓",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = DeliveryGreen,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = style.label.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
            }
        }

        is ActionButtonStyle.AbsentLight -> {
            Button(
                onClick = { onClick() },
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 14.dp),
                shape = shape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = BorderSubtle,
                    borderWidth = 1.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SurfaceCard,
                    contentColor = TextPrimary
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "✕",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = AlertRed,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = style.title.uppercase(),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = style.subtitle.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        is ActionButtonStyle.IncidentLight -> {
            Button(
                onClick = { onClick() },
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .padding(horizontal = 20.dp),
                shape = shape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = BorderSubtle,
                    borderWidth = 1.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SurfaceCard,
                    contentColor = TextPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = style.label.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = " ⚠️",
                        fontSize = 11.sp
                    )
                }
            }
        }

        is ActionButtonStyle.Outlined -> {
            Button(
                onClick = { onClick() },
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .padding(horizontal = 20.dp),
                shape = shape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = style.tint,
                    borderWidth = 1.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = style.tint
                )
            ) {
                Text(
                    text = style.label.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = style.tint,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
