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
import com.omniroute.wear.ui.theme.SurfaceButtonSecondary
import com.omniroute.wear.ui.theme.TextPrimary
import com.omniroute.wear.ui.theme.TextSecondary
import com.omniroute.wear.ui.theme.WarningAmber

sealed class ActionButtonStyle {
    // Primary Vibrant Green Success Actions ("INICIAR RUTA", "¡LLEGUE!")
    data class PrimaryGreen(val label: String, val icon: ImageVector? = null) : ActionButtonStyle()

    // Primary Vibrant Green Delivery Confirmation ("✓ ENTREGADO")
    data class DeliveredSuccess(val label: String = "ENTREGADO") : ActionButtonStyle()
    // Backward-compatible alias
    data class DeliveredLight(val label: String = "ENTREGADO") : ActionButtonStyle()

    // Secondary Dark Pill Actions to prevent accidental taps ("✕ NO ENTREGADO / AUSENTE")
    data class AbsentSecondary(val title: String = "NO ENTREGADO", val subtitle: String = "AUSENTE") : ActionButtonStyle()
    // Backward-compatible alias
    data class AbsentLight(val title: String = "NO ENTREGADO", val subtitle: String = "AUSENTE") : ActionButtonStyle()

    // Secondary Dark Pill Incident Action ("INCIDENCIA ⚠️")
    data class IncidentSecondary(val label: String = "INCIDENCIA") : ActionButtonStyle()
    // Backward-compatible alias
    data class IncidentLight(val label: String = "INCIDENCIA") : ActionButtonStyle()

    // Minimal Outlined Action
    data class Outlined(val label: String, val tint: Color = TextSecondary) : ActionButtonStyle()
}

/**
 * Premium Pill-shaped action button for circular Wear OS displays.
 * Employs vibrant green for primary success and dark-grey secondary pills for exceptions.
 */
@Composable
fun DeliveryActionButton(
    style: ActionButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val pillShape = RoundedCornerShape(26.dp)

    when (style) {
        is ActionButtonStyle.PrimaryGreen -> {
            Button(
                onClick = { onClick() },
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 12.dp),
                shape = pillShape,
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
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
                }
            }
        }

        is ActionButtonStyle.DeliveredSuccess, is ActionButtonStyle.DeliveredLight -> {
            val label = if (style is ActionButtonStyle.DeliveredSuccess) style.label else (style as ActionButtonStyle.DeliveredLight).label
            Button(
                onClick = { onClick() },
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 12.dp),
                shape = pillShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = DeliveryGreen,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "✓",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = label.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
                }
            }
        }

        is ActionButtonStyle.AbsentSecondary, is ActionButtonStyle.AbsentLight -> {
            val title = if (style is ActionButtonStyle.AbsentSecondary) style.title else (style as ActionButtonStyle.AbsentLight).title
            val subtitle = if (style is ActionButtonStyle.AbsentSecondary) style.subtitle else (style as ActionButtonStyle.AbsentLight).subtitle

            Button(
                onClick = { onClick() },
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 12.dp),
                shape = pillShape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = BorderSubtle,
                    borderWidth = 1.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SurfaceButtonSecondary,
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
                            fontSize = 13.sp,
                            color = AlertRed,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = title.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = subtitle.uppercase(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp,
                        color = TextSecondary,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }

        is ActionButtonStyle.IncidentSecondary, is ActionButtonStyle.IncidentLight -> {
            val label = if (style is ActionButtonStyle.IncidentSecondary) style.label else (style as ActionButtonStyle.IncidentLight).label

            Button(
                onClick = { onClick() },
                enabled = enabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 18.dp),
                shape = pillShape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = BorderSubtle,
                    borderWidth = 1.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SurfaceButtonSecondary,
                    contentColor = TextPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = label.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = " ⚠️",
                        fontSize = 12.sp
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
                    .height(40.dp)
                    .padding(horizontal = 18.dp),
                shape = pillShape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = style.tint.copy(alpha = 0.6f),
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
