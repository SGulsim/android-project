package com.example.ourproject.util

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.example.ourproject.R

object ImageLoaderWrapper {
    private var imageLoader: ImageLoader? = null

    fun init(context: Context) {
        if (imageLoader == null) {
            imageLoader = ImageLoader.Builder(context.applicationContext)
                .crossfade(true)
                .build()
        }
    }

    /**
     * Загружает изображение по URL в ImageView
     */
    fun loadImage(
        imageView: ImageView,
        url: String?,
        placeholder: Int = R.drawable.ic_cloud,
        error: Int = R.drawable.ic_cloud
    ) {
        imageView.load(url) {
            placeholder(placeholder)
            error(error)
            crossfade(true)
        }
    }

    /**
     * Загружает изображение по ресурсу в ImageView
     */
    fun loadImageResource(
        imageView: ImageView,
        resourceId: Int
    ) {
        imageView.load(resourceId)
    }

    /**
     * Загружает изображение с кастомным запросом
     */
    suspend fun loadImageWithRequest(
        imageView: ImageView,
        url: String?,
        placeholder: Int = R.drawable.ic_cloud,
        error: Int = R.drawable.ic_cloud
    ): ImageResult {
        val request = ImageRequest.Builder(imageView.context)
            .data(url)
            .target(imageView)
            .placeholder(placeholder)
            .error(error)
            .crossfade(true)
            .build()
        return getImageLoader().execute(request)
    }

    /**
     * Получить экземпляр ImageLoader для кастомного использования
     */
    fun getImageLoader(): ImageLoader {
        requireNotNull(imageLoader) { "ImageLoader not initialized. Call init() first." }
        return imageLoader!!
    }
}

