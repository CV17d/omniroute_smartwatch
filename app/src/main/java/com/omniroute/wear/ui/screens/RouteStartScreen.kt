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
import com.omniroute.wear.ui.components.CircularRouteProgress
import com.omniroute.wear.ui.components.DeliveryActionButton
import com.omniroute.wear.ui.components.WearMiniMap
import com.omniroute.wear.ui.theme.BackgroundPrimary
import com.omniroute.wear.ui.theme.TextPrimary
import com.omniroute.wear.ui.theme.TextSecondary

/**
 * Screen 1: Route start overview with mini-map, progress badge, and start action.
 * Utilizes ScalingLazyColumn for native Wear OS curved viewport adaptation.
 */
@Composable
fun RouteStartScreen(
    routeName: String,
    completedStops: Int,
    totalStops: Int,
    onStartRoute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 1. Header (OMNIROUTE / RUTA: MOTO)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OMNIROUTE",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 0.6.sp,
                    color = TextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    Text(
                        text = routeName.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = " 🏍",
                        fontSize = 9.sp
                    )
                }
            }
        }

        // 2. Vector Map
        item {
            WearMiniMap(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        // 3. Progress Chip ("4/15 ENTREGAS")
        item {
            CircularRouteProgress(
                completedStops = completedStops,
                totalStops = totalStops
            )
        }

        // 4. Action Button ("INICIAR RUTA")
        item {
            DeliveryActionButton(
                style = ActionButtonStyle.PrimaryGreen(label = "INICIAR RUTA"),
                onClick = onStartRoute,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
        }
    }
}
