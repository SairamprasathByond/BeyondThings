package com.mukundafoods.chimneylauncherproduct.ui.settings.helpcenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.mukundafoods.chimneylauncherproduct.databinding.HelpCenterFragmentBinding
import com.mukundafoods.chimneylauncherproduct.ui.utils.Constants.buildSerialNumber

class HelpCenterFragment : Fragment() {

    private var _binding: HelpCenterFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = HelpCenterFragmentBinding.inflate(inflater, container, false)
        renderQRCode("https://mykitchenos.com/add_ticket_issue/${buildSerialNumber}}")
        return binding.root
    }

    private fun renderQRCode(string: String) {
        val multiFormatWriter = MultiFormatWriter()

        try {
            val mMatrix = multiFormatWriter.encode(string, BarcodeFormat.QR_CODE, 400, 400)
            val mEncoder = BarcodeEncoder()
            val mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
            binding.qrCode.setImageBitmap(mBitmap);//Setting generated QR code to imageView
        } catch (e: WriterException) {
            e.printStackTrace();
        }
    }

}