package com.omniroute.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import com.omniroute.wear.ui.components.AddressCard
import com.omniroute.wear.ui.components.DeliveryActionButton
import com.omniroute.wear.ui.components.StatusBadge
import com.omniroute.wear.ui.theme.BackgroundPrimary
import com.omniroute.wear.ui.theme.TextPrimary

/**
 * Screen 2: Active delivery in-progress view for OLED Dark Mode.
 * Features navigation instruction, hero street address, vibrant green "¡LLEGUE!" button,
 * and dark-grey secondary pill "INCIDENCIA" button.
 * Uses contentPadding to ensure buttons scroll cleanly past curved circular bezels.
 */
@Composable
fun ActiveDeliveryScreen(
    address: String,
    recipient: String,
    turnDirection: String,
    distanceInstruction: String,
    vehicleType: String,
    onArrived: () -> Unit,
    onIncident: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        state = listState,
        contentPadding = PaddingValues(top = 28.dp, bottom = 44.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // 1. Navigation Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = turnDirection,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    Text(
                        text = distanceInstruction,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                }
                StatusBadge(text = vehicleType)
            }
        }

        // 2. Hero Street Address Card (Crisp White & Light Grey)
        item {
            AddressCard(
                address = address,
                recipient = recipient,
                modifier = Modifier.padding(vertical = 4.dp),
                isEmphasized = true,
            )
        }

        // 3. Primary Massive Action: "¡LLEGUE!" (Vibrant Green Pill)
        item {
            DeliveryActionButton(
                style = ActionButtonStyle.PrimaryGreen(label = "¡LLEGUE!"),
                onClick = onArrived,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        // 4. Secondary Action: "INCIDENCIA ⚠️" (Dark Grey Pill with border to prevent accidental taps)
        item {
            DeliveryActionButton(
                style = ActionButtonStyle.IncidentSecondary(label = "INCIDENCIA"),
                onClick = onIncident,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
