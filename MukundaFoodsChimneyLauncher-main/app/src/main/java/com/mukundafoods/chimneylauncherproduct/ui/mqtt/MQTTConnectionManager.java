package com.mukundafoods.chimneylauncherproduct.ui.mqtt;

public class MQTTConnectionManager {

    public static void setMQTTConnectionManager(MQTTConnection mqttConnection, MQTTConnectionListener mqttConnectionListener) {
        mqttConnection.setListener(mqttConnectionListener);
    }

    public static interface MQTTConnectionListener {
        void onConnected();

        void onDisconnected();

        void onDataReceived(String topic, String data);
    }

}
