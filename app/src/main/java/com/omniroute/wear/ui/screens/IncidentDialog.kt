package com.omniroute.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.components.ActionButtonStyle
import com.omniroute.wear.ui.components.DeliveryActionButton
import com.omniroute.wear.ui.theme.OmniTheme

@Composable
fun IncidentDialog(
    onSelectIncident: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberScalingLazyListState()
    val colors = OmniTheme.colors
    val incidentOptions = listOf(
        "Dirección Errónea",
        "Zona Inaccesible",
        "Paquete Averiado",
        "Rechazado"
    )

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        state = listState,
        contentPadding = PaddingValues(top = 28.dp, bottom = 44.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = colors.warningText,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = "INCIDENCIA",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = colors.warningText
                )
            }
        }

        items(incidentOptions.size) { idx ->
            val option = incidentOptions[idx]
            DeliveryActionButton(
                style = ActionButtonStyle.Outlined(label = option, tint = colors.textPrimary),
                onClick = { onSelectIncident(option) }
            )
        }

        item {
            DeliveryActionButton(
                style = ActionButtonStyle.Outlined(
                    label = "CANCELAR",
                    tint = colors.textSecondary,
                    icon = Icons.Rounded.Close
                ),
                onClick = onCancel,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
