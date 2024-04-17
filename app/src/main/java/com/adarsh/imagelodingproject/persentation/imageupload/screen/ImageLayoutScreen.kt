package com.adarsh.imagelodingproject.persentation.imageupload.screen

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ElevatedCard
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adarsh.imagelodingproject.imageloading.ImageLoader
import com.adarsh.imagelodingproject.imageloading.ImageLoader.AsyncImage
import com.adarsh.imagelodingproject.persentation.imageupload.viewmodel.ImageLayoutViewModel

@Composable
fun ImageLayoutScreen(viewModel: ImageLayoutViewModel = hiltViewModel()) {
    val data by viewModel.imageData.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(data ?: emptyList(), key = { it.coverageURL ?: "" }) { imageItem ->
            ElevatedCard(
                modifier = Modifier
                    .padding(5.dp)
                    .aspectRatio(1f)

            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    ImageLoader.AsyncImage(
                        imageUrl = imageItem.coverageURL.toString(),
                        contentDescription = imageItem.title,
                        modifier = Modifier.fillMaxSize(),
                        error = { Text(text = it, fontSize = 10.sp) },
                        placeholder = {
                            ShimmerEffect(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "")
    val shimmerX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing)
        ), label = ""
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(shimmerX, 0f),
                    end = Offset(shimmerX + 50f, 0f)
                )
            )
    )
}