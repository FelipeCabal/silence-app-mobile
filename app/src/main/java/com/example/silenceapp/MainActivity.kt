package com.example.silenceapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.silenceapp.navigation.NavGraph
import com.example.silenceapp.ui.theme.SilenceAppTheme
import com.example.silenceapp.ui.theme.backgroundColor
import androidx.navigation.compose.rememberNavController
import com.example.silenceapp.utils.NotificationHelper

class MainActivity : ComponentActivity() {
    
    // Launcher para solicitar permiso de notificaciones
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.util.Log.d("MainActivity", "✅ Permiso de notificaciones concedido")
        } else {
            android.util.Log.w("MainActivity", "⚠️ Permiso de notificaciones denegado")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(this)
        
        // Solicitar permiso de notificaciones en Android 13+
        requestNotificationPermissionIfNeeded()
        
        setContent {
            SilenceAppTheme {
                val navController = rememberNavController()

                Surface(color = backgroundColor) {
                    NavGraph(navController)
                    //TestingViews(navController)
                }
            }
        }
    }
    
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    android.util.Log.d("MainActivity", "✅ Ya tiene permiso de notificaciones")
                }
                else -> {
                    android.util.Log.d("MainActivity", "📱 Solicitando permiso de notificaciones")
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
