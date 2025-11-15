package com.example.silenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import com.example.silenceapp.ui.theme.SilenceAppTheme
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.silenceapp.ui.theme.backgroundColor
import com.example.silenceapp.ui.components.TopBar
import com.example.silenceapp.ui.components.BottomNavigationBar
import com.example.silenceapp.ui.components.NavigationGraph
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SilenceAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = backgroundColor) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SilenceAppTheme {
        Greeting("Android")
    }
}

@Composable
fun NavBar() {
    SilenceAppTheme(darkTheme = true) {
        val navController = rememberNavController()
        Scaffold(
            //val vm: FeedViewModel = viewModel()
            topBar = { TopBar() },
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            NavigationGraph(navController = navController, innerPadding = innerPadding )//,vm = vm)
        }
    }
}
