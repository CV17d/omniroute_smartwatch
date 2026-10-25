package com.omniroute.wear.domain.model

data class RouteSummary(
    val id: String,
    val routeName: String,
    val vehicleType: String = "MOTO",
    val stops: List<DeliveryStop>,
    val totalStops: Int = 15,
    val completedStops: Int = 4,
    val totalDistanceKm: Double = 12.4
) {
    val progressRatio: Float
        get() = if (totalStops > 0) completedStops.toFloat() / totalStops.toFloat() else 0f

    val glanceableProgressLabel: String
        get() = "$completedStops/$totalStops ENTREGAS"

    val activeStop: DeliveryStop?
        get() = stops.firstOrNull { it.status == DeliveryStatus.IN_PROGRESS || it.status == DeliveryStatus.ARRIVED }
            ?: stops.firstOrNull { it.status == DeliveryStatus.PENDING }

    val activeStopIndex: Int
        get() {
            val current = activeStop ?: return 0
            val idx = stops.indexOfFirst { it.id == current.id }
            return if (idx >= 0) idx else 0
        }

    companion object {
        val SAMPLE_MOTO_ROUTE = RouteSummary(
            id = "route-01",
            routeName = "RUTA: MOTO",
            vehicleType = "MOTO",
            stops = listOf(
                DeliveryStop("stop-1", 1, "CRA 15 # 85-30", "Maria Gomez", null, "ENV-101", "0m", "Completada", "↑", DeliveryStatus.DELIVERED),
                DeliveryStop("stop-2", 2, "CALLE 19 # 4-20", "Pedro Diaz", "Piso 2", "ENV-102", "0m", "Completada", "↑", DeliveryStatus.DELIVERED),
                DeliveryStop("stop-3", 3, "CRA 10 # 24-05", "Farmacia Central", "Local 1", "ENV-103", "0m", "Completada", "↑", DeliveryStatus.DELIVERED),
                DeliveryStop.SAMPLE_CALLE_22,
                DeliveryStop.SAMPLE_CRA_7
            ),
            totalStops = 15,
            completedStops = 4,
            totalDistanceKm = 14.8
        )
    }
}
