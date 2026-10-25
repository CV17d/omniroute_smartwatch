package com.omniroute.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.components.ActionButtonStyle
import com.omniroute.wear.ui.components.DeliveryActionButton
import com.omniroute.wear.ui.theme.AlertRed
import com.omniroute.wear.ui.theme.BackgroundPrimary
import com.omniroute.wear.ui.theme.DeliveryGreen
import com.omniroute.wear.ui.theme.TextPrimary
import com.omniroute.wear.ui.theme.TextSecondary

@Composable
fun RouteFinishedScreen(
    deliveredCount: Int,
    absentCount: Int,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🏁", fontSize = 24.sp)
                Text(
                    text = "¡RUTA COMPLETADA!",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$deliveredCount", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DeliveryGreen)
                    Text(text = "Entregados", fontSize = 9.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$absentCount", fontWeight = FontWeight.Black, fontSize = 18.sp, color = AlertRed)
                    Text(text = "Ausentes", fontSize = 9.sp, color = TextSecondary)
                }
            }
        }

        item {
            DeliveryActionButton(
                style = ActionButtonStyle.Outlined(label = "REINICIAR DEMO", tint = DeliveryGreen),
                onClick = onReset,
                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
            )
        }
    }
}
