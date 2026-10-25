package com.omniroute.wear.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.components.ActionButtonStyle
import com.omniroute.wear.ui.components.DeliveryActionButton
import com.omniroute.wear.ui.theme.BackgroundPrimary
import com.omniroute.wear.ui.theme.DeliveryGreen
import com.omniroute.wear.ui.theme.DeliveryGreenLight
import com.omniroute.wear.ui.theme.TextPrimary
import com.omniroute.wear.ui.theme.TextSecondary

/**
 * Screen 3: Rapid 3-second delivery confirmation modal.
 * Features high-contrast dropoff buttons for delivered vs absent states.
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

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. Header (ENTREGAR PAQUETE)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ENTREGAR PAQUETE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                    color = TextSecondary
                )
                Text(
                    text = address,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp)
                )
            }
        }

        // 2. Feedback Banner (When an action is confirmed)
        if (confirmationFeedback != null) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DeliveryGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = DeliveryGreen
                            )
                        }
                        Text(
                            text = confirmationFeedback,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        } else {
            // 3. Positive Action: "✓ ENTREGADO"
            item {
                DeliveryActionButton(
                    style = ActionButtonStyle.DeliveredLight(label = "ENTREGADO"),
                    onClick = onConfirmDelivered,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 4. Negative Action: "✕ NO ENTREGADO / AUSENTE"
            item {
                DeliveryActionButton(
                    style = ActionButtonStyle.AbsentLight(title = "NO ENTREGADO", subtitle = "AUSENTE"),
                    onClick = onConfirmAbsent,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}
