package com.mukundafoods.chimneylauncherproduct.ui.chimney

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.GONE
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.wrappers.Wrappers.packageManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.mukundafoods.chimneylauncherproduct.Bldc
import com.mukundafoods.chimneylauncherproduct.DetectionState
import com.mukundafoods.chimneylauncherproduct.Normal
import com.mukundafoods.chimneylauncherproduct.WhistleCounter
import com.mukundafoods.chimneylauncherproduct.databinding.FragmentChimneyControllerBinding
import com.mukundafoods.chimneylauncherproduct.databinding.FragmentChimneyControllerNewBinding
import com.mukundafoods.chimneylauncherproduct.ui.animation.SafeClickListener
import com.mukundafoods.chimneylauncherproduct.ui.animation.blower.BlowerSpeedAnimation
import com.mukundafoods.chimneylauncherproduct.ui.animation.blowerbackground.BlowerBackgroundAnimation
import com.mukundafoods.chimneylauncherproduct.ui.animation.fanandcooking.FanAndCookingCircleAnimation
import com.mukundafoods.chimneylauncherproduct.ui.animation.fanandcookingbackground.FanAndCookingCircleAnimationBackground
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.MQTTConstants
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.SendClickEvent
import com.mukundafoods.chimneylauncherproduct.ui.service.AutoCleaningService
import com.mukundafoods.chimneylauncherproduct.ui.service.CookingTimerService
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants.buildSerialNumber
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.Utils
import org.jtransforms.fft.DoubleFFT_1D
//import org.tensorflow.lite.task.audio.classifier.AudioClassifier
//import org.tensorflow.lite.support.audio.TensorAudio
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
//import org.tensorflow.lite.task.audio.classifier.AudioClassifier
//import org.tensorflow.lite.task.audio.classifier.TensorAudio
//import android.media.AudioRecord


class ChimneyControllerFragment : Fragment(), OnSeekBarChangeListener {

    private var _binding: FragmentChimneyControllerNewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var detectionState = DetectionState.STOPPED

    private var isLampSelected = false
    private var blowerValue = 0
    private var mToneGenerator: ToneGenerator? = null

    private lateinit var autoCleaningService: AutoCleaningService
    private var isAutoCleaningServiceBound = false

    private lateinit var cookingTimerService: CookingTimerService
    private var isCookingTimerServiceBound = false

    private var player: MediaPlayer? = null

    private var micRecording = false

//    private var sm: SoundMeter? = null
    private var thread: Thread? = null

     val AMP_MIN: Double = 1500.0
    val FREQ_MIN: Double = 1200.0
    private var samp_cnt = 0

//    private var whistleCount = 0

    var miss_cnt: Int = 0

    val SAMPLE_DELAY: Int = 160
    private lateinit var whistleDetector: WhistleCounter


//     val MYTAG: String = "COOKER"
//    private var ar: AudioRecord? = null
    private var minSize = 0
    var freq: Double = 0.0


//    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false

    private val sampleRate = 8000
/*    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )*/

    private var whistleCount = 0
    private var targetCount = 3

    private var isPaused = false



    private var isDetecting = false
//    private var whistleCount = 0

//    private var audioRecord: AudioRecord? = null
    private var recordThread: Thread? = null

    private val sampleRateNew = 44100
//    private val bufferSizeNew = AudioRecord.getMinBufferSize(
//        sampleRateNew,
//        AudioFormat.CHANNEL_IN_MONO,
//        AudioFormat.ENCODING_PCM_16BIT
//    )


//    private lateinit var classifier: SoundClassifier
//    private lateinit var tensorAudio: TensorAudio
//    lateinit var audioRecord: AudioRecord
    private lateinit var chimneyControllerViewModel: ChimneyControllerViewModel

//    private lateinit var classifier: AudioClassifier
//    private lateinit var tensorAudio: TensorAudio
//    private lateinit var audioRecordForSound: AudioRecord

//    private val classifier: AudioClassifier = AudioClassifier.createFromFile(requireContext(), "yamnet.tflite")
//    private val tensorAudio: TensorAudio = classifier.createInputTensorAudio()
//    private val audioRecordForSOund = classifier.createAudioRecord()

    private var speechRecognizer: SpeechRecognizer? = null
    private val RECORD_AUDIO_PERMISSION = 2001
    private val handler = Handler(Looper.getMainLooper())
    private var shouldRestart = false
    private lateinit var appListRecyclerview : RecyclerView
    private lateinit var adapter: AppViewAdapter

    private  var appArrayList = ArrayList<String>()

     fun checkRecordPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf<String>(Manifest.permission.RECORD_AUDIO),
                123
            )
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChimneyControllerNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        classifier = AudioClassifier.createFromFile(requireContext(), "yamnet.tflite")
//        tensorAudio = classifier.createInputTensorAudio()
//        audioRecordForSound = classifier.createAudioRecord()
        chimneyControllerViewModel =
            activity?.let { ViewModelProvider(it)[ChimneyControllerViewModel::class.java] }!!
        //ChimneyControllerViewModel by viewModels { MyViewModelFactory(MainRepository(RetrofitService.getInstance())) }

        /* chimneyControllerViewModel =
             ViewModelProvider(this, MyViewModelFactory(MainRepository(RetrofitService.getInstance()))).get(
                 ChimneyControllerViewModel::class.java)*/
        whistleDetector = WhistleCounter(requireActivity())
//        appListRecyclerview =binding.appRecycler
        val fanBackgroundAnimation =
            FanAndCookingCircleAnimationBackground(binding.fanBackground, 360)
        fanBackgroundAnimation.duration = 10
        binding.fanBackground.startAnimation(fanBackgroundAnimation)
//        classifier = SoundClassifier(requireActivity())
        val cookingBackgroundAnimation =
            FanAndCookingCircleAnimationBackground(binding.cookingBackground, 360)
        cookingBackgroundAnimation.duration = 10
        binding.cookingBackground.startAnimation(cookingBackgroundAnimation)
        checkRecordPermission()

        binding.plusLayout.setSafeOnClickListener {
            if (binding.fanSpeedCircle.getCircleAngle() < 249) {
                val fanSpeedAnimation = BlowerSpeedAnimation(
                    binding.fanSpeedCircle,
                    (binding.fanSpeedCircle.getCircleAngle() +
                            if (Data.getVariant() ==1) Normal().fanSpeedRenderingValue else Bldc().fanSpeedRenderingValue
                            ).roundToInt(),
                    binding.fanStatusText,
                    binding.fanSpeedStatus,
                    binding.levelInfo,
                )
                fanSpeedAnimation.duration = 500
                binding.fanSpeedCircle.startAnimation(fanSpeedAnimation)
                Utils.blowerSpeed(++blowerValue)
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
                    MQTTConstants.CHIMNEY_CONTROL_BLOWER_INCREASE,
                    blowerValue.toString()
                )
            }
        }

//        binding.turboLayout.setSafeOnClickListener {
//            binding.fanStatusText.text = "F9"
//            if (binding.fanSpeedCircle.getCircleAngle() < 249) {
//                val fanSpeedAnimation = BlowerSpeedAnimation(
//                    binding.fanSpeedCircle,
//                    252,
//                    binding.fanStatusText,
//                    binding.fanSpeedStatus,
//                    binding.levelInfo,
//                )
//                fanSpeedAnimation.duration = 500
//                binding.fanSpeedCircle.startAnimation(fanSpeedAnimation)
//                Utils.blowerSpeed(9)
//                SendClickEvent().sendClickPacket(
//                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
//                    MQTTConstants.CHIMNEY_CONTROL_TURBO_CLICKED,
//                    "9"
//                )
//            }
//        }
//        binding.calmLayout.setSafeOnClickListener {
//
//                val fanSpeedAnimation = BlowerSpeedAnimation(
//                    binding.fanSpeedCircle,
//                    31,
//                    binding.fanStatusText,
//                    binding.fanSpeedStatus,
//                    binding.levelInfo,
//                )
//                fanSpeedAnimation.duration = 500
//                binding.fanSpeedCircle.startAnimation(fanSpeedAnimation)
//                Utils.blowerSpeed(1)
//                SendClickEvent().sendClickPacket(
//                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
//                    MQTTConstants.CHIMNEY_CONTROL_TURBO_CLICKED,
//                    "2"
//                )
//
//        }

        binding.minusLayout.setSafeOnClickListener {
            if (binding.fanSpeedCircle.getCircleAngle() > 0) {
                val fanSpeedAnimation = BlowerSpeedAnimation(
                    binding.fanSpeedCircle,
                    (binding.fanSpeedCircle.getCircleAngle() - if (Data.getVariant() ==1) Normal().fanSpeedRenderingValue else Bldc().fanSpeedRenderingValue
                            ).roundToInt(),
                    binding.fanStatusText,
                    binding.fanSpeedStatus,
                    binding.levelInfo,
                )
                fanSpeedAnimation.duration = 500
                binding.fanSpeedCircle.startAnimation(fanSpeedAnimation)
                Utils.blowerSpeed(--blowerValue)
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
                    MQTTConstants.CHIMNEY_CONTROL_BLOWER_REDUCE,
                    blowerValue.toString()
                )
            }
        }

        binding.bulb.setOnClickListener {
            if (isLampSelected) {
                updateBrightness(0, false)
            } else {
                updateBrightness(3, false)
            }
        }
        appArrayList.add("youtube")
        appArrayList.add("spotify")
        appArrayList.add("hotstar")
        appArrayList.add("netflix")
        appArrayList.add("amazonmusic")
        appArrayList.add("youtubemusic")

        adapter = AppViewAdapter(requireContext(),appArrayList) { pos->
            when (pos) {
                2 ->openThirdPartyApp ("in.startv.hotstar")      // Hotstar
                3-> openThirdPartyApp("com.netflix.mediaclient") // Netflix
                0 -> openThirdPartyApp("com.google.android.youtube") // YouTube
                1-> openThirdPartyApp("com.spotify.music") // Spotify
                4 -> openThirdPartyApp("com.amazon.mp3")
                5 -> openThirdPartyApp("com.google.android.apps.youtube.music")
            }
        }
        appListRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        appListRecyclerview.adapter = adapter
        adapter.notifyDataSetChanged()

        val snapHelper = PagerSnapHelper()
//        snapHelper.attachToRecyclerView(binding.appRecycler)

//        binding.dotsIndicator.attachTo(binding.appRecycler, snapHelper)
//        binding.dotsIndicator.attachTo(binding.appRecycler)
        // Check if the recognition service is available on startup
        if (!checkRecognizerAvailable()) {
                if (checkAudioPermission()) {
                    startListening()
                } else {
                    // Request permission if not granted
                    requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION)
                }
        }
         fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            // VITAL: Handle the result of the permission request
            if (requestCode == RECORD_AUDIO_PERMISSION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start listening
                    startListening()
                } else {
                    // Permission denied

                    Toast.makeText(requireContext(), "Microphone permission denied.", Toast.LENGTH_LONG).show()
                }
            }
        }


        binding.alarm.setOnClickListener {

        }

        binding.fanStatusText.setOnClickListener {
            if (binding.fanStatusText.text == "On") {
                if(Data.getVariant() ==1){
                    blowerValue = 0
                }else{
                    blowerValue = 3
                }
                Utils.blowerSpeed(++blowerValue)
                renderBlowerSpeed(
                    (if (Data.getVariant() ==1) Normal().fanSpeedRenderingValue else (Bldc().fanSpeedRenderingValue * blowerValue)).roundToInt()
                )
                binding.fanStatusText.isSelected = true
                binding.fanStatusText.text = "Off"
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
                    MQTTConstants.CHIMNEY_CONTROL_BLOWER_ON,
                    blowerValue.toString()
                )
            } else {
                blowerValue = 0
                Utils.blowerSpeed(blowerValue)
                renderBlowerSpeed(0)
                binding.fanStatusText.isSelected = false
                binding.fanStatusText.text = "On"
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
                    MQTTConstants.CHIMNEY_CONTROL_BLOWER_OFF,
                    blowerValue.toString()
                )
            }
        }

        binding.brightnessImage.setOnClickListener {
            if (it.isSelected) {
                updateBrightness(0, false)
            } else {
                updateBrightness(3, false)
            }
        }

        chimneyControllerViewModel.getIsDisconnected()
            .observe(viewLifecycleOwner) { isDisconnected ->
                run {
                    if (isDisconnected) {
                        resetTheFragment()
                    }
                }
            }

        chimneyControllerViewModel.getPacket().observe(viewLifecycleOwner) {
            Log.d("Narayan", "Narayan Received Chimney Control Packet")
            it.forEach {
                Log.d("Narayan", "Narayan Chimney Packet $it")
            }
            // ACK Response
            if (it[0].toInt() == 0x13 || it[0].toInt() == 19) {
                if (it[1].toInt() == 1) {
                    //light
                    /*val lightStatus = it[3]
                    if (lightStatus.toInt() == 0) {
                        updateLampStatus(binding.bulb, false)
                    } else {
                        updateLampStatus(binding.bulb, true)
                    }*/

                    updateBrightness(it[3].toInt(), false)
                    blowerValue = it[5].toInt()
                    renderBlowerSpeed(
                        ((if (Data.getVariant() ==1) Normal().fanSpeedRenderingValue else Bldc().fanSpeedRenderingValue)
                                * (it[5].toInt())).roundToInt()
                    )
                    return@observe
                }

                // Light ACK
                if (it[1].toInt() == 2) {
                    /*if (it[2].toInt() == 0) {
                        updateLampStatus(binding.bulb, false)
                    } else {
                        updateLampStatus(binding.bulb, true)
                    }*/
                    updateBrightness(it[2].toInt(), false)
                    return@observe
                }

                // Blower
                if (it[1].toInt() == 3) {
                    when (it[2].toInt()) {
                        0 -> binding.blowerLevel.text = "00"
                        1 -> binding.blowerLevel.text = "30"
                        2 -> binding.blowerLevel.text = "60"
                        3 -> binding.blowerLevel.text = "100"
                    }

                    return@observe
                }
            }
        }

        updateBrightness(0, false)
        fanAutoOffNumberPicker()
        cookingAutoOffNumberPicker()

        binding.fanTimerButton.setOnClickListener {
            if (binding.fanCircle.animation != null) {
                resetAutoCleanAnimation()
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
                    MQTTConstants.CHIMNEY_CONTROL_AUTO_STOP,
                    binding.fanAutoOffNumberPicker.displayedValues[binding.fanAutoOffNumberPicker.value - 1]
                )
                return@setOnClickListener
            } else {
                val animation = FanAndCookingCircleAnimation(binding.fanCircle, 360, false)
                animation.duration = binding.fanAutoOffNumberPicker.displayedValues[binding.fanAutoOffNumberPicker.value - 1].toLong() * 1000 * 60
                binding.fanAutoOffNumberPicker.visibility = GONE
                binding.fanCountdownTimer.visibility = View.VISIBLE
                if (blowerValue == 0) {
                    blowerValue = 0
                    Utils.blowerSpeed(++blowerValue)
                    renderBlowerSpeed(
                        (if (Data.getVariant() ==1) Normal().fanSpeedRenderingValue else Bldc().fanSpeedRenderingValue).roundToInt()
                    )
                }
                autoCleaningService.startTimer(
                    binding.fanAutoOffNumberPicker.displayedValues[binding.fanAutoOffNumberPicker.value - 1].toLong() * 1000 * 60,
                    this::onBlowerTimerTicking,
                    this::onBlowerTimerFinish
                )
                binding.fanCircle.startAnimation(animation)
                binding.fanCircle.animation.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        binding.fanTimerButton.isSelected = true
                        binding.fanTimerButton.text = "Stop"
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        binding.fanTimerButton.isSelected = false
                        binding.fanTimerButton.text = "Start"
                        binding.fanCircle.clearAnimation()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                })
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
                    MQTTConstants.CHIMNEY_CONTROL_AUTO_START,
                    binding.fanAutoOffNumberPicker.displayedValues[binding.fanAutoOffNumberPicker.value - 1]
                )
            }
        }

        binding.cookingTimerButton.setOnClickListener {
            if (binding.cookingCircle.animation != null) {
                resetCookingAnimation()
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
                    MQTTConstants.CHIMNEY_CONTROL_TIMER_STOP,
                    binding.cookingAutoOffNumberPicker.displayedValues[binding.cookingAutoOffNumberPicker.value - 1]
                )
                return@setOnClickListener
            } else {
                val animation = FanAndCookingCircleAnimation(binding.cookingCircle, 360, false)
                animation.duration =
                    binding.cookingAutoOffNumberPicker.displayedValues[binding.cookingAutoOffNumberPicker.value - 1].toLong() * 1000 * 60
                binding.cookingAutoOffNumberPicker.visibility = GONE
                binding.cookingCountdownTimer.visibility = View.VISIBLE
                //startCookingAutoOffCountDownTimer(binding.cookingAutoOffNumberPicker.displayedValues[binding.cookingAutoOffNumberPicker.value - 1].toLong() * 1000 * 60)
                cookingTimerService.startTimer(
                    binding.cookingAutoOffNumberPicker.displayedValues[binding.cookingAutoOffNumberPicker.value - 1].toLong() * 1000 * 60,
                    this::onCookingTimerTicking,
                    this::onCookingTimerFinished
                )

                binding.cookingCircle.startAnimation(animation)
                binding.cookingCircle.animation.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        binding.cookingTimerButton.isSelected = true
                        binding.cookingTimerButton.text = "Stop"
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        binding.cookingTimerButton.isSelected = false
                        binding.cookingTimerButton.text = "Start"
                        binding.cookingCircle.clearAnimation()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                })
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
                    MQTTConstants.CHIMNEY_CONTROL_TIMER_START,
                    binding.cookingAutoOffNumberPicker.displayedValues[binding.cookingAutoOffNumberPicker.value - 1]
                )

            }
        }
//        binding.cookingTimerButton.setOnClickListener {
//            when (detectionState){
//                DetectionState.STOPPED -> {
//                    binding.whistleCountText.text = binding.cookingAutoOffNumberPicker.value.toString()
//                    binding.cookingAutoOffNumberPicker.visibility = View.GONE
//                    binding.whistleCountText.visibility = View.VISIBLE
//                    startListeningNewLogicWithEndTime()
////                    startListeningNewLogic()
////                    startListeningNew()
////                    startListening()
////                    setTargetCount(binding.cookingAutoOffNumberPicker.value)
////                    whistleStart() // start recording + detection
////                    startDetection()
////                    startModel()
//                    detectionState = DetectionState.RUNNING
//                    binding.cookingTimerButton.text = "Stop"
//                }
//
//                DetectionState.RUNNING -> {
////                    stop()
//                stopListening()
////                    stopDetection()
//                    binding.whistleCountText.visibility = View.GONE
//                    binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
////                    pauseDetection() // pauses detection loop
//                    detectionState = DetectionState.STOPPED
//
//                    binding.cookingTimerButton.text = "Start"
//                }
//                DetectionState.PAUSED -> {
//
//                }
//            }
//        }
//        binding.cookingTimerButton.setOnClickListener {
//            when (detectionState) {
//                DetectionState.STOPPED -> {
//                    binding.whistleCountText.text = binding.cookingAutoOffNumberPicker.value.toString()
//                    binding.cookingAutoOffNumberPicker.visibility = View.GONE
//                    binding.whistleCountText.visibility = View.VISIBLE
//
//                    setTargetCount(binding.cookingAutoOffNumberPicker.value)
//                    whistleStart() // start recording + detection
//
//                    detectionState = DetectionState.RUNNING
//                    binding.cookingTimerButton.text = "Pause"
//                }
//
//                DetectionState.RUNNING -> {
//                    pauseDetection() // pauses detection loop
//                    detectionState = DetectionState.PAUSED
//                    binding.cookingTimerButton.text = "Resume"
//                }
//
//                DetectionState.PAUSED -> {
//                    resumeDetection() // resumes detection loop
//                    detectionState = DetectionState.RUNNING
//                    binding.cookingTimerButton.text = "Pause"
//                }
//            }
//        }


        val backgroundFanSpeedAnimation = BlowerBackgroundAnimation(
            binding.fanCircleBackground,
            249,
        )
        backgroundFanSpeedAnimation.duration = 500
        binding.fanCircleBackground.startAnimation(backgroundFanSpeedAnimation)
    }

    private fun hideQRCodeAndShowChimneyLayout() {
        binding.qrCodeLayout.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        if(!Data.isTestingQrCodeEnabled()){
            chimneyControllerViewModel.checkSerialNumberResponse.observe(viewLifecycleOwner) {
                when (it) {
                    CheckSerialNumber.Failure -> {
                        renderQRCode("https://mykitchenos.com/assign_machine/${buildSerialNumber}")
                    }
                    CheckSerialNumber.Success -> {
                        hideQRCodeAndShowChimneyLayout()
                    }
                    CheckSerialNumber.NoInternet -> {
                       /* binding.qrCode.visibility = View.GONE
                        binding.noInternetText.visibility = View.VISIBLE*/
                        hideQRCodeAndShowChimneyLayout()
                    }
                }
            }
            chimneyControllerViewModel.checkSerialNumber()
        }else{
            hideQRCodeAndShowChimneyLayout()
        }
    }



    private fun openThirdPartyApp(name: String) {
        try {
            startActivity(activity?.packageManager?.getLaunchIntentForPackage(name))
        } catch (e: Exception) {
            Toast.makeText(activity, "Unavailable!!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkRecognizerAvailable(): Boolean {
        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Speech Recognition Service not available on this device.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun resetCookingAnimation() {
        val animation = FanAndCookingCircleAnimation(binding.cookingCircle, 0, true)
        animation.duration = 10
        binding.cookingCircle.startAnimation(animation)
        cookingTimerService.stopTimer()

        binding.cookingTimerButton.isSelected = false
        binding.cookingTimerButton.text = "Start"
        binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
        binding.cookingCountdownTimer.visibility = GONE
    }

    private fun resetAutoCleanAnimation() {
        val animation = FanAndCookingCircleAnimation(binding.fanCircle, 0, true)
        animation.duration = 10
        binding.fanCircle.startAnimation(animation)
        renderBlowerSpeed(0)
        blowerValue = 0
        Utils.blowerSpeed(blowerValue)
        autoCleaningService.stopTimer()

        binding.fanTimerButton.isSelected = false
        binding.fanTimerButton.text = "Start"
        binding.fanAutoOffNumberPicker.visibility = View.VISIBLE
        binding.fanCountdownTimer.visibility = GONE
    }

    private fun resetTheFragment() {
        updateBrightness(0, false)
        renderBlowerSpeed(0)
        resetAutoCleanAnimation()
        resetCookingAnimation()
    }

    private fun cookingAutoOffNumberPicker() {
        val array = arrayOf(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15"

        )
        binding.cookingAutoOffNumberPicker.minValue = 1
        binding.cookingAutoOffNumberPicker.maxValue = array.size
        binding.cookingAutoOffNumberPicker.displayedValues = array
    }

    private fun onCookingTimerTicking(minute: Long, second: Long) {
        val text = String.format(
            Locale.getDefault(), "%02d:%02d",
            minute,
            second
        )
        if (minute == 0L && second <= 5L) {
            playTone()
        }

        binding?.cookingCountdownTimer?.text = text
    }

    private fun onCookingTimerFinished() {
        val animation = FanAndCookingCircleAnimation(binding.cookingCircle, 0, true)
        animation.duration = 10
        binding.cookingCircle.startAnimation(animation)
        binding.cookingTimerButton.isSelected = false
        binding.cookingTimerButton.text = "Start"
        binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
        binding.cookingCountdownTimer.visibility = GONE
    }


    private fun onBlowerTimerTicking(text: String) {
        binding?.fanCountdownTimer?.text = text
    }

    private fun onBlowerTimerFinish() {
        blowerValue = 0
        Utils.blowerSpeed(blowerValue)
        renderBlowerSpeed(0)
        val animation = FanAndCookingCircleAnimation(binding.fanCircle, 0, true)
        animation.duration = 10
        binding?.fanCircle?.startAnimation(animation)
        binding?.fanTimerButton?.isSelected = false
        binding?.fanTimerButton?.text = "Start"
        binding?.fanAutoOffNumberPicker?.visibility = View.VISIBLE
        binding?.fanCountdownTimer?.visibility = GONE
    }


    override fun onStop() {
        super.onStop()
    }


    private fun fanAutoOffNumberPicker() {
        val array = arrayOf(
            "1",
            "3",
            "5",
            "10",
            "15",
            "30",
            "45",
            "60"
        )
        binding.fanAutoOffNumberPicker.minValue = 1
        binding.fanAutoOffNumberPicker.maxValue = array.size
        binding.fanAutoOffNumberPicker.displayedValues = array
    }




    private fun renderBlowerSpeed(level: Int) {
        val fanSpeedAnimation = BlowerSpeedAnimation(
            binding.fanSpeedCircle,
            level,
            binding.fanStatusText,
            binding.fanSpeedStatus,
            binding.levelInfo,
        )
        fanSpeedAnimation.duration = 500
        binding.fanSpeedCircle.startAnimation(fanSpeedAnimation)
    }
    private fun playTone() {
        try {
            if (mToneGenerator == null) {
                mToneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            }
            mToneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_S_X4, 3000)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                if (mToneGenerator != null) {
                    mToneGenerator?.release()
                    mToneGenerator = null
                }
            }, 200)
        } catch (e: Exception) {
            Log.d("PlayFragment Narayan", "Exception while playing sound:$e")
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        updateBrightness(progress, fromUser)
    }

     fun updateBrightness(value: Int, fromUser: Boolean) {
        if (!fromUser) {
            binding.brightnessSeekbar.setOnSeekBarChangeListener(null)
        }
        isLampSelected = value != 0
        binding.brightnessImage.isSelected = value != 0
        Utils.turnOnOffBulb(value)
        binding.brightnessStatus.text = "Chimney Light - Level ${if (value != 0) value else "Off"}"
        binding.brightnessSeekbar.progress = value
        SendClickEvent().sendClickPacket(
            MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
            MQTTConstants.CHIMNEY_CONTROL_CHIMNEY_LIGHT,
            value.toString()
        )
        if (!fromUser)
            binding.brightnessSeekbar.setOnSeekBarChangeListener(this)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStart() {
        super.onStart()
        Intent(activity, AutoCleaningService::class.java).also { intent ->
            activity?.bindService(intent, autoCleaningServiceConnection, Context.BIND_AUTO_CREATE)
        }

        Intent(activity, CookingTimerService::class.java).also { intent ->
            activity?.bindService(intent, cookingTimerServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }


    private val autoCleaningServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AutoCleaningService.LocalBinder
            autoCleaningService = binder.getService()
            isAutoCleaningServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isAutoCleaningServiceBound = false
        }

    }

    private val cookingTimerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CookingTimerService.LocalBinder
            cookingTimerService = binder.getService()
            isCookingTimerServiceBound = true

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isCookingTimerServiceBound = false
        }
    }

    private fun renderQRCode(string: String) {
        binding.noInternetText.visibility = View.GONE
        binding.qrCode.visibility = View.VISIBLE
        binding.qrCodeLayout.visibility = View.VISIBLE
        val multiFormatWriter = MultiFormatWriter()

        try {
            val mMatrix = multiFormatWriter.encode(string, BarcodeFormat.QR_CODE, 400, 400)
            val mEncoder = BarcodeEncoder()
            val mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
            binding.qrCodeImage.setImageBitmap(mBitmap);//Setting generated QR code to imageView
        } catch (e: WriterException) {
            e.printStackTrace();
        }
    }
    fun setFanSpeedLevel(level: Int) {
        val angle = when (level) {
            1 -> 31
            2 -> 63
            3 -> 94
            else -> 0
        }

        val fanSpeedAnimation = BlowerSpeedAnimation(
            binding.fanSpeedCircle,
            angle,
            binding.fanStatusText,
            binding.fanSpeedStatus,
            binding.levelInfo
        )
        fanSpeedAnimation.duration = 500
        binding.fanSpeedCircle.startAnimation(fanSpeedAnimation)

        Utils.blowerSpeed(level)
        SendClickEvent().sendClickPacket(
            MQTTConstants.CHIMNEY_CONTROL_SCREEN_NAME,
            MQTTConstants.CHIMNEY_CONTROL_BLOWER_INCREASE,
            level.toString()
        )
    }
//    private fun startRecording() {
////        sm = SoundMeter()
////        start()
//        thread = Thread(object : Runnable {
//            override fun run() {
//                while (thread != null && !thread!!.isInterrupted()) {
//                    try {
//                        Thread.sleep(SAMPLE_DELAY.toLong())
//                    } catch (ie: InterruptedException) {
//                        ie.printStackTrace()
//                    }
//                    requireActivity().runOnUiThread(object : Runnable {
//                        override fun run() {
////                            val av: TextView = findViewById<TextView?>(R.id.ampBox)
//                             {
//                                val amp: Double = getAmplitude()
//                                handleSample(amp, freq)
//
////                                av.setText(
////                                    "amp=" + amp +
////                                            " freq=" + sm.freq +
////                                            " samp=" + samp_cnt +
////                                            " miss=" + miss_cnt
////                                )
//                            }
//                        }
//                    })
//                }
//            }
//        })
//        thread!!.start()
//    }

    private fun handleSample(amp: Double, freq: Double) {
        if (amp > AMP_MIN && freq > FREQ_MIN) {
            samp_cnt++
            if (samp_cnt > 5) {
                miss_cnt = 0
            }
        } else {
            miss_cnt++
            if (miss_cnt > 3) {
                if (samp_cnt > 10) {
                    incrementWhistleCount()
                }
                samp_cnt = 0
            }
        }
    }

    private fun incrementWhistleCount() {
        whistleCount++
        binding.whistleCountText.text = (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
        Log.v("Whistle counted ", whistleCount.toString())
        Toast.makeText(context, "Whistle counted" + whistleCount, Toast.LENGTH_LONG).show()



//        val av: TextView = findViewById(R.id.whistleCount)
//        av.setText(whistleCount.toString())
        if (whistleCount >= getUserRequestedCount()) {
            binding.whistleCountText.visibility = View.GONE
            binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
            if (!player!!.isPlaying()) {
                player!!.start()
            }
        }
    }
    private fun getUserRequestedCount(): Int {
        return binding.cookingAutoOffNumberPicker.value
//        val av: EditText? = findViewById(R.id.numOfWhistles)
//        if (av == null) {
//            return -1
//        }
//        return av.getText().toString().toInt()
    }

    fun onStartStopButton() {
//        val startStopButton: Button = findViewById<Button?>(R.id.reset)
        if (!micRecording) {
//            startRecording()
//            startStopButton.setText("Pause")
        } else {
            stopRecording()
//            startStopButton.setText("Resume")
        }
        micRecording = !micRecording
    }
    private fun stopRecording() {
        try {
             {
//                stop()
//                sm = null
            }

            // Check thread state before interrupting
            if (thread != null && thread!!.isAlive()) {
                thread!!.interrupt()
                thread = null
            } else {
                // Log a message if thread is already stopped or not initialized
                Log.w("MainActivity", "Thread already stopped or not initialized")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    //    int minMin = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    //if (minSize == AudioRecord.ERROR || minSize == AudioRecord.ERROR_BAD_VALUE) {
    //        Log.e("AudioRecord", "Invalid AudioRecord parameters!");
    //        return;
    //    }
    //if (ar.getState() == AudioRecord.STATE_INITIALIZED) {
    //        ar.startRecording();
    //        Log.d("AudioRecord", "Recording started");
    //    } else {
    //        Log.e("AudioRecord", "AudioRecord not initialized!");
    //    }
//    fun start() {
//        minSize = AudioRecord.getMinBufferSize(
//            8000,
//            AudioFormat.CHANNEL_IN_MONO,
//            AudioFormat.ENCODING_PCM_16BIT
//        )
//        Log.i(MYTAG, "minSize:" + minSize)
//        if(checkAudioPermission()){
//             ar =  AudioRecord(MediaRecorder.AudioSource.MIC, 8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);
//            ar!!.startRecording()
//
//        }
//    }

//
    fun start() {
    minSize = AudioRecord.getMinBufferSize(
        8000,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    Log.i("MYTAG", "minSize: $minSize")

//    if (checkAudioPermission()) {
////        ar = AudioRecord(
////            MediaRecorder.AudioSource.MIC,
////            8000,
////            AudioFormat.CHANNEL_IN_MONO,
////            AudioFormat.ENCODING_PCM_16BIT,
////            minSize
////        )
//
//
//
////        ar?.startRecording()
//    }
}





    private val RECORD_AUDIO_REQUEST_CODE = 101

    private fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_REQUEST_CODE
        )
    }

//    fun stop() {
//        if (ar != null) {
//            ar!!.stop()
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                onStartStopButton()
//                // Permission granted ✅
//                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
//            } else {
//                // Permission denied ❌
//                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


//    fun getAmplitude(): Double {
//        val buffer = ShortArray(minSize)
//        ar!!.read(buffer, 0, minSize)
//        var max = 0
//        for (s in buffer) {
//            if (abs(s.toInt()) > max) {
//                max = abs(s.toInt())
//            }
//        }
//        freq = getFrequency(8000, buffer)
//        return max.toDouble()
//    }

    fun getFrequency(sampleRate: Int, audioData: ShortArray): Double {
        val numSamples = audioData.size
        var numCrossing = 0
        for (p in 0..<numSamples - 1) {
            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
                (audioData[p] < 0 && audioData[p + 1] >= 0)
            ) {
                numCrossing++
            }
        }
        val numSecondsRecorded = numSamples.toFloat() / sampleRate.toFloat()
        val numCycles = (numCrossing / 2).toFloat()
        val frequency = numCycles / numSecondsRecorded

        return frequency.toDouble()
    }


    //whistle detecter full code

    fun setTargetCount(count: Int) {
        targetCount = count
    }

//    fun whistleStart() {
//        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            Toast.makeText(context, "Microphone permission required!", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        audioRecord = AudioRecord(
//            MediaRecorder.AudioSource.MIC,
//            sampleRate,
//            AudioFormat.CHANNEL_IN_MONO,
//            AudioFormat.ENCODING_PCM_16BIT,
//            bufferSize
//        )
//        audioRecord?.startRecording()
//        isRecording = true
//
//        recordingThread = Thread {
//            detectCookerWhistles()
//
//        }
//        recordingThread?.start()
//
//    }
//
//    fun stop() {
//        isRecording = false
//        audioRecord?.stop()
//        audioRecord?.release()
//        audioRecord = null
//        recordingThread = null
//        whistleCount = 0
//    }
//
//    private fun detectWhistles() {
//        val buffer = ShortArray(bufferSize)
//        var whistleInProgress = false
//        while (isRecording) {
//            binding.whistleCountText.text = (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
//
//            val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
//            if (read > 0) {
//                val amplitude = calcRMS(buffer, read)
//                val frequency = calcFrequency(buffer, read, sampleRate)
//                val isWhistle = amplitude > 2000 && frequency in 2000.0..4000.0
//                if (isWhistle && !whistleInProgress) {
//                    // whistle range
//                    whistleInProgress = true
//                    whistleCount++
//                    Log.d("WhistleDetector", "Whistle detected! Count = $whistleCount")
//
//
//                    Handler(Looper.getMainLooper()).post {
//                        Toast.makeText(
//                            context,
//                            "Whistle Count: $whistleCount",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    if (whistleCount >= targetCount) {
//                        stop()
//                        Handler(Looper.getMainLooper()).post {
//                            Toast.makeText(context, "Target reached, auto stop!", Toast.LENGTH_LONG)
//                                .show()
//
//                        binding.whistleCountText.visibility = View.GONE
//                            binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                        }
//                        break
//                    }
//
//                    Thread.sleep(1000) // prevent multiple triggers for same whistle
//                }else if (!isWhistle){
//                    whistleInProgress = false
//                }
//            }
//        }
//    }
//    private fun detectWhistleNew() {
//        val buffer = ShortArray(bufferSize)
//
//        // Tuning parameters - adjust these to suit your environment
//        val AMP_THRESHOLD = 2000.0               // amplitude threshold (RMS). Tune this.
//        val FREQ_LOW = 2000.0                    // lower freq bound (Hz)
//        val FREQ_HIGH = 4000.0                   // upper freq bound (Hz)
//        val START_CONSECUTIVE = 2                // # consecutive "whistle frames" required to start
//        val END_CONSECUTIVE = 3                  // # consecutive "non-whistle frames" to declare end
//        val MIN_GAP_MS: Long = 700               // min gap after a whistle ends (ms) to accept a new one
//
//        var whistleInProgress = false
//        var consecutiveStart = 0
//        var consecutiveEnd = 0
//        var lastWhistleEndTime = 0L
//
//        while (isRecording) {
//            if (isPaused) {
//                Thread.sleep(200) // wait a bit while paused
//                continue
//            }
//            val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
//            if (read <= 0) continue
//
//            val amplitude = calcRMS(buffer, read)
//            val frequency = calcFrequency(buffer, read, sampleRate)
//            val isWhistle = amplitude > AMP_THRESHOLD && frequency in FREQ_LOW..FREQ_HIGH
//
//            val isCookerWhistle = amplitude > 2500 && frequency in 2000.0..2400.0
//
//
//            // Update consecutive counters
//            if (isWhistle) {
//                consecutiveStart++
//                consecutiveEnd = 0
//            } else {
//                consecutiveEnd++
//                consecutiveStart = 0
//            }
//
//            // Try to start a whistle only if we have enough consecutive whistle frames
//            if (!whistleInProgress && consecutiveStart >= START_CONSECUTIVE) {
//                val now = System.currentTimeMillis()
//                if (now - lastWhistleEndTime >= MIN_GAP_MS) {
//                    whistleInProgress = true
//                    whistleCount++
//
//                    Handler(Looper.getMainLooper()).post {
//                        Toast.makeText(context, "Whistle Count: $whistleCount", Toast.LENGTH_SHORT).show()
//                        // Update remaining count in UI (example)
//                        binding.whistleCountText.text = (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
//                    }
//
//                    // If we reached the target, stop and update UI
//                    if (whistleCount >= targetCount) {
//                        stop()
//                        Handler(Looper.getMainLooper()).post {
//                            Toast.makeText(context, "Target reached, auto stop!", Toast.LENGTH_LONG).show()
//                            binding.whistleCountText.visibility = View.GONE
//                            binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                            binding.cookingTimerButton.text = "Start"
//                            detectionState = DetectionState.STOPPED
//
//                        }
//                        break
//                    }
//                } else {
//                    // Too soon after previous whistle end; ignore this small spike
//                    // Keep state: whistleInProgress = false and wait for next valid start
//                    consecutiveStart = 0
//                }
//            }
//
//            // When we see enough consecutive non-whistle frames, mark whistle ended
//            if (whistleInProgress && consecutiveEnd >= END_CONSECUTIVE) {
//                whistleInProgress = false
//                lastWhistleEndTime = System.currentTimeMillis()
//                consecutiveStart = 0
//                consecutiveEnd = 0
//            }
//
//            // (no Thread.sleep here — loop runs as fast as audio read provides)
//        }
//    }
//    private fun detectCookerWhistles() {
//        val buffer = ShortArray(bufferSize)
//        var whistleInProgress = false
//        var lastWhistleTime = 0L
//        var lastFrequency = 0.0
//        var consecutiveStable = 0
//
//        while (isRecording) {
//            if (isPaused) {
//                Thread.sleep(200)
//                continue
//            }
//
//            val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
//            if (read <= 0) continue
//            val amplitude = calcRMS(buffer, read)
//            val frequency = calcFrequency(buffer, read, sampleRate)
//            val tonality = calcTonality(buffer, read)
//            val isTonal = tonality > 5.0
//            val isCookerWhistle = amplitude > 1500 && frequency in 1800.0..3000.0
//
//
//            val stable = Math.abs(lastFrequency - frequency) < 200
//            lastFrequency = frequency
//
//            if (isCookerWhistle && isTonal && stable) {
//                consecutiveStable++
//            } else {
//                consecutiveStable = 0
//            }
//
//            if (!whistleInProgress && consecutiveStable >= 4) {
//                val now = System.currentTimeMillis()
//                if (now - lastWhistleTime > 4000) { // gap check
//                    whistleInProgress = true
//                    whistleCount++
//
//                    Handler(Looper.getMainLooper()).post {
//                        binding.whistleCountText.text =
//                            (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
//                        Toast.makeText(context, "Cooker Whistle Count: $whistleCount", Toast.LENGTH_SHORT).show()
//                    }
//
//                    if (whistleCount >= targetCount) {
//                        stop()
//                        Handler(Looper.getMainLooper()).post {
//                            Toast.makeText(context, "Target reached, auto stop!", Toast.LENGTH_LONG).show()
//                            binding.whistleCountText.visibility = View.GONE
//                            binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                        }
//                        break
//                    }
//                    lastWhistleTime = now
//
//                }
//            }
//            if (consecutiveStable == 0 && whistleInProgress) {
//                whistleInProgress = false
//            }
//
//        }
//    }
//    private fun detectCookerWhistle() {
//        val buffer = ShortArray(bufferSize)
//        var whistleInProgress = false
//        var lastWhistleTime = 0L
//        var lastFrequency = 0.0
//        var consecutiveStable = 0
//        var whistleStartTime = 0L
//
//        while (isRecording) {
//            if (isPaused) {
//                Thread.sleep(200)
//                continue
//            }
//
//            val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
//            if (read <= 0) continue
//
//            val amplitude = calcRMS(buffer, read)
//            val frequency = calcFrequency(buffer, read, sampleRate)
//
//            val isCookerWhistle = amplitude > 1500 && frequency in 1800.0..3000.0
//            val stable = Math.abs(lastFrequency - frequency) < 200
//            lastFrequency = frequency
//
//            if (isCookerWhistle && stable) {
//                if (whistleStartTime == 0L) {
//                    whistleStartTime = System.currentTimeMillis()
//                }
//                consecutiveStable++
//            } else {
//                whistleStartTime = 0L
//                consecutiveStable = 0
//            }
//
//            // ✅ Check if whistle lasted 5 seconds continuously
//            if (!whistleInProgress && whistleStartTime > 0 &&
//                System.currentTimeMillis() - whistleStartTime >= 3000
//            ) {
//                val now = System.currentTimeMillis()
//                if (now - lastWhistleTime > 4000) { // gap check
//                    whistleInProgress = true
//                    whistleCount++
//
//                    Handler(Looper.getMainLooper()).post {
//                        Toast.makeText(context, "Cooker Whistle Count: $whistleCount", Toast.LENGTH_SHORT).show()
//                        binding.whistleCountText.text =
//                            (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
//                    }
//
//                    if (whistleCount >= targetCount) {
//                        stop()
//                        Handler(Looper.getMainLooper()).post {
//                            Toast.makeText(context, "Target reached, auto stop!", Toast.LENGTH_LONG).show()
//                            binding.whistleCountText.visibility = View.GONE
//                            binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                        }
//                        break
//                    }
//                    lastWhistleTime = now
//                }
//            }
//
//            if (!isCookerWhistle && whistleInProgress) {
//                whistleInProgress = false
//            }
//        }
//    }
//

    private fun calcTonality(buffer: ShortArray, read: Int): Double {
        val audioData = DoubleArray(read)
        for (i in 0 until read) audioData[i] = buffer[i].toDouble()

        val fft = DoubleFFT_1D(read.toLong())
        val fftData = DoubleArray(read * 2)
        for (i in audioData.indices) {
            fftData[2 * i] = audioData[i]
            fftData[2 * i + 1] = 0.0
        }
        fft.complexForward(fftData)

        val mags = DoubleArray(read / 2)
        for (i in mags.indices) {
            mags[i] = kotlin.math.sqrt(fftData[2 * i].pow(2) + fftData[2 * i + 1].pow(2))
        }

        val peak = mags.maxOrNull() ?: 0.0
        val avg = mags.average()
        return if (avg > 0) peak / avg else 0.0
    }

    fun pauseDetection() {
        isPaused = true
        // Don't stop audioRecord, just stop processing
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, "Whistle detection paused", Toast.LENGTH_SHORT).show()
        }
    }

    fun resumeDetection() {
        isPaused = false
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, "Whistle detection resumed", Toast.LENGTH_SHORT).show()
        }
    }


    // RMS amplitude
    private fun calcRMS(buffer: ShortArray, read: Int): Double {
        var sum = 0.0
        for (i in 0 until read) {
            sum += buffer[i] * buffer[i]
        }
        return sqrt(sum / read)
    }

    // Basic frequency detection using zero-crossing
    private fun calcFrequency(buffer: ShortArray, read: Int, sampleRate: Int): Double {
        var numCrossings = 0
        for (i in 1 until read) {
            if ((buffer[i - 1] > 0 && buffer[i] <= 0) ||
                (buffer[i - 1] < 0 && buffer[i] >= 0)
            ) {
                numCrossings++
            }
        }
        return (numCrossings * sampleRate / (2.0 * read))
    }



    //New Method of detecting

//    private fun startDetection() {
//        isDetecting = true
//        whistleCount = 0
////        binding.whistleCountText.text = "Whistle Count: 0"
//        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            Toast.makeText(requireActivity(), "Microphone permission required!", Toast.LENGTH_SHORT).show()
//            return
//        }
//        audioRecord = AudioRecord(
//            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
//            sampleRateNew,
//            AudioFormat.CHANNEL_IN_MONO,
//            AudioFormat.ENCODING_PCM_16BIT,
//            bufferSizeNew
//        )
//
//        if (NoiseSuppressor.isAvailable()) {
//            val ns = NoiseSuppressor.create(audioRecord!!.audioSessionId)
//            ns?.enabled = true
//        }
//
//        audioRecord?.startRecording()
//
//        recordThread = Thread {
//            val buffer = ShortArray(bufferSizeNew)
//
//            while (isDetecting && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
//                val read = audioRecord!!.read(buffer, 0, buffer.size)
//                if (read > 0) {
//                    val detected = detectWhistle(buffer)
//                    if (detected) {
//                        whistleCount++
//                        Handler(Looper.getMainLooper()).post {
//                            binding.whistleCountText.text = (binding.cookingAutoOffNumberPicker.value-whistleCount).toString()
//                        }
//                        Thread.sleep(2000)
//
//                    }
//                    if(whistleCount >=binding.cookingAutoOffNumberPicker.value){
//                        stopDetection()
//                        Handler(Looper.getMainLooper()).post {
//                            Toast.makeText(context, "Target reached, auto stop!", Toast.LENGTH_LONG).show()
//                            binding.whistleCountText.visibility = View.GONE
//                            binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                        }
//                        break
//                    }
//                }
//            }
//        }
//        recordThread?.start()
//    }
//
//    private fun stopDetection() {
//        isDetecting = false
//        audioRecord?.stop()
//        audioRecord?.release()
//        audioRecord = null
//        recordThread = null
//    }
//    private fun detectWhistle(buffer: ShortArray): Boolean {
//        val audioData = buffer.map { it.toDouble() }.toDoubleArray()
//        val fft = DoubleFFT_1D(audioData.size.toLong())
//
//        val fftData = DoubleArray(audioData.size * 2)
//        for (i in audioData.indices) {
//            fftData[2 * i] = audioData[i]
//            fftData[2 * i + 1] = 0.0
//        }
//        fft.complexForward(fftData)
//
//        var maxMag = 0.0
//        var maxIndex = 0
//        for (i in 0 until fftData.size / 2 step 2) {
//            val mag = sqrt(fftData[i].pow(2) + fftData[i + 1].pow(2))
//            if (mag > maxMag) {
//                maxMag = mag
//                maxIndex = i / 2
//            }
//        }
//
//        val frequency: Double = (maxIndex * sampleRateNew / audioData.size).toDouble()
//        return frequency in 1800.0..4000.0
//    }

    // MainActivity.kt (important parts)


//
//
//    fun startModel() {
//        classifier = AudioClassifier.createFromFile(requireActivity(), "whistle_model.tflite")
//        tensorAudio = classifier.createInputTensorAudio()
//        val format = classifier.requiredTensorAudioFormat
//        audioRecord = classifier.createAudioRecord()
//        audioRecord!!.startRecording()
//
//        // smoothing: sliding average of top-class score
//        val window = ArrayDeque<Float>()
//        val windowSize = 6 // e.g., average over last 6 inferences (3s if interval 500ms)
//        val threshold = 0.6f
//        var onCooldown = false
//
//        timer(period = 500) { // run every 500 ms
//            val num = tensorAudio.load(audioRecord) // loads latest samples into tensor
//            val output = classifier.classify(tensorAudio)
//            // output is list per timestamp chunk -> categories
//            val topScore = output[0].categories.maxByOrNull { it.score }?.score ?: 0f
//            synchronized(window) {
//                window.addLast(topScore)
//                if (window.size > windowSize) window.removeFirst()
//                val avg = window.average().toFloat()
//                if (avg > threshold && !onCooldown) {
//                    // Detected a whistle
//
//
//                   requireActivity().runOnUiThread {
//                        binding.whistleCountText.text ="${++whistleCount}" }
//                    onCooldown = true
//                    // cooldown to avoid double counting same whistle
//                    Handler(Looper.getMainLooper()).postDelayed({ onCooldown = false }, 1500)
//                }
//            }
//        }
//    }

//
//    fun startListening() {
//        audioRecordForSound.startRecording()
//
//        Thread {
//            while (true) {
//                tensorAudio.load(audioRecordForSound)
//                val results: List<Classifications> = classifier.classify(tensorAudio)
//
//                val categories = results[0].categories
//                val topResult = categories.maxByOrNull { it.score }
//
//                topResult?.let {
//                    val label = it.label.lowercase()
//                    val score = it.score
//                    when {
//                        label.contains("clap") && score > 0.5 -> {
//                            Log.d("SoundClassifier", "👏 Hand Clap Detected")
//                        }
//                        label.contains("whistle") && score > 0.5 -> {
//                            whistleCount++
//
//                            Log.d("SoundClassifier", "🔥 Cooker Whistle Detected")
//
//                            activity?.runOnUiThread {
//                                binding.whistleCountText.text =
//                                    (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
//
//                                if (whistleCount >= binding.cookingAutoOffNumberPicker.value) {
//                                    binding.whistleCountText.visibility = View.GONE
//                                    binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                                    binding.cookingTimerButton.text = "Start"
//                                }
//                            }
//                        }
//                        label.contains("music") && score > 0.5 -> {
//                            Log.d("SoundClassifier", "🎶 Music Playing")
//                        }
//                        else -> {
//                            if(label.equals("jet engine") || label.equals("tools")){
//                                whistleCount++
////                                binding.whistleCountText.text = (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
////                                Toast.makeText(this,"Cooker")
////                                Toast.makeText(context, "Whistle Detected!" + whistleCount, Toast.LENGTH_LONG).show()
//
//
//                                Log.d("SoundClassifier", "🔥 Cooker Whistle Detected")
//                                activity?.runOnUiThread {
//                                    binding.whistleCountText.text =
//                                        (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
//
//                                    if (whistleCount >= binding.cookingAutoOffNumberPicker.value) {
//                                        binding.whistleCountText.visibility = View.GONE
//                                        binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                                        binding.cookingTimerButton.text = "Start"
//                                    }
//                                }
//                            }else{
//                                Log.d("SoundClassifier", "Other sound: $label ($score)")
//                            }
//                        }
//                    }
//                }
//                Thread.sleep(500) // Check every half second
//            }
//        }.start()
//    }
    private var lastWhistleTime = 0L   // To track time of last whistle
//
//        fun startListeningNew() {
//            audioRecordForSound.startRecording()
//
//            Thread {
//                while (true) {
//                    tensorAudio.load(audioRecordForSound)
//                    val results: List<Classifications> = classifier.classify(tensorAudio)
//
//                    val categories = results[0].categories
//                    val topResult = categories.maxByOrNull { it.score }
//
//                    topResult?.let {
//                        val label = it.label.lowercase()
//                        val score = it.score
//                        val currentTime = System.currentTimeMillis()
//
//                        when {
//                            label.contains("clap") && score > 0.5 -> {
//                                Log.d("SoundClassifier", "👏 Hand Clap Detected")
//                            }
//
//                            // ✅ Check multiple labels that can represent cooker whistle
//                            (label.contains("whistle")
//                                    ||
//                                    label.contains("steam") || label.contains("tools")) && score > 0.5-> {
//
//                                // ✅ Only count if 50 seconds have passed since last one
//                                if (currentTime - lastWhistleTime > 50000) {
//                                    lastWhistleTime = currentTime
//                                    whistleCount++
//                                    Log.d("SoundClassifier", "🔥 Cooker Whistle Detected ($label, $score)")
//                                    Log.d("last Whistle time",  "$lastWhistleTime")
//
//
//                                    activity?.runOnUiThread {
//                                        binding.whistleCountText.text =
//                                            (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
//
//                                        if (whistleCount >= binding.cookingAutoOffNumberPicker.value) {
//                                            binding.whistleCountText.visibility = View.GONE
//                                            binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                                            binding.cookingTimerButton.text = "Start"
//                                        }
//                                    }
//                                } else {
//                                    Log.d("SoundClassifier", "🕒 Ignored whistle (cooldown active)")
//                                }
//                            }
//
//                            label.contains("music") && score > 0.5 -> {
//                                Log.d("SoundClassifier", "🎶 Music Playing")
//                            }
//
//                            else -> {
//                                Log.d("SoundClassifier", "Other sound: $label ($score)")
//                            }
//                        }
//                    }
//
//                    Thread.sleep(500)
//                }
//            }.start()
//        }

//    private var lastWhistleTime = 0L
    private var lastSoundTime = 0L
    private var isWhistleActive = false

//    fun startListeningNewLogic() {
//        audioRecordForSound.startRecording()
//
//        Thread {
//            while (true) {
//                tensorAudio.load(audioRecordForSound)
//                val results: List<Classifications> = classifier.classify(tensorAudio)
//
//                val categories = results[0].categories
//                val topResult = categories.maxByOrNull { it.score }
//
//                topResult?.let {
//                    val label = it.label.lowercase()
//                    val score = it.score
//                    val currentTime = System.currentTimeMillis()
//                    when {
//                        label.contains("clap") && score > 0.5 -> {
//                            Log.d("SoundClassifier", "👏 Hand Clap Detected")
//                        }
//                        label.contains("whistle") ||
//                                label.contains("steam") ||
//                                label.contains("tools") && score > 0.5 -> {
//                                 lastSoundTime = currentTime
//
//                            if (!isWhistleActive) {
//                                // ✅ First frame of a new whistle event
//                                isWhistleActive = true
//                                whistleCount++
//                                lastWhistleTime = currentTime
//                                Log.d("SoundClassifier", "🔥 New Cooker Whistle Detected ($label, $score)")
//
//                                activity?.runOnUiThread {
//                                    binding.whistleCountText.text =
//                                        (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
//
//                                    if (whistleCount >= binding.cookingAutoOffNumberPicker.value) {
//                                        binding.whistleCountText.visibility = View.GONE
//                                        binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                                        binding.cookingTimerButton.text = "Start"
//                                    }
//                                }
//                            } else {
//                                // 🔁 Still the same whistle (continuing sound)
//                                Log.d("SoundClassifier", "➡ Continuing same whistle ($label)")
//                            }
//                        }
//
//                        else -> {
//                            // 🕒 No whistle detected — check if whistle ended
//                            if(isWhistleActive && currentTime - lastSoundTime > 3000) {
//                                // if 3 seconds of silence passed
//                                isWhistleActive = false
//                                Log.d("SoundClassifier", "✅ Whistle ended, waiting for next one")
//                            }else{
//
//                            }
//                        }
//                    }
//                }
//
//                Thread.sleep(500)
//            }
//        }.start()
//    }
//

    private var whistleEndTime = 0L

//    fun startListeningNewLogicWithEndTime() {
//        audioRecordForSound.startRecording()
//
//        Thread {
//            while (true) {
//                tensorAudio.load(audioRecordForSound)
//                val results: List<Classifications> = classifier.classify(tensorAudio)
//                val categories = results[0].categories
//                val topResult = categories.maxByOrNull { it.score }
//
//                topResult?.let {
//                    val label = it.label.lowercase()
//                    val score = it.score
//                    val currentTime = System.currentTimeMillis()
//
//                    val isWhistleLabel =
//                        label.contains("whistle") ||
//                                label.contains("steam") ||
//                                label.contains("tools")
//
//                    when {
//                        label.contains("clap") && score > 0.5 -> {
//
//                            Log.d("SoundClassifier", "👏 Hand Clap Detected")
//                        }
//
//                        isWhistleLabel && score > 0.5 -> {
//                            lastSoundTime = currentTime
//
//                            // 🔒 allow new whistle only if no whistle active AND at least 5 s since last ended
//                            if (!isWhistleActive && (currentTime - whistleEndTime > 5000)) {
//                                isWhistleActive = true
//                                whistleCount++
//                                lastWhistleTime = currentTime
//                                Log.d("SoundClassifier", "🔥 New Cooker Whistle Detected ($label, $score)")
//
//
//                                activity?.runOnUiThread {
//
//                                    if (whistleCount >= binding.cookingAutoOffNumberPicker.value) {
//                                        binding.whistleCountText.visibility = View.GONE
//                                        binding.cookingAutoOffNumberPicker.visibility = View.VISIBLE
//                                        binding.cookingTimerButton.text = "Start"
//                                    }
//                                }
//                            } else {
//                                Log.d("SoundClassifier", "➡ Continuing same whistle ($label)")
//                            }
//                        }
//                        else -> {
//                            // 🕒 consider whistle ended only if 2 s of silence passed
//                            if (isWhistleActive && currentTime - lastSoundTime > 5000) {
//                                isWhistleActive = false
//                                whistleEndTime = currentTime
//                                activity?.runOnUiThread {
//                                    Toast.makeText(context, "Whistle counted" + whistleCount, Toast.LENGTH_LONG).show()
//
//
//                                    binding.whistleCountText.text = (binding.cookingAutoOffNumberPicker.value - whistleCount).toString()
//
//                                }
//                                Log.d("SoundClassifier", "✅ Whistle ended, ready for next")
//                            }else{
//
//                            }
//                        }
//                    }
//                }
//                Thread.sleep(500)
//            }
//        }.start()
//    }
//    fun stopListening() {
//        audioRecordForSound!!.stop()
//    }


    //Speech Recognition Code

    override fun onPause() {
        super.onPause()
        // Stop listening when the fragment is paused
        stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the recognizer instance
        speechRecognizer?.destroy()
        speechRecognizer = null
        _binding = null
    }

    // --- Core Logic ---

    private fun checkAudioPermissionForWhistle(): Boolean =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED



    private fun prepareRecognizer() {
        // FIX: Use the system default, most reliable SpeechRecognizer.createSpeechRecognizer()
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext()).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {

                    }
                    override fun onBeginningOfSpeech() {

                    }
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onError(error: Int) {
                        // Crucial: check for permission error here
                        if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
//                            binding.tvPartial.text = getErrorText(error)
                            stopListening() // Stop if permission is lost
                            return
                        }

//                        binding.tvPartial.text = "Error: ${getErrorText(error)}"
                        if (shouldRestart) {
                            // Delay restart to avoid immediate crash loop
                            handler.postDelayed({ startListening() }, 500)
                        } else {
                            // If not in continuous mode, stop the toggle button
//                            binding.toggleListening.isChecked = false
                        }
                    }
                    override fun onResults(results: Bundle?) {
                        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull().orEmpty()
//                        binding.tvFinal.text = text

                        // Restart for continuous listening
                        if (shouldRestart) {
                            startListening()
                        } else {
//                            binding.toggleListening.isChecked = false
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
//                        binding.tvPartial.text = partialResults
//                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                            ?.firstOrNull() ?: ""
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    private fun createRecognizerIntent() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    private fun startListening() {
        if (!checkRecognizerAvailable()) return

        shouldRestart = true
        prepareRecognizer()
        try {
            speechRecognizer?.startListening(createRecognizerIntent())

        } catch (e: Exception) {

        }
    }

    private fun stopListening() {
        shouldRestart = false
        // Using cancel() or destroy() is better than stopListening() for a clean shutdown
        speechRecognizer?.cancel()

    }

    // --- Utility Function ---

    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error (e.g., failed to start recognition)"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "No mic permission. Please check settings."
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "Didn't catch that"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
            else -> "Unknown error ($errorCode)"
        }
    }

}

private fun RelativeLayout.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

