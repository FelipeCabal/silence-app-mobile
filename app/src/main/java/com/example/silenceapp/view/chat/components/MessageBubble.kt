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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.silenceapp.data.local.entity.Message
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
    val myMessageColor = MaterialTheme.colorScheme.surface
    val otherMessageColorLight = DimGray
    val otherMessageColorDark = DimGray
    
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
                myMessageColor
            else 
                if (isDarkTheme) otherMessageColorDark else otherMessageColorLight,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Contenido del mensaje
                Text(
                    text = message.content,
                    color = if (isMyMessage) 
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
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
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
                                MaterialTheme.colorScheme.secondary
                            else 
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
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
