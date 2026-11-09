package com.omniroute.wear.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Payload sent to Spring Boot over STOMP WebSocket (e.g. to /app/delivery.status).
 * Consumed by the Spring backend and relayed to the React web dashboard in real time.
 */
data class DeliveryStatusStompDto(
    @SerializedName("stopId")
    val stopId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("notes")
    val notes: String? = null
)
