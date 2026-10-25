package com.omniroute.wear.data.service

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import com.omniroute.wear.data.repository.LocalCacheRepository
import com.omniroute.wear.domain.model.DeliveryStatusUpdateEvent
import com.omniroute.wear.domain.model.RouteSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Service encapsulating the Google Play Services Wearable Data Layer API.
 * Uses MessageClient for instantaneous RPC confirmation events to the companion phone,
 * and DataClient for synchronized route data items.
 */
class WearableDataLayerService(
    private val context: Context,
    private val cacheRepository: LocalCacheRepository = LocalCacheRepository(context)
) {
    private val messageClient: MessageClient = Wearable.getMessageClient(context)
    private val dataClient: DataClient = Wearable.getDataClient(context)
    private val nodeClient: NodeClient = Wearable.getNodeClient(context)

    private val _isPhoneConnected = MutableStateFlow(true)
    val isPhoneConnected: StateFlow<Boolean> = _isPhoneConnected.asStateFlow()

    private val _incomingRoute = MutableStateFlow<RouteSummary?>(null)
    val incomingRoute: StateFlow<RouteSummary?> = _incomingRoute.asStateFlow()

    /**
     * Checks if a nearby paired companion smartphone node is currently reachable over Bluetooth.
     */
    suspend fun checkConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            val nodes = nodeClient.connectedNodes.await()
            val connected = nodes.isNotEmpty()
            _isPhoneConnected.value = connected
            if (connected) {
                flushOfflineQueue()
            }
            connected
        } catch (e: Exception) {
            _isPhoneConnected.value = false
            false
        }
    }

    /**
     * Sends a 3-second rapid delivery status change event to the companion mobile app.
     * If the phone is disconnected, safely queues the payload in local storage.
     */
    suspend fun sendStatusUpdate(event: DeliveryStatusUpdateEvent): Boolean = withContext(Dispatchers.IO) {
        val payload = JSONObject().apply {
            put("stopId", event.stopId)
            put("newStatus", event.newStatus.name)
            put("timestamp", event.timestamp)
            if (event.notes != null) put("notes", event.notes)
        }.toString().toByteArray(Charsets.UTF_8)

        try {
            val nodes = nodeClient.connectedNodes.await()
            val targetNode = nodes.firstOrNull()

            if (targetNode != null) {
                messageClient.sendMessage(
                    targetNode.id,
                    PATH_STATUS_UPDATE,
                    payload
                ).await()
                _isPhoneConnected.value = true
                true
            } else {
                // Phone is not reachable: Queue offline
                cacheRepository.queuePendingEvent(event)
                _isPhoneConnected.value = false
                false
            }
        } catch (e: Exception) {
            // Communication error: Fallback to offline queue
            cacheRepository.queuePendingEvent(event)
            _isPhoneConnected.value = false
            false
        }
    }

    /**
     * Flushes queued events that were recorded while offline.
     */
    suspend fun flushOfflineQueue() = withContext(Dispatchers.IO) {
        val pending = cacheRepository.getPendingEvents()
        if (pending.isEmpty()) return@withContext

        val nodes = nodeClient.connectedNodes.await()
        val targetNode = nodes.firstOrNull() ?: return@withContext

        val failed = mutableListOf<DeliveryStatusUpdateEvent>()
        for (event in pending) {
            try {
                val payload = JSONObject().apply {
                    put("stopId", event.stopId)
                    put("newStatus", event.newStatus.name)
                    put("timestamp", event.timestamp)
                    if (event.notes != null) put("notes", event.notes)
                }.toString().toByteArray(Charsets.UTF_8)

                messageClient.sendMessage(targetNode.id, PATH_STATUS_UPDATE, payload).await()
            } catch (e: Exception) {
                failed.add(event)
            }
        }

        cacheRepository.clearPendingEvents()
        for (f in failed) {
            cacheRepository.queuePendingEvent(f)
        }
    }

    companion object {
        const val PATH_STATUS_UPDATE = "/delivery/status_update"
        const val PATH_ROUTE_SYNC = "/route/summary"
    }
}
