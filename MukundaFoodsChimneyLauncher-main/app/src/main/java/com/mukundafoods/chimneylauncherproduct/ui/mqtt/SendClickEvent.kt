package com.mukundafoods.chimneylauncherproduct.ui.mqtt

import android.os.Build
import android.provider.Settings
import com.google.gson.Gson
import com.mukundafoods.chimneylauncherproduct.BuildConfig
import com.mukundafoods.chimneylauncherproduct.MyApplication.Companion.context
import com.mukundafoods.chimneylauncherproduct.Variants
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data

class SendClickEvent {


    private val topic = "clickEvent/${getDeviceId()}"


    fun sendClickPacket(screen: String, clickEvent: String, subEvent: String) {
        if(Variants.IS_MQTT_SUPPORTED){
            val mqttClickEvent = MqttClickEvent(
                time = System.currentTimeMillis(),
                sln = getDeviceId(),
                lon = Data.getLatLong(),
                sv = BuildConfig.VERSION_NAME,
                hb = Data.getUsbConnectionStatus(),
                screen = screen,
                clickEvent = clickEvent,
                subEvent = subEvent
            )

            val gson = Gson()
            val machinePacket = gson.toJson(mqttClickEvent)
            MQTTConnection.getInstance().publishMessage(topic, machinePacket)
        }
    }
    fun getDeviceSerial(): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            } else {
                Build.SERIAL
            }
        } catch (e: SecurityException) {
            "Permission denied"
        } catch (e: Exception) {
            "Unknown error"
        }
    }
    fun getDeviceId(): String {
        return Settings.Secure.getString(context .contentResolver, Settings.Secure.ANDROID_ID)
    }
}