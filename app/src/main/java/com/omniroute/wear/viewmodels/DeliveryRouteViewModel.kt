package com.omniroute.wear.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.omniroute.wear.data.repository.DeliveryRepository
import com.omniroute.wear.domain.model.DeliveryStatus
import com.omniroute.wear.domain.model.RouteSummary
import kotlinx.coroutines.Job
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
class DeliveryRouteViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: DeliveryRepository = DeliveryRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DeliveryUiState())
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    // Declared before init so the initial fetch in init can set them.
    private var autoRefreshJob: Job? = null
    private var lastFetchAtMs = 0L

    init {
        // Observe direct internet connectivity (Wi-Fi / LTE)
        viewModelScope.launch {
            repository.isOnline.collect { online ->
                _uiState.update {
                    it.copy(isOnline = online)
                }
            }
        }

        // Observe pending mutations queued in Room database
        viewModelScope.launch {
            repository.pendingSyncCount.collect { count ->
                _uiState.update {
                    it.copy(pendingSyncCount = count)
                }
            }
        }

        // Observe reactive route changes from Room
        viewModelScope.launch {
            repository.routeFlow.collect { cachedRoute ->
                if (cachedRoute != null) {
                    _uiState.update {
                        it.copy(
                            route = cachedRoute,
                            currentStopIndex = cachedRoute.activeStopIndex
                        )
                    }
                }
            }
        }

        // Initial fetch from backend over HTTP
        fetchActiveRoute()
    }

    /**
     * Triggers active route fetch from backend (GET /api/wearable/routes/active).
     */
    fun fetchActiveRoute() {
        lastFetchAtMs = System.currentTimeMillis()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.fetchActiveRoute()
            result.onSuccess { route ->
                _uiState.update {
                    it.copy(
                        route = route,
                        currentStopIndex = route.activeStopIndex,
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
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
                currentStopIndex = 3,
            )
        }
    }

    /**
     * Polls the backend for the active route while the app is on screen, so routes assigned
     * from the dashboard show up without reopening the app. Call from onStart.
     */
    fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                // Skip the immediate refresh if we just fetched (e.g. on cold start).
                if (System.currentTimeMillis() - lastFetchAtMs >= RECENT_FETCH_WINDOW_MS) {
                    refreshRouteSilently()
                }
                delay(AUTO_REFRESH_INTERVAL_MS)
            }
        }
    }

    /** Stops polling to save battery when the app leaves the screen. Call from onStop. */
    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    /**
     * Background refresh without the loading indicator. Skipped while the courier is mid-action,
     * and while local updates are still queued (the backend copy would overwrite them).
     */
    private suspend fun refreshRouteSilently() {
        val state = _uiState.value
        val busy = state.currentScreen == ScreenDestination.DELIVERY_CONFIRMATION ||
            state.confirmationFeedback != null ||
            state.isIncidentModalVisible ||
            state.pendingSyncCount > 0 ||
            !state.isOnline
        if (busy) return

        lastFetchAtMs = System.currentTimeMillis()
        val previousRouteId = state.route.id
        repository.fetchActiveRoute().onSuccess { route ->
            // Same route: the Room observer already applies stop changes.
            // New route: show it from its start screen.
            if (route.id != previousRouteId) {
                _uiState.update {
                    it.copy(
                        route = route,
                        currentStopIndex = route.activeStopIndex,
                        currentScreen = ScreenDestination.ROUTE_START,
                    )
                }
            }
        }
    }

    /**
     * Starts STOMP WebSocket connection and subscribes to /topic/route-updates.
     */
    fun startRealtimeSync() {
        viewModelScope.launch {
            repository.startRealtimeSync()
        }
    }

    /**
     * Stops STOMP WebSocket connection to save smartwatch battery when the app is paused/stopped.
     */
    fun stopRealtimeSync() {
        viewModelScope.launch {
            repository.stopRealtimeSync()
        }
    }

    companion object {
        private const val AUTO_REFRESH_INTERVAL_MS = 30_000L
        private const val RECENT_FETCH_WINDOW_MS = 5_000L
    }

    override fun onCleared() {
        repository.cleanup()
        super.onCleared()
    }

    class Factory(
        private val application: Application,
        private val repositoryProvider: () -> DeliveryRepository = { DeliveryRepository(application) }
    ) : ViewModelProvider.Factory {
        constructor(application: Application, repository: DeliveryRepository) : this(
            application = application,
            repositoryProvider = { repository }
        )

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DeliveryRouteViewModel::class.java)) {
                return DeliveryRouteViewModel(application, repositoryProvider()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
