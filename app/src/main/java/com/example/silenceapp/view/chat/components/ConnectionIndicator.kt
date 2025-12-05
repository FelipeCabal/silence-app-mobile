package com.example.silenceapp.view.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.silenceapp.data.remote.socket.ConnectionState

/**
 * Indicador visual del estado de conexión Socket.IO
 */
@Composable
fun ConnectionIndicator(
    state: ConnectionState,
    modifier: Modifier = Modifier,
    showText: Boolean = false
) {
    when (state) {
        is ConnectionState.Connected -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Conectado",
                    tint = Color(0xFF4CAF50), // Verde
                    modifier = Modifier.size(20.dp)
                )
                if (showText) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Conectado",
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
        
        is ConnectionState.Connecting -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (showText) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Conectando...",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        is ConnectionState.Reconnecting -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFFFF9800) // Naranja
                )
                if (showText) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reconectando (${state.attempt})...",
                        fontSize = 12.sp,
                        color = Color(0xFFFF9800)
                    )
                }
            }
        }
        
        is ConnectionState.Error -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Color(0xFFF44336), // Rojo
                    modifier = Modifier.size(20.dp)
                )
                if (showText) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Error de conexión",
                        fontSize = 12.sp,
                        color = Color(0xFFF44336)
                    )
                }
            }
        }
        
        is ConnectionState.Disconnected -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "Desconectado",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                if (showText) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Desconectado",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
