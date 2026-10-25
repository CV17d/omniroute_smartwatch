package com.omniroute.wear.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.omniroute.wear.data.service.WearableDataLayerService
import com.omniroute.wear.domain.model.DeliveryStatus
import com.omniroute.wear.domain.model.DeliveryStatusUpdateEvent
import com.omniroute.wear.domain.model.RouteSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing state transitions, navigation flow, and Bluetooth dispatching
 * for OmniRoute on Wear OS.
 */
class DeliveryRouteViewModel(
    application: Application,
    private val dataLayerService: WearableDataLayerService = WearableDataLayerService(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataLayerService.isPhoneConnected.collect { connected ->
                _uiState.update { it.copy(isPhoneConnected = connected, isOfflineAlertVisible = !connected) }
            }
        }
    }

    /**
     * Starts active delivery navigation (Action from Screen 1 "INICIAR RUTA").
     */
    fun startRoute() {
        _uiState.update { it.copy(currentScreen = ScreenDestination.ACTIVE_DELIVERY) }
    }

    /**
     * Courier arrives at stop destination (Action from Screen 2 "¡LLEGUE!").
     */
    fun arriveAtStop() {
        val stop = _uiState.value.currentStop ?: return
        updateStopStatus(stop.id, DeliveryStatus.ARRIVED)
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.DELIVERY_CONFIRMATION,
                confirmationFeedback = null
            )
        }
    }

    /**
     * Confirms successful delivery (Action from Screen 3 "✓ ENTREGADO").
     */
    fun confirmDelivery() {
        val stop = _uiState.value.currentStop ?: return
        updateStopStatus(stop.id, DeliveryStatus.DELIVERED)

        _uiState.update { it.copy(confirmationFeedback = "¡ENTREGADO!") }

        viewModelScope.launch {
            dataLayerService.sendStatusUpdate(
                DeliveryStatusUpdateEvent(
                    stopId = stop.id,
                    newStatus = DeliveryStatus.DELIVERED,
                    notes = "Entregado por mensajero"
                )
            )

            // Auto-advance after 1.3s glanceable feedback
            delay(1300)
            _uiState.update { it.copy(confirmationFeedback = null) }
            advanceToNextStop()
        }
    }

    /**
     * Records delivery absence (Action from Screen 3 "✕ NO ENTREGADO / AUSENTE").
     */
    fun markAsAbsent(reason: String = "Destinatario ausente") {
        val stop = _uiState.value.currentStop ?: return
        updateStopStatus(stop.id, DeliveryStatus.ABSENT)

        _uiState.update { it.copy(confirmationFeedback = "REGISTRADO: AUSENTE") }

        viewModelScope.launch {
            dataLayerService.sendStatusUpdate(
                DeliveryStatusUpdateEvent(
                    stopId = stop.id,
                    newStatus = DeliveryStatus.ABSENT,
                    notes = reason
                )
            )

            delay(1300)
            _uiState.update { it.copy(confirmationFeedback = null) }
            advanceToNextStop()
        }
    }

    /**
     * Reports an on-route delivery incident (e.g. wrong address, access denied).
     */
    fun reportIncident(tag: String) {
        val stop = _uiState.value.currentStop ?: return
        updateStopStatus(stop.id, DeliveryStatus.INCIDENT)

        viewModelScope.launch {
            dataLayerService.sendStatusUpdate(
                DeliveryStatusUpdateEvent(
                    stopId = stop.id,
                    newStatus = DeliveryStatus.INCIDENT,
                    notes = tag
                )
            )
            _uiState.update { it.copy(isIncidentModalVisible = false) }
            advanceToNextStop()
        }
    }

    fun toggleIncidentModal(show: Boolean) {
        _uiState.update { it.copy(isIncidentModalVisible = show) }
    }

    /**
     * Advances index to next pending stop or completes route if none remain.
     */
    private fun advanceToNextStop() {
        val currentStops = _uiState.value.route.stops
        val pendingIndices = currentStops.indices.filter { !currentStops[it].status.isFinalState }

        if (pendingIndices.isNotEmpty()) {
            val nextIdx = pendingIndices.firstOrNull { it > _uiState.value.currentStopIndex }
                ?: pendingIndices.first()
            _uiState.update {
                it.copy(
                    currentStopIndex = nextIdx,
                    currentScreen = ScreenDestination.ACTIVE_DELIVERY
                )
            }
        } else {
            // All deliveries fulfilled
            _uiState.update { it.copy(currentScreen = ScreenDestination.ROUTE_FINISHED) }
        }
    }

    private fun updateStopStatus(stopId: String, status: DeliveryStatus) {
        _uiState.update { state ->
            val updatedStops = state.route.stops.map { stop ->
                if (stop.id == stopId) stop.copy(status = status) else stop
            }
            val completedCount = updatedStops.count { it.status.isFinalState }
            val updatedRoute = state.route.copy(
                stops = updatedStops,
                completedStops = completedCount
            )
            state.copy(route = updatedRoute)
        }
    }

    /**
     * Resets demo route state to original sample.
     */
    fun resetDemoRoute() {
        _uiState.value = DeliveryUiState(
            currentScreen = ScreenDestination.ROUTE_START,
            route = RouteSummary.SAMPLE_MOTO_ROUTE,
            currentStopIndex = 3
        )
    }
}
