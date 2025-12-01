package com.mukundafoods.chimneylauncherproduct;

//package com.example.myapplication;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

public class SoundMeter {
    private Context context;
    private static final String MYTAG = "COOKER";
    private AudioRecord ar = null;
    private int minSize;
    public double freq;


    int sampleRate = 8000;
    int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

//    int minMin = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

//if (minSize == AudioRecord.ERROR || minSize == AudioRecord.ERROR_BAD_VALUE) {
//        Log.e("AudioRecord", "Invalid AudioRecord parameters!");
//        return;
//    }



//if (ar.getState() == AudioRecord.STATE_INITIALIZED) {
//        ar.startRecording();
//        Log.d("AudioRecord", "Recording started");
//    } else {
//        Log.e("AudioRecord", "AudioRecord not initialized!");
//    }


    public void start() {
        minSize= AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        Log.i(MYTAG, "minSize:" + minSize);
//        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);
//        ActivityCompat.requestPermissions(context,
//                new String[]{Manifest.permission.RECORD_AUDIO}, 1000);


//        AudioRecord ar = new AudioRecord(
//                MediaRecorder.AudioSource.MIC,
//                sampleRate,
//                channelConfig,
//                audioFormat,
//                minSize
//        );
        ar.startRecording();
    }
    public void stop() {
        if (ar != null) {
            ar.stop();
        }
    }

    public double getAmplitude() {
        short[] buffer = new short[minSize];
        ar.read(buffer, 0, minSize);
        int max = 0;
        for (short s : buffer)
        {
            if (Math.abs(s) > max)
            {
                max = Math.abs(s);
            }
        }
        freq = getFrequency(8000, buffer);
        return max;
    }

    public static double getFrequency(int sampleRate, short [] audioData){
        int numSamples = audioData.length;
        int numCrossing = 0;
        for (int p = 0; p < numSamples-1; p++)
        {
            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
                    (audioData[p] < 0 && audioData[p + 1] >= 0))
            {
                numCrossing++;
            }
        }
        float numSecondsRecorded = (float)numSamples/(float)sampleRate;
        float numCycles = numCrossing/2;
        float frequency = numCycles/numSecondsRecorded;

        return frequency;
    }

}

