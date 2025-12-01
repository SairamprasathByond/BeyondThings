package com.mukundafoods.chimneylauncherproduct.ui.backend

import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants.buildSerialNumber

class MainRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun checkLatestVersion(): NetworkState<CheckLaterVersion> {
        val response = retrofitService.checkLatestVersion()
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }


    suspend fun checkSerialNumber(): NetworkState<CheckSerialNumberResponse> {
        val response = retrofitService.checkSerialNumber(
            SerialNumber(
                serial_number = buildSerialNumber
            )
        )
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun checkSerialNumberFeedback(): NetworkState<CheckSerialNumberFeedbackResponse> {
        val response = retrofitService.checkSerialNumberFeedback(
            SerialNumber(
                serial_number = buildSerialNumber
            )
        )
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun getProductList() : NetworkState<MarketingData>{
        val response = retrofitService.getProducts()
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun getBrochuresList() : NetworkState<MarketingData>{
        val response = retrofitService.getBrochures()
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun getTestimonialList() : NetworkState<MarketingData>{
        val response = retrofitService.getTestimonials()
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun getOthersList() : NetworkState<MarketingData>{
        val response = retrofitService.getOthers()
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

}