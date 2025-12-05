package com.example.silenceapp.view.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Indicador animado de usuarios escribiendo
 */
@Composable
fun TypingIndicator(
    chatId: String,
    typingUserNames: Map<String, Set<String>>,
    modifier: Modifier = Modifier
) {
    val userNamesTyping = typingUserNames[chatId] ?: emptySet()
    
    AnimatedVisibility(
        visible = userNamesTyping.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animación de 3 puntos
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    BouncingDot(delay = index * 150L)
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Texto según cantidad de usuarios con nombres
            Text(
                text = when {
                    userNamesTyping.size == 1 -> "${userNamesTyping.first()} está escribiendo..."
                    userNamesTyping.size == 2 -> {
                        val names = userNamesTyping.toList()
                        "${names[0]} y ${names[1]} están escribiendo..."
                    }
                    else -> "${userNamesTyping.size} personas están escribiendo..."
                },
                fontSize = 13.sp,
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

/**
 * Punto animado con efecto bounce
 */
@Composable
private fun BouncingDot(
    delay: Long = 0L,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        delay(delay)
        while (true) {
            isVisible = false
            delay(300)
            isVisible = true
            delay(600)
        }
    }
    
    Box(
        modifier = modifier
            .size(8.dp)
            .background(
                color = if (isVisible) Color.Gray else Color.Gray.copy(alpha = 0.3f),
                shape = CircleShape
            )
    )
}
