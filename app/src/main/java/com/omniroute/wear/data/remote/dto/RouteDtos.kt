package com.omniroute.wear.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.omniroute.wear.domain.model.DeliveryStatus
import com.omniroute.wear.domain.model.DeliveryStop
import com.omniroute.wear.domain.model.RouteSummary

data class RouteSummaryDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("routeName")
    val routeName: String,
    @SerializedName("vehicleType")
    val vehicleType: String = "MOTO",
    @SerializedName("stops")
    val stops: List<DeliveryStopDto> = emptyList(),
    @SerializedName("totalStops")
    val totalStops: Int = stops.size,
    @SerializedName("completedStops")
    val completedStops: Int = 0,
    @SerializedName("totalDistanceKm")
    val totalDistanceKm: Double = 0.0
) {
    fun toDomainModel(): RouteSummary {
        @Suppress("USELESS_ELVIS")
        val safeStops = (stops ?: emptyList()).map { it.toDomainModel() }
        val effectiveTotal = if (totalStops > 0) totalStops else safeStops.size
        @Suppress("USELESS_ELVIS")
        return RouteSummary(
            id = id ?: "route-default",
            routeName = routeName ?: "Ruta Activa",
            vehicleType = vehicleType ?: "MOTO",
            stops = safeStops,
            totalStops = effectiveTotal,
            completedStops = completedStops,
            totalDistanceKm = totalDistanceKm
        )
    }
}

data class DeliveryStopDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("stopNumber")
    val stopNumber: Int,
    @SerializedName("address")
    val address: String,
    @SerializedName("recipientName")
    val recipientName: String,
    @SerializedName("apartmentOrSuite")
    val apartmentOrSuite: String? = null,
    @SerializedName("packageId")
    val packageId: String,
    @SerializedName("distanceText")
    val distanceText: String = "150m",
    @SerializedName("navigationInstruction")
    val navigationInstruction: String = "Continuar",
    @SerializedName("turnDirection")
    val turnDirection: String = "↓",
    @SerializedName("status")
    val status: String = "PENDING",
    @SerializedName("notes")
    val notes: String? = null
) {
    fun toDomainModel(): DeliveryStop {
        @Suppress("USELESS_ELVIS")
        val rawStatus = status ?: "PENDING"
        val parsedStatus = try {
            DeliveryStatus.valueOf(rawStatus.uppercase())
        } catch (_: Exception) {
            DeliveryStatus.PENDING
        }

        @Suppress("USELESS_ELVIS")
        return DeliveryStop(
            id = id ?: "stop-$stopNumber",
            stopNumber = stopNumber,
            address = address ?: "Sin dirección",
            recipientName = recipientName ?: "Destinatario",
            apartmentOrSuite = apartmentOrSuite,
            packageId = packageId ?: "PKG-$stopNumber",
            distanceText = distanceText ?: "150m",
            navigationInstruction = navigationInstruction ?: "Continuar",
            turnDirection = turnDirection ?: "↓",
            status = parsedStatus,
            notes = notes
        )
    }
}
