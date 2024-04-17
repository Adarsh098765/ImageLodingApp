
# ImageLoading App


The main goal of the app is to load and display images from a remote data source while also implementing image caching to improve performance and reduce data usage.


# Usage

Initialization

Before using ImageLoader, ensure that you initialize it by calling the init() method. This initializes the disk cache directory.









Certainly, let's go through each function in detail:

1. **init()**:

```kotlin
fun init() {
    initDiskCache()
}

private fun initDiskCache() {
    val cacheDir = File(ContextProvider.context?.cacheDir, DISK_CACHE_DIRECTORY)
    cacheDir.mkdirs()
}
```
The `init()` function is responsible for initializing the disk cache. It calls the `initDiskCache()` function, which creates a directory named `image_cache` within the application's cache directory using the `ContextProvider.context?.cacheDir` and the `DISK_CACHE_DIRECTORY` constant. If the directory doesn't exist, it is created using the `mkdirs()` function.

2. **AsyncImage()**:
```kotlin
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
            Log.e(TAG, "AsyncImage: " + e.message)
            hasError = true
            errorMessage = "Error loading image: $imageUrl"
            null
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "AsyncImage: " + e.message)
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
```
The `AsyncImage()` function is a Composable function that handles the asynchronous loading and display of an image. It takes the following parameters:
- `imageUrl`: The URL of the image to be loaded.
- `contentDescription`: The content description for the image.
- `modifier`: The Modifier to be applied to the image.
- `placeholder`: An optional Composable function to be displayed while the image is loading.
- `error`: An optional Composable function to be displayed if an error occurs while loading the image.

The function uses `remember` to maintain the state of the image loading process, including the `Bitmap`, the loading state, the error state, and the error message. It then uses a `LaunchedEffect` to start the image loading process when the `imageUrl` changes.

Inside the `LaunchedEffect`, the function attempts to load the image by calling the `loadImage()` function. If the image is successfully loaded, the `bitmap` state is updated. If an `IOException` or any other exception occurs, the error state and error message are updated accordingly.

Finally, the function displays the appropriate UI element based on the current state of the image loading process: the placeholder if the image is still loading, the error message if an error occurred, or the loaded image if the image was successfully loaded.

3. **loadImage()**:
```kotlin
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
```
The `loadImage()` function is responsible for loading an image from the memory cache, disk cache, or the network. It follows these steps:

1. It first checks the memory cache using the `memoryCache.get(imageUrl)` call. If the image is found in the memory cache, it is returned.
2. If the image is not found in the memory cache, it checks the disk cache by calling the `getDiskCachedBitmap(imageUrl)` function. If the image is found in the disk cache, it is added to the memory cache and then returned.
3. If the image is not found in either the memory or disk cache, it fetches the image from the network by calling the `fetchImageFromUrl(imageUrl)` function. The fetched image is then added to both the memory and disk caches and returned.

If any exception occurs during the image loading process, the function logs the error and returns `null`.

4. **getDiskCachedBitmap()**:
```kotlin
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
```
The `getDiskCachedBitmap()` function is responsible for retrieving a cached image from the disk cache. It takes the `imageUrl` as a parameter and returns the `Bitmap` if the cached image file exists in the disk cache directory, or `null` if the file is not found.

The function uses the `withContext(Dispatchers.IO)` block to perform the disk I/O operation on a background thread. It then constructs the file path for the cached image using the `getCacheDirectory()` and `getCacheFileName(imageUrl)` functions, and checks if the file exists. If the file exists, it decodes the file into a `Bitmap` and returns it.

5. **fetchImageFromUrl()**:
```kotlin
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
```
The `fetchImageFromUrl()` function is responsible for fetching an image from a given URL. It takes the `imageUrl` as a parameter and returns the fetched `Bitmap`.

The function uses the `withContext(Dispatchers.IO)` block to perform the network operation on a background thread. It then creates an `HttpURLConnection` to the `imageUrl`, connects to the URL, and reads the input stream. The `BitmapFactory.decodeStream()` method is used to decode the input stream into a `Bitmap`.

If any `IOException` or other exception occurs during the network operation, the function logs the error and returns `null`.

6. **saveToDiskCache()**:
```kotlin
private fun saveToDiskCache(imageUrl: String, bitmap: Bitmap) {
    val cacheFile = File(getCacheDirectory(), getCacheFileName(imageUrl))
    try {
        FileOutputStream(cacheFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
        }
    } catch (e: IOException) {
        Log.e(TAG, "saveToDiskCache: " + e.message)
        e.printStackTrace()
    }
}
```
The `saveToDiskCache()` function is responsible for saving a `Bitmap` to the disk cache. It takes the `imageUrl` and the `Bitmap` to be cached as parameters.

The function first constructs the file path for the cached image using the `getCacheDirectory()` and `getCacheFileName(imageUrl)` functions. It then creates a `FileOutputStream` for the cache file and uses it to compress the `Bitmap` into a JPEG file with 100% quality. Finally, it flushes the output stream.

If any `IOException` occurs during the file write operation, the function logs the error.

7. **getCacheDirectory()**:
```kotlin
private fun getCacheDirectory(): File {
    val cacheDir = File(ContextProvider.context?.cacheDir, DISK_CACHE_DIRECTORY)
    if (!cacheDir.exists()) {
        cacheDir.mkdirs()
    }
    return cacheDir
}
```
The `getCacheDirectory()` function is responsible for retrieving the disk cache directory. It constructs the path for the `image_cache` directory within the application's cache directory using the `ContextProvider.context?.cacheDir` and the `DISK_CACHE_DIRECTORY` constant.

If the `image_cache` directory does not exist, the function creates it using the `mkdirs()` method and returns the directory.

8. **getCacheFileName()**:

```kotlin
private fun getCacheFileName(imageUrl: String): String {
    return imageUrl.hashCode().toString()
}
```
The `getCacheFileName()` function is responsible for generating a unique file name for a cached image. It takes the `imageUrl` as a parameter and returns the hash code of the URL as a string.

This function is used to construct the file path for the cached image in the disk cache directory.


# Customization
You can customize the behavior of ImageLoader by modifying its constants and methods:

1.MAX_MEMORY_CACHE_SIZE: Adjust the maximum size of the memory cache for images.

2.DISK_CACHE_DIRECTORY: Change the name of the disk cache directory.

3.initDiskCache(): Modify the initialization of the disk cache directory if needed.

4.loadImage(), getDiskCachedBitmap(), fetchImageFromUrl(): Customize image loading and caching strategies.

# Caching Mechanism

The ImageLoader uses a two-level caching mechanism:

    Memory Cache:
        1. The ImageLoader maintains an in-memory cache using an LruCache (Least Recently Used cache) with a maximum size of 10 MB.
        2. When an image is loaded, it is first checked in the memory cache. If found, the cached Bitmap is returned.
    Disk Cache:
        1. If the image is not found in the memory cache, the ImageLoader then checks the disk cache for the cached image.
        2. The disk cache is stored in the image_cache directory within the application's cache directory.
        3. The file name for the cached image is generated using the hash code of the image URL.
        4. If the image is found in the disk cache, it is loaded, added to the memory cache, and returned.

If the image is not found in either the memory or disk cache, the ImageLoader fetches the image from the network, saves it to both the memory and disk caches, and then returns the Bitmap.

