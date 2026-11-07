package com.omniroute.wear.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.omniroute.wear.data.local.entity.DeliveryStopEntity
import com.omniroute.wear.data.local.entity.PendingDeliverySyncEntity
import com.omniroute.wear.data.local.entity.RouteSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryDao {

    // ==========================================
    // Route Summary & Stops
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouteSummary(summary: RouteSummaryEntity)

    @Query("SELECT * FROM route_summary LIMIT 1")
    fun getRouteSummaryFlow(): Flow<RouteSummaryEntity?>

    @Query("SELECT * FROM route_summary LIMIT 1")
    suspend fun getRouteSummary(): RouteSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStops(stops: List<DeliveryStopEntity>)

    @Query("SELECT * FROM delivery_stops ORDER BY stopNumber ASC")
    fun getAllStopsFlow(): Flow<List<DeliveryStopEntity>>

    @Query("SELECT * FROM delivery_stops ORDER BY stopNumber ASC")
    suspend fun getAllStops(): List<DeliveryStopEntity>

    @Query("SELECT * FROM delivery_stops WHERE id = :stopId LIMIT 1")
    suspend fun getStopById(stopId: String): DeliveryStopEntity?

    @Query("UPDATE delivery_stops SET status = :status, notes = :notes WHERE id = :stopId")
    suspend fun updateStopStatus(stopId: String, status: String, notes: String?)

    @Query("UPDATE route_summary SET completedStops = :completedCount WHERE id = :routeId")
    suspend fun updateCompletedStops(routeId: String, completedCount: Int)

    @Query("DELETE FROM delivery_stops")
    suspend fun clearStops()

    @Query("DELETE FROM route_summary")
    suspend fun clearRouteSummary()

    @Transaction
    suspend fun replaceRoute(summary: RouteSummaryEntity, stops: List<DeliveryStopEntity>) {
        clearStops()
        clearRouteSummary()
        insertRouteSummary(summary)
        insertStops(stops)
    }

    // ==========================================
    // Offline Queue (Pending Delivery Sync)
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingSync(pending: PendingDeliverySyncEntity): Long

    @Query("SELECT * FROM pending_delivery_sync ORDER BY timestamp ASC")
    suspend fun getPendingSyncList(): List<PendingDeliverySyncEntity>

    @Query("SELECT * FROM pending_delivery_sync ORDER BY timestamp ASC")
    fun getPendingSyncFlow(): Flow<List<PendingDeliverySyncEntity>>

    @Query("SELECT COUNT(*) FROM pending_delivery_sync")
    fun getPendingSyncCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM pending_delivery_sync")
    suspend fun getPendingSyncCount(): Int

    @Delete
    suspend fun deletePendingSync(pending: PendingDeliverySyncEntity)

    @Query("DELETE FROM pending_delivery_sync WHERE syncId = :syncId")
    suspend fun deletePendingSyncById(syncId: Long)

    @Query("DELETE FROM pending_delivery_sync WHERE stopId = :stopId")
    suspend fun deletePendingSyncByStopId(stopId: String)

    @Query("UPDATE pending_delivery_sync SET attempts = attempts + 1 WHERE syncId = :syncId")
    suspend fun incrementSyncAttempts(syncId: Long)

    @Query("DELETE FROM pending_delivery_sync")
    suspend fun clearPendingSync()
}
