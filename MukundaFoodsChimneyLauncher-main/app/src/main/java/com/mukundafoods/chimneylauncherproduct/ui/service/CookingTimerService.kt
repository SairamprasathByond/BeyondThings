package com.mukundafoods.chimneylauncherproduct.ui.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import java.util.concurrent.TimeUnit

open class CookingTimerService : Service(

) {

    private lateinit var timer: CountDownTimer

    // Binder given to clients.
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): CookingTimerService = this@CookingTimerService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun startTimer(time: Long, countDownListener: (Long, Long) -> Unit, countDownFinishListener : () -> Unit){
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

    private fun createTimer(time: Long, countDownListener: (Long, Long) -> Unit, countDownFinishListener: () -> Unit): CountDownTimer =
        object : CountDownTimer(time, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                countDownListener(minute, second)
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