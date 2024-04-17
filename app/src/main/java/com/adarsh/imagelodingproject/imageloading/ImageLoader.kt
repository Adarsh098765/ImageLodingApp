package com.adarsh.imagelodingproject.imageloading


import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.adarsh.imagelodingproject.ImageLoadingApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


object ImageLoader {
    private const val MAX_MEMORY_CACHE_SIZE = 10 * 1024 * 1024 // 10MB
    private const val DISK_CACHE_DIRECTORY = "image_cache"
    private val memoryCache = LruCache<String, Bitmap>(MAX_MEMORY_CACHE_SIZE)

    fun init() {
        initDiskCache()
    }

    private fun initDiskCache() {
        val cacheDir = File(ImageLoadingApplication.instance.cacheDir, DISK_CACHE_DIRECTORY)
        cacheDir.mkdirs()
    }

    @Composable
    fun ImageLoader.AsyncImage(
        imageUrl: String,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        placeholder: @Composable (() -> Unit)? = null,
        error: @Composable ((String) -> Unit)? = null
    ) {
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var hasError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        LaunchedEffect(imageUrl) {
            isLoading = true
            hasError = false
            errorMessage = ""
            bitmap = try {
                withContext(Dispatchers.IO) {
                    loadImage(imageUrl){
                        hasError = true
                        errorMessage = it
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "AsyncImage: "+e.message, )
                hasError = true
                errorMessage = "Error loading image: $imageUrl"
                null
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "AsyncImage: "+e.message, )
                hasError = true
                errorMessage = "Unable to load image: $imageUrl"
                null
            }
            isLoading = false
        }

        if (isLoading) {
            placeholder?.invoke()
        } else if (hasError) {
            error?.invoke(errorMessage)
        } else {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = contentDescription,
                    modifier = modifier
                )
            }
        }
    }

    private suspend fun loadImage(imageUrl: String,error: ((String) -> Unit)?): Bitmap? {
        Log.d(TAG, "loadImage: Attempting to load image from $imageUrl")
        return try {
            memoryCache.get(imageUrl)
                ?: getDiskCachedBitmap(imageUrl)?.also { memoryCache.put(imageUrl, it) }
                ?: fetchImageFromUrl(imageUrl)?.also { memoryCache.put(imageUrl, it); saveToDiskCache(imageUrl, it) }
        } catch (e: Exception) {
            Log.e(TAG, "loadImage: Exception - ${e.message}")
            error?.invoke(e.message.toString())
            e.printStackTrace()
            null
        }
    }


    private suspend fun getDiskCachedBitmap(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val cacheFile = File(getCacheDirectory(), getCacheFileName(imageUrl))
            if (cacheFile.exists()) {
                BitmapFactory.decodeFile(cacheFile.absolutePath)
            } else {
                null
            }
        }
    }

    private suspend fun fetchImageFromUrl(imageUrl: String): Bitmap {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(imageUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.connect()


                if (connection.responseCode == HttpURLConnection.HTTP_OK) {

                    val contentType = connection.contentType
                    if (contentType != null && contentType.startsWith("image/")) {
                        val inputStream = connection.inputStream
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap != null && bitmap.width > 0 && bitmap.height > 0) {
                            bitmap
                        } else {
                            throw Exception("Invalid image data")
                        }
                    } else {
                        throw Exception("Unsupported image format")
                    }
                } else {
                    throw Exception("HTTP error code: ${connection.responseCode}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "fetchImageFromUrl: $imageUrl - ${e.message}")
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "fetchImageFromUrl: $imageUrl - ${e.message}")
                throw e
            }
        }
    }

    private fun saveToDiskCache(imageUrl: String, bitmap: Bitmap) {
        val cacheFile = File(getCacheDirectory(), getCacheFileName(imageUrl))
        try {
            FileOutputStream(cacheFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                outputStream.flush()
            }
        } catch (e: IOException) {
            Log.e(TAG, "saveToDiskCache: "+e.message, )
            e.printStackTrace()
        }
    }

    private fun getCacheDirectory(): File {
        val cacheDir = File(ImageLoadingApplication.instance.cacheDir, DISK_CACHE_DIRECTORY)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        return cacheDir
    }

    private fun getCacheFileName(imageUrl: String): String {
        return imageUrl.hashCode().toString()
    }
}


