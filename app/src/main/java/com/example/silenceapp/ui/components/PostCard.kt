package com.example.silenceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun PostCard(post: Post) {
    val context = LocalContext.current
    val gson = Gson()
    val imageUris = post.images?.let {
        try {
            gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    } ?: emptyList()
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(post.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp),
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
                // Avatar circular
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PaleMint)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.userName.firstOrNull()?.uppercase() ?: "U",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineSmall
                    )
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

            // Imágenes (todas las que tenga el post)
            if (imageUris.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    imageUris.forEach { uri ->
                        val imageData = if (uri.startsWith("content://") || uri.startsWith("file://")) {
                            uri
                        } else {
                            // Es una ruta absoluta, convertir a File
                            java.io.File(uri)
                        }
                        
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(imageData)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = "Imagen del post",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp, max = 400.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de interacción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Likes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(secondaryColor.copy(0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = "Likes",
                        tint = secondaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.cantLikes.toString(),
                        color = onBackgroundColor,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

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
