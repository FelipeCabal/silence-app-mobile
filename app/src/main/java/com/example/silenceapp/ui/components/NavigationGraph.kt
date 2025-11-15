package com.example.silenceapp.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag

@Composable
fun NavigationGraph(navController: NavHostController,innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
        composable("notify") { (NotifyScreen()) }
        composable("add") { (AddScreen()) }
        composable("profile") { ProfileScreen() }
        composable("search") { SearchScreen() }
    }
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
                            .testTag("screen-home"),
        contentAlignment = Alignment.Center
    ) {
        Text("home")
    }
}

@Composable
fun SearchScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
                            .testTag("screen-search"),
        contentAlignment = Alignment.Center
    ) {
        Text("search")
    }
}

@Composable
fun NotifyScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
                            .testTag("screen-notify"),
        contentAlignment = Alignment.Center
    ) {
        Text("notify")
    }
}

@Composable
fun AddScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
                            .testTag("screen-add"),
        contentAlignment = Alignment.Center
    ) {
        Text("add")
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
                            .testTag("screen-profile"),
        contentAlignment = Alignment.Center
    ) {
        Text("profile")
    }
}
