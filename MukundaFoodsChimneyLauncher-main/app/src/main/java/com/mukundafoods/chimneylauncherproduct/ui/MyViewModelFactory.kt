package com.mukundafoods.chimneylauncherproduct.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.chefconnect.ChefConnectViewModel
import com.mukundafoods.chimneylauncherproduct.ui.chimney.ChimneyControllerViewModel
import com.mukundafoods.chimneylauncherproduct.ui.database.ChimneyDatabaseHelperImpl
import com.mukundafoods.chimneylauncherproduct.ui.entertainment.EntertainmentViewModel
import com.mukundafoods.chimneylauncherproduct.ui.marketing.MarketingViewModel
import com.mukundafoods.chimneylauncherproduct.ui.settings.settingsfragment.SettingsFragmentViewModel

class MyViewModelFactory(
    private val repository: MainRepository,
    private val chimneyDatabaseHelperImpl: ChimneyDatabaseHelperImpl? = null,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsFragmentViewModel::class.java)) {
            SettingsFragmentViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(ChimneyControllerViewModel::class.java)) {
            ChimneyControllerViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(EntertainmentViewModel::class.java)) {
            EntertainmentViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(ChefConnectViewModel::class.java)) {
            ChefConnectViewModel(this.repository) as T
        } else if (modelClass.isAssignableFrom(MarketingViewModel::class.java)) {
            MarketingViewModel(this.repository, chimneyDatabaseHelperImpl) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}