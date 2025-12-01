package com.mukundafoods.chimneylauncherproduct.ui.mqtt;


import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.mukundafoods.chimneylauncherproduct.R;
import com.mukundafoods.chimneylauncherproduct.ui.database.ChimneyDatabaseHelperImpl;
import com.mukundafoods.chimneylauncherproduct.ui.database.MqttMessageEntity;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MQTTConnection {

    private static MqttAndroidClient mqttAndroidClient;
    final static String serverUri = "ssl://a1bmjgj4h06eyc-ats.iot.ap-south-1.amazonaws.com:8883";
    final static String clientId = Build.SERIAL.toString();
    final static String subscriptionTopic = "startDevice/" + Build.SERIAL.toString();


    private static MQTTConnection mInstance;

    private ChimneyDatabaseHelperImpl chimneyDatabaseHelper = null;

    public static MQTTConnection getInstance() {
        if (mInstance == null) {
            mInstance = new MQTTConnection();
        }
        return mInstance;
    }

    private MqttConnectOptions mqttConnectOptions;

    public void setUpMQTTClient(Context context, ChimneyDatabaseHelperImpl chimneyDatabaseHelper) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected())
            return;

        this.chimneyDatabaseHelper = chimneyDatabaseHelper;
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttConnectOptions = new MqttConnectOptions();
        //   mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        SSLSocketFactory sslSocketFactory = null;
        try {
            sslSocketFactory = getSocketFactory(context.getResources().openRawResource(R.raw.ca),
                    context.getResources().openRawResource(R.raw.cert),
                    context.getResources().openRawResource(R.raw.key), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mqttConnectOptions.setSocketFactory(sslSocketFactory);
    }
    private Object lock = new Object();
    public void connectToMQTTServer() {

        try {
            if (mqttAndroidClient.isConnected()) {
                Log.d("MQTT", "MQTT Already connected");
                return;
            }
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(10);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    if (mqttAndroidClient != null) {
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                        subscribeToTopic();
                        mMQTTconnectionListener.onConnected();

                        Log.d("MQTTConnection","MQTTConnection onSuccess");
                      List<MqttMessageEntity> list =  chimneyDatabaseHelper.getMqttCachedData();
                      Log.d("MQTTConnection","MQTTConnection Cached List ="+list.size());

                      new Thread(){
                          @Override
                          public void run() {
                              for(int i=0;i<list.size();i++){
                                  synchronized (lock){
                                      publishCachedMessage(list.get(i));
                                      try {
                                          sleep(4000);
                                      } catch (InterruptedException e) {
                                          throw new RuntimeException(e);
                                      }
                                  }

                              }
                          }
                      }.start();

                    } else {
                        try {
                            mqttAndroidClient.disconnectForcibly();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mMQTTconnectionListener.onDisconnected();
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, (topic, message) ->
                    mMQTTconnectionListener.onDataReceived(topic, message.toString()));

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public synchronized void publishCachedMessage(MqttMessageEntity mqttMessageEntity) {
        if (mqttAndroidClient == null) {
            return;
        }
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(mqttMessageEntity.getPayload().getBytes());
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                Log.d("MQTT","MQTT is connected so the data is published FROM cached data "+mqttMessageEntity.getId());
                mqttAndroidClient.publish(mqttMessageEntity.getTopic(), message);
                chimneyDatabaseHelper.deleteMqttData(mqttMessageEntity);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public synchronized void publishMessage(String topic, String payload) {
        if (mqttAndroidClient == null) {
            return;
        }
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                Log.d("MQTT","MQTT is connected so the data is published");
                mqttAndroidClient.publish(topic, message);
            }else{
                Log.d("MQTT","MQTT is not connected so the data is not published");
                chimneyDatabaseHelper.insertMqttData(new MqttMessageEntity(topic, payload));
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MQTTConnectionManager.MQTTConnectionListener mMQTTconnectionListener;

    public void setListener(MQTTConnectionManager.MQTTConnectionListener mqttConnectionListener) {
        mMQTTconnectionListener = mqttConnectionListener;
    }

    public synchronized boolean isMQTTConnected() {
        return mqttAndroidClient != null && mqttAndroidClient.isConnected();
    }


    public static SSLSocketFactory getSocketFactory(InputStream caCrtFile, InputStream crtFile, InputStream keyFile,
                                                    String password) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        X509Certificate caCert = null;

        BufferedInputStream bis = new BufferedInputStream(caCrtFile);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        while (bis.available() > 0) {
            caCert = (X509Certificate) cf.generateCertificate(bis);
        }

        // load client certificate
        bis = new BufferedInputStream(crtFile);
        X509Certificate cert = null;
        while (bis.available() > 0) {
            cert = (X509Certificate) cf.generateCertificate(bis);
        }

        // load client private cert
        PEMParser pemParser = new PEMParser(new InputStreamReader(keyFile));
        Object object = pemParser.readObject();
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        KeyPair key = converter.getKeyPair((PEMKeyPair) object);

        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("cert-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", cert);
        ks.setKeyEntry("private-cert", key.getPrivate(), password.toCharArray(),
                new java.security.cert.Certificate[]{cert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());

        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }

    public static void disconnect() {
        try {
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                mqttAndroidClient.disconnectForcibly();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
