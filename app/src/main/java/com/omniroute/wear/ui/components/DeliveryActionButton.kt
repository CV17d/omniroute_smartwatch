package com.omniroute.wear.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.omniroute.wear.ui.theme.OmniTheme

sealed class ActionButtonStyle {
    // Botón Principal Verde Vibrante (ej. "INICIAR RUTA", "¡LLEGUE!")
    data class PrimaryGreen(
        val label: String,
        val icon: ImageVector? = Icons.AutoMirrored.Rounded.ArrowForward,
        val iconTrailing: Boolean = false
    ) : ActionButtonStyle()

    // Confirmación de Entrega ("ENTREGADO" + Check blanco)
    data class DeliveredSuccess(
        val label: String = "ENTREGADO",
        val icon: ImageVector = Icons.Rounded.Check
    ) : ActionButtonStyle()
    data class DeliveredLight(val label: String = "ENTREGADO") : ActionButtonStyle()

    // Botón Negativo Outlined ("NO ENTREGADO" + X roja, borde rojo oscuro/brillante)
    data class AbsentSecondary(
        val title: String = "NO ENTREGADO",
        val subtitle: String = "AUSENTE",
        val icon: ImageVector = Icons.Rounded.Close
    ) : ActionButtonStyle()
    data class AbsentLight(val title: String = "NO ENTREGADO", val subtitle: String = "AUSENTE") : ActionButtonStyle()

    // Botón Secundario Píldora ("INCIDENCIA" + WarningAmber)
    data class IncidentSecondary(
        val label: String = "INCIDENCIA",
        val icon: ImageVector = Icons.Rounded.WarningAmber
    ) : ActionButtonStyle()
    data class IncidentLight(val label: String = "INCIDENCIA") : ActionButtonStyle()

    // Botón Outlined Genérico
    data class Outlined(
        val label: String,
        val tint: Color? = null,
        val icon: ImageVector? = null
    ) : ActionButtonStyle()
}

/**
 * Botón Píldora Premium (RoundedCornerShape(50)) para Wear OS.
 * Adaptativo a Modo Claro / Oscuro, con íconos integrados y efecto bounce táctil (95%).
 */
@Composable
fun DeliveryActionButton(
    style: ActionButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val pillShape = RoundedCornerShape(percent = 50)
    val interactionSource = remember { MutableInteractionSource() }
    val colors = OmniTheme.colors

    val animatedModifier = modifier.bounceClick(
        interactionSource = interactionSource,
        scaleDown = 0.95f
    )

    when (style) {
        is ActionButtonStyle.PrimaryGreen -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                modifier = animatedModifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 12.dp),
                shape = pillShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colors.primaryGreen,
                    contentColor = colors.onPrimaryGreen
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (style.icon != null && !style.iconTrailing) {
                        Icon(
                            imageVector = style.icon,
                            contentDescription = null,
                            tint = colors.onPrimaryGreen,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 6.dp)
                        )
                    }
                    Text(
                        text = style.label.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp,
                        color = colors.onPrimaryGreen
                    )
                    if (style.icon != null && style.iconTrailing) {
                        Icon(
                            imageVector = style.icon,
                            contentDescription = null,
                            tint = colors.onPrimaryGreen,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(start = 6.dp)
                        )
                    }
                }
            }
        }

        is ActionButtonStyle.DeliveredSuccess, is ActionButtonStyle.DeliveredLight -> {
            val label = if (style is ActionButtonStyle.DeliveredSuccess) style.label else (style as ActionButtonStyle.DeliveredLight).label
            val icon = if (style is ActionButtonStyle.DeliveredSuccess) style.icon else Icons.Rounded.Check

            Button(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                modifier = animatedModifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 12.dp),
                shape = pillShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colors.primaryGreen,
                    contentColor = colors.onPrimaryGreen
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.onPrimaryGreen,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = label.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp,
                        color = colors.onPrimaryGreen
                    )
                }
            }
        }

        is ActionButtonStyle.AbsentSecondary, is ActionButtonStyle.AbsentLight -> {
            val title = if (style is ActionButtonStyle.AbsentSecondary) style.title else (style as ActionButtonStyle.AbsentLight).title
            val subtitle = if (style is ActionButtonStyle.AbsentSecondary) style.subtitle else (style as ActionButtonStyle.AbsentLight).subtitle
            val icon = if (style is ActionButtonStyle.AbsentSecondary) style.icon else Icons.Rounded.Close

            // Diseño tipo "Outlined": borde rojo oscuro en modo oscuro, borde rojo brillante en modo claro
            Button(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                modifier = animatedModifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 12.dp),
                shape = pillShape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = colors.negativeBorder,
                    borderWidth = 1.5.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = colors.negativeText
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = colors.negativeText,
                            modifier = Modifier
                                .size(15.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            text = title.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.negativeText
                        )
                    }
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle.uppercase(),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp,
                            color = colors.negativeText.copy(alpha = 0.8f),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        is ActionButtonStyle.IncidentSecondary, is ActionButtonStyle.IncidentLight -> {
            val label = if (style is ActionButtonStyle.IncidentSecondary) style.label else (style as ActionButtonStyle.IncidentLight).label
            val icon = if (style is ActionButtonStyle.IncidentSecondary) style.icon else Icons.Rounded.WarningAmber

            // Tarjeta / Píldora secundaria adaptable (#1A1A1A oscuro / blanco con borde claro #E0E0E0)
            Button(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                modifier = animatedModifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(horizontal = 16.dp),
                shape = pillShape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = colors.borderSubtle,
                    borderWidth = 1.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colors.surfaceCard,
                    contentColor = colors.textPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.warningText,
                        modifier = Modifier
                            .size(15.dp)
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = label.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = colors.textPrimary
                    )
                }
            }
        }

        is ActionButtonStyle.Outlined -> {
            val contentTint = style.tint ?: colors.textPrimary
            Button(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                modifier = animatedModifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp),
                shape = pillShape,
                border = ButtonDefaults.outlinedButtonBorder(
                    borderColor = colors.borderSubtle,
                    borderWidth = 1.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colors.surfaceCard,
                    contentColor = contentTint
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
                            tint = contentTint,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = style.label.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = contentTint,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
