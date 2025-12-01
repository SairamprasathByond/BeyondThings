package com.mukundafoods.chimneylauncherproduct.ui.ymodule;

public class USBConnectionManager {

    public static void setConnectionManager(USBConnection usbConnection, USBConnectionListener usbConnectionListener) {
        usbConnection.setListener(usbConnectionListener);
    }

    public static interface USBConnectionListener {
        void onConnected();

        void onDisconnected();

        void onDataReceived(byte[] data);
    }
}
