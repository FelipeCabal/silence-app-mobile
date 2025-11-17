package com.example.silenceapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            label = { Text("Buscar") },
            selected = false,
            onClick = { navController.navigate("search") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddBox, contentDescription = "Add") },
            label = { Text("Add") },
            selected = false,
            onClick = { navController.navigate("add") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notificaciones") },
            label = { Text("Notificaciones") },
            selected = false,
            onClick = { navController.navigate("notify") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = false,
            onClick = { navController.navigate("edit-profile") }
        )
    }
}
