package com.example.silenceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.ui.theme.PaleMint
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.secondaryColor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun PostDetailContent(
    post: Post,
    onLikeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val gson = Gson()

    // Parseo imágenes (post.images es JSON string o null)
    val images: List<String> = try {
        if (!post.images.isNullOrEmpty()) {
            gson.fromJson(post.images, Array<String>::class.java).toList()
        } else emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    // Parseo comentarios (post.comentarios es JSON string o null)
    val comentarios: List<Comment> = try {
        if (!post.comentarios.isNullOrEmpty()) {
            // Usamos TypeToken para evitar problemas si Comment tiene tipos complejos
            val listType = object : TypeToken<List<Comment>>() {}.type
            gson.fromJson<List<Comment>>(post.comentarios, listType)
        } else emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Header: Avatar + Nombre
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PaleMint)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (post.esAnonimo) {
                    Text(
                        text = "A",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else if (post.userImageProfile.isNullOrEmpty()) {
                    Text(
                        text = post.userName.firstOrNull()?.uppercase() ?: "U",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(post.userImageProfile),
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (post.esAnonimo) "Anónimo" else post.userName,
                style = MaterialTheme.typography.bodyLarge,
                color = onBackgroundColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido: imágenes o texto
        if (images.isNotEmpty()) {
            ImageCarousel(imagePaths = images)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Descripción del post
        if (!post.description.isNullOrEmpty()) {
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyLarge,
                color = onBackgroundColor,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de interacción (Like y Comentarios) - usando componente compartido
        PostActions(
            post = post,
            onLikeClick = onLikeClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sección comentarios
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Comentarios", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (comentarios.isEmpty()) {
                Text("Sé el primero en comentar", style = MaterialTheme.typography.bodySmall)
            } else {
                comentarios.forEach { comment ->
                    CommentCard(comment = comment)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
