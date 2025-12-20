package com.example.injector

import android.content.Context
import androidx.startup.Initializer
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin

internal class InjectorInitializer : Initializer<Koin>  {

    override fun create(context: Context): Koin {
        return startKoin {
            androidContext(context.applicationContext)
        }.koin
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }

}
