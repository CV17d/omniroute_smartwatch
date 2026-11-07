package com.omniroute.wear.data.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.omniroute.wear.data.local.database.RouteDatabase
import com.omniroute.wear.data.remote.ApiClient
import java.util.concurrent.TimeUnit

/**
 * WorkManager CoroutineWorker that observes network connectivity (Wi-Fi / LTE)
 * and flushes all pending delivery mutations stored in Room to the REST backend.
 */
class SyncPendingDeliveriesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = RouteDatabase.getInstance(applicationContext)
        val dao = database.deliveryDao()
        val apiService = ApiClient.routeApiService

        val pendingList = dao.getPendingSyncList()
        if (pendingList.isEmpty()) {
            return Result.success()
        }

        var anyFailures = false

        for (item in pendingList) {
            try {
                val response = apiService.updateDeliveryStatus(
                    stopId = item.stopId,
                    request = item.toRequestDto()
                )

                if (response.isSuccessful) {
                    dao.deletePendingSync(item)
                } else {
                    dao.incrementSyncAttempts(item.syncId)
                    anyFailures = true
                }
            } catch (e: Exception) {
                dao.incrementSyncAttempts(item.syncId)
                anyFailures = true
            }
        }

        return if (anyFailures) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "SyncPendingDeliveriesWork"

        /**
         * Enqueues an expedited or deferred sync task requiring internet connection (Wi-Fi or LTE).
         */
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncPendingDeliveriesWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    15,
                    TimeUnit.SECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
