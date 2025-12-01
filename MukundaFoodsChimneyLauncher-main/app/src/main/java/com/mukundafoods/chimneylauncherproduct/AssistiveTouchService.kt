package com.mukundafoods.chimneylauncherproduct

//package com.mukundafoods.sampleappforassistivetouch

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.view.*
import android.view.accessibility.AccessibilityEvent
import com.mukundafoods.chimneylauncherproduct.databinding.AssistiveBubbleBinding
import com.mukundafoods.chimneylauncherproduct.databinding.AssistiveTouchBinding
//import com.mukundafoods.sampleappforassistivetouch.databinding.AssistiveBubbleBinding
//import com.mukundafoods.sampleappforassistivetouch.databinding.AssistiveTouchBinding
//import com.mukundafoods.sampleappforassistivetouch.databinding.AssistiveTouchBubbleBinding
//import com.mukundafoods.sampleappforassistivetouch.databinding.AssistiveTouchMenuBinding

class AssistiveTouchService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private lateinit var bubbleBinding: AssistiveBubbleBinding
    private lateinit var menuBinding: AssistiveTouchBinding
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var menuParams: WindowManager.LayoutParams

    override fun onServiceConnected() {
        super.onServiceConnected()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Inflate bubble & menu using ViewBinding
        bubbleBinding = AssistiveBubbleBinding.inflate(LayoutInflater.from(this))
        menuBinding = AssistiveTouchBinding.inflate(LayoutInflater.from(this))

        // Window params for bubble
        bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        bubbleParams.gravity = Gravity.CENTER or Gravity.START

        // Window params for menu
        menuParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        menuParams.gravity = Gravity.CENTER or Gravity.START

        // Add bubble initially
        windowManager.addView(bubbleBinding.root, bubbleParams)

        setupBubble()
        setupMenu()
    }

    private fun setupBubble() {
        val btnBubble = bubbleBinding.btnBubble

        btnBubble.setOnClickListener {
            // Remove bubble, show menu
            windowManager.removeView(bubbleBinding.root)
            windowManager.addView(menuBinding.root, menuParams)
        }

        // Drag bubble
        bubbleBinding.root.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = bubbleParams.x
                        initialY = bubbleParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(bubbleBinding.root, bubbleParams)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun setupMenu() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        menuBinding.apply {
            btnVolumeUp.setOnClickListener {
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
//                collapseMenu()
            }

            btnVolumeDown.setOnClickListener {
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
//                collapseMenu()
            }

            btnWifi.setOnClickListener {
                wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
//                collapseMenu()
            }

            btnHome.setOnClickListener {
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
//                collapseMenu()
            }

            btnRecent.setOnClickListener {
                performGlobalAction(GLOBAL_ACTION_RECENTS)
//                collapseMenu()
            }
        }
    }

    private fun collapseMenu() {
        if (menuBinding.root.parent != null) {
            windowManager.removeView(menuBinding.root)
        }
        if (bubbleBinding.root.parent == null) {
            windowManager.addView(bubbleBinding.root, bubbleParams)
        }
    }

    //    override fun onAccessibilityEvent(event: android.view.accessibilityservice.AccessibilityEvent?) {}
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onInterrupt() {}
}
