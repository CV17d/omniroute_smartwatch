package com.omniroute.wear.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.google.gson.Gson
import com.omniroute.wear.data.local.dao.DeliveryDao
import com.omniroute.wear.data.local.database.RouteDatabase
import com.omniroute.wear.data.local.entity.DeliveryStopEntity
import com.omniroute.wear.data.local.entity.PendingDeliverySyncEntity
import com.omniroute.wear.data.local.entity.RouteSummaryEntity
import com.omniroute.wear.data.remote.ApiClient
import com.omniroute.wear.data.remote.DeliveryApiService
import com.omniroute.wear.data.remote.dto.DeliveryStatusRequestDto
import com.omniroute.wear.data.remote.dto.DeliveryStatusStompDto
import com.omniroute.wear.data.remote.dto.RouteSummaryDto
import com.omniroute.wear.data.remote.stomp.StompClientManager
import com.omniroute.wear.data.worker.SyncPendingDeliveriesWorker
import com.omniroute.wear.domain.model.DeliveryStatus
import com.omniroute.wear.domain.model.RouteSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Clean Architecture repository coordinating direct REST HTTP calls (Retrofit),
 * real-time bidirectional STOMP WebSockets, and local offline storage / queueing (Room Database).
 */
class DeliveryRepository(
    private val context: Context,
    private val dao: DeliveryDao = RouteDatabase.getInstance(context).deliveryDao(),
    private val apiService: DeliveryApiService = ApiClient.deliveryApiService,
    private val stompClientManager: StompClientManager = ApiClient.stompClientManager,
) {
    companion object {
        private const val TAG = "DeliveryRepository"
        const val TOPIC_ROUTE_UPDATES = "/topic/route-updates"
        const val DESTINATION_DELIVERY_STATUS = "/app/delivery.status"
    }

    private val appContext = context.applicationContext
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val connectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val gson = Gson()

    private var stompSubscriptionJob: Job? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    private val _isOnline = MutableStateFlow(checkInitialInternet())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    val isStompConnected: StateFlow<Boolean> = stompClientManager.isConnected

    val pendingSyncCount: Flow<Int> = dao.getPendingSyncCountFlow()

    init {
        registerNetworkCallback()
    }

    /**
     * Observes the active route reactively from Room.
     * Emits whenever local stops or status change.
     */
    val routeFlow: Flow<RouteSummary?> = combine(
        dao.getRouteSummaryFlow(),
        dao.getAllStopsFlow()
    ) { summaryEntity, stopEntities ->
        if (summaryEntity != null && stopEntities.isNotEmpty()) {
            summaryEntity.toDomainModel(stopEntities.map { it.toDomainModel() })
        } else {
            null
        }
    }

    /**
     * Fetches the active route directly from the backend via GET /api/routes/active.
     * Caches downloaded route in Room. Falls back to Room if offline.
     */
    suspend fun fetchActiveRoute(): Result<RouteSummary> = withContext(Dispatchers.IO) {
        try {
            if (_isOnline.value) {
                val response = apiService.getActiveRoute()
                if (response.isSuccessful && response.body() != null) {
                    val routeDto = response.body()!!
                    val domainRoute = routeDto.toDomainModel()

                    // Cache in Room
                    dao.replaceRoute(
                        summary = RouteSummaryEntity.fromDomain(domainRoute),
                        stops = domainRoute.stops.map { DeliveryStopEntity.fromDomain(it, domainRoute.id) }
                    )
                    return@withContext Result.success(domainRoute)
                }
            }

            // Fallback to local Room cache
            val localSummary = dao.getRouteSummary()
            val localStops = dao.getAllStops()
            if (localSummary != null && localStops.isNotEmpty()) {
                return@withContext Result.success(
                    localSummary.toDomainModel(localStops.map { it.toDomainModel() })
                )
            }

            // Fallback to sample data for first launch / offline demo
            val sample = RouteSummary.SAMPLE_MOTO_ROUTE
            dao.replaceRoute(
                summary = RouteSummaryEntity.fromDomain(sample),
                stops = sample.stops.map { DeliveryStopEntity.fromDomain(it, sample.id) }
            )
            Result.success(sample)
        } catch (e: Exception) {
            // Local Room fallback
            val localSummary = dao.getRouteSummary()
            val localStops = dao.getAllStops()
            if (localSummary != null && localStops.isNotEmpty()) {
                Result.success(localSummary.toDomainModel(localStops.map { it.toDomainModel() }))
            } else {
                val sample = RouteSummary.SAMPLE_MOTO_ROUTE
                Result.success(sample)
            }
        }
    }

    /**
     * Updates delivery stop status:
     * 1. Mutates local Room DB immediately for ultra-fast glanceable UI (3-second target).
     * 2. Attempts direct HTTP POST /api/deliveries/{id}/status.
     * 3. On network failure or offline state, stores mutation in Room queue and schedules WorkManager.
     */
    suspend fun updateDeliveryStatus(
        stopId: String,
        newStatus: DeliveryStatus,
        notes: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        // 1. Immediate local persistence in Room
        dao.updateStopStatus(stopId, newStatus.name, notes)

        val allStops = dao.getAllStops()
        val completedCount = allStops.count {
            val status = try { DeliveryStatus.valueOf(it.status) } catch (_: Exception) { DeliveryStatus.PENDING }
            status.isFinalState
        }
        val currentRoute = dao.getRouteSummary()
        if (currentRoute != null) {
            dao.updateCompletedStops(currentRoute.id, completedCount)
        }

        // Intermediate states (e.g. ARRIVED) only drive the watch UI. The backend only accepts
        // final states (DELIVERED, ABSENT, INCIDENT), so don't send or queue the others.
        if (!newStatus.isFinalState) {
            return@withContext true
        }

        // 2. Instant real-time STOMP broadcast to Spring Boot -> React web dashboard
        val stompPayload = DeliveryStatusStompDto(
            stopId = stopId,
            status = newStatus.name,
            timestamp = System.currentTimeMillis(),
            notes = notes,
        )
        if (_isOnline.value) {
            repositoryScope.launch {
                stompClientManager.send(DESTINATION_DELIVERY_STATUS, stompPayload)
            }
        }

        // 3. Direct HTTP POST if online (redundant backup for REST persistence)
        val requestDto = DeliveryStatusRequestDto(
            status = newStatus.name,
            timestamp = stompPayload.timestamp,
            notes = notes,
        )

        var directPostSuccessful = false
        var rejectedByBackend = false
        if (_isOnline.value) {
            try {
                val response = apiService.updateDeliveryStatus(stopId, requestDto)
                if (response.isSuccessful) {
                    directPostSuccessful = true
                } else if (SyncPendingDeliveriesWorker.isPermanentFailure(response.code())) {
                    rejectedByBackend = true
                    Log.w(TAG, "Backend rejected status update for stop $stopId: HTTP ${response.code()}")
                }
            } catch (_: Exception) {
                directPostSuccessful = false
            }
        }

        // 4. If direct POST failed or offline, enqueue in Room for WorkManager.
        // Updates the backend rejected as invalid are not queued: retrying them can never succeed.
        if (!directPostSuccessful && !rejectedByBackend) {
            dao.insertPendingSync(
                PendingDeliverySyncEntity(
                    stopId = stopId,
                    status = newStatus.name,
                    timestamp = requestDto.timestamp,
                    notes = notes,
                ),
            )
            // Schedule WorkManager deferred sync
            SyncPendingDeliveriesWorker.enqueue(appContext)
        }

        directPostSuccessful
    }

    /**
     * Connects to the STOMP WebSocket broker and subscribes to real-time route updates.
     * Safely invoked when the watch enters the foreground (onStart/onResume).
     */
    suspend fun startRealtimeSync() {
        if (!_isOnline.value) {
            Log.d(TAG, "Cannot start STOMP sync: offline")
            return
        }

        val connected = stompClientManager.connect()
        if (connected) {
            stompSubscriptionJob?.cancel()
            stompSubscriptionJob = repositoryScope.launch {
                Log.i(TAG, "Listening for route modifications on $TOPIC_ROUTE_UPDATES")
                stompClientManager.subscribe(TOPIC_ROUTE_UPDATES).collect { json ->
                    handleIncomingRouteUpdate(json)
                }
            }
        }
    }

    /**
     * Disconnects the STOMP WebSocket session to conserve smartwatch battery life.
     * Invoked when the app moves to background (onStop/onPause).
     */
    suspend fun stopRealtimeSync() {
        Log.d(TAG, "Stopping STOMP real-time sync to conserve battery")
        stompSubscriptionJob?.cancel()
        stompSubscriptionJob = null
        stompClientManager.disconnect()
    }

    /**
     * Parses real-time route JSON payload pushed from Spring Boot and updates Room DB.
     * Room's Flow automatically triggers UI recomposition via StateFlow.
     */
    private suspend fun handleIncomingRouteUpdate(json: String) {
        try {
            Log.d(TAG, "Received STOMP route update: $json")
            val routeDto = gson.fromJson(json, RouteSummaryDto::class.java)
            if (routeDto != null) {
                val domainRoute = routeDto.toDomainModel()
                dao.replaceRoute(
                    summary = RouteSummaryEntity.fromDomain(domainRoute),
                    stops = domainRoute.stops.map { DeliveryStopEntity.fromDomain(it, domainRoute.id) },
                )
                Log.i(TAG, "Updated local Room cache from real-time STOMP broadcast: ${domainRoute.routeName}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing incoming STOMP route JSON: ${e.message}")
        }
    }

    /**
     * Triggers manual synchronization of pending mutations and refreshes route.
     */
    fun triggerManualSync() {
        SyncPendingDeliveriesWorker.enqueue(appContext)
        repositoryScope.launch {
            fetchActiveRoute()
        }
    }

    /**
     * Releases network callbacks and disconnects STOMP safely when ViewModel is cleared.
     */
    fun cleanup() {
        try {
            networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
            networkCallback = null
        } catch (e: Exception) {
            Log.w(TAG, "Error unregistering network callback: ${e.message}")
        }
        repositoryScope.launch {
            stopRealtimeSync()
        }
    }

    private fun checkInitialInternet(): Boolean {
        return try {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            Log.w(TAG, "Unable to inspect initial network state: ${e.message}")
            false
        }
    }

    private fun registerNetworkCallback() {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isOnline.value = true
                    // Network restored: trigger background sync of queued mutations and reconnect STOMP
                    SyncPendingDeliveriesWorker.enqueue(appContext)
                    repositoryScope.launch {
                        startRealtimeSync()
                    }
                }

                override fun onLost(network: Network) {
                    _isOnline.value = false
                    repositoryScope.launch {
                        stopRealtimeSync()
                    }
                }
            }
            networkCallback = callback
            connectivityManager.registerNetworkCallback(request, callback)
        } catch (e: Exception) {
            Log.w(TAG, "Unable to register network callback: ${e.message}")
        }
    }
}
