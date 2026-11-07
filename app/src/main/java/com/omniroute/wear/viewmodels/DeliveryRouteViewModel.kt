package com.omniroute.wear.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.omniroute.wear.data.repository.DeliveryRepository
import com.omniroute.wear.domain.model.DeliveryStatus
import com.omniroute.wear.domain.model.RouteSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Standalone Wear OS client.
 * Interacts directly with DeliveryRepository (REST API via Retrofit + Room Database).
 */
class DeliveryRouteViewModel(
    application: Application,
    private val repository: DeliveryRepository = DeliveryRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    init {
        // Observe direct internet connectivity (Wi-Fi / LTE)
        viewModelScope.launch {
            repository.isOnline.collect { online ->
                _uiState.update {
                    it.copy(
                        isOnline = online,
                        isOfflineAlertVisible = !online || it.pendingSyncCount > 0
                    )
                }
            }
        }

        // Observe pending mutations queued in Room database
        viewModelScope.launch {
            repository.pendingSyncCount.collect { count ->
                _uiState.update {
                    it.copy(
                        pendingSyncCount = count,
                        isOfflineAlertVisible = !it.isOnline || count > 0
                    )
                }
            }
        }

        // Observe reactive route changes from Room
        viewModelScope.launch {
            repository.routeFlow.collect { cachedRoute ->
                if (cachedRoute != null) {
                    _uiState.update {
                        it.copy(route = cachedRoute)
                    }
                }
            }
        }

        // Initial fetch from backend over HTTP
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.fetchActiveRoute()
            _uiState.update { it.copy(isLoading = false) }
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
        viewModelScope.launch {
            repository.updateDeliveryStatus(stop.id, DeliveryStatus.ARRIVED)
        }
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
        _uiState.update { it.copy(confirmationFeedback = "¡ENTREGADO!") }

        viewModelScope.launch {
            repository.updateDeliveryStatus(
                stopId = stop.id,
                newStatus = DeliveryStatus.DELIVERED,
                notes = "Entregado por mensajero"
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
        _uiState.update { it.copy(confirmationFeedback = "REGISTRADO: AUSENTE") }

        viewModelScope.launch {
            repository.updateDeliveryStatus(
                stopId = stop.id,
                newStatus = DeliveryStatus.ABSENT,
                notes = reason
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
        viewModelScope.launch {
            repository.updateDeliveryStatus(
                stopId = stop.id,
                newStatus = DeliveryStatus.INCIDENT,
                notes = tag
            )
            _uiState.update { it.copy(isIncidentModalVisible = false) }
            advanceToNextStop()
        }
    }

    fun toggleIncidentModal(show: Boolean) {
        _uiState.update { it.copy(isIncidentModalVisible = show) }
    }

    /**
     * Triggers manual synchronization retry.
     */
    fun retrySync() {
        repository.triggerManualSync()
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

    /**
     * Resets demo route state to original sample.
     */
    fun resetDemoRoute() {
        viewModelScope.launch {
            repository.fetchActiveRoute()
        }
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.ROUTE_START,
                route = RouteSummary.SAMPLE_MOTO_ROUTE,
                currentStopIndex = 3
            )
        }
    }
}
