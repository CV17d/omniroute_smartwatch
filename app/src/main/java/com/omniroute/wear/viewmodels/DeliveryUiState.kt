package com.omniroute.wear.viewmodels

import com.omniroute.wear.domain.model.DeliveryStop
import com.omniroute.wear.domain.model.RouteSummary

enum class ScreenDestination {
    ROUTE_START,
    ACTIVE_DELIVERY,
    DELIVERY_CONFIRMATION,
    ROUTE_FINISHED
}

data class DeliveryUiState(
    val currentScreen: ScreenDestination = ScreenDestination.ROUTE_START,
    val route: RouteSummary = RouteSummary.SAMPLE_MOTO_ROUTE,
    val currentStopIndex: Int = 3, // Stop 4 ("CALLE 22 # 5-43") is active
    val isOnline: Boolean = true,
    val pendingSyncCount: Int = 0,
    val confirmationFeedback: String? = null,
    val isIncidentModalVisible: Boolean = false,
    val isLoading: Boolean = false
) {
    val currentStop: DeliveryStop?
        get() = if (currentStopIndex in route.stops.indices) {
            route.stops[currentStopIndex]
        } else {
            route.activeStop
        }
}
