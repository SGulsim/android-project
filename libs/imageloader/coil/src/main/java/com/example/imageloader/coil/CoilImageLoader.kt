package com.example.imageloader.coil

import android.widget.ImageView
import coil3.load
import com.example.imageloader.api.ImageLoader

internal class CoilImageLoader: ImageLoader<ImageView> {
    override fun loadImage(imageView: ImageView, model: Any?) {
        imageView.load(model)
    }
}
