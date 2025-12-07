package com.example.silenceapp.view.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.silenceapp.data.remote.dto.FriendDto
import com.example.silenceapp.ui.theme.backgroundColor
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.primaryColor
import com.example.silenceapp.viewmodel.GroupInvitationViewModel

@Composable
fun InviteFriendsDialog(
    groupId: String,
    token: String,
    onDismiss: () -> Unit,
    viewModel: GroupInvitationViewModel = viewModel()
) {
    val friends by viewModel.friends.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val invitationSent by viewModel.invitationSent.collectAsState()
    
    // Cargar amigos con datos del grupo al abrir el dialog
    LaunchedEffect(Unit) {
        viewModel.loadFriendsWithGroupData(token, groupId)
    }
    
    // Mostrar mensaje cuando se envía invitación
    LaunchedEffect(invitationSent) {
        invitationSent?.let { name ->
            kotlinx.coroutines.delay(2000)
            viewModel.clearInvitationSent()
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Invitar Amigos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = onBackgroundColor
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = onBackgroundColor
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Mensaje de éxito
                invitationSent?.let { name ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = primaryColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Invitación enviada a $name",
                            modifier = Modifier.padding(12.dp),
                            color = primaryColor,
                            fontSize = 14.sp
                        )
                    }
                }
                
                // Mensaje de error
                error?.let { errorMsg ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = errorMsg,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }
                
                // Loading o lista
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primaryColor)
                    }
                } else if (friends.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes amigos para invitar",
                            color = onBackgroundColor.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(friends) { friend ->
                            FriendInviteItem(
                                friend = friend,
                                isMember = viewModel.isMember(friend.id),
                                hasPendingInvitation = viewModel.hasPendingInvitation(friend.id),
                                onInvite = {
                                    viewModel.sendInvitation(token, groupId, friend.id, friend.nombre)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendInviteItem(
    friend: FriendDto,
    isMember: Boolean,
    hasPendingInvitation: Boolean,
    onInvite: () -> Unit
) {
    val context = LocalContext.current
    val canInvite = !isMember && !hasPendingInvitation
    
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar con placeholder
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(friend.imagen)
                        .crossfade(true)
                        .error(com.example.silenceapp.R.drawable.avatar_placeholder)
                        .placeholder(com.example.silenceapp.R.drawable.avatar_placeholder)
                        .build(),
                    contentDescription = "Foto de ${friend.nombre}",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                // Nombre y estado
                Column {
                    Text(
                        text = friend.nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = onBackgroundColor
                    )
                    
                    // Mostrar estado si es miembro o tiene invitación pendiente
                    if (isMember) {
                        Text(
                            text = "Ya es miembro",
                            fontSize = 12.sp,
                            color = onBackgroundColor.copy(alpha = 0.5f)
                        )
                    } else if (hasPendingInvitation) {
                        Text(
                            text = "Invitación pendiente",
                            fontSize = 12.sp,
                            color = primaryColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // Botón de invitar (solo si puede invitar)
            if (canInvite) {
                IconButton(
                    onClick = onInvite,
                    modifier = Modifier
                        .size(40.dp)
                        .background(primaryColor, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar invitación",
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
