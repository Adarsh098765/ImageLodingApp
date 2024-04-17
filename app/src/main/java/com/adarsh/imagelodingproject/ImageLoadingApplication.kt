package com.adarsh.imagelodingproject

import android.app.Application
import com.adarsh.imagelodingproject.imageloading.ImageLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ImageLoadingApplication : Application() {
    companion object{
        lateinit var instance: ImageLoadingApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this@ImageLoadingApplication
        ImageLoader.init()
    }
}