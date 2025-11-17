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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.ui.theme.PaleMint
import com.example.silenceapp.ui.theme.backgroundInteraction
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.postBackgroundColor
import com.example.silenceapp.ui.theme.secondaryColor

@Composable
fun PostCard(post: Post) {
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
                .padding(8.dp)
        ) {
            // Header: Avatar + Nombre + Tiempo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circular
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            color = PaleMint
                        )
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.user.nombre.first().uppercase(),
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nombre y tiempo
                Column {
                    Text(
                        text = post.user.nombre,
                        color = onBackgroundColor,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = post.createdAt.toString(),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto del post
            post.description?.let{
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = onBackgroundColor,
                )
            }

            // Imagen
            post.imagen?.let { url ->
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                        //.clip(RoundedCornerShape(8.dp)),
                   contentScale = ContentScale.FillHeight
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contador de likes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(secondaryColor.copy(0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = "likes",
                        tint = secondaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.cantlikes.toString(),
                        color = onBackgroundColor,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(backgroundInteraction)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "comments",
                        tint = postBackgroundColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.cantComentarios.toString(),
                        color = postBackgroundColor,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}