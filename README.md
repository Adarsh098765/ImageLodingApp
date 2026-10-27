# Custom Image Loader for Jetpack Compose

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?logo=kotlin&logoColor=white) ![Jetpack](https://img.shields.io/badge/Jetpack-Compose-4285F4?logo=jetpackcompose&logoColor=white) ![Retrofit](https://img.shields.io/badge/Retrofit-2.9-48B983) ![Hilt](https://img.shields.io/badge/Hilt-DI-2496ED) ![Android](https://img.shields.io/badge/Android-minSdk%2026-3DDC84?logo=android&logoColor=white)

A small **Coil-style image-loading library written from scratch**, plus a demo app that uses it to show a remote image feed. It shows how image loaders work inside: async decoding, two-level caching, and Compose integration.

## Features

- **`AsyncImage` composable** with `placeholder` and `error` slots, the same shape as Coil's API.
- **Two-level cache**: an in-memory `LruCache` for instant hits, then a **disk cache** in `cacheDir/image_cache`, then the network.
- **Off the main thread**: downloads and decoding run on `Dispatchers.IO` using coroutines.
- **Disk cache writes** compress to JPEG to save storage.
- Demo feed loaded with **Retrofit**, with dependencies injected by **Hilt**.

## Usage

```kotlin
// once, e.g. in Application.onCreate()
ImageLoader.init()

// in any composable
ImageLoader.AsyncImage(
    imageUrl = url,
    contentDescription = "Thumbnail",
    modifier = Modifier.size(120.dp),
    placeholder = { CircularProgressIndicator() },
    error = { message -> Text("Failed: $message") }
)
```

## Load path

```
AsyncImage(url)
  ├─ memory cache hit?  → show bitmap
  ├─ disk cache hit?    → decode file → put in memory cache → show
  └─ network            → download + decode → save to disk + memory → show
```

## Tech stack

Kotlin · Jetpack Compose · Coroutines · LruCache · Retrofit · Hilt · MVVM

## Run it

Clone the repo, set `BASEURL` in `api.properties`, open it in Android Studio and run (API 26+).

---

Built by **[Adarsh Thakur](https://adarsh098765.github.io/adarshthakur.github.io/)**, an Android engineer working with Kotlin and Jetpack Compose · [LinkedIn](https://www.linkedin.com/in/adarsh-thakur-b31281171/)

