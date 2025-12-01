package com.mukundafoods.chimneylauncherproduct.ui.ymodule;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

public class DeviceItem {
    public UsbDevice device;
    public int port;
    public UsbSerialDriver driver;

    public DeviceItem(UsbDevice device, int port, UsbSerialDriver driver) {
        this.device = device;
        this.port = port;
        this.driver = driver;
    }
}