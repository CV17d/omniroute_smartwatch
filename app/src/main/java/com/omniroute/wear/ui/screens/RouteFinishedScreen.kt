package com.omniroute.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
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
import com.omniroute.wear.ui.theme.OmniTheme

@Composable
fun RouteFinishedScreen(
    deliveredCount: Int,
    absentCount: Int,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🏁", fontSize = 24.sp)
                Text(
                    text = "¡RUTA COMPLETADA!",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = colors.textPrimary,
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
                    Text(
                        text = "$deliveredCount",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = colors.primaryGreen
                    )
                    Text(text = "Entregados", fontSize = 9.sp, color = colors.textSecondary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$absentCount",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = colors.negativeText
                    )
                    Text(text = "Ausentes", fontSize = 9.sp, color = colors.textSecondary)
                }
            }
        }

        item {
            DeliveryActionButton(
                style = ActionButtonStyle.PrimaryGreen(
                    label = "REINICIAR DEMO",
                    icon = Icons.Rounded.Refresh
                ),
                onClick = onReset,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
