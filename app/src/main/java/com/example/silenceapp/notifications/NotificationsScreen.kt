package com.example.silenceapp.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.R

enum class NotificationType {
    LIKE,
    COMMENT,
    FRIEND_REQUEST,
    GROUP_INVITE
}

data class Notification(
    val id: Int,
    val user: String,
    val action: String,
    val time: String,
    val avatar: Any? = null,
    val type: NotificationType
)

val sampleNotifications = listOf(
    Notification(1, "Esperanza0", "le gusto tu publicacion", "Ahora", type = NotificationType.LIKE),
    Notification(2, "GinaPao", "comento tu publicacion", "Ahora", type = NotificationType.COMMENT),
    Notification(3, "RezeBoom", "quiere ser tu amigo:", "Ahora", R.mipmap.ic_launcher, type = NotificationType.FRIEND_REQUEST),
    Notification(4, "Esperanza0", "le gusto tu publicacion", "Ahora", type = NotificationType.LIKE),
    Notification(5, "RezeBoom", "te invitó al grupo BOOM:", "Hace 5m", R.mipmap.ic_launcher, type = NotificationType.GROUP_INVITE),
    Notification(6, "JuanDC", "le gusto tu publicacion", "Hace 10m", type = NotificationType.LIKE),
    Notification(7, "MariaR", "comento tu publicacion", "Hace 15m", type = NotificationType.COMMENT),
    Notification(8, "CarlosP", "quiere ser tu amigo:", "Hace 20m", R.mipmap.ic_launcher, type = NotificationType.FRIEND_REQUEST),
    Notification(9, "AnaBanana", "te invitó al grupo 'Memes'", "Hace 1h", R.mipmap.ic_launcher, type = NotificationType.GROUP_INVITE),
    Notification(10, "PepeG", "le gusto tu publicacion", "Hace 2h", type = NotificationType.LIKE),
    Notification(11, "LauraV", "comento tu publicacion", "Hace 3h", type = NotificationType.COMMENT),
    Notification(12, "TheGamer", "quiere ser tu amigo:", "Ayer", R.mipmap.ic_launcher, type = NotificationType.FRIEND_REQUEST),
    Notification(13, "Luisito", "le gusto tu publicacion", "Ayer", type = NotificationType.LIKE),
    Notification(14, "MusicLover", "te invitó al grupo 'Rock 80s'", "2d", R.mipmap.ic_launcher, type = NotificationType.GROUP_INVITE),
    Notification(15, "ElAdmin", "comento tu publicacion", "3d", type = NotificationType.COMMENT)
)

@Composable
fun NotificationsScreen() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleNotifications) { notification ->
                NotificationItem(notification)
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (notification.type) {
                    NotificationType.LIKE, NotificationType.COMMENT -> {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            val iconVector = if (notification.type == NotificationType.LIKE) Icons.Default.Favorite else Icons.Default.ChatBubble
                            Icon(
                                imageVector = iconVector,
                                contentDescription = notification.type.name,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    else -> {
                        Image(
                            painter = rememberAsyncImagePainter(model = notification.avatar ?: R.mipmap.ic_launcher),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${notification.user} ${notification.action}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = notification.time,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (notification.type == NotificationType.FRIEND_REQUEST || notification.type == NotificationType.GROUP_INVITE) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
                ) {
                    Button(
                        onClick = {  },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text(text = "Rechazar", color = Color.White)
                    }
                    Button(
                        onClick = {  },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Aceptar", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    com.example.silenceapp.ui.theme.SilenceAppTheme {
        NotificationsScreen()
    }
}
