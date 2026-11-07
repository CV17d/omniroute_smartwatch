package com.omniroute.wear.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.omniroute.wear.data.remote.dto.DeliveryStatusRequestDto

/**
 * Stores failed / queued POST updates when the smartwatch loses Wi-Fi or LTE connection.
 * Processed asynchronously by WorkManager (SyncPendingDeliveriesWorker) once connection is restored.
 */
@Entity(tableName = "pending_delivery_sync")
data class PendingDeliverySyncEntity(
    @PrimaryKey(autoGenerate = true)
    val syncId: Long = 0,
    val stopId: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val attempts: Int = 0
) {
    fun toRequestDto(): DeliveryStatusRequestDto {
        return DeliveryStatusRequestDto(
            status = status,
            timestamp = timestamp,
            notes = notes
        )
    }
}
