package com.mukundafoods.chimneylauncherproduct.ui.ymodule;


import com.mukundafoods.chimneylauncherproduct.BuildConfig;

//import com.google.zxing.client.android.BuildConfig;

public class Constants {

    // values have to be unique within each app
    static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;
    // values have to be globally unique
    static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity";


    private Constants() {}
}
