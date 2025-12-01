package com.mukundafoods.chimneylauncherproduct.ui.settings.qrcodetesting

import android.content.ComponentName
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import com.mukundafoods.chimneylauncherproduct.R
import com.mukundafoods.chimneylauncherproduct.databinding.QrCodeTestingFragmentBinding
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.Utils
import java.lang.ref.WeakReference

class QrCodeTestingActivity : AppCompatActivity() {

    private lateinit var binding: QrCodeTestingFragmentBinding

    private var _binding: QrCodeTestingFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val uiBinding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private var isFadingVolume = false
    private var fadeHandler: Handler? = null
    private var fadeRunnable: Runnable? = null

    var totalRunTimeMillis = 1 * 60 * 1000L // 2 minutes = 120,000 ms



    val handler = Handler(Looper.getMainLooper())
    val handlerForFan = Handler(Looper.getMainLooper())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QrCodeTestingFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.turnOnOffBulb(0)
        binding.enableTestingWithoutQrCode.isChecked = Data.isTestingQrCodeEnabled()
        binding.enableDownload.isChecked = Data.isDownloadMarketingDataEnabled()
        binding.variantSelection.isChecked = Data.getVariant() == 1
        binding.enableTestingWithoutQrCode.setOnCheckedChangeListener { buttonView, isChecked ->
            Data.enableTestingQrCode(isChecked)
        }

        binding.enableDownload.setOnCheckedChangeListener { compoundButton, b ->
            Data.setDownloadMarketingDataEnabled(b)
        }

        binding.variantSelection.setOnCheckedChangeListener { _, b ->
            if(b){
                Data.setVariant(1)
            }else{
                Data.setVariant(2)
            }
        }
//        binding.enableTestingModeToggle.setOnClickListener {
//
//
//            binding.fullTestingLayout.visibility = View.VISIBLE
//    if(Data.getVariant() == 1){
//        handler.postDelayed({
//            // Step 2: F2
//
//            Utils.blowerSpeed(1)
//            binding.fanLevel1.visibility = View.VISIBLE
//        }, 2000)
//
//        handler.postDelayed({
//            // Step 3: F3
//            Utils.blowerSpeed(2)
//
//            binding.fanLevel2.visibility = View.VISIBLE
//
//        }, 4000)
//        handler.postDelayed({
//            // Step 3: F3
//            Utils.blowerSpeed(3)
//            binding.fanLevel3.visibility = View.VISIBLE
//
//
//        }, 6000)
//    }else{
//        handler.postDelayed({
//            Utils.blowerSpeed(1)
//            binding.fanLevel1.visibility = View.VISIBLE
//        }, 2000)
//
//        handler.postDelayed({
//            Utils.blowerSpeed(2)
//
//            binding.fanLevel2.visibility = View.VISIBLE
//
//        }, 4000)
//        handler.postDelayed({
//            // Step 3: F3
//            Utils.blowerSpeed(3)
//            binding.fanLevel3.visibility = View.VISIBLE
//                            }, 6000)
//    }
//
//            // Step 1: F1
//
//            handler.postDelayed({
//                // Step 4 : Light Level 1
//                Utils.turnOnOffBulb(1)
//                binding.lightLevel1.visibility = View.VISIBLE
//
//
//            }, 8000)
//            handler.postDelayed({
//                // Step 4 : Light Level 2
//
//                binding.lightLevel2.visibility = View.VISIBLE
//
//                Utils.turnOnOffBulb(2)
//
//
//
//            }, 10000)
//
//            handler.postDelayed({
//                // Step 4 : Light Level 2
//
//                binding.lightLevel3.visibility = View.VISIBLE
//
//                Utils.turnOnOffBulb(3)
//
//
//
//            }, 12000)
//
//                // Step 4 : Light Level 3
//            binding.speakerLevel.visibility = View.VISIBLE
//
//                playSoundForDuration()
//
//
//        }

        binding.enableTestingModeToggle.setOnClickListener {
            binding.fullTestingLayout.visibility = View.VISIBLE
            binding.btnStartTexting.visibility = View.VISIBLE
            binding.btnStopTexting.visibility = View.VISIBLE
        }
        binding.btnStartTexting.setOnClickListener {
            binding.btnStartTexting.isEnabled = false
            binding.btnBackTexting.visibility =View.GONE
            // Duration Constants

// Start playing the song
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.sample)
            mediaPlayer?.start()

            mediaPlayer?.let {
                fadeInVolume(it, durationMillis = 25000L, steps = 25)
            }

// FAN LOGIC: Automatically stops after 2 minutes
// Fan Level 1 (0s -> 5s)
            handler.postDelayed({
                Utils.blowerSpeed(1)
                binding.fanLevel1.visibility = View.VISIBLE
            }, 0)

// Fan Level 2 (5s -> 10s)
            handler.postDelayed({
                Utils.blowerSpeed(2)
                binding.fanLevel2.visibility = View.VISIBLE
            }, 5000)

// Fan Level 3 (10s -> 2 mins)
            handler.postDelayed({
                Utils.blowerSpeed(3)
                binding.fanLevel3.visibility = View.VISIBLE
            }, 10000)

// STOP Fan after 2 minutes
//            handler.postDelayed({
//                Utils.blowerSpeed(0)
//                Utils.turnOnOffBulb(0)
//                binding.fanLevel1.visibility = View.GONE
//                binding.fanLevel2.visibility = View.GONE
//                binding.fanLevel3.visibility = View.GONE
//            }, totalRunTimeMillis)

// LIGHTS
            handler.postDelayed({
                Utils.turnOnOffBulb(1)
                binding.lightLevel1.visibility = View.VISIBLE
            }, 15000)

            handler.postDelayed({
                Utils.turnOnOffBulb(2)
                binding.lightLevel2.visibility = View.VISIBLE
            }, 20000)

            handler.postDelayed({
                Utils.turnOnOffBulb(3)
                binding.lightLevel3.visibility = View.VISIBLE
            }, 25000)
            handler.postDelayed({
                Utils.turnOnOffBulb(0)
                Utils.turnOnOffBulb(0)
                Utils.turnOnOffBulb(0)

                binding.lightLevel1.visibility = View.GONE
                binding.lightLevel2.visibility = View.GONE
                binding.lightLevel3.visibility = View.GONE

//                binding.lightLevel3.visibility = View.VISIBLE
            }, 58000)


// SPEAKER UI
            binding.speakerLevel.visibility = View.VISIBLE
//            playSoundForDuration()

// STOP SONG and CLEAN UP after 2 minutes
            handler.postDelayed({
                binding.btnBackTexting.visibility = View.VISIBLE
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                // Hide all UI
                binding.fanLevel1.visibility = View.GONE
                binding.fanLevel2.visibility = View.GONE
                binding.fanLevel3.visibility = View.GONE
                binding.lightLevel1.visibility = View.GONE
                binding.lightLevel2.visibility = View.GONE
                binding.lightLevel3.visibility = View.GONE
                binding.speakerLevel.visibility = View.GONE
//                binding.fullTestingLayout.visibility = View.GONE
                binding.enableTestingModeToggle.isChecked = false

                // Stop fade-in if still running
                isFadingVolume = false
                fadeHandler?.removeCallbacks(fadeRunnable!!)
                fadeRunnable = null
                fadeHandler = null

                // Turn off all devices
//                Utils.turnOnOffBulb(0)

//                uiBinding.brightnessSeekbar.progress = 0

                Utils.blowerSpeed(0)
            }, totalRunTimeMillis)
        }
        binding.btnStopTexting.setOnClickListener {
            binding.loadingProgressBar.visibility =View.VISIBLE
            binding.btnBackTexting.visibility =View.VISIBLE
            handler.removeCallbacksAndMessages(null)
            binding.btnStartTexting.isEnabled = true

            Utils.turnOnOffBulb(0)
            handlerForFan.postDelayed({

                stopTesting()
                binding.loadingProgressBar.visibility =View.GONE


            }, 3000)


            // Turn off all devices
        }

        binding.btnBackTexting.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            isFadingVolume =false
            fadeHandler?.removeCallbacks(fadeRunnable!!)
            fadeRunnable = null
            fadeHandler = null
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            // Hide all UI
            binding.fullTestingLayout.visibility = View.GONE
            binding.enableTestingModeToggle.isChecked = false
            binding.fanLevel1.visibility = View.GONE
            binding.fanLevel2.visibility = View.GONE
            binding.fanLevel3.visibility = View.GONE
            binding.lightLevel1.visibility = View.GONE
            binding.lightLevel2.visibility = View.GONE
            binding.lightLevel3.visibility = View.GONE
            binding.speakerLevel.visibility = View.GONE
            // Turn off all devices
//            Utils.turnOnOffBulb(0)

            Utils.blowerSpeed(0)
        }
//        binding.emableAssitiveTouchToggle.setOnClickListener {
//            if (!isAccessibilityServiceEnabled(this, AssistiveTouchService::class.java)) {
//                // Redirect user to Accessibility settings
//                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
//                Toast.makeText(this, "Enable Assistive Touch service", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(this, "Assistive Touch already enabled", Toast.LENGTH_SHORT).show()
//            }
//        }

    }

    private fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out android.accessibilityservice.AccessibilityService>
    ): Boolean {
        val expectedComponentName = ComponentName(context, service)
        val enabledServices =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                ?: return false
        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabledServices)
        while (splitter.hasNext()) {
            if (expectedComponentName.flattenToString() == splitter.next()) {
                return true
            }
        }
        return false
    }

        private fun fadeInVolume(mediaPlayer: MediaPlayer, durationMillis: Long = 3000, steps: Int = 30) {
        val playerRef = WeakReference(mediaPlayer) // Prevent memory leak or crash
//        val handler = Handler(Looper.getMainLooper())
        fadeHandler = handler

        val delay = durationMillis / steps
        var currentStep = 0
        isFadingVolume = true
        fadeRunnable = object : Runnable {
            override fun run() {
                val player = playerRef.get()
                if (!isFadingVolume || player == null || !player.isPlaying) {
                    return
                }
                if (currentStep <= steps) {
                    val volume = currentStep.toFloat() / steps
                    try {
                        player.setVolume(volume, volume)
                    } catch (e: Exception) {
                        return
                    }
                    currentStep++
                    handler.postDelayed(this, delay)
                }
            }
        }
        try {
            playerRef.get()?.setVolume(0f, 0f)
        } catch (_: Exception) {
        }

        handler.post(fadeRunnable!!)
    }


    private fun stopTesting() {
//        totalRunTimeMillis = 0
        // Cancel fade-in if active
        isFadingVolume = false
        fadeHandler?.removeCallbacks(fadeRunnable!!)
        fadeRunnable = null
        fadeHandler = null

        // Stop and release media player
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        // Hide all UI
        binding.fanLevel1.visibility = View.GONE
        binding.fanLevel2.visibility = View.GONE
        binding.fanLevel3.visibility = View.GONE
        binding.lightLevel1.visibility = View.GONE
        binding.lightLevel2.visibility = View.GONE
        binding.lightLevel3.visibility = View.GONE
        binding.speakerLevel.visibility = View.GONE
//        binding.fullTestingLayout.visibility = View.GONE
        binding.enableTestingModeToggle.isChecked = false

        // Turn off devices
//        Utils.turnOnOffBulb(0)
//        uiBinding.brightnessSeekbar.progress = 0
        onResume()

        Utils.blowerSpeed(0)

    }



}