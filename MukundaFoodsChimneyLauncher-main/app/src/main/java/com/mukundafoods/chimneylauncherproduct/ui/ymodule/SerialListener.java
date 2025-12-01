package com.mukundafoods.chimneylauncherproduct.ui.ymodule;

public interface SerialListener {
    void onSerialConnect();
    void onSerialConnectError(Exception e);
    void onSerialRead(byte[] data);
    void onSerialIoError(Exception e);
    void onSerialWrite(byte[] data);
}
