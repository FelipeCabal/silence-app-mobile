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
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.silenceapp.ui.theme.*

@Composable
fun BottomNavigationBar(navController: NavController)
{
    // Obtener la ruta actual correctamente
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar (
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.height(50.dp)
    ){
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(){
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Inicio",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Línea verde solo cuando está seleccionado
                    if (currentRoute == "home") {
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(26.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }},
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(){
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )}

                    // Línea verde solo cuando está seleccionado
                    if (currentRoute == "search") {
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(26.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }},
            selected = currentRoute == "search",
            onClick = { navController.navigate("search") }
        )
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(){
                        Icon(
                            Icons.Default.AddBox,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )}

                    if (currentRoute == "add-post") {
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(26.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }},
            selected =  currentRoute == "add-post",
            onClick = { navController.navigate("add-post") }
        )
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(){
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )}

                    if (currentRoute == "notifications") {
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(26.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }},
            selected =  currentRoute == "notifications",
            onClick = { navController.navigate("notifications") }
        )
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
            icon = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(){
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )}

                    if (currentRoute == "edit-profile") {
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(26.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }},
            selected =  currentRoute == "edit-profile",
            onClick = { navController.navigate("edit-profile") }
        )
    }
}
