package com.mukundafoods.chimneylauncherproduct.ui.entertainment

import android.content.Intent
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
import com.mukundafoods.chimneylauncherproduct.databinding.FragmentEntertainmentBinding
import com.mukundafoods.chimneylauncherproduct.ui.MyViewModelFactory
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.backend.RetrofitService
import com.mukundafoods.chimneylauncherproduct.ui.chimney.CheckSerialNumber
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.MQTTConstants
import com.mukundafoods.chimneylauncherproduct.ui.mqtt.SendClickEvent
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants.buildSerialNumber

class EntertainmentFragment : Fragment() {

    private var _binding: FragmentEntertainmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var entertainmentViewModel: EntertainmentViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        entertainmentViewModel =
            ViewModelProvider(
                this,
                MyViewModelFactory(MainRepository(RetrofitService.getInstance()))
            ).get(
                EntertainmentViewModel::class.java
            )

        _binding = FragmentEntertainmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (!Data.isTestingQrCodeEnabled()) {
            entertainmentViewModel.checkSerialNumberFeedbackResponse.observe(viewLifecycleOwner) {
                when (it) {
                    CheckSerialNumber.Failure -> {
                        renderQRCode("https://mykitchenos.com/add_user_details/${buildSerialNumber}")
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
            entertainmentViewModel.checkSerialNumber()
        } else {
            hideQRCodeAndShowEntertainmentLayout()
        }
    }

    private fun hideQRCodeAndShowEntertainmentLayout() {
        binding.noInternetText.visibility = View.GONE
        binding.qrCodeLayout.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* binding.youtube.setOnClickListener {
             val url = "https://www.youtube.com/"
             val i = Intent(Intent.ACTION_VIEW)
             i.data = Uri.parse(url)
             startActivity(i)
         }

         binding.saavn.setOnClickListener {
             val url = "https://www.jiosaavn.com//"
             val i = Intent(Intent.ACTION_VIEW)
             i.data = Uri.parse(url)
             startActivity(i)
         }

         binding.spotify.setOnClickListener {
             val url = "https://open.spotify.com/"
             val i = Intent(Intent.ACTION_VIEW)
             i.data = Uri.parse(url)
             startActivity(i)
         }

         binding.news.setOnClickListener {
             startActivity( activity?.packageManager?.getLaunchIntentForPackage("com.google.android.apps.magazines"))
         }*/

        binding.netflix.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_OTT_APP,
                MQTTConstants.APPS_AND_MORE_SCREEN_NETFLIX
            )
            openThirdPartyApp("com.netflix.mediaclient")
        }
        binding.prime.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_OTT_APP,
                MQTTConstants.APPS_AND_MORE_SCREEN_PRIME
            )
            openThirdPartyApp("com.amazon.avod.thirdpartyclient")
        }
        binding.spotify.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_MUSIC_AUDIO,
                MQTTConstants.APPS_AND_MORE_SCREEN_SPOTIFY
            )
            openThirdPartyApp("com.spotify.music")
        }
        binding.youtubeMusic.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_MUSIC_AUDIO,
                MQTTConstants.APPS_AND_MORE_SCREEN_YOUTUBE_MUSIC
            )
            openThirdPartyApp("com.google.android.apps.youtube.music")
        }
        binding.youtube.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_OTT_APP,
                MQTTConstants.APPS_AND_MORE_SCREEN_YOUTUBE
            )
            openThirdPartyApp("com.google.android.youtube")
        }
        binding.gaana.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_MUSIC_AUDIO,
                MQTTConstants.APPS_AND_MORE_SCREEN_GAANA
            )
            openThirdPartyApp("com.gaana")
        }
        binding.amazonMusic.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_MUSIC_AUDIO,
                MQTTConstants.APPS_AND_MORE_SCREEN_AMAZON_MUSIC
            )
            openThirdPartyApp("com.amazon.mp3")
        }
        binding.wynk.setOnClickListener {
            openThirdPartyApp("com.bsbportal.music")
        }
        binding.saavan.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_MUSIC_AUDIO,
                MQTTConstants.APPS_AND_MORE_SCREEN_SAAVN
            )
            openThirdPartyApp("com.jio.media.jiobeats")
        }
        binding.hotstar.setOnClickListener {
            //openThirdPartyApp("com.disney.disneyplus")
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_OTT_APP,
                MQTTConstants.APPS_AND_MORE_SCREEN_DISNEY_HOT_STAR
            )
            openThirdPartyApp("in.startv.hotstar")
        }
        binding.sonyliv.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_OTT_APP,
                MQTTConstants.APPS_AND_MORE_SCREEN_SONY_LIV
            )
            openThirdPartyApp("com.sonyliv")
        }
        binding.zee.setOnClickListener {
            openThirdPartyApp("com.graymatrix.did")
        }
        binding.swiggy.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_FOOD_AND_BEVARAGES,
                MQTTConstants.APPS_AND_MORE_SCREEN_SWIGGY
            )
            openThirdPartyApp("in.swiggy.android")
        }
        binding.zomato.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_FOOD_AND_BEVARAGES,
                MQTTConstants.APPS_AND_MORE_SCREEN_ZOMATO
            )
            openThirdPartyApp("com.application.zomato")
        }
        binding.licious.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_FOOD_AND_BEVARAGES,
                MQTTConstants.APPS_AND_MORE_SCREEN_LICIOUS
            )
            openThirdPartyApp("com.licious")
        }
        binding.amazon.setOnClickListener {
            openThirdPartyApp("com.amazon.mShop.android.shopping")
        }
        binding.chrome.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_OTHER_APPS,
                MQTTConstants.APPS_AND_MORE_SCREEN_BROWSER
            )
            openThirdPartyApp("com.android.chrome")
        }
        binding.googleNews.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_OTHER_APPS,
                MQTTConstants.APPS_AND_MORE_SCREEN_NEWS
            )
            openThirdPartyApp("com.google.android.apps.magazines")
        }
        binding.calendar.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_APP_CALENDAR)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "Unavailable!!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.keep.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_OTHER_APPS,
                MQTTConstants.APPS_AND_MORE_SCREEN_NOTES
            )
            openThirdPartyApp("com.google.android.keep")
        }
        binding.lpg.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_OTHER_APPS,
                MQTTConstants.APPS_AND_MORE_SCREEN_LGP_REMINDER
            )
            openThirdPartyApp("com.nstapp.bookmylpg")
        }
        binding.blinkIt.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_FOOD_AND_BEVARAGES,
                MQTTConstants.APPS_AND_MORE_SCREEN_BLINK_IT
            )
            openThirdPartyApp("com.grofers.customerapp")
        }
        binding.zeptoApp.setOnClickListener {
            SendClickEvent().sendClickPacket(
                MQTTConstants.APPS_AND_MORE_SCREEN_NAME,
                MQTTConstants.APPS_AND_MORE_SCREEN_FOOD_AND_BEVARAGES,
                MQTTConstants.APPS_AND_MORE_SCREEN_ZEPTO
            )
            openThirdPartyApp("com.zeptoconsumerapp")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openThirdPartyApp(name: String) {
        try {
            startActivity(activity?.packageManager?.getLaunchIntentForPackage(name))
        } catch (e: Exception) {
            Toast.makeText(activity, "Unavailable!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderQRCode(string: String) {
        binding.qrCodeLayout.visibility = View.VISIBLE
        binding.qrCode.visibility = View.VISIBLE
        binding.noInternetText.visibility = View.GONE
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