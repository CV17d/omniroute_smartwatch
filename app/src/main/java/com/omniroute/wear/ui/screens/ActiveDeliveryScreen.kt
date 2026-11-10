package com.omniroute.wear.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Text
import com.omniroute.wear.ui.components.*
import com.omniroute.wear.ui.theme.OmniTheme

/**
 * Pantalla Principal: Navegación de Entrega Activa (ActiveDeliveryScreen).
 * - Adaptable automáticamente a Modo Oscuro (#000000) / Modo Claro (#FFFFFF)
 * - Insignias de estado tipo píldora con fondo transparente y bordes de color
 * - Tarjeta principal de dirección con superficie y borde adaptativos
 * - Botones píldora con íconos y animación de escala táctil (95%)
 * - Píldora inferior de sincronización con AnimatedVisibility y CircularProgressIndicator (2dp)
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
    isSyncing: Boolean = false,
    isOnline: Boolean = true,
    pendingSyncCount: Int = 0,
    onRetrySync: () -> Unit = {}
) {
    val listState = rememberScalingLazyListState()
    val colors = OmniTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(top = 28.dp, bottom = 52.dp, start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // 1. Insignias de Estado tipo píldora transparente con borde de color
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        text = "GPS ÓPTIMO",
                        icon = Icons.Rounded.GpsFixed,
                        textColor = colors.primaryGreen,
                        borderColor = colors.primaryGreen
                    )
                    StatusBadge(
                        text = vehicleType,
                        icon = Icons.Rounded.LocalHospital,
                        textColor = colors.textSecondary,
                        borderColor = colors.borderSubtle
                    )
                }
            }

            // 2. Instrucción de Navegación
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = turnDirection,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(end = 6.dp),
                    )
                    Text(
                        text = distanceInstruction,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                    )
                }
            }

            // 3. Tarjeta de Dirección (#1A1A1A oscuro / blanco borde claro #E0E0E0)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.surfaceCard)
                        .border(BorderStroke(1.dp, colors.borderSubtle), RoundedCornerShape(18.dp))
                        .padding(vertical = 10.dp, horizontal = 6.dp)
                ) {
                    AddressCard(
                        address = address,
                        recipient = recipient,
                        isEmphasized = true,
                    )
                }
            }

            // 4. Botón Principal Verde Vibrante con ícono y bounce tactile feedback
            item {
                DeliveryActionButton(
                    style = ActionButtonStyle.PrimaryGreen(
                        label = "¡LLEGUE!",
                        icon = Icons.Rounded.Navigation
                    ),
                    onClick = onArrived,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            // 5. Botón Secundario Píldora ("INCIDENCIA") con bounce tactile feedback
            item {
                DeliveryActionButton(
                    style = ActionButtonStyle.IncidentSecondary(label = "INCIDENCIA"),
                    onClick = onIncident,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        // 6. Píldora inferior animada de sincronización (AnimatedVisibility + Loader 2.dp)
        SyncStatusPill(
            visible = isSyncing || !isOnline || pendingSyncCount > 0,
            isOnline = isOnline,
            pendingSyncCount = pendingSyncCount,
            onRetry = onRetrySync,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
        )
    }
}
