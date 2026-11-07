package com.omniroute.wear.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.omniroute.wear.domain.model.DeliveryStatus
import com.omniroute.wear.domain.model.DeliveryStop

@Entity(tableName = "delivery_stops")
data class DeliveryStopEntity(
    @PrimaryKey
    val id: String,
    val stopNumber: Int,
    val address: String,
    val recipientName: String,
    val apartmentOrSuite: String? = null,
    val packageId: String,
    val distanceText: String,
    val navigationInstruction: String,
    val turnDirection: String,
    val status: String,
    val notes: String? = null,
    val routeId: String
) {
    fun toDomainModel(): DeliveryStop {
        val parsedStatus = try {
            DeliveryStatus.valueOf(status.uppercase())
        } catch (_: Exception) {
            DeliveryStatus.PENDING
        }

        return DeliveryStop(
            id = id,
            stopNumber = stopNumber,
            address = address,
            recipientName = recipientName,
            apartmentOrSuite = apartmentOrSuite,
            packageId = packageId,
            distanceText = distanceText,
            navigationInstruction = navigationInstruction,
            turnDirection = turnDirection,
            status = parsedStatus,
            notes = notes
        )
    }

    companion object {
        fun fromDomain(stop: DeliveryStop, routeId: String): DeliveryStopEntity {
            return DeliveryStopEntity(
                id = stop.id,
                stopNumber = stop.stopNumber,
                address = stop.address,
                recipientName = stop.recipientName,
                apartmentOrSuite = stop.apartmentOrSuite,
                packageId = stop.packageId,
                distanceText = stop.distanceText,
                navigationInstruction = stop.navigationInstruction,
                turnDirection = stop.turnDirection,
                status = stop.status.name,
                notes = stop.notes,
                routeId = routeId
            )
        }
    }
}
