package com.example.silenceapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun BottomNavigationBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "icon-home") },
            label = { Text("home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            modifier = Modifier.testTag("btn-home")
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "icon-search") },
            label = { Text("search") },
            selected = currentRoute == "search",
            onClick = { navController.navigate("search") },
            modifier = Modifier.testTag("btn-search")
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.AddBox, contentDescription = "icon-add") },
            label = { Text("add") },
            selected = currentRoute == "add",
            onClick = { navController.navigate("add") },
            modifier = Modifier.testTag("btn-add")
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "icon-notify") },
            label = { Text("notify") },
            selected = currentRoute == "notify",
            onClick = { navController.navigate("notify") },
            modifier = Modifier.testTag("btn-notify")
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "icon-profile") },
            label = { Text("profile") },
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
            modifier = Modifier.testTag("btn-profile")
        )
    }
}
