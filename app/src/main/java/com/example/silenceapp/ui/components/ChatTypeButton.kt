package com.example.silenceapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.postBackgroundColor
import com.example.silenceapp.ui.theme.secondaryColor

/**
 * Botón de selección de tipo (Grupo/Comunidad)
 */
@Composable
fun ChatTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) secondaryColor else postBackgroundColor,
            contentColor = if (isSelected) Color.Black else onBackgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected) {
            BorderStroke(1.dp, onBackgroundColor.copy(alpha = 0.2f))
        } else null
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
