package com.mukundafoods.chimneylauncherproduct.ui.backend


import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.mukundafoods.chimneylauncherproduct.MyApplication.Companion.context
import java.io.File

class DownloadManager(private val activity:  Activity, private val url: String) {

    companion object {
        private const val FILE_NAME = "ChimneyApplication.apk"
        private const val FILE_BASE_PATH = "file://"
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val PROVIDER_PATH = ".provider"
        private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
    }

    fun enqueueDownload() {

        val downloadFileDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/apk"
        )

        if(downloadFileDir!=null){
            if(downloadFileDir.exists()){
                downloadFileDir.delete()
            }else{
                downloadFileDir.mkdirs()
            }
        }


        var destination =
           // activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
            "$downloadFileDir/"
        destination += FILE_NAME

        val uri = Uri.parse("$FILE_BASE_PATH$destination")

        val file = File(destination)
        if (file.exists()) file.delete()

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType(MIME_TYPE)
        request.setTitle("Downloading Apk")
        request.setDescription("Downloading latest version")

        println("Destination $destination")
        // set destination
        request.setDestinationUri(uri)

        showInstallOption(destination)
        // Enqueue a new download and same the referenceId
        downloadManager.enqueue(request)
        Toast.makeText(context, "Downloading...", Toast.LENGTH_LONG)
            .show()


    }

    private fun showInstallOption(
        destination: String,
    ) {

        // set BroadcastReceiver to install app when .apk is downloaded
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent,
            ) {
                install(destination)
            }
        }
        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun install(apkPath: String) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri = FileProvider.getUriForFile(activity, "${activity.packageName}.FileProvider", File(apkPath))
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            intent.setDataAndType(Uri.fromFile(File(apkPath)), "application/vnd.android.package-archive")
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        activity.startActivity(intent)
       activity.finish()
    }
    }