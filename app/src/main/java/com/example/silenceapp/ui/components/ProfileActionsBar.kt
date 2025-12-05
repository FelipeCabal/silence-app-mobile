package com.example.silenceapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import com.example.silenceapp.ui.theme.secondaryColor
import com.example.silenceapp.viewmodel.RelationshipStatus

@Composable
fun ProfileActionsBar(
    isOwnProfile: Boolean,
    relationshipStatus: RelationshipStatus,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onReport: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (isOwnProfile) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onEdit,
                    shape = buttonShape()
                ) {
                    Text(text = "Editar perfil", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onShare,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = secondaryColor),
                    shape = buttonShape()
                ) {
                    Text(text = "Compartir", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onPrimaryAction,
                    enabled = relationshipStatus != RelationshipStatus.PENDING,
                    shape = buttonShape()
                ) {
                    val label = when (relationshipStatus) {
                        RelationshipStatus.NONE -> "Agregar"
                        RelationshipStatus.PENDING -> "Pendiente"
                        RelationshipStatus.ACCEPTED -> "Eliminar"
                    }
                    Text(text = label, fontWeight = FontWeight.Bold)
                }

                if (relationshipStatus == RelationshipStatus.ACCEPTED) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onSecondaryAction,
                        shape = buttonShape()
                    ) {
                        Text(text = "Mensaje", fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onReport,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = buttonShape()
            ) {
                Text(text = "Reportar usuario", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun buttonShape(): Shape = RoundedCornerShape(10.dp)