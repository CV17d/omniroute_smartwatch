package com.omniroute.wear.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.omniroute.wear.data.local.dao.DeliveryDao
import com.omniroute.wear.data.local.entity.DeliveryStopEntity
import com.omniroute.wear.data.local.entity.PendingDeliverySyncEntity
import com.omniroute.wear.data.local.entity.RouteSummaryEntity

@Database(
    entities = [
        DeliveryStopEntity::class,
        RouteSummaryEntity::class,
        PendingDeliverySyncEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RouteDatabase : RoomDatabase() {

    abstract fun deliveryDao(): DeliveryDao

    companion object {
        private const val DATABASE_NAME = "omniroute_wear.db"

        @Volatile
        private var instance: RouteDatabase? = null

        fun getInstance(context: Context): RouteDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    RouteDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }

        fun getDatabase(context: Context): RouteDatabase = getInstance(context)
    }
}
