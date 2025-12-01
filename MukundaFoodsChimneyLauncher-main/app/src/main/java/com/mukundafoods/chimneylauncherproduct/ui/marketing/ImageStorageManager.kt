package com.mukundafoods.chimneylauncherproduct.ui.marketing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

class ImageStorageManager {
    companion object {
        fun saveToInternalStorage(
            context: Context,
            bitmapImage: Bitmap,
            imageFileName: String,
        ): String {
            context.openFileOutput(imageFileName, Context.MODE_PRIVATE).use { fos ->
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 25, fos)
            }
            return context.filesDir.absolutePath
        }

        fun getImageFromInternalStorage(context: Context, imageFileName: String): Bitmap? {
            val directory = context.filesDir
            val file = File(directory, imageFileName)
            return BitmapFactory.decodeStream(FileInputStream(file))
        }

        fun deleteImageFromInternalStorage(context: Context, imageFileName: String): Boolean {
            val dir = context.filesDir
            val file = File(dir, imageFileName)
            return file.delete()
        }

        fun deleteVideosFromInternalStorage() {
            val downloadFileDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/video"
            )
            if(downloadFileDir.exists()){
                downloadFileDir.listFiles().forEach {
                    val delete = it.delete()
                    println("Delete result = $delete")
                }
            }
        }

        fun isFilePresent(context: Context, fileName: String): Boolean {
            val path: String =
                context.getFilesDir().getAbsolutePath().toString() + "/" + fileName
            val file = File(path)
            return file.exists()
        }

        fun isVideoPresent(fileName: String): Boolean {
            val path =
                "/storage/emulated/0/Pictures/video/$fileName"
            val file = File(path)
            return file.exists()
        }

        fun saveImage(image: Bitmap): String? {
            var savedImagePath: String? = null
            val imageFileName = "JPEG_" + "FILE_NAME" + ".jpg"
            val storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/YOUR_FOLDER_NAME"
            )
            var success = true
            if (!storageDir.exists()) {
                success = storageDir.mkdirs()
            }
            if (success) {
                val imageFile = File(storageDir, imageFileName)
                savedImagePath = imageFile.getAbsolutePath()
                try {
                    val fOut: OutputStream = FileOutputStream(imageFile)
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                    fOut.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return savedImagePath
        }
    }
}