package com.mukundafoods.chimneylauncherproduct

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlin.math.*

// Whistle detection class
class WhistleCounter(private val context: android.content.Context) {

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false

    private val sampleRate = 8000
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var whistleCount = 0
    private var targetCount = 3 // default, you can set from user input

    fun setTargetCount(count: Int) {
        targetCount = count
    }

    fun start() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Microphone permission required!", Toast.LENGTH_SHORT).show()
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        audioRecord?.startRecording()
        isRecording = true

        recordingThread = Thread { detectWhistles() }
        recordingThread?.start()
    }

    fun stop() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        recordingThread = null
    }

    private fun detectWhistles() {
        val buffer = ShortArray(bufferSize)

        while (isRecording) {
            val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
            if (read > 0) {
                val amplitude = calcRMS(buffer, read)
                val frequency = calcFrequency(buffer, read, sampleRate)

                if (amplitude > 2000 && frequency in 2000.0..4000.0) { // whistle range
                    whistleCount++
                    Log.d("WhistleDetector", "Whistle detected! Count = $whistleCount")

                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            context,
                            "Whistle Count: $whistleCount",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (whistleCount >= targetCount) {
                        stop()
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "Target reached, auto stop!", Toast.LENGTH_LONG)
                                .show()
                        }
                        break
                    }

                    Thread.sleep(1000) // prevent multiple triggers for same whistle
                }
            }
        }
    }

    // RMS amplitude
    private fun calcRMS(buffer: ShortArray, read: Int): Double {
        var sum = 0.0
        for (i in 0 until read) {
            sum += buffer[i] * buffer[i]
        }
        return sqrt(sum / read)
    }

    // Basic frequency detection using zero-crossing
    private fun calcFrequency(buffer: ShortArray, read: Int, sampleRate: Int): Double {
        var numCrossings = 0
        for (i in 1 until read) {
            if ((buffer[i - 1] > 0 && buffer[i] <= 0) ||
                (buffer[i - 1] < 0 && buffer[i] >= 0)
            ) {
                numCrossings++
            }
        }
        return (numCrossings * sampleRate / (2.0 * read))
    }
}
