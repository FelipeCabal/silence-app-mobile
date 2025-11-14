package com.example.silenceapp.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.silenceapp.ui.HomeScreen
import com.example.silenceapp.ui.FeedViewModel
import com.example.silenceapp.ui.ProfileScreen
import com.example.silenceapp.ui.NotifyScreen
import com.example.silenceapp.ui.AddScreen
import com.example.silenceapp.ui.SearchScreen
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun NavigationGraph(navController: NavHostController,innerPadding: PaddingValues, vm: FeedViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(vm) }
        composable("notify") { (NotifyScreen()) }
        composable("add") { (AddScreen()) }
        composable("profile") { ProfileScreen() }
        composable("search") { SearchScreen() }
    }
}
