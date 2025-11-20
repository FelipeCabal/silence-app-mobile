package com.example.silenceapp.navigation

import androidx.compose.runtime.Composable
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
import com.example.silenceapp.view.testingView.TestingViews
import com.example.silenceapp.view.profile.EditProfileScreen
import com.example.silenceapp.viewmodel.UserViewModel
import com.example.silenceapp.viewmodel.PostViewModel

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {

    val userViewModel: UserViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val ROUTE_ADD_POST = "add-post"

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
                userViewModel = userViewModel,
                postViewModel = postViewModel
            )
        }
        composable("home") { backStackEntry ->
            PostScreen(postViewModel = postViewModel, key = backStackEntry.id)
        }
        
        composable("testing") {
            TestingViews()
        }

        composable("edit-profile") {
            EditProfileScreen(navController, userViewModel)
        }
    }
}
