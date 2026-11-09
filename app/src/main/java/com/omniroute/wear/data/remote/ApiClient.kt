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

    // URL Pública Segura (HTTPS) vía Cloudflare Tunnel: Funciona en Emulador, Reloj Físico por Wi-Fi y Reloj en la calle por Bluetooth/4G
    private const val BASE_URL = "https://science-marble-strap-strengths.trycloudflare.com/"

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
