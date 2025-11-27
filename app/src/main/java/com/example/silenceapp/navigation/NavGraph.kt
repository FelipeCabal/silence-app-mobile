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
import androidx.navigation.navArgument
import com.example.silenceapp.view.auth.LoginScreen
import com.example.silenceapp.view.auth.RegisterScreen
import com.example.silenceapp.view.posts.CreatePostScreen
import com.example.silenceapp.view.posts.PostScreen
import com.example.silenceapp.view.profile.EditProfileScreen
import com.example.silenceapp.view.profile.ProfileScreen
import com.example.silenceapp.viewmodel.AuthViewModel
import com.example.silenceapp.view.notifications.NotificationsScreen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.example.silenceapp.viewmodel.UserViewModel
import com.example.silenceapp.viewmodel.PostViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import com.example.silenceapp.ui.components.TopBar
import com.example.silenceapp.ui.components.BottomNavigationBar
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.example.silenceapp.view.home.HomeScreen
import com.example.silenceapp.viewmodel.ProfileViewModel

@Composable
fun NavGraph(navController: NavHostController) {

    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val ROUTE_ADD_POST = "add-post"
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()


    if (isAuthenticated == null) {
        Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        
        return
    }

    //Debe cambiar esto cuando se implemente la homepage
    val homescreen = "profile/self"
    val start = if (isAuthenticated == true) homescreen else "login"

    // Obtener la ruta actual correctamente
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Ocultar barras en login y register
    val showBar = currentRoute !in listOf("login", "register")
    val showBarTop = currentRoute !in listOf("login", "edit-profile", "register") && 
                     !(currentRoute?.startsWith("add-post") ?: false)

    Scaffold(
        topBar = {
            if (showBarTop) TopBar()
        },
        bottomBar = {
            if (showBar) BottomNavigationBar(navController)

        }
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = start,
            modifier = Modifier.padding(innerPadding)
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
            composable(
                route = "profile/{userId}",
                arguments = listOf(navArgument("userId") { defaultValue = "self" })
            ) { backStackEntry ->
                val userIdArg = backStackEntry.arguments?.getString("userId") ?: "self"
                if (isAuthenticated != true) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login") {
                            popUpTo("profile/{userId}") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    ProfileScreen(navController, userIdArg, profileViewModel)
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
            composable("notifications") {
                if (isAuthenticated != true) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login") {
                            popUpTo("notifications") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    NotificationsScreen()
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
                    PostScreen()
                }
            }
            composable ("$ROUTE_ADD_POST?imageUri = {imageUri}",
            listOf( navArgument("imageUri"){
            nullable = true
            defaultValue = null
        })
        ){  backStack ->
            val imageUri = backStack.arguments?.getString("imageUri")
            CreatePostScreen(
                navController = navController,
                imageUri = imageUri,
                authViewModel = authViewModel,
                postViewModel = postViewModel
            )
        }
        }
    }
}