package com.example.silenceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImageCarousel(imagePaths: List<String>, imageHeight: Dp = 500.dp) {
    val listState = rememberLazyListState()
    val firstVisible by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            itemsIndexed(imagePaths) { _, path ->
                // Coil puede cargar URLs y también paths de archivos locales si se usan con scheme "file://"
                val painter = rememberAsyncImagePainter(
                    model = if (path.startsWith("/")) "file://$path" else path

                )

                Image(
                    painter = painter,
                    contentDescription = "Imagen del post",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(end = 8.dp)
                )
            }
        }

        // Indicadores simples
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            imagePaths.forEachIndexed { index, _ ->
                val alpha = if (index == firstVisible) 1f else 0.4f
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .alpha(alpha)
                )
            }
        }
    }
}
