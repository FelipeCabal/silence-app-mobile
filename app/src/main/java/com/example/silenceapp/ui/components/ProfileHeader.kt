package com.example.silenceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.silenceapp.R
import com.example.silenceapp.data.remote.response.ProfileResponse
import com.example.silenceapp.ui.theme.PaleMint
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.secondaryColor

@Composable
fun ProfileHeader(profile: ProfileResponse) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(88.dp),
                shape = CircleShape,
                color = PaleMint
            ) {
                AsyncImage(
                    model = profile.imagen ?: R.drawable.avatar_placeholder,
                    contentDescription = "Avatar del usuario",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = onBackgroundColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                profile.username?.let {
                    if (it.isNotBlank()) {
                        Text(
                            text = "@$it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = secondaryColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Text(
                    text = profile.pais,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onBackgroundColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        profile.descripcion?.takeIf { it.isNotBlank() }?.let { description ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = onBackgroundColor,
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileStat(label = "Seguidores", value = profile.seguidores)
            ProfileStat(label = "Amigos", value = profile.amigos)
            ProfileStat(label = "Publicaciones", value = profile.publicacionesCount)
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = onBackgroundColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = onBackgroundColor
        )
    }
}