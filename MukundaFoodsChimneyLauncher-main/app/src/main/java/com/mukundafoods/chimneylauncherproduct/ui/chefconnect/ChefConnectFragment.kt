package com.mukundafoods.chimneylauncherproduct.ui.chefconnect

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.mukundafoods.chimneylauncherproduct.databinding.ChefConnectFragmentBinding
import com.mukundafoods.chimneylauncherproduct.ui.MyViewModelFactory
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.backend.RetrofitService
import com.mukundafoods.chimneylauncherproduct.ui.chimney.CheckSerialNumber
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.MQTTConstants
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.MQTTConstants.CHEF_CONNECT_SCREEN_NOTES
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.SendClickEvent
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants
import java.util.Calendar
import java.util.concurrent.TimeUnit


class ChefConnectFragment : Fragment() {

    private var _binding: ChefConnectFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val numberArray = arrayOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "20",
        "21",
        "22",
        "23",
        "24",
        "25",
        "30",
        "35",
        "40",
        "45",
        "50",
        "55",
        "60"
    )

    private lateinit var chefConnectViewModel: ChefConnectViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = ChefConnectFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        chefConnectViewModel =
            ViewModelProvider(
                this,
                MyViewModelFactory(MainRepository(RetrofitService.getInstance()))
            ).get(
                ChefConnectViewModel::class.java
            )

        return root
    }

    override fun onResume() {
        super.onResume()
        updateDays()
        if (!Data.isTestingQrCodeEnabled()) {
            chefConnectViewModel.checkSerialNumberFeedbackResponse.observe(viewLifecycleOwner) {
                when (it) {
                    CheckSerialNumber.Failure -> {
                        renderQRCode("https://mykitchenos.com/add_user_details/${Constants.buildSerialNumber}")
                    }

                    CheckSerialNumber.Success -> {
                        hideQRCodeAndShowEntertainmentLayout()
                    }

                    CheckSerialNumber.NoInternet -> {
                        binding.qrCode.visibility = View.GONE
                        binding.noInternetText.visibility = View.VISIBLE
                    }
                }
            }
            chefConnectViewModel.checkSerialNumber()
        } else {
            hideQRCodeAndShowEntertainmentLayout()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*        binding.alarm.setOnClickListener {
                    *//* val i = Intent(AlarmClock.ACTION_SHOW_ALARMS)
             startActivity(i)*//*

            startActivity(activity?.packageManager?.getLaunchIntentForPackage("com.google.android.deskclock"))


        }
*/
        binding.reminder.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.CHEF_CONNECT_SCREEN_NAME,
                MQTTConstants.CHEF_CONNECT_SCREEN_REMINDER,
                "calendar"
            )
            try {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_APP_CALENDAR)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "Unavailable!!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.chefRecipeBook.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.CHEF_CONNECT_SCREEN_NAME,
                MQTTConstants.CHEF_CONNECT_SCREEN_WOKIE,
                "mukunda foods"
            )
            try {
                val intent = Intent()
                intent.component = ComponentName(
                    "com.mukundafoods.wokie",
                    "com.mukundafoods.wokie.newbackend.signinup.SignInSignUpActivity"
                )
                /*   intent.component = ComponentName(
                     "com.bravery.hob_app",
                     "com.bravery.hob_app.MainActivity"
                 )*/
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "Unavailable!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.notesLayout.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.CHEF_CONNECT_SCREEN_NAME,
                MQTTConstants.CHEF_CONNECT_SCREEN_REMINDER,
                CHEF_CONNECT_SCREEN_NOTES
            )
            try {
                startActivity(activity?.packageManager?.getLaunchIntentForPackage("com.google.android.keep"))
            } catch (e: Exception) {
                Toast.makeText(activity, "Unavailable!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.lpgReminder.minValue = 1
        binding.lpgReminder.maxValue = 100

        binding.groceriesAppLayoutHeading.setOnClickListener {
            if (binding.groceriesAppLayout.visibility == View.VISIBLE) {
                binding.groceriesAppLayout.visibility = View.GONE
                binding.groceryArrow.rotation = (90).toFloat()
            } else {
                binding.groceriesAppLayout.visibility = View.VISIBLE
                binding.groceryArrow.rotation = (-90).toFloat()
            }
        }

        if (Data.getLpgReminderLong() != 0L) {
            binding.lpgReminderButton.isSelected = true
            binding.lpgReminderButton.text = "Reset"
            binding.lpgReminder.visibility = View.GONE
            binding.lpgDaysRemaining.visibility = View.VISIBLE
            updateDays()
        } else {
            binding.lpgReminderButton.isSelected = false
            binding.lpgReminderButton.text = "Set"
            binding.lpgReminder.visibility = View.VISIBLE
            binding.lpgDaysRemaining.visibility = View.GONE
        }

        binding.lpgReminderButton.setOnClickListener {
            if (it.isSelected) {
                binding.lpgReminderButton.isSelected = false
                binding.lpgReminderButton.text = "Set"
                binding.lpgReminder.visibility = View.VISIBLE
                binding.lpgDaysRemaining.visibility = View.GONE
                //Data.setLpgReminder(0)
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHEF_CONNECT_SCREEN_NAME,
                    "lpg reminder reset",
                    remainingDays.toString()
                )
            } else {
                binding.lpgReminderButton.isSelected = true
                binding.lpgReminderButton.text = "Reset"
                binding.lpgReminder.visibility = View.GONE
                binding.lpgDaysRemaining.visibility = View.VISIBLE

                val calendar = Calendar.getInstance()
                // val currentDay = calendar.time
                calendar.add(Calendar.DAY_OF_MONTH, binding.lpgReminder.value)

                /*  val dateSet = calendar.time
                  println("Narayan Date Set : ${dateSet.time}")
                  println("Narayan Current Date : ${currentDay.time}")
                  println("Diff ${TimeUnit.MILLISECONDS.toDays(dateSet.time - currentDay.time)}")*/

                Data.setIsLpgReminderShown(false)
                Data.setLpgReminderLong(calendar.time.time)
                updateDays()
                SendClickEvent().sendClickPacket(
                    MQTTConstants.CHEF_CONNECT_SCREEN_NAME,
                    "lpg reminder set",
                    remainingDays.toString()
                )

                /* val daysRemaining = TimeUnit.MILLISECONDS.toDays(dateSet.time - currentDay.time)
                 binding.lpgDaysRemaining.text =
                     "" + daysRemaining

                 if (daysRemaining <= 5) {
                     binding.lpgDaysRemaining.setTextColor(Color.RED)
                 } else {
                     binding.lpgDaysRemaining.setTextColor(Color.parseColor("#43392E"))
                 }*/

            }
        }
    }

    private var remainingDays = 0L
    private fun updateDays() {

        if (Data.getLpgReminderLong() == 0L)
            return

        val calendar = Calendar.getInstance().time.time

        val daysRemaining =
            TimeUnit.MILLISECONDS.toDays(Data.getLpgReminderLong() - calendar)

        remainingDays = daysRemaining
        binding.lpgDaysRemaining.text =
            "" + daysRemaining

        if (daysRemaining <= 5) {
            binding.lpgDaysRemaining.setTextColor(Color.RED)
        } else {
            binding.lpgDaysRemaining.setTextColor(Color.parseColor("#43392E"))
        }
    }

    private fun openThirdPartyApp(name: String) {
        try {
            startActivity(activity?.packageManager?.getLaunchIntentForPackage(name))
        } catch (e: Exception) {
            Toast.makeText(activity, "Unavailable!!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideQRCodeAndShowEntertainmentLayout() {
        binding.qrCodeLayout.visibility = View.GONE
        binding.noInternetText.visibility = View.GONE
    }

    private fun renderQRCode(string: String) {
        binding.noInternetText.visibility = View.GONE
        binding.qrCode.visibility = View.VISIBLE
        binding.qrCodeLayout.visibility = View.VISIBLE
        val multiFormatWriter = MultiFormatWriter()

        try {
            val mMatrix = multiFormatWriter.encode(string, BarcodeFormat.QR_CODE, 400, 400)
            val mEncoder = BarcodeEncoder()
            val mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
            binding.qrCodeImage.setImageBitmap(mBitmap);//Setting generated QR code to imageView
        } catch (e: WriterException) {
            e.printStackTrace();
        }
    }
}