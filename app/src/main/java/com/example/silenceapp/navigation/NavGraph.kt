package com.example.silenceapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.silenceapp.view.notifications.NotificationsScreen
import com.example.silenceapp.view.auth.LoginScreen
import com.example.silenceapp.view.auth.RegisterScreen
import com.example.silenceapp.view.home.HomeScreen
import com.example.silenceapp.view.profile.EditProfileScreen
import com.example.silenceapp.viewmodel.AuthViewModel
import com.example.silenceapp.viewmodel.UserViewModel


@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {

    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    //DEVELOPMENT: Skip login and go directly to notifications
    val startDestination = "notifications"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("notifications") {
            NotificationsScreen()
        }
        composable("login") {
            if (isAuthenticated == true) {
                LaunchedEffect(Unit) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                LoginScreen(navController, authViewModel)
            }
        }
        composable("register") {
            if (isAuthenticated == true) {
                LaunchedEffect(Unit) {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                RegisterScreen(navController, authViewModel)
            }
        }
        composable("edit-profile") {
            if (isAuthenticated != true) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("edit-profile") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                EditProfileScreen(navController, authViewModel, userViewModel)
            }
        }
        composable("home") {
            if (isAuthenticated != true) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            } else {
                HomeScreen()
            }
        }
    }
}