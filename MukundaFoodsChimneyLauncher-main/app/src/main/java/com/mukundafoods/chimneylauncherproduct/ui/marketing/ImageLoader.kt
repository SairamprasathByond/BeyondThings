package com.mukundafoods.chimneylauncherproduct.ui.marketing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mukundafoods.chimneylauncherproduct.R
import kotlinx.coroutines.*


class ImageLoader(
    imageView: ImageView,
    progressBar: ProgressBar?,
    context: Context,
    imageURL: String,
    name: String,
) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(FitCenter(), RoundedCorners(16))

            Glide.with(imageView)
                .asBitmap()
                .load(imageURL)
                .apply(requestOptions)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        context?.let {
                            progressBar?.visibility = View.GONE
                            imageView?.post { imageView.setImageResource(R.drawable.default_chimney) }

                        }
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {

                        context?.let {
                            MainScope().launch {
                                progressBar?.visibility = View.GONE
                                imageView?.post { imageView.setImageBitmap(resource) }
                                ImageStorageManager.saveToInternalStorage(it,
                                    resource,
                                    name)
                            }
                        }
                    }
                })
        }
    }
}