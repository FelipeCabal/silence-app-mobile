package com.example.silenceapp.testingView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.silenceapp.ui.components.BottomNavigationBar
import androidx.compose.foundation.layout.padding
import com.example.silenceapp.ui.components.TopBar
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TestingViews() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { ScreenDummy("home") }
            composable("search") { ScreenDummy("search") }
            composable("profile") { ScreenDummy("profile") }
            composable("add") { ScreenDummy("add") }
            composable("notify") { ScreenDummy("notify") }
        }
    }
}

@Composable
fun ScreenDummy(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen-$name"),
        contentAlignment = Alignment.Center
    ) {
        Text(name)
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTestingView() {
    TestingViews()
}