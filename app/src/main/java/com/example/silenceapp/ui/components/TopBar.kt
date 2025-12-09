package com.example.silenceapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import  com.example.silenceapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController? = null) {
    TopAppBar(
        title = {
            Text(
                "SILENCE",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        },
        actions = {
            IconButton(onClick = { 
                navController?.navigate("chats")
            }) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chats",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}
