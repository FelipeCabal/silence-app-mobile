package com.example.silenceapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.silenceapp.view.auth.LoginScreen
import com.example.silenceapp.view.auth.RegisterScreen
import com.example.silenceapp.view.profile.EditProfileScreen
import com.example.silenceapp.viewmodel.UserViewModel
import com.example.silenceapp.view.home.HomeScreen


@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {

    val userViewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController, userViewModel)
        }
        composable("register") {
            RegisterScreen(navController, userViewModel)
        }
        composable("edit-profile") {
            EditProfileScreen(navController, userViewModel)
        }
        composable("home") {
            HomeScreen()
        }
    }
}
