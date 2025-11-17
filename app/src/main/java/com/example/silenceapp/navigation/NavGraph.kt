package com.example.silenceapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.silenceapp.view.auth.LoginScreen
import com.example.silenceapp.view.auth.RegisterScreen
import com.example.silenceapp.view.profile.EditProfileScreen
import com.example.silenceapp.viewmodel.AuthViewModel
import com.example.silenceapp.view.home.HomeScreen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.example.silenceapp.viewmodel.UserViewModel


@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {

    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    if (isAuthenticated == null) {
        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    //Debe cambiar esto cuando se implemente la homepage
    val homescreen = "edit-profile"

    val start = if (isAuthenticated == true) homescreen else "login"

    NavHost(
        navController = navController,
        startDestination = start
    ) {
        composable("login") {
            if (isAuthenticated == true) {
                LaunchedEffect(Unit) {
                    navController.navigate(homescreen) {
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
                    navController.navigate(homescreen) {
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
