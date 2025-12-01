package com.mukundafoods.chimneylauncherproduct.ui.ymodule;


import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialProber;

/**
 * add devices here, that are not known to DefaultProber
 */
public class CustomProber {

    public static UsbSerialProber getCustomProber() {
        ProbeTable customTable = new ProbeTable();
        customTable.addProduct(0x483, 0x5740 , CdcAcmSerialDriver.class); // e.g. ST CDC
        return new UsbSerialProber(customTable);
    }

}
