package com.omniroute.wear.data.remote.stomp

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.util.concurrent.TimeUnit

/**
 * Manages WebSocket connection using STOMP 1.2 protocol for Wear OS.
 * Provides coroutine-first subscription and message dispatch with battery-conscious lifecycle management.
 */
class StompClientManager(
    private val wsUrl: String = DEFAULT_WS_URL,
    private val gson: Gson = Gson(),
) {
    companion object {
        private const val TAG = "StompClientManager"
        // 10.0.2.2 points to localhost from Android Emulator (Spring Boot on 8080)
        const val DEFAULT_WS_URL = "ws://10.0.2.2:8080/ws"
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS) // Keep alive for WebSocket
        .retryOnConnectionFailure(true)
        .build()

    private val stompClient = StompClient(OkHttpWebSocketClient(okHttpClient))

    @Volatile
    private var currentSession: StompSession? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    /**
     * Connects to the STOMP broker. Safe to call multiple times.
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        if (_isConnected.value && currentSession != null) {
            return@withContext true
        }

        try {
            Log.d(TAG, "Connecting to STOMP WebSocket at: $wsUrl")
            val session = stompClient.connect(wsUrl)
            currentSession = session
            _isConnected.value = true
            Log.i(TAG, "STOMP WebSocket connected successfully")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Failed to connect to STOMP WebSocket: ${e.message}")
            _isConnected.value = false
            currentSession = null
            false
        }
    }

    /**
     * Subscribes to a given STOMP topic and emits received text frames as a Flow.
     */
    fun subscribe(topic: String): Flow<String> = flow {
        val session = currentSession
        if (session == null || !_isConnected.value) {
            Log.w(TAG, "Cannot subscribe to $topic: session is not connected")
            return@flow
        }

        try {
            Log.d(TAG, "Subscribing to topic: $topic")
            val subscriptionFlow = session.subscribeText(topic)
            subscriptionFlow.collect { messageBody ->
                emit(messageBody)
            }
        } catch (e: CancellationException) {
            Log.d(TAG, "Subscription to $topic cancelled")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error in STOMP subscription for $topic: ${e.message}")
        }
    }

    /**
     * Dispatches a message to a STOMP destination (e.g. "/app/delivery.status").
     */
    suspend fun send(destination: String, payload: Any): Boolean = withContext(Dispatchers.IO) {
        val session = currentSession
        if (session == null || !_isConnected.value) {
            Log.w(TAG, "Cannot send to $destination: STOMP session not connected")
            return@withContext false
        }

        try {
            val jsonBody = if (payload is String) payload else gson.toJson(payload)
            Log.d(TAG, "Sending STOMP message to $destination: $jsonBody")
            session.sendText(destination, jsonBody)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send STOMP message to $destination: ${e.message}")
            false
        }
    }

    /**
     * Disconnects from the STOMP broker and releases resources to conserve smartwatch battery.
     */
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Disconnecting STOMP WebSocket session")
            currentSession?.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Error during STOMP disconnect: ${e.message}")
        } finally {
            currentSession = null
            _isConnected.value = false
        }
    }
}
