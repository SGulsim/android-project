package com.example.imageloader.coil

import android.content.Context
import android.widget.ImageView
import com.example.imageloader.api.ImageLoader
import com.example.injector.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class CoilImagenitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(
            module {
                single<ImageLoader<ImageView>> { CoilImageLoader()}
             }
        )
    }
}