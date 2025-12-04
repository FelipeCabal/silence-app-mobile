package com.example.silenceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.secondaryColor

@Composable
fun PostActions(
    post: Post,
    onLikeClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de Likes
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
                        contentDescription = "Me gusta",
                        tint = secondaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = if (post.hasLiked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                    contentDescription = "Me gusta",
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

        // Comentarios (solo visualización)
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
