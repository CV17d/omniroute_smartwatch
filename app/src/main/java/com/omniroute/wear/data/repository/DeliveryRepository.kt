package com.omniroute.wear.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.omniroute.wear.data.local.dao.DeliveryDao
import com.omniroute.wear.data.local.database.RouteDatabase
import com.omniroute.wear.data.local.entity.DeliveryStopEntity
import com.omniroute.wear.data.local.entity.PendingDeliverySyncEntity
import com.omniroute.wear.data.local.entity.RouteSummaryEntity
import com.omniroute.wear.data.remote.ApiClient
import com.omniroute.wear.data.remote.RouteApiService
import com.omniroute.wear.data.remote.dto.DeliveryStatusRequestDto
import com.omniroute.wear.data.worker.SyncPendingDeliveriesWorker
import com.omniroute.wear.domain.model.DeliveryStatus
import com.omniroute.wear.domain.model.RouteSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Clean Architecture repository coordinating direct REST HTTP calls (Retrofit)
 * and local offline storage / queueing (Room Database).
 */
class DeliveryRepository(
    private val context: Context,
    private val dao: DeliveryDao = RouteDatabase.getInstance(context).deliveryDao(),
    private val apiService: RouteApiService = ApiClient.routeApiService
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isOnline = MutableStateFlow(checkInitialInternet())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

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

        // 2. Direct HTTP POST if online
        val requestDto = DeliveryStatusRequestDto(
            status = newStatus.name,
            timestamp = System.currentTimeMillis(),
            notes = notes
        )

        var directPostSuccessful = false
        if (_isOnline.value) {
            try {
                val response = apiService.updateDeliveryStatus(stopId, requestDto)
                if (response.isSuccessful) {
                    directPostSuccessful = true
                }
            } catch (_: Exception) {
                directPostSuccessful = false
            }
        }

        // 3. If direct POST failed or offline, enqueue in Room for WorkManager
        if (!directPostSuccessful) {
            dao.insertPendingSync(
                PendingDeliverySyncEntity(
                    stopId = stopId,
                    status = newStatus.name,
                    timestamp = requestDto.timestamp,
                    notes = notes
                )
            )
            // Schedule WorkManager deferred sync
            SyncPendingDeliveriesWorker.enqueue(context)
        }

        directPostSuccessful
    }

    /**
     * Triggers manual synchronization of pending mutations and refreshes route.
     */
    fun triggerManualSync() {
        SyncPendingDeliveriesWorker.enqueue(context)
        repositoryScope.launch {
            fetchActiveRoute()
        }
    }

    private fun checkInitialInternet(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
                // Network restored: trigger background sync of queued mutations
                SyncPendingDeliveriesWorker.enqueue(context)
            }

            override fun onLost(network: Network) {
                _isOnline.value = false
            }
        })
    }
}
