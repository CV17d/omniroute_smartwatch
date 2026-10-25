package com.omniroute.wear.domain.model

data class DeliveryStop(
    val id: String,
    val stopNumber: Int,
    val address: String,
    val recipientName: String,
    val apartmentOrSuite: String? = null,
    val packageId: String,
    val distanceText: String,
    val navigationInstruction: String,
    val turnDirection: String = "↓",
    val status: DeliveryStatus = DeliveryStatus.PENDING,
    val notes: String? = null
) {
    val glanceableSubtitle: String
        get() = if (!apartmentOrSuite.isNullOrBlank()) {
            "$recipientName • $apartmentOrSuite"
        } else {
            recipientName
        }

    companion object {
        val SAMPLE_CALLE_22 = DeliveryStop(
            id = "stop-4",
            stopNumber = 4,
            address = "CALLE 22 # 5-43",
            recipientName = "Envia",
            apartmentOrSuite = "Apt 302",
            packageId = "ENV-2024-991",
            distanceText = "150m",
            navigationInstruction = "Continuar abajo",
            turnDirection = "↓",
            status = DeliveryStatus.IN_PROGRESS,
            notes = "Timbre 302"
        )

        val SAMPLE_CRA_7 = DeliveryStop(
            id = "stop-5",
            stopNumber = 5,
            address = "CRA 7 # 32-16",
            recipientName = "Carlos Mendez",
            apartmentOrSuite = "Oficina 501",
            packageId = "ENV-2024-992",
            distanceText = "600m",
            navigationInstruction = "Girar a la derecha",
            turnDirection = "→",
            status = DeliveryStatus.PENDING,
            notes = "Portería principal"
        )
    }
}
