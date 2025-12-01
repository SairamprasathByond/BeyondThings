package com.mukundafoods.chimneylauncherproduct.ui.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.mukundafoods.chimneylauncherproduct.MyApplication.Companion.context

object Constants {

     val speed = arrayOf(
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
        "15",
        "20",
        "25",
        "30",
        "35", //18
        "40",
        "45",
        "50",
        "55",
        "60"
    )

   val displayTimeOut = arrayOf(
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
      "15",
   )

//   val buildSerialNumber = Build.getSerial()
//   val buildSerialNumber = getDeviceSerial()

   val androidId = Settings.Secure.ANDROID_ID
   val buildSerialNumber = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
   

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
   fun getDeviceId(context: Context): String {
      return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
   }


}