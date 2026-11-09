package com.omniroute.wear.data.remote

import com.omniroute.wear.data.remote.dto.DeliveryStatusRequestDto
import com.omniroute.wear.data.remote.dto.DeliveryStatusResponseDto
import com.omniroute.wear.data.remote.dto.RouteSummaryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Direct REST API Service for OmniRoute / VibeRoute Spring Boot backend.
 * Provides endpoints for extracting active routes and dispatching delivery updates.
 */
interface DeliveryApiService {

    /**
     * Extracts the active assigned route with all its stops from Spring Boot.
     * Corresponds to: GET /api/wearable/routes/active
     */
    @GET("api/wearable/routes/active")
    suspend fun getActiveRoute(): Response<RouteSummaryDto>

    /**
     * Dispatches real-time delivery status updates ("DELIVERED", "ABSENT", "INCIDENT")
     * directly to the Spring Boot backend.
     * Corresponds to: POST /api/wearable/deliveries/{id}/status
     */
    @POST("api/wearable/deliveries/{id}/status")
    suspend fun updateDeliveryStatus(
        @Path("id") stopId: String,
        @Body request: DeliveryStatusRequestDto
    ): Response<DeliveryStatusResponseDto>
}
