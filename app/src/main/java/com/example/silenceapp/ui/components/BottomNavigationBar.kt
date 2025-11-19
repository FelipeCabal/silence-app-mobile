package com.example.silenceapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController)
{
    // Obtener la ruta actual correctamente
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio", tint = Color(0xFF2A8C4A)) },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color(0xFF2A8C4A)) },
            selected = currentRoute == "search",
            onClick = { navController.navigate("search") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddBox, contentDescription = "Add", tint = Color(0xFF2A8C4A)) },
            selected =  currentRoute == "add",
            onClick = { navController.navigate("add") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color(0xFF2A8C4A)) },
            selected =  currentRoute == "notify",
            onClick = { navController.navigate("notify") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = Color(0xFF2A8C4A)) },
            selected =  currentRoute == "edit-profile",
            onClick = { navController.navigate("edit-profile") }
        )
    }
}
