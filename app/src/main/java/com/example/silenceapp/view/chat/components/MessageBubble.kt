package com.example.silenceapp.view.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.silenceapp.data.local.entity.Message
import com.example.silenceapp.ui.theme.PaleMint
import com.example.silenceapp.ui.theme.DarkGreen
import com.example.silenceapp.ui.theme.lightGray
import com.example.silenceapp.ui.theme.DimGray
import java.text.SimpleDateFormat
import java.util.*

/**
 * Burbuja de mensaje individual
 */
@Composable
fun MessageBubble(
    message: Message,
    isMyMessage: Boolean,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    // Usar colores del tema
    val myMessageColorLight = PaleMint // Verde claro del tema para modo claro
    val myMessageColorDark = DarkGreen // Verde oscuro del tema para modo oscuro
    val otherMessageColorLight = lightGray // Gris claro del tema
    val otherMessageColorDark = DimGray // Gris oscuro del tema
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMyMessage) 16.dp else 4.dp,
                bottomEnd = if (isMyMessage) 4.dp else 16.dp
            ),
            color = if (isMyMessage)
                if (isDarkTheme) myMessageColorDark else myMessageColorLight
            else
                if (isDarkTheme) otherMessageColorDark else otherMessageColorLight,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Nombre del usuario (solo en mensajes de otros)
                if (!isMyMessage && message.userName != null) {
                    Text(
                        text = "@${message.userName}",
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.9f) else Color(0xFF2196F3),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Contenido del mensaje
                Text(
                    text = message.content,
                    color = if (isMyMessage)
                        if (isDarkTheme) Color.White else Color.Black
                    else
                        if (isDarkTheme) Color.White else Color.Black,
                    fontSize = 15.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Timestamp y check marks
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formatTime(message.timestamp),
                        fontSize = 11.sp,
                        color = if (isMyMessage)
                            if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Gray
                        else
                            Color.Gray
                    )
                    
                    // Check marks solo en mis mensajes
                    if (isMyMessage) {
                        Icon(
                            imageVector = if (message.isRead) 
                                Icons.Default.DoneAll // ✓✓
                            else 
                                Icons.Default.Done, // ✓
                            contentDescription = if (message.isRead) "Leído" else "Enviado",
                            tint = if (message.isRead) 
                                Color(0xFF03A9F4) // Azul cuando está leído
                            else 
                                if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formatear timestamp a hora legible
 */
private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
