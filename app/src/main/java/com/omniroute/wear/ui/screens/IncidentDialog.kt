package com.omniroute.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
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
import com.omniroute.wear.ui.theme.BackgroundPrimary
import com.omniroute.wear.ui.theme.TextPrimary
import com.omniroute.wear.ui.theme.TextSecondary
import com.omniroute.wear.ui.theme.WarningAmber

@Composable
fun IncidentDialog(
    onSelectIncident: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberScalingLazyListState()
    val incidentOptions = listOf(
        "Dirección Errónea",
        "Zona Inaccesible",
        "Paquete Averiado",
        "Rechazado"
    )

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Text(
                text = "⚠️ INCIDENCIA",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = WarningAmber,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(incidentOptions.size) { idx ->
            val option = incidentOptions[idx]
            DeliveryActionButton(
                style = ActionButtonStyle.Outlined(label = option, tint = TextPrimary),
                onClick = { onSelectIncident(option) }
            )
        }

        item {
            DeliveryActionButton(
                style = ActionButtonStyle.Outlined(label = "CANCELAR", tint = TextSecondary),
                onClick = onCancel,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
