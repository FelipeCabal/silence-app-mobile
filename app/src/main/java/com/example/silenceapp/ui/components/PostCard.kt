package com.example.silenceapp.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.ui.theme.PaleMint
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.postBackgroundColor
import com.example.silenceapp.ui.theme.secondaryColor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostCard(
    post: Post, 
    onClick: (String) -> Unit,
    onLikeClick: ((String) -> Unit)? = null,
    onProfileClick: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    
    // images ahora es List<String> directamente
    val imageUris = post.images
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(post.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp)
            .clickable {
                if(post.remoteId != null){
                    onClick(post.remoteId)
                    }else{
                    Toast.makeText(context, "No se pudo cargar el post", Toast.LENGTH_SHORT).show()
                    }
            },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = postBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: Avatar + Nombre + Tiempo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circular - clickeable para ir al perfil
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PaleMint)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable {
                            if (!post.esAnonimo && onProfileClick != null) {
                                onProfileClick(post.userId)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (post.userImageProfile.isNullOrEmpty()) {
                        Text(
                            text = post.userName.firstOrNull()?.uppercase() ?: "U",
                            color = Color.Black,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(post.userImageProfile),
                            contentDescription = "Foto de perfil de ${post.userName}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Nombre y tiempo
                    Column {
                        Text(
                            text = if (post.esAnonimo) "Anónimo" else post.userName,
                            color = onBackgroundColor,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = formattedDate,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
            }

                // Texto del post
                post.description?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = onBackgroundColor,
                    )
                }
            Spacer(modifier = Modifier.height(12.dp))


                // Imágenes (todas las que tenga el post)
            if (imageUris.isNotEmpty()) {
                ImageCarousel(
                    images = imageUris,
                    context = context
                )
            }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de interacción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    //  horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Likes
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(secondaryColor.copy(0.15f))
                            .padding(horizontal = if (onLikeClick != null) 4.dp else 12.dp, vertical = 6.dp)
                    ) {
                        if (onLikeClick != null) {
                            IconButton(
                                onClick = { 
                                    post.remoteId?.let { onLikeClick(it) } 
                                },
                                modifier = Modifier.size(32.dp),
                                enabled = post.remoteId != null
                            ) {
                                Icon(
                                    imageVector = if (post.hasLiked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                    contentDescription = "Likes",
                                    tint = secondaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (post.hasLiked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                contentDescription = "Likes",
                                tint = secondaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = post.cantLikes.toString(),
                            color = onBackgroundColor,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))


                    // Comentarios
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.Gray.copy(0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comentarios",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.cantComentarios.toString(),
                            color = onBackgroundColor,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }

@Composable
fun ImageCarousel(
    images: List<String>,
    context: android.content.Context
) {
    val listState = rememberLazyListState()

    // Calcular el índice visible actual
    val currentIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Cuadrado estilo Instagram
    ) {
        // Carrusel de imágenes
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) {
            itemsIndexed(images) { _, imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            //.error(androidx.compose.ui.graphics.ColorPainter(Color.Red.copy(alpha = 0.3f)))
                            .build()
                    ),
                    contentDescription = "Imagen del post",
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .fillParentMaxHeight(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Indicadores de posición (solo si hay más de 1 imagen)
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                images.forEachIndexed { index, _ ->
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = null,
                        tint = if (index == currentIndex) Color.White else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(6.dp)
                    )
                }
            }
        }

        // Contador de imágenes (opcional, estilo Instagram)
        if (images.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${currentIndex + 1}/${images.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}


