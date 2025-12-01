package com.mukundafoods.chimneylauncherproduct.ui.settings.chimneysettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mukundafoods.chimneylauncherproduct.databinding.ChimneySettingsFragmentBinding
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants.displayTimeOut
import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants.speed

class ChimneySettingsFragment : Fragment() {

    private var _binding: ChimneySettingsFragmentBinding? = null
    private val binding get() = _binding!!

    private val blowerSpeedArray =
        if (Data.getVariant() == 1) arrayOf(
            "Low",
            "Medium",
            "High"
        ) else arrayOf("F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ChimneySettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.blowerModeNumberPicker.minValue = 1
        binding.blowerModeNumberPicker.maxValue = blowerSpeedArray.size
        binding.blowerModeNumberPicker.displayedValues = blowerSpeedArray

        binding.blowerTimeNumberPicker.minValue = 1
        binding.blowerTimeNumberPicker.maxValue = speed.size
        binding.blowerTimeNumberPicker.displayedValues = speed

        binding.heaterTimeNumberPicker.minValue = 1
        binding.heaterTimeNumberPicker.maxValue = speed.size
        binding.heaterTimeNumberPicker.displayedValues = speed

        binding.cleanTimeNumberPicker.minValue = 1
        binding.cleanTimeNumberPicker.maxValue = speed.size
        binding.cleanTimeNumberPicker.displayedValues = speed

        binding.displayTimeoutNumberPicker.minValue = 1
        binding.displayTimeoutNumberPicker.maxValue = displayTimeOut.size
        binding.displayTimeoutNumberPicker.displayedValues = displayTimeOut

        binding.heaterTimeNumberPicker.value = Data.getHeaterTime()
        binding.blowerTimeNumberPicker.value = Data.getBlowerTime()
        binding.blowerModeNumberPicker.value = Data.getBlowerMode()
        binding.cleanTimeNumberPicker.value = Data.getCleaningScheduled()
        binding.displayTimeoutNumberPicker.value = Data.getDisplayTimeOut()
    }

    override fun onStop() {
        super.onStop()
        Data.apply {
            setBlowerMode(binding.blowerModeNumberPicker.value)
            setBlowerTime(binding.blowerTimeNumberPicker.value)
            setHeaterTime(binding.heaterTimeNumberPicker.value)
            scheduleCleaning(binding.cleanTimeNumberPicker.value)
            displayTimeOut(binding.displayTimeoutNumberPicker.value)
        }
    }

}