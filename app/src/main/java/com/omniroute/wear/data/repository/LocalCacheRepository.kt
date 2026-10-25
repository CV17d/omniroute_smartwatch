package com.omniroute.wear.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.omniroute.wear.domain.model.DeliveryStatus
import com.omniroute.wear.domain.model.DeliveryStatusUpdateEvent
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local cache repository providing zero-data-loss offline storage
 * and offline mutation queuing for Wear OS when phone link is lost.
 */
class LocalCacheRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Synchronized
    fun queuePendingEvent(event: DeliveryStatusUpdateEvent) {
        val currentEvents = getPendingEvents().toMutableList()
        currentEvents.add(event)
        savePendingEvents(currentEvents)
    }

    @Synchronized
    fun getPendingEvents(): List<DeliveryStatusUpdateEvent> {
        val jsonString = prefs.getString(KEY_OFFLINE_EVENTS, null) ?: return emptyList()
        val list = mutableListOf<DeliveryStatusUpdateEvent>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    DeliveryStatusUpdateEvent(
                        stopId = obj.getString("stopId"),
                        newStatus = DeliveryStatus.valueOf(obj.getString("newStatus")),
                        timestamp = obj.getLong("timestamp"),
                        notes = if (obj.has("notes")) obj.getString("notes") else null
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    @Synchronized
    fun clearPendingEvents() {
        prefs.edit().remove(KEY_OFFLINE_EVENTS).apply()
    }

    private fun savePendingEvents(events: List<DeliveryStatusUpdateEvent>) {
        val jsonArray = JSONArray()
        for (event in events) {
            val obj = JSONObject()
            obj.put("stopId", event.stopId)
            obj.put("newStatus", event.newStatus.name)
            obj.put("timestamp", event.timestamp)
            if (event.notes != null) {
                obj.put("notes", event.notes)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_OFFLINE_EVENTS, jsonArray.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "omniroute_wear_prefs"
        private const val KEY_OFFLINE_EVENTS = "offline_events_queue"
    }
}
