package com.example.silenceapp.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text(
                "SILENCE",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF2A8C4A)
            )
        },
        actions = {
            IconButton(onClick = { /* mensajes */ }) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chats",
                    tint = Color(0xFF2A8C4A)
                )
            }
        }
    )
}
