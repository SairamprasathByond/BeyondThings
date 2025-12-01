//package com.mukundafoods.chimneylauncher
//
//import android.content.Context
//import android.util.Log
//import org.tensorflow.lite.support.audio.TensorAudio
//import org.tensorflow.lite.task.audio.classifier.AudioClassifier
//import org.tensorflow.lite.task.audio.classifier.Classifications
//
////import org.tensorflow.lite.support.audio.TensorAudio
////import org.tensorflow.lite.support.audio.TensorAudio
////import org.tensorflow.lite.support.audio.TensorAudio
////import org.tensorflow.lite.support.audio.TensorAudio
////import org.tensorflow.lite.task.audio.classifier.AudioClassifier
////import org.tensorflow.lite.task.audio.classifier.Classifications
////import org.tensorflow.lite.task.audio.classifier.TensorAudio
//
//class SoundClassifier(context: Context) {
//
//    private val classifier: AudioClassifier =
//        AudioClassifier.createFromFile(context, "yamnet.tflite")
//    private val tensorAudio: TensorAudio = classifier.createInputTensorAudio()
//    private val audioRecord = classifier.createAudioRecord()
//
//    fun startListening() {
//        audioRecord.startRecording()
//
//        Thread {
//            while (true) {
//                tensorAudio.load(audioRecord)
//                val results: List<Classifications> = classifier.classify(tensorAudio)
//
//                val categories = results[0].categories
//                val topResult = categories.maxByOrNull { it.score }
//
//                topResult?.let {
//                    val label = it.label.lowercase()
//                    val score = it.score
//
//                    when {
//                        label.contains("clap") && score > 0.5 -> {
//                            Log.d("SoundClassifier", "👏 Hand Clap Detected")
//                        }
//                        label.contains("whistle") && score > 0.5 -> {
//                            Log.d("SoundClassifier", "🔥 Cooker Whistle Detected")
//                        }
//                        label.contains("music") && score > 0.5 -> {
//                            Log.d("SoundClassifier", "🎶 Music Playing")
//                        }
//                        else -> {
//                            Log.d("SoundClassifier", "Other sound: $label ($score)")
//                        }
//                    }
//                }
//                Thread.sleep(500) // Check every half second
//            }
//        }.start()
//    }
//
//    fun stopListening() {
//        audioRecord.stop()
//    }
//}
