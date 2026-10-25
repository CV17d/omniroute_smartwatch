package com.omniroute.wear.domain.model

enum class DeliveryStatus(
    val displayName: String,
    val isFinalState: Boolean
) {
    PENDING("PENDIENTE", false),
    IN_PROGRESS("EN RUTA", false),
    ARRIVED("EN DESTINO", false),
    DELIVERED("ENTREGADO", true),
    ABSENT("AUSENTE", true),
    INCIDENT("INCIDENCIA", true)
}

data class DeliveryStatusUpdateEvent(
    val stopId: String,
    val newStatus: DeliveryStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null
)
