package com.adarsh.imagelodingproject

import android.app.Application
import com.adarsh.imagelodingproject.imageloading.ContextProvider
import com.adarsh.imagelodingproject.imageloading.ImageLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ImageLoadingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextProvider.context = applicationContext
        ImageLoader.init()
    }
}