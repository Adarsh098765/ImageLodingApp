package com.adarsh.imagelodingproject.data.remote

import com.adarsh.imagelodingproject.data.model.ImageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET(image_url)
    suspend fun getData(
       @Query("limit") limit: Int? = 100
    ): Response<ImageResponse>
}