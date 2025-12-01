package com.mukundafoods.chimneylauncherproduct.ui.backend

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitService {

    @GET("api/chimney/check-latest-version")
    suspend fun checkLatestVersion(): Response<CheckLaterVersion>

    @GET("api/chimney-test/check-latest-version")
    suspend fun checkLatestVersionForTest(): Response<CheckLaterVersion>

    @POST("api/chimney/check_serial_number")
    suspend fun checkSerialNumber(@Body serialNumber: SerialNumber): Response<CheckSerialNumberResponse>

    @POST("api/chimney/check_serial_number_feedback")
    suspend fun checkSerialNumberFeedback(@Body serialNumber: SerialNumber): Response<CheckSerialNumberFeedbackResponse>

    @GET("api/chimney/products")
    suspend fun getProducts(): Response<MarketingData>

    @GET("api/chimney/brochures")
    suspend fun getBrochures(): Response<MarketingData>

    @GET("api/chimney/testimonials")
    suspend fun getTestimonials(): Response<MarketingData>

    @GET("api/chimney/others")
    suspend fun getOthers(): Response<MarketingData>

    companion object {
        var retrofitService: RetrofitService? = null
        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://mykitchenos.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }
}