package com.example.silenceapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.primaryColor

@Composable
fun CommentInput(
    comment: String,
    onCommentChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Escribe un comentario...")
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = onBackgroundColor,
                    unfocusedTextColor = Color.Gray.copy(alpha = 0.8f),
                ),
                singleLine = false
            )

            IconButton(
                onClick = onSendClick,
                enabled = comment.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (comment.isNotBlank()) primaryColor
                        else Color.Gray.copy(alpha = 0.3f)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar comentario",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
