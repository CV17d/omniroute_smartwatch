package com.omniroute.wear.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DeliveryStatusRequestDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("notes")
    val notes: String? = null
)

data class DeliveryStatusResponseDto(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("stopId")
    val stopId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null
)
