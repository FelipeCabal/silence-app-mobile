package com.example.silenceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.R
import com.example.silenceapp.data.local.entity.Notification
import com.example.silenceapp.data.local.entity.NotificationType
import java.sql.Timestamp

@Composable
fun NotificationItem(
    notification: Notification,
    onNotificationClicked: () -> Unit
) {
    val backgroundColor = if (notification.alreadySeen) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    Surface(
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNotificationClicked() },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
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
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = getTimeAgo(notification.time),
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
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Aceptar",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Button(
                        onClick = {  },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text(
                            text = "Rechazar",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getTimeAgo(timestamp: Timestamp): String {
    val now = System.currentTimeMillis()
    val time = timestamp.time
    val diff = now - time

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> if (days == 1L) "Hace 1 día" else "Hace $days días"
        hours > 0 -> if (hours == 1L) "Hace 1 hora" else "Hace $hours horas"
        minutes > 0 -> if (minutes == 1L) "Hace 1 minuto" else "Hace $minutes minutos"
        else -> "Ahora"
    }
}
