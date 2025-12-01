package com.mukundafoods.chimneylauncherproduct.ui.sharedpreference

import android.content.Context
import com.mukundafoods.chimneylauncherproduct.MyApplication

private const val HEATER_TIME = "HeaterTime"
private const val BLOWER_TIME = "BlowerTime"
private const val BLOWER_MODE = "BlowerMode"
private const val USER_NAME = "UserName"
private const val EMAIL = "Email"
private const val CONTACT = "Contact"
private const val LPG_REMINDER = "LpgReminder"
private const val TESTING_QR_CODE = "TestingQrCode"
private const val SCHEDULE_CLEANING_AFTER = "ScheduleCleaningAfter"
private const val IS_CLEANING_DONE = "CLEANING_DONE"
private const val LATER_COUNT = "LATER_COUNT"
private const val DISPLAY_TIMEOUT = "DISPLAY_TIMEOUT"
private const val LPG_REMINDER_SHOWN = "LPG_REMINDER_SHOWN"
private const val LPG_REMINDER_LONG = "LPG_REMINDER_LONG"
private const val MARKETING_DATA = "MARKETING_DATA"
private const val LATLONG = "LATLONG"
private const val USB_CONNECTION = "USB_CONNECTION"
private const val VARIANT = "VARIANT" // 1 - Normal 2 - Bldc

object Data {

    private val sharedPreferences =
        MyApplication.context.getSharedPreferences("ChimneyLauncher", Context.MODE_PRIVATE)
    private var editor = sharedPreferences.edit()

    fun setHeaterTime(heaterTime: Int) {
        editor.putInt(HEATER_TIME, heaterTime).commit()
    }

    fun getHeaterTime(): Int = sharedPreferences.getInt(HEATER_TIME, 18)

    fun setBlowerTime(blowerTime: Int) {
        editor.putInt(BLOWER_TIME, blowerTime).commit()
    }

    fun getBlowerTime(): Int = sharedPreferences.getInt(BLOWER_TIME, 18)

    fun setBlowerMode(blowerMode: Int) {
        editor.putInt(BLOWER_MODE, blowerMode).commit()
    }

    fun getBlowerMode(): Int = sharedPreferences.getInt(BLOWER_MODE, 1)

    fun setUserName(userName: String) {
        editor.putString(USER_NAME, userName).commit()
    }

    fun getUserName(): String = sharedPreferences.getString(USER_NAME, "")!!

    fun setEmail(email: String) {
        editor.putString(EMAIL, email).commit()
    }

    fun getEmail(): String = sharedPreferences.getString(EMAIL, "")!!

    fun setContact(contact: String) {
        editor.putString(CONTACT, contact).commit()
    }

    fun getContact(): String = sharedPreferences.getString(CONTACT, "")!!


    fun setLatLong(contact: String) {
        editor.putString(LATLONG, contact).commit()
    }

    fun getLatLong(): String? = sharedPreferences.getString(LATLONG, null)

    fun setLpgReminder(days: Int) {
        editor.putInt(LPG_REMINDER, days).commit()
    }

    fun getLpgReminder(): Int = sharedPreferences.getInt(LPG_REMINDER, 0)


    fun setLpgReminderLong(days: Long) {
        editor.putLong(LPG_REMINDER_LONG, days).commit()
    }

    fun getLpgReminderLong(): Long = sharedPreferences.getLong(LPG_REMINDER_LONG, 0L)

    fun enableTestingQrCode(qrCode: Boolean) {
        editor.putBoolean(TESTING_QR_CODE, qrCode).commit()
    }

    fun isTestingQrCodeEnabled(): Boolean = sharedPreferences.getBoolean(TESTING_QR_CODE, false)

    fun setDownloadMarketingDataEnabled(qrCode: Boolean) {
        editor.putBoolean(MARKETING_DATA, qrCode).commit()
    }

    fun isDownloadMarketingDataEnabled(): Boolean =
        sharedPreferences.getBoolean(MARKETING_DATA, false)

    fun setVariant(variant: Int) {
        editor.putInt(VARIANT, variant).commit()
    }

    fun getVariant() : Int = sharedPreferences.getInt(VARIANT, 1)

    fun scheduleCleaning(after: Int) {
        editor.putInt(SCHEDULE_CLEANING_AFTER, after).commit()
    }

    fun getCleaningScheduled(): Int = sharedPreferences.getInt(SCHEDULE_CLEANING_AFTER, 23)

    fun setIsCleaningDone(qrCode: Boolean) {
        editor.putBoolean(IS_CLEANING_DONE, qrCode).commit()
    }

    fun isCleaningDone(): Boolean = sharedPreferences.getBoolean(IS_CLEANING_DONE, true)

    fun setIsLpgReminderShown(isShown: Boolean) {
        editor.putBoolean(LPG_REMINDER_SHOWN, isShown).commit()
    }

    fun isLpgReminderShown(): Boolean = sharedPreferences.getBoolean(LPG_REMINDER_SHOWN, false)

    fun setLaterCount(count: Int) {
        editor.putInt(LATER_COUNT, count).commit()
    }

    fun getLaterCount(): Int = sharedPreferences.getInt(LATER_COUNT, 0)

    fun displayTimeOut(after: Int) {
        editor.putInt(DISPLAY_TIMEOUT, after).commit()
    }

    fun getDisplayTimeOut(): Int = sharedPreferences.getInt(DISPLAY_TIMEOUT, 10)

    fun setUsbConnectionStatus(status: Boolean) {
        editor.putBoolean(USB_CONNECTION, status).commit()
    }

    fun getUsbConnectionStatus(): Boolean = sharedPreferences.getBoolean(USB_CONNECTION, false)
}