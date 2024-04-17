package com.adarsh.imagelodingproject.data.repo

import com.adarsh.imagelodingproject.data.remote.RemoteDataSource
import com.adarsh.imagelodingproject.utils.performGetOperation
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
   suspend fun getData() = performGetOperation { remoteDataSource.getData() }
}