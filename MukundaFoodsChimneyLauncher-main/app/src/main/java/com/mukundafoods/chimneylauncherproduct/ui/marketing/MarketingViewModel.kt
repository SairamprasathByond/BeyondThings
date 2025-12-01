package com.mukundafoods.chimneylauncherproduct.ui.marketing

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukundafoods.chimneylauncherproduct.ui.backend.DownloadVideoManager
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.backend.MarketingData
import com.mukundafoods.chimneylauncherproduct.ui.backend.MarketingDataItem
import com.mukundafoods.chimneylauncherproduct.ui.backend.NetworkState
import com.mukundafoods.chimneylauncherproduct.ui.database.BrochuersAndPriceListEntity
import com.mukundafoods.chimneylauncherproduct.ui.database.ChimneyDatabaseHelperImpl
import com.mukundafoods.chimneylauncherproduct.ui.database.OthersEntity
import com.mukundafoods.chimneylauncherproduct.ui.database.ProductsEntity
import com.mukundafoods.chimneylauncherproduct.ui.database.TestimonialEntity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MarketingViewModel(
    private val mainRepository: MainRepository,
    private val chimneyDatabaseHelperImpl: ChimneyDatabaseHelperImpl?,
) : ViewModel() {

    val productsList = MutableLiveData<MarketingData>()
    val brochuresList = MutableLiveData<MarketingData>()
    val testimonialsList = MutableLiveData<MarketingData>()
    val othersList = MutableLiveData<MarketingData>()

    val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable -> run {
           // checkSerialNumberFeedbackResponse.postValue(CheckSerialNumber.NoInternet)
            throwable.printStackTrace() } }

    fun getProducts() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = chimneyDatabaseHelperImpl?.getProducts()
            if (result.isNullOrEmpty()) {
                when (val response = mainRepository.getProductList()) {
                    is NetworkState.Success -> {

                        response.data.data.forEach {
                            chimneyDatabaseHelperImpl?.insertProducts(
                                ProductsEntity(
                                    id = it.id,
                                    name = it.name,
                                    image = it.image,
                                    files = it.files,
                                    version = it.version
                                )
                            )

                            val fileName = "${it.files.split("/").last().split(".")[0]}.${
                                it.files.split("/").last().split(".")[1]
                            }"
                            if (ImageStorageManager.isVideoPresent(
                                    fileName
                                )
                            ) {
                                println("Video/PDF Products is already present so don't download")
                            } else {
                                println("Video/PDF Products is already present so download")
                                DownloadVideoManager(it.files, fileName).enqueueDownload()
                            }
                        }
                        productsList.postValue(response.data)
                    }

                    is NetworkState.Error -> {
                        if (response.response.code() == 401) {
                            Log.d(
                                "Products Error",
                                "Products Error ${response.response.code()}"
                            )
                        } else {
                            Log.d(
                                "Products Error",
                                "Products Error ${response.response.code()}"
                            )
                        }
                    }

                }
            } else {
                val data = ArrayList<MarketingDataItem>()
                for (res in result) {
                    res?.let {
                        data.add(
                            MarketingDataItem(
                                id = it.id,
                                files = it.files,
                                name = it.name,
                                image = it.image,
                                version = it.version
                            )

                        )
                    }

                }
                productsList.postValue(MarketingData(data = data))
            }
        }
    }

    fun getBrochures() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = chimneyDatabaseHelperImpl?.getBrochuresAndPriceLists()
            if (result.isNullOrEmpty()) {
                when (val response = mainRepository.getBrochuresList()) {
                    is NetworkState.Success -> {

                        response.data.data.forEach {
                            chimneyDatabaseHelperImpl?.insertBrochuresAndPriceLists(
                                BrochuersAndPriceListEntity(
                                    id = it.id,
                                    name = it.name,
                                    image = it.image,
                                    files = it.files,
                                    version = it.version
                                )
                            )

                            val fileName = "${it.files.split("/").last().split(".")[0]}.${
                                it.files.split("/").last().split(".")[1]
                            }"
                            if (ImageStorageManager.isVideoPresent(
                                    fileName
                                )
                            ) {
                                println("Video/PDF Products is already present so don't download")
                            } else {
                                println("Video/PDF Products is already present so download")
                                DownloadVideoManager(it.files, fileName).enqueueDownload()
                            }
                        }
                        brochuresList.postValue(response.data)

                    }

                    is NetworkState.Error -> {
                        if (response.response.code() == 401) {
                            Log.d(
                                "Brochures Error",
                                "Brochures Error ${response.response.code()}"
                            )
                        } else {
                            Log.d(
                                "Brochures Error",
                                "Brochures Error ${response.response.code()}"
                            )
                        }
                    }

                }
            } else {
                val data = ArrayList<MarketingDataItem>()
                for (res in result) {
                    res?.let {
                        data.add(
                            MarketingDataItem(
                                id = it.id,
                                files = it.files,
                                name = it.name,
                                image = it.image,
                                version = it.version
                            )

                        )
                    }
                    brochuresList.postValue(MarketingData(data = data))
                }
            }
        }
    }

    fun getTestimonials() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = chimneyDatabaseHelperImpl?.getTestimonials()
            if (result.isNullOrEmpty()) {
                when (val response = mainRepository.getTestimonialList()) {
                    is NetworkState.Success -> {

                        response.data.data.forEach {
                            chimneyDatabaseHelperImpl?.insertTestimonials(
                                TestimonialEntity(
                                    id = it.id,
                                    name = it.name,
                                    image = it.image,
                                    files = it.files,
                                    version = it.version
                                )
                            )

                            val fileName = "${it.files.split("/").last().split(".")[0]}.${
                                it.files.split("/").last().split(".")[1]
                            }"
                            if (ImageStorageManager.isVideoPresent(
                                    fileName
                                )
                            ) {
                                println("Video/PDF Products is already present so don't download")
                            } else {
                                println("Video/PDF Products is already present so download")
                                DownloadVideoManager(it.files, fileName).enqueueDownload()
                            }
                        }
                        testimonialsList.postValue(response.data)
                    }

                    is NetworkState.Error -> {
                        if (response.response.code() == 401) {
                            Log.d(
                                "Testimonials",
                                "Testimonials Error ${response.response.code()}"
                            )
                        } else {
                            Log.d(
                                "Testimonials",
                                "Testimonials Error ${response.response.code()}"
                            )
                        }
                    }

                }
            } else {
                val data = ArrayList<MarketingDataItem>()
                for (res in result) {
                    res?.let {
                        data.add(
                            MarketingDataItem(
                                id = it.id,
                                files = it.files,
                                name = it.name,
                                image = it.image,
                                version = it.version
                            )

                        )
                    }
                    testimonialsList.postValue(MarketingData(data = data))
                }
            }
        }
    }

    fun getOthers() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = chimneyDatabaseHelperImpl?.getOthers()
            if (result.isNullOrEmpty()) {
                when (val response = mainRepository.getOthersList()) {
                    is NetworkState.Success -> {

                        response.data.data.forEach {
                            chimneyDatabaseHelperImpl?.insertOthers(
                                OthersEntity(
                                    id = it.id,
                                    name = it.name,
                                    image = it.image,
                                    files = it.files,
                                    version = it.version
                                )
                            )

                            val fileName = "${it.files.split("/").last().split(".")[0]}.${
                                it.files.split("/").last().split(".")[1]
                            }"
                            if (ImageStorageManager.isVideoPresent(
                                    fileName
                                )
                            ) {
                                println("Video/PDF Products is already present so don't download")
                            } else {
                                println("Video/PDF Products is already present so download")
                                DownloadVideoManager(it.files, fileName).enqueueDownload()
                            }
                        }
                        othersList.postValue(response.data)
                    }

                    is NetworkState.Error -> {
                        if (response.response.code() == 401) {
                            Log.d(
                                "Others",
                                "Others Error ${response.response.code()}"
                            )
                        } else {
                            Log.d(
                                "Others",
                                "Others Error ${response.response.code()}"
                            )
                        }
                    }

                }
            } else {
                val data = ArrayList<MarketingDataItem>()
                for (res in result) {
                    res?.let {
                        data.add(
                            MarketingDataItem(
                                id = it.id,
                                files = it.files,
                                name = it.name,
                                image = it.image,
                                version = it.version
                            )

                        )
                    }
                    othersList.postValue(MarketingData(data = data))
                }
            }
        }
    }

    fun clearDB(){
        viewModelScope.launch {
            chimneyDatabaseHelperImpl?.let{
                it.deleteProducts()
                it.deleteBrochuresAndPriceLists()
                it.deleteTestimonials()
                it.deleteOthers()

                delay(100)
                getData()
            }
        }
    }

    fun getData(){
        getProducts()
        getBrochures()
        getTestimonials()
        getOthers()
    }
}

