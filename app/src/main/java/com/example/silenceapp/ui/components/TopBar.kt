package com.example.silenceapp.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text("SILENCE", style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = { /* Buscar */ }) {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            }
            IconButton(onClick = { /* Notificaciones */ }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
            }
        }
    )
}
