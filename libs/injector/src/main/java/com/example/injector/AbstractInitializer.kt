package com.example.injector

import androidx.startup.Initializer

abstract class AbstractInitializer<T>: Initializer<T> {
    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(InjectorInitializer::class.java)
    }
}