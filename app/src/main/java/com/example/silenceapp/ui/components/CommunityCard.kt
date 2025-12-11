package com.example.silenceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.data.remote.response.Community

@Composable
fun CommunityCard(
    community: Community,
    onFollow: (Community) -> Unit,
    requestSent: Boolean = false
) {
    android.util.Log.d("CommunityCard", "🏘️ Renderizando CommunityCard")
    android.util.Log.d("CommunityCard", "   - id: ${community.id}")
    android.util.Log.d("CommunityCard", "   - nombre: ${community.nombre}")
    android.util.Log.d("CommunityCard", "   - imagen: ${community.imagen}")
    android.util.Log.d("CommunityCard", "   - requestSent: $requestSent")

    var localRequestSent by remember { mutableStateOf(requestSent) }

    LaunchedEffect(requestSent) {
        localRequestSent = requestSent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF4CAF50),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = community.nombre.first().uppercase(),
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Image(
                painter = rememberAsyncImagePainter(
                    model = community.imagen,
                    error = painterResource(android.R.drawable.ic_menu_report_image),
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(16.dp)) {

                Text(
                    community.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = community.lastMessage ?: "Sin mensajes",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Button(
                        onClick = {
                            onFollow(community)
                            localRequestSent = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!localRequestSent) Color(0xFF4CAF50) else Color.Gray
                        ),
                        enabled = !localRequestSent
                    ) {
                        Text(
                            text = if (localRequestSent) "Already a Member" else "Follow"
                        )
                    }
                }
            }
        }
    }
}
