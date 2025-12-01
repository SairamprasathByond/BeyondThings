package com.mukundafoods.chimneylauncherproduct

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager


class UsbPermissionReceiver : BroadcastReceiver() {


    override fun onReceive(p0: Context?, intent: Intent?) {

        if (intent!!.action == BuildConfig.APPLICATION_ID + ".GRANT_USB") {
            val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
            MainActivity.instance?.connect(granted)
        }

    }
}
