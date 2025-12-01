package com.mukundafoods.chimneylauncherproduct.ui.entertainment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.backend.NetworkState
import com.mukundafoods.chimneylauncherproduct.ui.chimney.CheckSerialNumber
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EntertainmentViewModel(
    private val mainRepository: MainRepository,
) : ViewModel() {

    val checkSerialNumberFeedbackResponse = MutableLiveData<CheckSerialNumber>()
    private var isSerialNumberFeedbackVerified = false

    fun checkSerialNumber() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            while (!isSerialNumberFeedbackVerified && !Data.isTestingQrCodeEnabled()) {
                when (val response = mainRepository.checkSerialNumberFeedback()) {
                    is NetworkState.Success -> {
                        isSerialNumberFeedbackVerified = true
                        checkSerialNumberFeedbackResponse.postValue(CheckSerialNumber.Success)
                    }

                    is NetworkState.Error -> {
                        if (response.response.code() == 401) {
                            Log.d(
                                "EntertainmentViewModel",
                                "Entertainment Error ${response.response.code()}"
                            )
                            checkSerialNumberFeedbackResponse.postValue(CheckSerialNumber.Failure)
                        } else {
                            Log.d(
                                "EntertainmentViewModel",
                                "Entertainment Error ${response.response.code()}"
                            )
                            checkSerialNumberFeedbackResponse.postValue(CheckSerialNumber.Failure)
                        }
                    }

                }

                delay(60_000)
            }
        }
    }


    val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable -> run {
            checkSerialNumberFeedbackResponse.postValue(CheckSerialNumber.NoInternet)
            throwable.printStackTrace() } }
}

