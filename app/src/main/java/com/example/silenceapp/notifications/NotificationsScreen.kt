package com.example.silenceapp.notifications

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.R

data class Notification(
    val id: Int,
    val user: String,
    val action: String,
    val time: String,
    val avatarUrl: String,
    val isRead: Boolean = false
)

val sampleNotifications = listOf(
    Notification(1, "Esperanza0", "le gusto tu publicacion", "Ahora", "https://via.placeholder.com/150", isRead = true),
    Notification(2, "Lupita18", "comento tu publicacion", "Ahora", "https://via.placeholder.com/150"),
    Notification(3, "Harrison69", "comento tu publicacion", "Ahora", "https://via.placeholder.com/150"),
    Notification(4, "", "Escribio en la comunidad M.E.S.S.I", "Hace 2m", "https://via.placeholder.com/150"),
    Notification(5, "Natalia", "te envio la solicitud de amistad", "Hace 2h", "https://via.placeholder.com/150"),
    Notification(6, "gaby69", "te invito a la comunidad LGTBI", "Hace 5h", "https://via.placeholder.com/150", isRead = true),
)

@Composable
fun NotificationsScreen() {
    Surface(
        color = Color(0xFF121212),
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(sampleNotifications) { notification ->
                NotificationItem(notification)
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    val backgroundColor = if (notification.isRead) Color(0xFF424242) else Color(0xFF303030)
    Surface(
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = notification.avatarUrl),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${notification.user} ${notification.action}",
                    color = Color.White
                )
            }
            Text(
                text = notification.time,
                color = Color.Gray,
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
