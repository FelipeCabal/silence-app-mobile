package com.example.silenceapp.view.chat.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.silenceapp.ui.theme.onBackgroundColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Input de mensaje con detección de escritura
 */
@Composable
fun MessageInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    onTypingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val coroutineScope = rememberCoroutineScope()
    var typingJob by remember { mutableStateOf<Job?>(null) }

    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 2.dp,
        color = onBackgroundColor.copy(alpha = 0.1f)
    )
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TextField
            OutlinedTextField(
                value = messageText,
                onValueChange = { newText ->
                    onMessageChange(newText)
                    
                    // Cancelar job anterior
                    typingJob?.cancel()
                    
                    // Manejar indicador de escritura
                    if (newText.isNotEmpty()) {
                        onTypingChange(true)
                        
                        // Después de 3 segundos sin escribir, detener indicador
                        typingJob = coroutineScope.launch {
                            delay(3000)
                            onTypingChange(false)
                        }
                    } else {
                        // Si borra todo, detener indicador inmediatamente
                        onTypingChange(false)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp, max = 120.dp),
                placeholder = { 
                    Text(
                        "Escribe un mensaje...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ) 
                },
                shape = RoundedCornerShape(24.dp),
                enabled = enabled,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
            
            // Botón enviar
            FloatingActionButton(
                onClick = {
                    android.util.Log.d("MessageInput", "🔘 Botón enviar presionado, texto='$messageText', blank=${messageText.isBlank()}")
                    if (messageText.isNotBlank()) {
                        android.util.Log.d("MessageInput", "✅ Mensaje válido, llamando onSend()")
                        typingJob?.cancel()
                        onTypingChange(false)
                        onSend()
                    } else {
                        android.util.Log.d("MessageInput", "❌ Mensaje vacío o en blanco, no se envía")
                    }
                },
                modifier = Modifier.size(48.dp),
                containerColor = if (messageText.isNotBlank()) 
                    MaterialTheme.colorScheme.primary
                else 
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (messageText.isNotBlank())
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar mensaje"
                )
            }
        }
    }
}
