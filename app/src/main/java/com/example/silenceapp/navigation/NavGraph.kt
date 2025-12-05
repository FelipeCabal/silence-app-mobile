package com.example.silenceapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.view.auth.LoginScreen
import com.example.silenceapp.view.auth.RegisterScreen
import com.example.silenceapp.view.chat.ChatListScreen
import com.example.silenceapp.view.chat.ChatScreen
import com.example.silenceapp.view.chat.CreateChatScreen
import com.example.silenceapp.view.posts.CreatePostScreen
import com.example.silenceapp.view.profile.EditProfileScreen
import com.example.silenceapp.viewmodel.AuthViewModel
import com.example.silenceapp.view.notifications.NotificationsScreen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.navigation.NavType
import com.example.silenceapp.viewmodel.UserViewModel
import com.example.silenceapp.viewmodel.PostViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import com.example.silenceapp.ui.components.TopBar
import com.example.silenceapp.ui.components.BottomNavigationBar
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.example.silenceapp.view.posts.PostDetailScreen
import com.example.silenceapp.view.posts.PostScreenSimple

@Composable
fun NavGraph(navController: NavHostController) {

    val context = LocalContext.current
    val authDataStore = remember { AuthDataStore(context) }
    
    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val ROUTE_ADD_POST = "add-post"
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()


    if (isAuthenticated == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        return
    }

    //Debe cambiar esto cuando se implemente la homepage
    val homescreen = "home"
    val start = if (isAuthenticated == true) homescreen else "login"

    // Obtener la ruta actual correctamente
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Ocultar barras en login y register
    val showBar = currentRoute !in listOf("login", "register")
    val showBarTop = currentRoute !in listOf("login", "edit-profile", "register", "chats", "create-chat") &&
                     !(currentRoute?.startsWith("add-post") ?: false) &&
                     !(currentRoute?.startsWith("chat/") ?: false)

    Scaffold(
        topBar = {
            if (showBarTop) TopBar(navController)
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
           /** composable("home") {
                //TestingViews(navController)
                PostScreenSimple(
                    onPostClick = { postId ->
                        navController.navigate("post/$postId")
                    },
                    onCreatePostClick = {}
                )
            }*/

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
                    NotificationsScreen(authViewModel = authViewModel)
                }
            }
            composable("chats") {
                if (isAuthenticated != true) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login") {
                            popUpTo("chats") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    ChatListScreen(navController, authViewModel = authViewModel)
                }
            }
            composable("create-chat") {
                if (isAuthenticated != true) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login") {
                            popUpTo("create-chat") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    CreateChatScreen(navController, authViewModel = authViewModel)
                }
            }
            
            // Ruta para ChatScreen individual
            composable(
                route = "chat/{chatId}/{chatName}/{chatType}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("chatName") { type = NavType.StringType },
                    navArgument("chatType") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                if (isAuthenticated != true) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login") {
                            popUpTo("chat/{chatId}/{chatName}/{chatType}") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    ChatScreen(
                        chatId = backStackEntry.arguments?.getString("chatId") ?: "",
                        chatName = backStackEntry.arguments?.getString("chatName") ?: "",
                        chatType = backStackEntry.arguments?.getString("chatType") ?: "group",
                        navController = navController,
                        authDataStore = authDataStore
                    )
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
                    PostScreenSimple(
                        viewModel = postViewModel, // Pasar el ViewModel
                        onPostClick = { postId ->
                            navController.navigate("post/$postId")
                        },
                        onCreatePostClick = {
                            navController.navigate(ROUTE_ADD_POST)
                        }
                    )
                }
            }
            composable(
                "$ROUTE_ADD_POST?imageUri = {imageUri}",
                listOf(navArgument("imageUri") {
                    nullable = true
                    defaultValue = null
                })
            ) { backStack ->
                val imageUri = backStack.arguments?.getString("imageUri")
                CreatePostScreen(
                    navController = navController,
                    imageUri = imageUri,
                    authViewModel = authViewModel,
                    postViewModel = postViewModel
                )
            }
            composable("post/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                // Si en la home pasas remoteId (string) esto funciona.
                PostDetailScreen(postId = id, postViewModel = postViewModel)
            }


        }
    }
}
