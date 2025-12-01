package com.mukundafoods.chimneylauncherproduct

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
//import com.bumptech.glide.Priority
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
//import com.google.zxing.client.android.BuildConfig
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort

import com.hoho.android.usbserial.driver.UsbSerialProber

import com.mukundafoods.chimneylauncherproduct.ui.MyViewModelFactory
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.backend.RetrofitService
import com.mukundafoods.chimneylauncherproduct.ui.chimney.ChimneyControllerViewModel
import com.mukundafoods.chimneylauncherproduct.ui.cleaningfragment.CleaningFragment
import com.mukundafoods.chimneylauncherproduct.ui.cleaningfragment.CleaningViewModel
import com.mukundafoods.chimneylauncherproduct.ui.database.ChimneyDatabaseBuilder
import com.mukundafoods.chimneylauncherproduct.ui.database.ChimneyDatabaseHelperImpl
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.MQTTConnection
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.MQTTConnectionManager
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.MQTTConstants
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.SendClickEvent
import com.mukundafoods.chimneylauncherproduct.ui.profilefragment.ProfileFragment
import com.mukundafoods.chimneylauncherproduct.ui.settings.SettingsType
import com.mukundafoods.chimneylauncherproduct.ui.settings.chimneysettings.ChimneySettingsFragment
import com.mukundafoods.chimneylauncherproduct.ui.settings.helpcenter.HelpCenterFragment
import com.mukundafoods.chimneylauncherproduct.ui.settings.qrcodetesting.QrCodeTestingActivity
import com.mukundafoods.chimneylauncherproduct.ui.settings.settingsfragment.SettingsFragment
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.CustomProber
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.DeviceItem
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.SerialService
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.SerialSocket
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.USBConnection
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.USBConnectionManager
import com.mukundafoods.chimneylauncherproduct.ui.ymodule.Utils
import com.mukundafoods.chimneylauncherproduct.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), ServiceConnection {

    private lateinit var binding: ActivityMainBinding

    // Micro controller code start
    private enum class Connected {
        False, Pending, True
    }

    val INTENT_ACTION_GRANT_USB: String = BuildConfig.APPLICATION_ID + ".GRANT_USB"
    private var deviceId = 0
    private var portNum = 0
    private var service: SerialService? = null
    private var initialStart = true
    private var connected = Connected.False
    private var broadcastReceiver: BroadcastReceiver? = null

    private var usbPermissionReceiver : BroadcastReceiver? =null
    private val baudRate = 115200
    private lateinit var chimneyViewModel: ChimneyControllerViewModel
    private lateinit var cleaningViewModel: CleaningViewModel
    private lateinit var cleaningDialog: Dialog
    private lateinit var lpgReminderDialog: Dialog
    private var handler = Handler()

    private val ACTION_USB_PERMISSION = "com.mukundafoods.chimneylauncher.USB_PERMISSION"
    private var usbSerialPort: UsbSerialPort? = null
    private lateinit var chimneyDatabaseHelper: ChimneyDatabaseHelperImpl

    // Micro controller code end
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chimneyViewModel = ViewModelProvider(
            this, MyViewModelFactory(
                MainRepository(
                    RetrofitService.getInstance()
                )
            )
        ).get(
            ChimneyControllerViewModel::class.java
        )
        instance = this
        this.onBackPressedDispatcher.addCallback { }
        cleaningViewModel = ViewModelProvider(this).get(CleaningViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cleaningDialog = Dialog(this@MainActivity)
        lpgReminderDialog = Dialog(this@MainActivity)
        bindService(Intent(this@MainActivity, SerialService::class.java), this, BIND_AUTO_CREATE)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            registerBroadcastfor15()
//            // Android 12 (API 31) and above
//            Log.d("VersionCheck", "Android 12 or higher")
//        } else {
//            registerBroadcast()
//
//            // Android 11 (API 30) and below
//            Log.d("VersionCheck", "Android 11 or lower")
//        }
        registerBroadcast()

        renderLayout()

        binding.timerLayout.setOnClickListener {
            it.visibility = View.GONE
            startCountDownTimer()
        }
        chimneyDatabaseHelper =
            ChimneyDatabaseHelperImpl(ChimneyDatabaseBuilder.getInstance(this?.applicationContext!!))

        binding.frameContainer.setOnTouchListener { v, event ->
            startCountDownTimer()
            return@setOnTouchListener false
        }
        binding.wifi.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.MAIN_ACTIVITY_SCREEN_NAME,
                MQTTConstants.MAIN_ACTIVITY_SCREEN_WIFI,
                "wifi click"
            )
            startActivityForResult(Intent(Settings.ACTION_WIFI_SETTINGS), 0)
        }
        binding.usb.isSelected = false

        binding.backButtonLayout.setOnClickListener {
            supportFragmentManager.popBackStack()
            startCountDownTimer()
        }
        updateTime()
        clean()
        settings()
        profile()
        beyondAppliance()

        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                binding.wifi.isSelected = true
                SendClickEvent().sendClickPacket(
                    MQTTConstants.MAIN_ACTIVITY_SCREEN_NAME,
                    MQTTConstants.MAIN_ACTIVITY_SCREEN_WIFI,
                    "wifi on"
                )
            }

            override fun onLost(network: Network) {
                binding.wifi.isSelected = false
                SendClickEvent().sendClickPacket(
                    MQTTConstants.MAIN_ACTIVITY_SCREEN_NAME,
                    MQTTConstants.MAIN_ACTIVITY_SCREEN_WIFI,
                    "wifi off"
                )
            }
        })
        if (Variants.IS_MQTT_SUPPORTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            MQTTConnectionManager.setMQTTConnectionManager(
                MQTTConnection.getInstance(),
                mqttConnectionListener
            )
        }
    }

    private fun startCountDownTimer() {
        showTimerLayout(View.GONE)
        handler.removeCallbacksAndMessages(null)

        handler.postDelayed({
            val fragment = supportFragmentManager.findFragmentById(binding.frameContainer.id)
            if (fragment == null) {
                showTimerLayout(View.VISIBLE)
            }

        }, (Data.getDisplayTimeOut() * 1000 * 60).toLong())
    }

    private fun showTimerLayout(visibility: Int) {
        binding.timerLayout.visibility = visibility
    }

    private fun beyondAppliance() {
        binding.beyondAppliances.setOnClickListener {
            supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            );
        }
    }

    private fun openSettingsFragment(type: SettingsType) {
        binding.currentTime.text = "Back"
        when (type) {
            SettingsType.CHIMNEY_SETTINGS -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(
                    binding.frameContainer.id,
                    ChimneySettingsFragment()
                )
                transaction.addToBackStack("ChimneySettings")
                transaction.commit()

            }
            SettingsType.HELP_CENTER -> {

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(
                    binding.frameContainer.id,
                    HelpCenterFragment()
                )
                transaction.addToBackStack("HelpCenter")
                transaction.commit()
            }

            SettingsType.QR_CODE_TESTING -> {
                startActivity(Intent(this, QrCodeTestingActivity::class.java))
            }
        }
    }

    private fun updateTime() {
        CoroutineScope(context = Dispatchers.IO).launch {
            delay(1000)
            CoroutineScope(Dispatchers.Main).launch {
                val simpleTimeFormat = SimpleDateFormat("hh:mm aa")
                val simpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy")
                val calendar = Calendar.getInstance()
                if (supportFragmentManager.backStackEntryCount == 0) {
                    binding.currentTime.text = simpleTimeFormat.format(calendar.time)
                    val list =
                        simpleTimeFormat.format(calendar.time).toString().split(" ")[0].split(":")
                    binding.hh.text = list[0]
                    binding.mm.text = list[1]
                    binding.amPm.text =
                        simpleTimeFormat.format(calendar.time).toString().split(" ")[1]
                    binding.timerLayoutDate.text = "" + simpleDateFormat.format(calendar.time)
                } else {
                    binding.currentTime.text = "Back"
                }
                if (Data.getLpgReminderLong() != 0L) {
                    val daysRemaining =
                        TimeUnit.MILLISECONDS.toDays(Data.getLpgReminderLong() - calendar.time.time)
                    //     println("Diff Days Main Activity $daysRemaining")
                    if (daysRemaining <= 5 && !Data.isLpgReminderShown()) {
                        showLpgReminderDialog()
                    }
                }
                updateTime()
            }
        }
    }

    private fun clean() {
        binding.clean.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.MAIN_ACTIVITY_SCREEN_NAME,
                MQTTConstants.MAIN_ACTIVITY_SCREEN_CLEAN,
                "clean"
            )
            launchCleaningScreen()
        }
    }

    private fun launchCleaningScreen() {
        val fragment = supportFragmentManager.findFragmentById(binding.frameContainer.id)
        if (fragment is CleaningFragment) {
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.frameContainer.id, CleaningFragment())
        transaction.addToBackStack("CleaningFragment")
        transaction.commit()
    }

    private fun profile() {
        binding.profile.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.MAIN_ACTIVITY_SCREEN_NAME,
                MQTTConstants.MAIN_ACTIVITY_SCREEN_ACCOUNT,
                "account"
            )
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(binding.frameContainer.id, ProfileFragment())
            transaction.addToBackStack("ProfileFragment")
            transaction.commit()
        }
    }

    private fun settings() {
        binding.settings.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.MAIN_ACTIVITY_SCREEN_NAME,
                MQTTConstants.MAIN_ACTIVITY_SCREEN_SETTINGS,
                "settings"
            )
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(
                binding.frameContainer.id,
                SettingsFragment.newInstance { SettingsType -> openSettingsFragment(SettingsType) })
            transaction.addToBackStack("SettingsFragment")
            transaction.commit()
        }
    }

    private fun renderLayout() {
        binding.viewPager.adapter = PagerAdapter(supportFragmentManager)
        binding.viewPager.offscreenPageLimit = 3
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.home_screen)
        binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.app_logo_more)
        binding.tabLayout.getTabAt(2)?.setIcon(R.drawable.chef_connect_image)
    }

    // MC code
    private fun registerBroadcast() {

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == INTENT_ACTION_GRANT_USB) {
                    val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    connect(granted)
                }
            }
        }
    }

    private fun registerBroadcastfor15(){
        usbPermissionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ACTION_USB_PERMISSION) {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)

                    if (granted && device != null) {
                        var driver = UsbSerialProber.getDefaultProber().probeDevice(device)
                        if (driver == null) {
                            driver = CustomProber.getCustomProber().probeDevice(device)
                        }
                        if (driver != null) {
                            usbSerialPort = driver.ports[portNum]
                            openSerialPortForAndroid15(device, driver)
                        } else {
                            status("Permission granted but driver not found")
                        }
                    } else {
                        status("USB permission denied")
                    }
                }
            }
        }
    }




    override fun onResume() {
        super.onResume()
        if (Variants.IS_MQTT_SUPPORTED)
            setUpMQTT()



        registerReceiver(broadcastReceiver, IntentFilter(INTENT_ACTION_GRANT_USB), Context.RECEIVER_NOT_EXPORTED)

//        registerReceiver(broadcastReceiver, IntentFilter(ACTION_USB_PERMISSION), Context.RECEIVER_NOT_EXPORTED)


        val filter = IntentFilter(ACTION_USB_PERMISSION)
//        registerReceiver(usbPermissionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)

        startCountDownTimer()
        refresh()
        if (initialStart && service != null) {
            initialStart = false
            runOnUiThread { this.connect() }
        }

        if (!com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data.isCleaningDone()) {
            showCleaningDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        service?.attach(USBConnection.getInstance())
            ?: startService(
                Intent(
                    this@MainActivity,
                    SerialService::class.java
                )
            ) // prevents service destroy on unbind from recreated activity caused by orientation change

//        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
//        val device = usbManager.deviceList.values.find { it.vendorId == 0x0483 }
//        if (device != null) {
//            deviceId = device.deviceId
//            connectfor15()
//        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        onDestroy()
        service = null
    }


    private fun disconnect() {
        connected = Connected.False
        if (service != null) {
            service!!.disconnect()
        }
    }
    fun status(str: String) {
        Log.d("Mahika", " RESULT =$str")
    }

    override fun onNewIntent(intent: Intent?) {
        if (intent != null && intent.action != null && intent.action == "android.hardware.usb.action.USB_DEVICE_ATTACHED") {
            if (connected != Connected.True && service != null) connect()
        }
        super.onNewIntent(intent)
    }

    fun refresh() {
        val usbManager = this.getSystemService(USB_SERVICE) as UsbManager
        val usbDefaultProber = UsbSerialProber.getDefaultProber()
        val usbCustomProber: UsbSerialProber = CustomProber.getCustomProber()
        for (device in usbManager.deviceList.values) {
            var driver = usbDefaultProber.probeDevice(device)
            if (driver == null) {
                driver = usbCustomProber.probeDevice(device)
            }
            if (driver != null) {
                val deviceItem = DeviceItem(device, 0, driver)
                deviceId = deviceItem.device.deviceId
                portNum = deviceItem.port
                connect()
            } else {
                Toast.makeText(this@MainActivity, "No Device Found", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
        service = (binder as SerialService.SerialBinder).service
        if (initialStart && this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            initialStart = false
            runOnUiThread { this.connect() }
        }
    }

    private fun updateToolBarColor(status: Int) {
        // 0 is connected
        //1 is disconnected
        Data.setUsbConnectionStatus(status == 0)
        binding.usb.isSelected = status == 0
        SendClickEvent().sendClickPacket(
            MQTTConstants.MAIN_ACTIVITY_SCREEN_NAME,
            MQTTConstants.MAIN_ACTIVITY_SCREEN_USB,
            if (status == 0) "connected" else "disconnected"
        )
    }

    override fun onPause() {
        super.onPause()
        if (broadcastReceiver != null) unregisterReceiver(broadcastReceiver)
        println("Main Activity On Pause")

        if(usbPermissionReceiver != null){
            unregisterReceiver(usbPermissionReceiver)
        }
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        if (service != null && !isChangingConfigurations) service!!.detach()
        super.onStop()
    }

    override fun onDestroy() {
        unbindService(this)
        if (connected != Connected.False) disconnect()

        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver)
        }
        stopService(Intent(this@MainActivity, SerialService::class.java))
        super.onDestroy()
    }

    /*
     * Serial + UI
     */
    fun connect() {
        if (service != null)

            connect(null)
    }

//
     fun connect(permissionGranted: Boolean?) {
        status("connect")
        var device: UsbDevice? = null
        val usbManager = getSystemService(USB_SERVICE) as UsbManager
        for (v in usbManager.deviceList.values) if (v.deviceId == deviceId) device = v
        if (device == null) {
            status("connection failed: device not found")
            return
        }
        var driver = UsbSerialProber.getDefaultProber().probeDevice(device)
        if (driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device)
        }
        if (driver == null) {
            status("connection failed: no driver for device")
            return
        }
        if (driver.ports.size < portNum) {
            status("connection failed: not enough ports at device")
            return
        }
        val usbSerialPort = driver.ports[portNum]

        if (!usbManager.hasPermission(
                driver.device
            )
        ) {
            val usbPermissionIntent = PendingIntent.getBroadcast(
                this@MainActivity,
                0,
                Intent(INTENT_ACTION_GRANT_USB),
                PendingIntent.FLAG_IMMUTABLE
            )
            usbManager.requestPermission(driver.device, usbPermissionIntent)
            return
        }
//     usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
    val connection = usbManager.openDevice(device)

    if (connection == null) {
        status("Connection failed: Cannot open device")
        return
    }
    connected = Connected.Pending
    USBConnection.getInstance().disconnect()
    val socket = SerialSocket()
    service?.connect(USBConnection.getInstance(), "Connected")

    socket.connect(this, service!!, connection, usbSerialPort, baudRate)

    updateToolBarColor(0)
    USBConnection.getInstance().setSocket(socket)
    USBConnectionManager.setConnectionManager(USBConnection.getInstance(), connectionManager)

//    openSerialPort(device,driver)
//    openSerialPortForAndroid15(device,driver)

    }


    //android15forconnect




//    fun connect for android 15
     fun connectfor15(){
        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        val device = usbManager.deviceList.values.find { it.deviceId == deviceId }

        if (device == null) {
            status("Connection failed: Device not found")
            return
        }

        var driver = UsbSerialProber.getDefaultProber().probeDevice(device)
        if (driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device)
        }

        if (driver == null) {
            status("Connection failed: No driver for device")
            return
        }

        if (driver.ports.size <= portNum) {
            status("Connection failed: Not enough ports")
            return
        }

        usbSerialPort = driver.ports[portNum]

        if (!usbManager.hasPermission(device)) {
            val permissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE
            )
            usbManager.requestPermission(device, permissionIntent)
            return // Wait for broadcast before continuing
        }

        openSerialPortForAndroid15(device, driver)
    }




    //for testing andrid 15

    private fun openSerialPortForAndroid15(device: UsbDevice, driver: UsbSerialDriver) {
        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        val connection = usbManager.openDevice(device)

        if (connection == null) {
            status("Connection failed: Cannot open device")
            return
        }

        try {
            usbSerialPort?.open(connection)
            usbSerialPort?.setParameters(
                baudRate,
                8,
                UsbSerialPort.STOPBITS_1,
                UsbSerialPort.PARITY_NONE
            )

            status("Connected to STM32")

            // Set up SerialSocket if needed
            connected = Connected.Pending
            USBConnection.getInstance().disconnect()
            val socket = SerialSocket()
            service?.connect(USBConnection.getInstance(), "Connected")

            socket.connect(this, service!!, connection, usbSerialPort, baudRate)

            updateToolBarColor(0)
            USBConnection.getInstance().setSocket(socket)
            USBConnectionManager.setConnectionManager(USBConnection.getInstance(), connectionManager)

        } catch (e: IOException) {
            status("Connection failed: ${e.message}")
            e.printStackTrace()
            try {
                usbSerialPort?.close()
            } catch (_: Exception) {}
        }
    }
    private val connectionManager: USBConnectionManager.USBConnectionListener =
        object : USBConnectionManager.USBConnectionListener {
            override fun onConnected() {
                runOnUiThread {
                    updateToolBarColor(0)
                    Utils.writeHandshakePacket()
                    Utils.chimneySettings(Constants.speed[com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data.getCleaningScheduled() - 1].toInt())
                }
            }

            override fun onDisconnected() {
                runOnUiThread {
                    updateToolBarColor(1)
                    chimneyViewModel.setIsDisconnected(true)
                }
            }

            override fun onDataReceived(data: ByteArray) {
                data.forEach {
                    println("Narayan On Data Received $it")
                }
                println("--------")
                if (data[1].toInt() == 7 || data[1].toInt() == 6) {
                    if (data[1].toInt() == 7 && data[2].toInt() == 1) {
                        enableBackButton(false)
                    }
                    if (data[1].toInt() == 7 && data[2].toInt() == 3) {
                        Data.setIsCleaningDone(
                            false
                        )
                        checkIfChimneyScreenIsLaunchedIfNotThenShowCleaningDialog()
                    }
                    if (data[1].toInt() == 7 && data[2].toInt() == 2) {
                        enableBackButton(true)
                        Data.setIsCleaningDone(
                            true
                        )
                        Data.setLaterCount(0)
                    }
                    cleaningViewModel.setPacket(data)
                } else {
                    chimneyViewModel.setPacket(data)
                }
            }
        }

    private fun checkIfChimneyScreenIsLaunchedIfNotThenShowCleaningDialog() {
        val fragment = supportFragmentManager.findFragmentById(binding.frameContainer.id)
        if (fragment is CleaningFragment) {
            Log.d("Main Activity", "Cleaning Fragemnt is Already opened so do nothing")
        } else {
            showCleaningDialog()
        }
    }

    private fun showCleaningDialog() {
        if (cleaningDialog.isShowing) {
            return
        }
        cleaningDialog.setContentView(R.layout.cleaning_dialog)
        cleaningDialog.window!!.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cleaningDialog.setCancelable(false)
        cleaningDialog.window!!.attributes.windowAnimations = R.style.animation

        val okayText = cleaningDialog.findViewById<View>(R.id.okay_text) as TextView
        val cancelText = cleaningDialog.findViewById<View>(R.id.cancel_text) as TextView

        if (com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data.getLaterCount() >= 3) {
            cancelText.isEnabled = false
            cancelText.setTextColor(Color.LTGRAY)
        } else {
            cancelText.isEnabled = true
            cancelText.setTextColor(Color.RED)
        }


        okayText.setOnClickListener {
            cleaningDialog.dismiss()
            launchCleaningScreen()
        }

        cancelText.setOnClickListener {
            var value =
                com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data.getLaterCount() + 1
            Data.setLaterCount(value)
            cleaningDialog.dismiss()
        }

        cleaningDialog.show()
    }

    private fun showLpgReminderDialog() {
        if (lpgReminderDialog.isShowing) {
            return
        }

        lpgReminderDialog.setContentView(R.layout.lpg_reminder_dialog)
        lpgReminderDialog.window!!.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lpgReminderDialog.setCancelable(false)
        lpgReminderDialog.window!!.attributes.windowAnimations = R.style.animation

        val okayText = lpgReminderDialog.findViewById<View>(R.id.okay_text) as TextView

        /* if (com.mukundafoods.chimneylauncher.ui.sharedpreference.Data.getLaterCount() >= 3) {
             cancelText.isEnabled = false
             cancelText.setTextColor(Color.LTGRAY)
         } else {
             cancelText.isEnabled = true
             cancelText.setTextColor(Color.RED)
         }*/


        okayText.setOnClickListener {
            lpgReminderDialog.dismiss()
            Data.setIsLpgReminderShown(true)
            //launchCleaningScreen()
        }

        /*   cancelText.setOnClickListener {
               lpgReminderDialog.dismiss()
           }*/

        try {
            if (this != null && !this.isFinishing) {
                lpgReminderDialog.show()
            } else {
                Log.d("MainActivity", "Cannot Show LPG Dialog")
            }
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }

    }

    private fun enableBackButton(enabled: Boolean) {
        binding.backButtonLayout.isEnabled = enabled
        binding.beyondAppliances.isEnabled = enabled
        binding.wifi.isEnabled = enabled
        binding.settings.isEnabled = enabled
        binding.profile.isEnabled = enabled
        binding.clean.isEnabled = enabled
    }

    @Synchronized
    private fun setUpMQTT() {
        if (!MQTTConnection.getInstance().isMQTTConnected()) {
            MQTTConnection.getInstance().setUpMQTTClient(this@MainActivity, chimneyDatabaseHelper)
            MQTTConnection.getInstance().connectToMQTTServer()
        }
    }

    private val mqttConnectionListener: MQTTConnectionManager.MQTTConnectionListener =
        object : MQTTConnectionManager.MQTTConnectionListener {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun onConnected() {
                Log.d("MainActivity", "MainActivity :: MQTT onConnected")
                getLastLocation()
                /*   MachinePacketInRepeat.INSTANCE.startSendingMachinePacket()*/
            }

            override fun onDisconnected() {
//                getLastLocation()

                Log.d("MainActivity", "MainActivity :: MQTT on-Disconnected")
                //    MachinePacketInRepeat.INSTANCE.stopSendingMachinePacket()
            }

            override fun onDataReceived(topic: String, data: String) {
                Log.d("MainActivity", "MainActivity ::MQTT data received =$topic->$data")
            }
        }


    // location
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            Log.d("MainActivity", "MainActivity ::checkPermissions")
            if (isLocationEnabled()) {
                Log.d("MainActivity", "MainActivity ::isLocationEnabled")
                fusedLocationProviderClient?.lastLocation?.addOnCompleteListener { task ->
                    if (task?.result == null) {
                        Log.d("MainActivity", "MainActivity Location task in Null")
                        return@addOnCompleteListener
                    }
                    val location: Location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Data.setLatLong(" ${location.latitude} , ${location.longitude}")
                    }
                }
            } else {
//                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show()
//                requestPermissions()

//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        // Initializing LocationRequest
        // object with appropriate methods
//        val mLocationRequest = Loca.Builder(
//            Priority.PRIORITY_HIGH_ACCURACY, 5000L
//        )
//            .setWaitForAccurateLocation(true)
//            .setMinUpdateIntervalMillis(1000)
//            .setMaxUpdateDelayMillis(5000)
//            .build()

        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(5)
        mLocationRequest.setFastestInterval(0)
        mLocationRequest.setNumUpdates(1)

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
        /*   fusedLocationProviderClient.requestLocationUpdates(
               mLocationRequest,
               mLocationCallback,
               Looper.myLooper()
           )*/
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location = locationResult.getLastLocation()
        }
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf<String>(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    var PERMISSION_ID: Int = 44

    // If everything is alright then
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }
    companion object {
        var instance: MainActivity? = null
    }

}