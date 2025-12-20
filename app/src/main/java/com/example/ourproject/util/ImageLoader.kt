package com.example.ourproject.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import coil.size.Scale
import coil.size.Size
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.example.ourproject.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageLoaderWrapper {
    private var imageLoader: ImageLoader? = null

    fun init(context: Context) {
        if (imageLoader == null) {
            imageLoader = ImageLoader.Builder(context.applicationContext)
                .crossfade(true)
                .crossfade(300)
                .allowHardware(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .respectCacheHeaders(false)
                .build()
        }
    }

    fun loadImage(
        imageView: ImageView,
        url: String?,
        @DrawableRes placeholder: Int = R.drawable.ic_cloud,
        @DrawableRes error: Int = R.drawable.ic_cloud,
        crossfade: Boolean = true,
        size: Size? = null
    ) {
        imageView.load(url) {
            placeholder(placeholder)
            error(error)
            if (crossfade) {
                crossfade(300)
            }
            size?.let { size(it) }
            scale(Scale.FIT)
        }
    }

    fun loadImageResource(
        imageView: ImageView,
        @DrawableRes resourceId: Int
    ) {
        imageView.load(resourceId) {
            crossfade(true)
        }
    }

    fun loadCircularImage(
        imageView: ImageView,
        url: String?,
        @DrawableRes placeholder: Int = R.drawable.ic_cloud,
        @DrawableRes error: Int = R.drawable.ic_cloud
    ) {
        imageView.load(url) {
            placeholder(placeholder)
            error(error)
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    fun loadRoundedImage(
        imageView: ImageView,
        url: String?,
        radius: Float = 16f,
        @DrawableRes placeholder: Int = R.drawable.ic_cloud,
        @DrawableRes error: Int = R.drawable.ic_cloud
    ) {
        imageView.load(url) {
            placeholder(placeholder)
            error(error)
            crossfade(true)
            transformations(RoundedCornersTransformation(radius))
        }
    }

    fun loadImageWithTransformations(
        imageView: ImageView,
        url: String?,
        transformations: List<Transformation>,
        @DrawableRes placeholder: Int = R.drawable.ic_cloud,
        @DrawableRes error: Int = R.drawable.ic_cloud
    ) {
        imageView.load(url) {
            placeholder(placeholder)
            error(error)
            crossfade(true)
            transformations(transformations)
        }
    }

    suspend fun loadImageWithRequest(
        imageView: ImageView,
        url: String?,
        @DrawableRes placeholder: Int = R.drawable.ic_cloud,
        @DrawableRes error: Int = R.drawable.ic_cloud
    ): ImageResult {
        val request = ImageRequest.Builder(imageView.context)
            .data(url)
            .target(imageView)
            .placeholder(placeholder)
            .error(error)
            .crossfade(300)
            .build()
        return getImageLoader().execute(request)
    }

    suspend fun loadImageAsBitmap(
        context: Context,
        url: String?
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()
            val result = getImageLoader().execute(request)
            if (result is SuccessResult) {
                (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loadImageAsDrawable(
        context: Context,
        url: String?
    ): Drawable? = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            val result = getImageLoader().execute(request)
            if (result is SuccessResult) {
                result.drawable
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun preloadImage(
        context: Context,
        url: String?
    ) = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            getImageLoader().enqueue(request)
        } catch (e: Exception) {
        }
    }

    suspend fun preloadImages(
        context: Context,
        urls: List<String?>
    ) = withContext(Dispatchers.IO) {
        urls.forEach { url ->
            try {
                preloadImage(context, url)
            } catch (e: Exception) {
            }
        }
    }

    fun clearMemoryCache() {
        imageLoader?.memoryCache?.clear()
    }

    suspend fun clearDiskCache(context: Context) = withContext(Dispatchers.IO) {
        imageLoader?.diskCache?.clear()
    }

    suspend fun clearAllCache(context: Context) = withContext(Dispatchers.IO) {
        clearMemoryCache()
        clearDiskCache(context)
    }

    fun getImageLoader(): ImageLoader {
        requireNotNull(imageLoader) {
            "ImageLoader not initialized. Call init() first in Application or MainActivity."
        }
        return imageLoader!!
    }

    fun isInitialized(): Boolean = imageLoader != null

    fun shutdown() {
        imageLoader?.shutdown()
        imageLoader = null
    }
}
