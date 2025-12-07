package com.example.silenceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import com.example.silenceapp.data.remote.response.User
import androidx.compose.ui.res.painterResource
import com.example.silenceapp.R


@Composable
fun PersonCard(
    user: User,
    onFollow: (User) -> Unit,
    requestSent: Boolean = false
) {
    var localRequestSent by remember { mutableStateOf(requestSent) }

    LaunchedEffect(requestSent) {
        localRequestSent = requestSent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(40.dp))

        Image(
            painter = rememberAsyncImagePainter(
                model = user.imagen,
                error = painterResource(R.drawable.avatar_placeholder),
                fallback = painterResource(R.drawable.avatar_placeholder),
                placeholder = painterResource(R.drawable.avatar_placeholder)
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 6.dp)
                .size(100.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(40.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = user.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onFollow(user)
                    localRequestSent = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!localRequestSent) Color(0xFF4CAF50) else Color.Gray
                ),
                enabled = !localRequestSent
            ) {
                Text(
                    text = if (localRequestSent) "Solicitud enviada" else "Follow"
                )
            }
        }
    }
}
