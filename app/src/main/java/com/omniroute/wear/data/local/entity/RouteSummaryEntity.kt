package com.omniroute.wear.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.omniroute.wear.domain.model.DeliveryStop
import com.omniroute.wear.domain.model.RouteSummary

@Entity(tableName = "route_summary")
data class RouteSummaryEntity(
    @PrimaryKey
    val id: String,
    val routeName: String,
    val vehicleType: String = "MOTO",
    val totalStops: Int = 0,
    val completedStops: Int = 0,
    val totalDistanceKm: Double = 0.0
) {
    fun toDomainModel(stops: List<DeliveryStop>): RouteSummary {
        return RouteSummary(
            id = id,
            routeName = routeName,
            vehicleType = vehicleType,
            stops = stops,
            totalStops = totalStops,
            completedStops = completedStops,
            totalDistanceKm = totalDistanceKm
        )
    }

    companion object {
        fun fromDomain(route: RouteSummary): RouteSummaryEntity {
            return RouteSummaryEntity(
                id = route.id,
                routeName = route.routeName,
                vehicleType = route.vehicleType,
                totalStops = route.totalStops,
                completedStops = route.completedStops,
                totalDistanceKm = route.totalDistanceKm
            )
        }
    }
}
