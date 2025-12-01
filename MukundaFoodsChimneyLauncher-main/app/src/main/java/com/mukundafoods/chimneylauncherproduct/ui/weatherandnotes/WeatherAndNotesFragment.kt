package com.mukundafoods.chimneylauncherproduct.ui.weatherandnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mukundafoods.chimneylauncherproduct.databinding.FragmentWeatherAndNotesBinding

class WeatherAndNotesFragment : Fragment() {

    private var _binding: FragmentWeatherAndNotesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(WeatherAndNotesViewModel::class.java)

        _binding = FragmentWeatherAndNotesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}