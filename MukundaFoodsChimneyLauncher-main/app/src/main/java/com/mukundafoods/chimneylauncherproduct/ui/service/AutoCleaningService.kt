package com.mukundafoods.chimneylauncherproduct.ui.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import java.util.Locale
import java.util.concurrent.TimeUnit

open class AutoCleaningService : Service(

) {

    private lateinit var timer: CountDownTimer

    // Binder given to clients.
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): AutoCleaningService = this@AutoCleaningService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun startTimer(time: Long, countDownListener: (String) -> Unit, countDownFinishListener : () -> Unit){
        timer = createTimer(time, countDownListener, countDownFinishListener)
        timer.start()
    }

    fun stopTimer(){
        if(::timer.isInitialized)
        timer.cancel()
    }

    override fun onDestroy() {
        if(::timer.isInitialized)
            timer.cancel()
    }

    private fun createTimer(time: Long, countDownListener: (String) -> Unit, countDownFinishListener: () -> Unit): CountDownTimer =
        object : CountDownTimer(time, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val text = String.format(
                    Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                )
                countDownListener(text)
            }

            override fun onFinish() {
                countDownFinishListener()
                stopSelf() // Stop the service within itself NOT the activity
            }
        }

    companion object {
        private val COUNTDOWN_INTERVAL = TimeUnit.SECONDS.toMillis(1)
    }
}