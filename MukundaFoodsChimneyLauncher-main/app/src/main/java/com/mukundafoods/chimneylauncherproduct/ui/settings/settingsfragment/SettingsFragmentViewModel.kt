package com.mukundafoods.chimneylauncherproduct.ui.settings.settingsfragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukundafoods.chimneylauncherproduct.ui.backend.CheckLaterVersion
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.backend.NetworkState
import kotlinx.coroutines.*

class SettingsFragmentViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()


    val latestVersionResponse = MutableLiveData<CheckLaterVersion>()

    var job: Job? = null

    val loading = MutableLiveData<Boolean>()

    fun checkLatestVersion() {
        Log.d("Thread Outside", Thread.currentThread().name)

        job = viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            Log.d("Thread Inside", Thread.currentThread().name)
            when (val response = mainRepository.checkLatestVersion()) {
                is NetworkState.Success -> {
                    println(response.data)
                    latestVersionResponse.postValue(response.data)
                }

                is NetworkState.Error -> {
                    if (response.response.code() == 401) {
                    }
                }
            }
        }
    }
    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }


    val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable -> throwable.printStackTrace() }
}