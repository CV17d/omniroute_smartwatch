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
 * Direct REST API Service for OmniRoute Standalone Wear OS client.
 * Connects directly to backend endpoints (Spring Boot / NestJS) over Wi-Fi / LTE.
 */
interface RouteApiService {

    /**
     * Downloads the active assigned route with all its stops.
     * Corresponds to: GET /api/routes/active
     */
    @GET("api/routes/active")
    suspend fun getActiveRoute(): Response<RouteSummaryDto>

    /**
     * Dispatches real-time delivery status updates ("DELIVERED", "ABSENT", "INCIDENT")
     * directly to backend without phone companion mediation.
     * Corresponds to: POST /api/deliveries/{id}/status
     */
    @POST("api/deliveries/{id}/status")
    suspend fun updateDeliveryStatus(
        @Path("id") stopId: String,
        @Body request: DeliveryStatusRequestDto
    ): Response<DeliveryStatusResponseDto>
}
