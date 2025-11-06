package com.example.silenceapp.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.R

data class Notification(
    val id: Int,
    val user: String,
    val action: String,
    val time: String,
    val avatar: Any,
    val isRead: Boolean = false
)

val sampleNotifications = listOf(
    Notification(1, "Esperanza0", "le gusto tu publicacion", "Ahora", R.mipmap.ic_launcher, isRead = true),
    Notification(2, "Lupita18", "comento tu publicacion", "Ahora", R.mipmap.ic_launcher),
    Notification(3, "Harrison69", "comento tu publicacion", "Ahora", R.mipmap.ic_launcher),
    Notification(4, "", "Escribio en la comunidad M.E.S.S.I", "Hace 2m", R.mipmap.ic_launcher),
    Notification(5, "Natalia", "te envio la solicitud de amistad", "Hace 2h", R.mipmap.ic_launcher),
    Notification(6, "gaby69", "te invito a la comunidad LGTBI", "Hace 5h", R.mipmap.ic_launcher, isRead = true),
    Notification(7, "JuanPerez", "le dio me encanta a tu foto", "Hace 10h", R.mipmap.ic_launcher),
    Notification(8, "CommunityN", "ha publicado en Novedades", "Hace 12h", R.mipmap.ic_launcher, isRead = true),
    Notification(9, "AnaLopez", "te envió una solicitud de mensaje", "Ayer", R.mipmap.ic_launcher),
    Notification(10, "CarlosR", "comentó en tu publicación: \"¡Qué genial!\"", "Ayer", R.mipmap.ic_launcher),
    Notification(11, "LauraG", "le gustó tu comentario", "2d", R.mipmap.ic_launcher, isRead = true),
    Notification(12, "GamingZone", "inició un nuevo evento", "2d", R.mipmap.ic_launcher),
    Notification(13, "PedroM", "te ha mencionado en un comentario", "3d", R.mipmap.ic_launcher),
    Notification(14, "SofiaH", "le gustó tu publicación", "3d", R.mipmap.ic_launcher),
    Notification(15, "ArtLovers", "compartió una nueva obra de arte", "4d", R.mipmap.ic_launcher, isRead = true),
    Notification(16, "MiguelA", "quiere conectar contigo", "5d", R.mipmap.ic_launcher)
)

@Composable
fun NotificationsScreen() {
    var notifications by remember { mutableStateOf(sampleNotifications) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(items = notifications, key = { it.id }) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = {
                        notifications = notifications.map {
                            if (it.id == notification.id) {
                                it.copy(isRead = true)
                            } else {
                                it
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    val backgroundColor = if (notification.isRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    Surface(
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = notification.avatar),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${notification.user} ${notification.action}",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = notification.time,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen()
}
