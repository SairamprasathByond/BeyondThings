package com.mukundafoods.chimneylauncherproduct.ui.video

import android.os.Bundle
import android.os.Environment
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.mukundafoods.chimneylauncherproduct.databinding.VideoActivityBinding
import java.io.File


class VideoActivity : AppCompatActivity() {
    private lateinit var binding: VideoActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VideoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)

        binding.close.setOnClickListener {
            binding.videoView.stopPlayback()
            finish()
        }

        val storageDir =  File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/video/DKDTec9Xxsnb1pgGVATLZ2EXuQkPI3LnPvSV5vk4.mp4"
        )

       /* val file = File.createTempFile(
            "DKDTec9Xxsnb1pgGVATLZ2EXuQkPI3LnPvSV5vk4",
            ".mp4",
            storageDir
        )*/

       /* intent?.extras?.getString("uri")?.let {
            println("@@ ${storageDir.absolutePath}")
            binding.videoView.setVideoURI(Uri.parse(storageDir.absolutePath))
            startPlayingVideo()
        }*/


    }

    private fun startPlayingVideo() {
        binding.videoView.start()
    }
}