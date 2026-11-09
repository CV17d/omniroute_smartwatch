package com.omniroute.wear

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import com.omniroute.wear.data.local.database.RouteDatabase
import com.omniroute.wear.data.repository.DeliveryRepository
import com.omniroute.wear.domain.model.DeliveryStatus
import com.omniroute.wear.ui.screens.ActiveDeliveryScreen
import com.omniroute.wear.ui.screens.DeliveryConfirmationScreen
import com.omniroute.wear.ui.screens.IncidentDialog
import com.omniroute.wear.ui.screens.RouteFinishedScreen
import com.omniroute.wear.ui.screens.RouteStartScreen
import com.omniroute.wear.ui.theme.AlertRed
import com.omniroute.wear.ui.theme.AlertRedLight
import com.omniroute.wear.ui.theme.BackgroundPrimary
import com.omniroute.wear.ui.theme.OmniWearTheme
import com.omniroute.wear.ui.theme.TextSecondary
import com.omniroute.wear.viewmodels.DeliveryRouteViewModel
import com.omniroute.wear.viewmodels.ScreenDestination

class MainActivity : ComponentActivity() {

    private val viewModel: DeliveryRouteViewModel by viewModels {
        DeliveryRouteViewModel.Factory(
            application = application,
            repository = DeliveryRepository(
                context = applicationContext,
                dao = RouteDatabase.getDatabase(applicationContext).deliveryDao(),
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OmniWearTheme {
                val uiState by viewModel.uiState.collectAsState()
                val view = LocalView.current

                Scaffold(
                    timeText = {
                        TimeText(
                            timeTextStyle = TimeTextDefaults.timeTextStyle(
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundPrimary)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Main Navigation Crossfade
                        Crossfade(
                            targetState = uiState.currentScreen,
                            label = "ScreenCrossfade"
                        ) { destination ->
                            when (destination) {
                                ScreenDestination.ROUTE_START -> {
                                    RouteStartScreen(
                                        routeName = uiState.route.routeName,
                                        completedStops = uiState.route.completedStops,
                                        totalStops = uiState.route.totalStops,
                                        onStartRoute = {
                                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                            viewModel.startRoute()
                                        }
                                    )
                                }

                                ScreenDestination.ACTIVE_DELIVERY -> {
                                    val stop = uiState.currentStop
                                    ActiveDeliveryScreen(
                                        address = stop?.address ?: "CALLE 22 # 5-43",
                                        recipient = stop?.glanceableSubtitle ?: "Envia • Apt 302",
                                        turnDirection = stop?.turnDirection ?: "↓",
                                        distanceInstruction = "${stop?.distanceText ?: "150m"} - ${stop?.navigationInstruction ?: "Continuar abajo"}",
                                        vehicleType = uiState.route.vehicleType,
                                        onArrived = {
                                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                            viewModel.arriveAtStop()
                                        },
                                        onIncident = {
                                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                            viewModel.toggleIncidentModal(true)
                                        }
                                    )
                                }

                                ScreenDestination.DELIVERY_CONFIRMATION -> {
                                    val stop = uiState.currentStop
                                    DeliveryConfirmationScreen(
                                        address = stop?.address ?: "CALLE 22 # 5-43",
                                        confirmationFeedback = uiState.confirmationFeedback,
                                        onConfirmDelivered = {
                                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                            viewModel.confirmDelivery()
                                        },
                                        onConfirmAbsent = {
                                            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                                            viewModel.markAsAbsent()
                                        }
                                    )
                                }

                                ScreenDestination.ROUTE_FINISHED -> {
                                    val delivered = uiState.route.stops.count { it.status == DeliveryStatus.DELIVERED }
                                    val absent = uiState.route.stops.count { it.status == DeliveryStatus.ABSENT }
                                    RouteFinishedScreen(
                                        deliveredCount = delivered,
                                        absentCount = absent,
                                        onReset = {
                                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                            viewModel.resetDemoRoute()
                                        }
                                    )
                                }
                            }
                        }

                        // Incident Modal Overlay
                        if (uiState.isIncidentModalVisible) {
                            IncidentDialog(
                                onSelectIncident = { tag ->
                                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                                    viewModel.reportIncident(tag)
                                },
                                onCancel = {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    viewModel.toggleIncidentModal(false)
                                }
                            )
                        }

                        // Standalone Offline / Queue Alert Banner (Shown only for screens where it is not integrated inline)
                        if (uiState.isOfflineAlertVisible && uiState.currentScreen != ScreenDestination.ACTIVE_DELIVERY) {
                            val alertText = if (!uiState.isOnline) {
                                if (uiState.pendingSyncCount > 0) {
                                    "⚡ OFFLINE: ${uiState.pendingSyncCount} en cola Room"
                                } else {
                                    "⚡ OFFLINE: Sin Wi-Fi / LTE"
                                }
                            } else {
                                "🔄 Sincronizando ${uiState.pendingSyncCount} pendientes..."
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 12.dp, start = 18.dp, end = 18.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AlertRedLight)
                                    .clickable {
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        viewModel.retrySync()
                                    }
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = alertText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlertRed,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRealtimeSync()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRealtimeSync()
    }
}
