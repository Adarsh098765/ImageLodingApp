package com.adarsh.imagelodingproject.data.model


import com.google.gson.annotations.SerializedName

data class Thumbnail(
    @SerializedName("aspectRatio")
    val aspectRatio: Int?,
    @SerializedName("basePath")
    val basePath: String?,
    @SerializedName("domain")
    val domain: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("key")
    val key: String?,
    @SerializedName("qualities")
    val qualities: List<Int?>?,
    @SerializedName("version")
    val version: Int?
)