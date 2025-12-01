package com.mukundafoods.chimneylauncherproduct.ui.marketing

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mukundafoods.chimneylauncherproduct.databinding.FragmentMarketingBinding
import com.mukundafoods.chimneylauncherproduct.ui.MyViewModelFactory
import com.mukundafoods.chimneylauncherproduct.ui.backend.MainRepository
import com.mukundafoods.chimneylauncherproduct.ui.backend.RetrofitService
import com.mukundafoods.chimneylauncherproduct.ui.database.ChimneyDatabaseBuilder
import com.mukundafoods.chimneylauncherproduct.ui.database.ChimneyDatabaseHelperImpl
import com.mukundafoods.chimneylauncherproduct.ui.sharedpreference.Data
import java.io.File


class MarketingFragment : Fragment(), OnItemClick {

    private var _binding: FragmentMarketingBinding? = null

    private val binding get() = _binding!!
    private lateinit var marketingViewModel: MarketingViewModel

    private lateinit var productsAdapter: ProductsRecyclerAdapter
    private lateinit var brochuresAndPriceListAdapter: BrochuresAndPriceListRecyclerAdapter
    private lateinit var testimonialRecyclerAdapter: TestimonialRecyclerAdapter
    private lateinit var othersRecyclerAdapter: OthersRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        marketingViewModel =
            ViewModelProvider(
                this,
                MyViewModelFactory(
                    MainRepository(RetrofitService.getInstance()),
                    ChimneyDatabaseHelperImpl(ChimneyDatabaseBuilder.getInstance(activity?.applicationContext!!))
                )
            ).get(
                MarketingViewModel::class.java
            )

        _binding = FragmentMarketingBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun initRecyclerView() {

        binding.productsRecycler.apply {
            layoutManager =
                LinearLayoutManager(parentFragment?.context, LinearLayoutManager.HORIZONTAL, false)
            productsAdapter = ProductsRecyclerAdapter(context, this@MarketingFragment)
            adapter = productsAdapter
        }

        binding.brochuresRecycler.apply {
            layoutManager =
                LinearLayoutManager(parentFragment?.context, LinearLayoutManager.HORIZONTAL, false)
            brochuresAndPriceListAdapter =
                BrochuresAndPriceListRecyclerAdapter(context, this@MarketingFragment)
            adapter = brochuresAndPriceListAdapter
        }

        binding.testimonialRecycler.apply {
            layoutManager =
                LinearLayoutManager(parentFragment?.context, LinearLayoutManager.HORIZONTAL, false)
            testimonialRecyclerAdapter = TestimonialRecyclerAdapter(context, this@MarketingFragment)
            adapter = testimonialRecyclerAdapter
        }

        binding.othersRecycler.apply {
            layoutManager =
                LinearLayoutManager(parentFragment?.context, LinearLayoutManager.HORIZONTAL, false)
            othersRecyclerAdapter = OthersRecyclerAdapter(context, this@MarketingFragment)
            adapter = othersRecyclerAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        marketingViewModel.getData()

        /*    marketingViewModel.getProducts()
            marketingViewModel.getBrochures()
            marketingViewModel.getTestimonials()
            marketingViewModel.getOthers()*/


        if (Data.isDownloadMarketingDataEnabled()) {
            binding.refresh.visibility = View.VISIBLE
        } else {
            binding.refresh.visibility = View.GONE
        }

        marketingViewModel.productsList.observe(viewLifecycleOwner) {

            if (it.data.isEmpty()) {
                return@observe
            }
            productsAdapter.setItems(it.data)
            productsAdapter.notifyDataSetChanged()
        }

        marketingViewModel.brochuresList.observe(viewLifecycleOwner) {
            brochuresAndPriceListAdapter.setItems(it.data)
            brochuresAndPriceListAdapter.notifyDataSetChanged()
        }

        marketingViewModel.testimonialsList.observe(viewLifecycleOwner) {
            testimonialRecyclerAdapter.setItems(it.data)
            testimonialRecyclerAdapter.notifyDataSetChanged()
        }

        marketingViewModel.othersList.observe(viewLifecycleOwner) {
            othersRecyclerAdapter.setItems(it.data)
            othersRecyclerAdapter.notifyDataSetChanged()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        binding.refresh.setOnClickListener {
            ImageStorageManager.deleteVideosFromInternalStorage()
            marketingViewModel.clearDB()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(uri: String) {
        val fileName =
            "${uri.split("/").last().split(".")[0]}.${uri.split("/").last().split(".")[1]}"
        val downloadFileDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/video"
        )

        var destination =
            "$downloadFileDir/"
        destination += fileName


        val intent = Intent(
            Intent.ACTION_VIEW,
        )

        val contentUri = FileProvider.getUriForFile(
            this.requireActivity(),
            "${this.requireActivity().packageName}.FileProvider",
            File(destination)
        )
        val type =
            if (uri.split("/").last().split(".")[1] == "mp4") "video/mp4" else "image/*"
        intent.setDataAndType(contentUri, type)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(
            intent
        )
    }
}