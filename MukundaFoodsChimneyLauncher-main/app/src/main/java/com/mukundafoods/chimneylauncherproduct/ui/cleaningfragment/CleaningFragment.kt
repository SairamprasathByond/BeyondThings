package com.mukundafoods.chimneylauncherproduct.ui.cleaningfragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mukundafoods.chimneylauncherproduct.databinding.CleaningFragmentBinding
import com.mukundafoods.chimneylauncherproduct.ui.animation.cleaning.CleaningAnimation
import com.mukundafoods.chimneylauncherproduct.ui.animation.cleaningbackground.CleaningAnimationBackground
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.MQTTConstants
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.SendClickEvent
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.Utils
import java.util.concurrent.TimeUnit

class CleaningFragment : Fragment() {

    private var _binding: CleaningFragmentBinding? = null
    private lateinit var cookingCountDownTimer: CountDownTimer

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var cleaningViewModel: CleaningViewModel
    private var usedTime: String = "Used time is not received"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CleaningFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {  }
        cleaningViewModel =
            activity?.let { ViewModelProvider(it)[CleaningViewModel::class.java] }!!

        Utils.requestUsedTime()
        binding.startAutoClean.setOnClickListener {
            if (binding.startAutoClean.text == "Start Auto Clean") {
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CLEANING_SCREEN_NAME,
                    MQTTConstants.CLEANING_SCREEN_START_AUTO_CLEAN,
                    usedTime
                )
                Utils.initiateCleaning(
                    Constants.speed[Data.getHeaterTime() - 1].toInt(),
                    Constants.speed[Data.getBlowerTime() - 1].toInt(),
                )
            } else {
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CLEANING_SCREEN_NAME,
                    MQTTConstants.CLEANING_SCREEN_STOP_AUTO_CLEAN,
                    "" + (Constants.speed[Data.getHeaterTime() - 1].toInt() +
                            Constants.speed[Data.getBlowerTime() - 1].toInt()),
                )
                Utils.stopCleaning()
            }
        }


        val animation = CleaningAnimationBackground(binding.cleaningCircleBlackBackground, 360)
        animation.duration = 10
        binding.cleaningCircleBlackBackground.startAnimation(animation)

        cleaningViewModel.getPacket().observe(viewLifecycleOwner) {
            if (it[1].toInt() == 6) {
                updateTheUsedTime(it[2].toInt(), it[3].toInt())
            } else if (it[1].toInt() == 7) {
                if (it[2].toInt() == 1) {
                    startCleaningAnimation()
                } else if (it[2].toInt() == 2) {
                    stopCleaning()
                }
            }
        }
    }

    private fun startCleaningAnimation() {
        Constants.speed[Data.getHeaterTime() - 1].toInt()
        startCookingAutoOffCountDownTimer(
            (Constants.speed[Data.getHeaterTime() - 1].toInt() +
                    Constants.speed[Data.getBlowerTime() - 1].toInt()).toLong()
                    * 60
                    * 1000
        )
        binding.startAutoClean.text = "Stop"
    }

    private fun updateTheUsedTime(hours: Int, minutes: Int) {
        usedTime = "$hours h $minutes m"
        binding.fanUsageTime.text = "$hours h $minutes m"
    }

    private fun stopCleaning() {
        Data.setIsCleaningDone(true)
        Data.setLaterCount(0)
        binding.startAutoClean.text = "Start Auto Clean"
        val animation = CleaningAnimation(binding.cleaningCircle, 0, true)
        animation.duration = 10
        binding.cleaningCircle.startAnimation(animation)
        if (::cookingCountDownTimer.isInitialized) {
            cookingCountDownTimer.cancel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startCookingAutoOffCountDownTimer(time: Long) {
        val animation = CleaningAnimation(binding.cleaningCircle, 360, false)
        animation.duration = time
        binding.cleaningCircle.startAnimation(animation)

        cookingCountDownTimer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
            }

            override fun onFinish() {
                Data.setIsCleaningDone(true)
                Data.setLaterCount(0)
                Utils.stopCleaning()
                cookingCountDownTimer?.cancel()
                //       parentFragmentManager.popBackStack()
            }
        }.start()
    }
}