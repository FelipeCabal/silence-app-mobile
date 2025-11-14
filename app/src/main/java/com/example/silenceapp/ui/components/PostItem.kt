package com.example.silenceapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.silenceapp.data.Post
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton

@Composable
fun PostItem(post: Post) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // ---------- CABECERA (avatar, nombre, hora, menú) ----------
            Row(verticalAlignment = Alignment.CenterVertically) {

                AsyncImage(
                    model = post.avatarUrl,
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.width(8.dp))

                Column {
                    Text(post.author, fontWeight = FontWeight.Bold)
                    Text(
                        post.timeAgo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Spacer(Modifier.weight(1f))

                IconButton(onClick = { /* opciones post */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
            }

            // ---------- TEXTO DEL POST ----------
            Spacer(Modifier.height(8.dp))
            Text(
                text = post.text,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )

            // ---------- IMAGEN DEL POST (opcional) -----  -----
            post.imageUrl?.let { url ->
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = url,
                    contentDescription = "imagen del post",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            // ---------- BOTONES DE ACCIÓN ----------
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Botón Like
                TextButton(onClick = { /* acción Like */ }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Me gusta")
                    Spacer(Modifier.width(6.dp))
                    Text("201")
                }

                // Botón Comentario
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { /* acción Comentario */ }) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comentarios")
                    Spacer(Modifier.width(6.dp))
                    Text("123")
                }

                Spacer(Modifier.weight(1f))

                // Botón Compartir
                IconButton(onClick = { /* compartir */ }) {
                    Icon(Icons.Default.Send, contentDescription = "Compartir")
                }
            }
        }
    }
}
