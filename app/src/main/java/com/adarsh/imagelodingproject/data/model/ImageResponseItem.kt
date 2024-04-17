package com.adarsh.imagelodingproject.data.model


import com.google.gson.annotations.SerializedName

data class ImageResponseItem(
    @SerializedName("backupDetails")
    val backupDetails: BackupDetails?,
    @SerializedName("coverageURL")
    val coverageURL: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("mediaType")
    val mediaType: Int?,
    @SerializedName("publishedAt")
    val publishedAt: String?,
    @SerializedName("publishedBy")
    val publishedBy: String?,
    @SerializedName("thumbnail")
    val thumbnail: Thumbnail?,
    @SerializedName("title")
    val title: String?
)