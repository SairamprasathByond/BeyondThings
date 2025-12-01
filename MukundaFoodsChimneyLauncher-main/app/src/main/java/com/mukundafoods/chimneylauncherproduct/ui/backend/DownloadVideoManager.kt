package com.mukundafoods.chimneylauncherproduct.ui.backend


import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.mukundafoods.chimneylauncherproduct.MyApplication.Companion.context
import java.io.File

class DownloadVideoManager(private val url: String, private val fileName: String) {

    companion object {
        private const val FILE_BASE_PATH = "file://"
    }

    fun enqueueDownload() {

        val downloadFileDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/video"
        )

        if (downloadFileDir != null) {
            if (downloadFileDir.exists()) {
                downloadFileDir.delete()
            } else {
                downloadFileDir.mkdirs()
            }
        }


        var destination = "$downloadFileDir/"
        destination += "$fileName"

        val uri = Uri.parse("$FILE_BASE_PATH$destination")

        try{
            for (f in downloadFileDir.listFiles()) {
                if (f.name.startsWith(fileName.split(".")[0])) {
                    f.delete()
                }
            }
        }catch (e : NullPointerException){
            e.printStackTrace()
        }


        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri)

        request.setDestinationUri(uri)
        downloadManager.enqueue(request)
    }
}