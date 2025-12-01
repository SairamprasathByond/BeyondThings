package com.mukundafoods.chimneylauncherproduct.ui.cleaningfragment

import androidx.lifecycle.ViewModel
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.SingleLiveEvent

class CleaningViewModel : ViewModel() {
    val data = SingleLiveEvent<ByteArray>()

    fun setPacket(data: ByteArray) {
        this.data.value = data
    }

    fun getPacket(): SingleLiveEvent<ByteArray> {
        return data
    }
}