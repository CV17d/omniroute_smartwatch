package com.omniroute.wear.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit & OkHttp provider for direct REST communication with Spring Boot.
 */
object ApiClient {

    // IP de la PC en la red Wi-Fi local (backend en Docker, puerto 8080).
    // El reloj debe estar en la misma Wi-Fi. Si la IP de la PC cambia, actualizar aquí.
    // Para usarlo fuera de la red local, reemplazar por la URL de un túnel HTTPS (ej. cloudflared).
    private const val BASE_URL = "http://172.162.1.53:8080/"

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val deliveryApiService: DeliveryApiService by lazy {
        retrofit.create(DeliveryApiService::class.java)
    }

    // Alias for backwards compatibility across the project
    val routeApiService: DeliveryApiService get() = deliveryApiService

    /**
     * STOMP WebSocket URL derived automatically from BASE_URL (ws:// or wss://).
     * E.g. "ws://10.0.2.2:8080/ws" or "wss://science-marble-strap-strengths.trycloudflare.com/ws"
     */
    val wsUrl: String
        get() = if (BASE_URL.startsWith("https://")) {
            BASE_URL.replace("https://", "wss://").trimEnd('/') + "/ws"
        } else {
            BASE_URL.replace("http://", "ws://").trimEnd('/') + "/ws"
        }

    val stompClientManager: com.omniroute.wear.data.remote.stomp.StompClientManager by lazy {
        com.omniroute.wear.data.remote.stomp.StompClientManager(wsUrl)
    }
}
