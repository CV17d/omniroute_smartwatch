package com.omniroute.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
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
import com.omniroute.wear.ui.theme.OmniTheme

/**
 * Screen 1: Resumen de Ruta.
 * Adaptable a Modo Oscuro / Claro con píldora verde vibrante e ícono de flecha ->.
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
                    text = "OMNIROUTE",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 0.6.sp,
                    color = colors.textPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    Text(
                        text = routeName.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = colors.textSecondary
                    )
                    Text(
                        text = " 🏍",
                        fontSize = 9.sp
                    )
                }
            }
        }

        // 2. Vector Map (Reactivo Oscuro/Claro)
        item {
            WearMiniMap(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        // 3. Progress Pill ("4/15 ENTREGAS")
        item {
            CircularRouteProgress(
                completedStops = completedStops,
                totalStops = totalStops
            )
        }

        // 4. Botón "INICIAR RUTA" con flecha -> y animación tactile bounce
        item {
            DeliveryActionButton(
                style = ActionButtonStyle.PrimaryGreen(
                    label = "INICIAR RUTA",
                    icon = Icons.AutoMirrored.Rounded.ArrowForward,
                    iconTrailing = true
                ),
                onClick = onStartRoute,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
