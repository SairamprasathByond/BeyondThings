package com.mukundafoods.chimneylauncherproduct.ui.marketing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mukundafoods.chimneylauncherproduct.R
import com.mukundafoods.chimneylauncherproduct.ui.backend.MarketingDataItem


class BrochuresAndPriceListRecyclerAdapter(
    private val context: Context,
    private val marketingFragment: MarketingFragment
) :
    RecyclerView.Adapter<BrochuresAndPriceListRecyclerAdapter.ViewHolder>() {

    private var items: ArrayList<MarketingDataItem> = ArrayList()

    fun setItems(marketingDataItemList: List<MarketingDataItem>){
        items.clear()
        items.addAll(marketingDataItemList)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.marketing_recycler_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
       // ImageLoader(viewHolder.img_android, null, context, items[i].image, "${items[i].name}.png")
        if(ImageStorageManager.isFilePresent(context, "${items[i].name}.png")){
            viewHolder.img_android.setImageBitmap(ImageStorageManager.getImageFromInternalStorage(context, "${items[i].name}.png"))
        }else{
            ImageLoader(viewHolder.img_android, null, context, items[i].image, "${items[i].name}.png")
        }


        viewHolder.name.text = items[i].name

        viewHolder.parent.setOnClickListener {
            marketingFragment.onItemClick(items[i].files)
        }
  }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var img_android: ImageView = view.findViewById<View>(R.id.image) as ImageView
        var name : TextView = view.findViewById(R.id.text)
        var parent: ConstraintLayout = view.findViewById(R.id.parent)
    }

}