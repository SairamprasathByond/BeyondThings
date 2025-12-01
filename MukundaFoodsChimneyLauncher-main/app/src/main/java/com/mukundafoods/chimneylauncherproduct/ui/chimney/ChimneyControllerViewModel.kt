package com.mukundafoods.chimneylauncherproduct.ui.chimney

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.backend.NetworkState
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChimneyControllerViewModel(
    private val mainRepository: MainRepository,
) : ViewModel() {
    val data = SingleLiveEvent<ByteArray>()
    val isDisconnected = SingleLiveEvent<Boolean>()
    val checkSerialNumberResponse = MutableLiveData<CheckSerialNumber>()
    private var isSerialNumberVerified = false
    fun setPacket(data: ByteArray) {
        this.data.value = data
    }

    fun getPacket(): SingleLiveEvent<ByteArray> {
        return data
    }

    fun setIsDisconnected(status: Boolean) {
        this.isDisconnected.value = status
    }

    fun getIsDisconnected(): SingleLiveEvent<Boolean> {
        return isDisconnected
    }

    fun checkSerialNumber() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            while (!isSerialNumberVerified && !Data.isTestingQrCodeEnabled()) {
                when (val response = mainRepository.checkSerialNumber()) {
                    is NetworkState.Success -> {
                        isSerialNumberVerified = true
                        checkSerialNumberResponse.postValue(CheckSerialNumber.Success)
                    }

                    is NetworkState.Error -> {
                        if (response.response.code() == 401) {
                            checkSerialNumberResponse.postValue(CheckSerialNumber.Failure)
                        } else {
                            checkSerialNumberResponse.postValue(CheckSerialNumber.Failure)
                        }
                    }

                }

                delay(60_000)
            }
        }

    }


    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            run {
                checkSerialNumberResponse.postValue(CheckSerialNumber.NoInternet)
                throwable.printStackTrace()
            }
        }
}

