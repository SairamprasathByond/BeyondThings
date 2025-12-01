package com.mukundafoods.chimneylauncherproduct.ui.chimney

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.wrappers.Wrappers.packageManager
import com.mukundafoods.chimneylauncherproduct.R


class AppViewAdapter(
    private val context: Context, private  val listof_apps : ArrayList<String>, private val click: (Int) -> Unit
) : RecyclerView.Adapter<AppViewAdapter.ViewHolder>() {

    private var items: ArrayList<String> = ArrayList()



    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.app_recycler_list_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val itemsList = listof_apps.get(i)
        if(itemsList.equals("youtube")){
            viewHolder.img_app.setImageResource(R.drawable.youtube)
        }else if(itemsList.equals("spotify")){
            viewHolder.img_app.setImageResource(R.drawable.spotify)

        }else if(itemsList.equals("hotstar")){
            viewHolder.img_app.setImageResource(R.drawable.hotstar)

        }else if(itemsList.equals("netflix")){
            viewHolder.img_app.setImageResource(R.drawable.netflix)

        }else if(itemsList.equals("amazonmusic")){
            viewHolder.img_app.setImageResource(R.drawable.amazon_music)

        }else if(itemsList.equals("youtubemusic")){
            viewHolder.img_app.setImageResource(R.drawable.youtubemusic)

        }else{

        }
        viewHolder.itemView.setOnClickListener { click(i) }
    }

    override fun getItemCount(): Int {
        return listof_apps.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_app: ImageView = view.findViewById<View>(R.id.imageApp) as ImageView
    }

}

interface OnItemClick {
    fun onItemClick(uri: String)
}