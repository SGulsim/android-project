package com.example.imageloader.api

interface ImageLoader<T> {
    fun loadImage(imageView: T, model: Any?)
}