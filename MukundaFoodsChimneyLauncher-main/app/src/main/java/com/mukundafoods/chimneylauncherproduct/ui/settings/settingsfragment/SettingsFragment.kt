package com.mukundafoods.chimneylauncherproduct.ui.settings.settingsfragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.net.Uri
//import com.google.android.gms.common.wrappers.Wrappers.packageManager

//import com.google.android.gms.common.wrappers.Wrappers.packageManager
import com.mukundafoods.chimneylauncherproduct.BuildConfig

import com.mukundafoods.chimneylauncherproduct.databinding.SettingsFragmentBinding
import com.mukundafoods.chimneylauncherproduct.ui.MyViewModelFactory
import com.mukundafoods.chimneylauncherproduct.ui.backend.DownloadManager
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.backend.RetrofitService
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.MQTTConstants
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.SendClickEvent
import com.mukundafoods.chimneylauncherproduct.ui.settings.SettingsType
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Constants
import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants.buildSerialNumber


class SettingsFragment : Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var openSettingsFragment: (type: SettingsType) -> Unit
    private lateinit var settingsFragmentViewModel: SettingsFragmentViewModel
    private lateinit var downloadUrl: String

    companion object {

        fun newInstance(openChimneyScreen: (type: SettingsType) -> Unit): SettingsFragment {
            val fragment = SettingsFragment()
            fragment.openSettingsFragment = openChimneyScreen
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)

        settingsFragmentViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepository)).get(
                SettingsFragmentViewModel::class.java
            )


        binding.chimneySettingsLayout.setOnClickListener {
            openSettingsFragment(SettingsType.CHIMNEY_SETTINGS)
        }

        binding.helpCenter.setOnClickListener {
            openSettingsFragment(SettingsType.HELP_CENTER)
        }

        binding.appLandscapeSettings.setOnClickListener {
            try {
                startActivity(activity?.packageManager?.getLaunchIntentForPackage("info.kfsoft.force.rotation"))
            } catch (e: Exception) {
                Toast.makeText(activity, "Unavailable!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.systemSettingsLayout.setOnClickListener {
            if (binding.passcodeLayout.visibility == View.VISIBLE) {
                binding.passcodeLayout.visibility = View.GONE
                binding.settingArrow.rotation = (90).toFloat()
            } else {
                binding.passcodeLayout.visibility = View.VISIBLE
                binding.settingArrow.rotation = (-90).toFloat()
            }
        }

        binding.volumeControlLayout.setOnClickListener {
            try {
                startActivity(activity?.packageManager?.getLaunchIntentForPackage("com.tombayley.statusbar"))
            } catch (e: Exception) {
                Toast.makeText(activity, "Unavailable!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.passcodeValidate.setOnClickListener {

            val enteredText = binding.passcodeText.text.toString()
            if (enteredText == Constants.PASSCODE_FOR_SYSTEM_SETTINGS) {
                startActivityForResult(Intent(Settings.ACTION_SETTINGS), 0)
            } else if (enteredText == Constants.PASSCODE_FOR_QR_TOGGLE) {
                openSettingsFragment(SettingsType.QR_CODE_TESTING)
            } else {
                Toast.makeText(context, "Passcode invalid!", Toast.LENGTH_SHORT).show()
            }
        }

        settingsFragmentViewModel.latestVersionResponse.observe(viewLifecycleOwner) {
            val availableVersion = it.latest_version.replace(".", "")
            val currentVersion = BuildConfig.VERSION_NAME.replace(".", "")
            downloadUrl = it.download_url
            if (availableVersion > currentVersion) {
                binding.upgrade.visibility = View.VISIBLE
            } else {
                binding.upgrade.visibility = View.GONE
            }

            binding.availiableLatestVersion.text = "Version available: " + it.latest_version

            SendClickEvent().sendClickPacket(
                MQTTConstants.SETTINGS_SCREEN_NAME,
                MQTTConstants.SETTINGS_SCREEN_UPDATE,
                "current version ${BuildConfig.VERSION_NAME} available version ${availableVersion}"
            )
        }

        binding.checkForUpdate.setOnClickListener {
            binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            settingsFragmentViewModel.checkLatestVersion()
            if (binding.latestVersionLayout.visibility == View.VISIBLE) {
                binding.latestVersionLayout.visibility = View.GONE
                binding.checkForUpdateArrow.rotation = (90).toFloat()
            } else {
                binding.latestVersionLayout.visibility = View.VISIBLE
                binding.checkForUpdateArrow.rotation = (-90).toFloat()
            }
        }

        binding.serialNumber.text = buildSerialNumber
//        binding.serialNumber.text = getDeviceId(context)

        binding.upgrade.setOnClickListener {

            val pm = requireContext().packageManager
            if (!pm.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse("package:${requireContext().packageName}"))
                startActivity(intent)
            } else {
                if (::downloadUrl.isInitialized && downloadUrl != null)
                    activity?.let { it1 ->
                        DownloadManager(it1, downloadUrl).enqueueDownload()
                    }
                /*        val  manager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                      val uri =
                          Uri.parse("https://mykitchenos.com/api/chimney/download/2.0.1")
                      val request = DownloadManager.Request(uri)
                      request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                      val reference: Long = manager.enqueue(request)*/
            }
        }

        binding.currentVersion.text = "Current Version: " + BuildConfig.VERSION_NAME
    }

    fun getDeviceId(context: Context?): String {
        return Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private var downloadID: Long = 0
    /* private fun beginDownload() {
         val url = "http://speedtest.ftp.otenet.gr/files/test10Mb.db"
         var fileName = url.substring(url.lastIndexOf('/') + 1)
         fileName = fileName.substring(0, 1).uppercase(Locale.getDefault()) + fileName.substring(1)
         val file: File = Util.createDocumentFile(fileName, context)

         val request = DownloadManager.Request(Uri.parse(url))
             .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN) // Visibility of the download Notification
             .setDestinationUri(Uri.fromFile(file)) // Uri of the destination file
             .setTitle(fileName) // Title of the Download Notification
             .setDescription("Downloading") // Description of the Download Notification
             .setRequiresCharging(false) // Set if charging is required to begin the download
             .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
             .setAllowedOverRoaming(true) // Set if download is allowed on roaming network
         val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
         downloadID =
             downloadManager!!.enqueue(request) // enqueue puts the download request in the queue.


         // using query method
         var finishDownload = false
         var progress: Int
         while (!finishDownload) {
             val cursor = downloadManager!!.query(DownloadManager.Query().setFilterById(downloadID))
             if (cursor.moveToFirst()) {
                 val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                 when (status) {
                     DownloadManager.STATUS_FAILED -> {
                         finishDownload = true
                     }

                     DownloadManager.STATUS_PAUSED -> {}
                     DownloadManager.STATUS_PENDING -> {}
                     DownloadManager.STATUS_RUNNING -> {
                         val total =
                             cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                         if (total >= 0) {
                             val downloaded =
                                 cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                             progress = ((downloaded * 100L) / total).toInt()
                             // if you use downloadmanger in async task, here you can use like this to display progress.
                             // Don't forget to do the division in long to get more digits rather than double.
                             //  publishProgress((int) ((downloaded * 100L) / total));
                         }
                     }

                     DownloadManager.STATUS_SUCCESSFUL -> {
                         progress = 100
                         // if you use aysnc task
                         // publishProgress(100);
                         finishDownload = true
                         Toast.makeText(this@MainActivity, "Download Completed", Toast.LENGTH_SHORT)
                             .show()
                     }
                 }
             }
         }
     }*/
}