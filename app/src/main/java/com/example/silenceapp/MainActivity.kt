package com.example.silenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.silenceapp.navigation.NavGraph
import com.example.silenceapp.ui.theme.SilenceAppTheme
import com.example.silenceapp.ui.theme.backgroundColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        setContent {
            SilenceAppTheme {
                Surface (color = backgroundColor){
                    NavGraph()
                }
            }
        }
    }
}
