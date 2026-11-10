package com.omniroute.wear.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.components.ActionButtonStyle
import com.omniroute.wear.ui.components.DeliveryActionButton
import com.omniroute.wear.ui.theme.DeliveryGreenLight
import com.omniroute.wear.ui.theme.DeliveryGreenOled
import com.omniroute.wear.ui.theme.OmniTheme

/**
 * Screen 3: Confirmación rápida de entrega (3s).
 * Botón "ENTREGADO" con Check y botón Outlined "NO ENTREGADO" con X roja reactivo al tema.
 */
@Composable
fun DeliveryConfirmationScreen(
    address: String,
    onConfirmDelivered: () -> Unit,
    onConfirmAbsent: () -> Unit,
    modifier: Modifier = Modifier,
    confirmationFeedback: String? = null
) {
    val listState = rememberScalingLazyListState()
    val colors = OmniTheme.colors

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        state = listState,
        contentPadding = PaddingValues(top = 28.dp, bottom = 44.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ENTREGAR PAQUETE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                    color = colors.textSecondary
                )
                Text(
                    text = address,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp)
                )
            }
        }

        // 2. Feedback Banner Animado
        if (confirmationFeedback != null) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(250)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ),
                    exit = fadeOut(tween(200))
                ) {
                    val badgeBg = if (colors.isDark) DeliveryGreenOled else DeliveryGreenLight
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(badgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = colors.primaryGreen,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = confirmationFeedback,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = colors.textPrimary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        } else {
            // 3. Positivo: "ENTREGADO" (Píldora Verde Vibrante + Check Blanco + Bounce)
            item {
                DeliveryActionButton(
                    style = ActionButtonStyle.DeliveredSuccess(label = "ENTREGADO"),
                    onClick = onConfirmDelivered,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 4. Negativo: "NO ENTREGADO / AUSENTE" (Outlined Rojo + X + Bounce)
            item {
                DeliveryActionButton(
                    style = ActionButtonStyle.AbsentSecondary(title = "NO ENTREGADO", subtitle = "AUSENTE"),
                    onClick = onConfirmAbsent,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
