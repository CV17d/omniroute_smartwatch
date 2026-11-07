package com.omniroute.wear.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit & OkHttp provider for direct REST communication.
 */
object ApiClient {

    // Default development base URL (10.0.2.2 points to host localhost in Android Emulator)
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"

    @Volatile
    private var customBaseUrl: String = DEFAULT_BASE_URL

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofitBuilder by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val routeApiService: RouteApiService by lazy {
        retrofitBuilder
            .baseUrl(customBaseUrl)
            .build()
            .create(RouteApiService::class.java)
    }

    fun setBaseUrl(newUrl: String) {
        val sanitizedUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        customBaseUrl = sanitizedUrl
    }
}
