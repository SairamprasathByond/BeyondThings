package com.mukundafoods.chimneylauncherproduct.ui.ymodule;

import java.io.IOException;

public class USBConnection implements SerialListener {

    private static USBConnection mInstance;
    private SerialSocket socket;


    public void setSocket(SerialSocket socket) {
        this.socket = socket;
        onSerialConnect();
    }

    private USBConnectionManager.USBConnectionListener mUSBConnectionListener;

    public void setListener(USBConnectionManager.USBConnectionListener usbConnectionListener) {
        mUSBConnectionListener = usbConnectionListener;
    }

    private enum Connected {False, Pending, True}

    private Connected connected = Connected.False;

    public static USBConnection getInstance() {
        if (mInstance == null) {
            mInstance = new USBConnection();
        }
        return mInstance;
    }

    @Override
    public void onSerialConnect() {

        connected = Connected.True;
        //  updateToolBarColor(Color.GREEN);
        if (mUSBConnectionListener != null)
            mUSBConnectionListener.onConnected();
    }

    @Override
    public void onSerialConnectError(Exception e) {
        disconnect();
        if (mUSBConnectionListener != null)
            mUSBConnectionListener.onDisconnected();

    }

    @Override
    public void onSerialRead(byte[] data) {

        if (mUSBConnectionListener != null)
            mUSBConnectionListener.onDataReceived(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        disconnect();
        if (mUSBConnectionListener != null) {
            mUSBConnectionListener.onDisconnected();
        }
    }

    @Override
    public void onSerialWrite(byte[] data) {

    }

    public void disconnect() {
        connected = Connected.False;
        //   updateToolBarColor(Color.WHITE);

        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }

    public void cleanUp() {
        if (mUSBConnectionListener != null) {
            mUSBConnectionListener = null;
        }
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }

    public void send(String str) {
        if (connected != Connected.True) {
            return;
        }
        try {
            byte[] data = (str + "\r\n").getBytes();
            socket.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    public void writeArray(byte[] array) {
        if (connected != Connected.True || socket == null) {
            return;
        }

        try {
            socket.write(array);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeArrayForOTA(byte[] array) {
        if (connected != Connected.True || socket == null) {
            return;
        }
        //   Log.d("Madhu", "Madhu Write New Packet");
        for (int i = 0; i < array.length; i++) {
            try {
                if(socket!=null)
                    socket.write(array[i]);
                //    delayedIdle(200);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //    Log.d("Madhu", "Madhu Write Byte =" + array[i]);
        }
    }
}
