package com.adarsh.imagelodingproject.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> performGetOperation(networkCall: suspend () -> Resource<T>): Flow<Resource<T>> =
    flow {
        emit(Resource.loading())

        try {
            val response = networkCall.invoke()
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    response.data?.let { data ->
                        emit(Resource.success(data))
                        // Save call result asynchronously using coroutines (optional)
                    }
                }

                Resource.Status.ERROR -> emit(Resource.error(response.message!!))
                Resource.Status.LOADING -> { /* This shouldn't happen here, handle in networkCall */
                }
            }
        } catch (e: Exception) {
            // Handle network exceptions or other unexpected errors
            emit(Resource.error(e.localizedMessage ?: "Unknown Error"))
        }
    }