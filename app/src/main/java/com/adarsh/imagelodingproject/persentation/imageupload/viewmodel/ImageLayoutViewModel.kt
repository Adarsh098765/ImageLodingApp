package com.adarsh.imagelodingproject.persentation.imageupload.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarsh.imagelodingproject.data.model.ImageResponse
import com.adarsh.imagelodingproject.data.model.ImageResponseItem
import com.adarsh.imagelodingproject.data.repo.Repository
import com.adarsh.imagelodingproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ImageLayoutViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _imageData = MutableStateFlow<List<ImageResponseItem>?>(null)
    val imageData: StateFlow<List<ImageResponseItem>?> get() = _imageData

    init {
        fetchImageData()
    }

    private fun fetchImageData() {
        viewModelScope.launch {
            try {
                val response = repository.getData()
                response.collect { it ->
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            val filteredList = it.data?.filter { it.mediaType ==2 }
                            Log.i(TAG, "fetchImageData: "+filteredList)
                            _imageData.value = filteredList
                        }

                        Resource.Status.ERROR -> {}
                        Resource.Status.LOADING -> {}
                    }
                }

            } catch (e: Exception) {
                e.stackTrace
            }

        }
    }
}