package com.example.silenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.silenceapp.ui.FeedViewModel
import com.example.silenceapp.ui.components.NavigationGraph
import com.example.silenceapp.ui.components.TopBar
import com.example.silenceapp.ui.components.BottomNavigationBar
import com.example.silenceapp.ui.theme.SilenceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkTheme = isSystemInDarkTheme()
            SilenceAppTheme(darkTheme = true) {
                val navController = rememberNavController()
                val vm: FeedViewModel = viewModel()

                Scaffold(
                    topBar = { TopBar() },
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavigationGraph(navController = navController, innerPadding = innerPadding, vm = vm)
                }
            }
        }
    }
}
