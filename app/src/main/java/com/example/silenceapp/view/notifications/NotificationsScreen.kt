package com.example.silenceapp.view.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.R
import com.example.silenceapp.data.remote.response.FriendRequestResponse
import com.example.silenceapp.data.remote.response.GroupInvitationResponse
import com.example.silenceapp.ui.components.NotificationItem
import com.example.silenceapp.viewmodel.AuthViewModel
import com.example.silenceapp.viewmodel.NotificationViewModel
import com.example.silenceapp.viewmodel.RequestsViewModel

@Composable
fun NotificationsScreen(
    authViewModel: AuthViewModel = viewModel(),
    notificationViewModel: NotificationViewModel = viewModel(),
    requestsViewModel: RequestsViewModel = viewModel()
) {
    var userId by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    val tabs = listOf("Notificaciones", "Solicitudes")
    
    // Cargar userId y token
    LaunchedEffect(Unit) {
        android.util.Log.d("NotificationsScreen", "🔄 LaunchedEffect Unit - Cargando userId y token")
        authViewModel.loadUserId { 
            userId = it
            android.util.Log.d("NotificationsScreen", "✅ UserId cargado: $it")
        }
        authViewModel.loadToken { 
            token = it
            android.util.Log.d("NotificationsScreen", "✅ Token cargado: ${it.take(50)}...")
            android.util.Log.d("NotificationsScreen", "   Token length: ${it.length}")
            android.util.Log.d("NotificationsScreen", "   Token isEmpty: ${it.isEmpty()}")
        }
    }
    
    // Limpiar conexiones cuando se desmonta la pantalla
    DisposableEffect(Unit) {
        android.util.Log.d("NotificationsScreen", "🎬 DisposableEffect - Pantalla montada")
        
        onDispose {
            android.util.Log.d("NotificationsScreen", "🧹 DisposableEffect - Pantalla desmontada, desconectando notificaciones")
            // No desconectamos aquí porque puede ser solo navegación
        }
    }
    
    // Conectar al WebSocket cuando se obtiene el token
    LaunchedEffect(token) {
        android.util.Log.d("NotificationsScreen", "📡 LaunchedEffect token triggered")
        android.util.Log.d("NotificationsScreen", "   Token value: ${token.take(50)}...")
        android.util.Log.d("NotificationsScreen", "   Token isNotEmpty: ${token.isNotEmpty()}")
        
        if (token.isNotEmpty()) {
            android.util.Log.d("NotificationsScreen", "🔔 Llamando connectToNotifications()")
            notificationViewModel.connectToNotifications(token)
        } else {
            android.util.Log.w("NotificationsScreen", "⚠️ Token está vacío, NO conectando")
        }
    }
    
    // Cargar solicitudes cuando cambia el tab
    LaunchedEffect(selectedTabIndex, token) {
        android.util.Log.d("NotificationsScreen", "🔄 LaunchedEffect selectedTabIndex=$selectedTabIndex, token.isNotEmpty=${token.isNotEmpty()}")
        if (selectedTabIndex == 1 && token.isNotEmpty()) {
            android.util.Log.d("NotificationsScreen", "📞 Llamando loadFriendRequests y loadGroupInvitations")
            android.util.Log.d("NotificationsScreen", "   Token para solicitudes: ${token.take(30)}...")
            requestsViewModel.loadFriendRequests()
            requestsViewModel.loadGroupInvitations()
        }
    }
    
    // Esperar a que userId esté disponible antes de cargar notificaciones
    val notifications by if (userId.isNotEmpty()) {
        android.util.Log.d("NotificationsScreen", "✅ userId disponible, obteniendo notificaciones")
        notificationViewModel.getNotifications(userId).collectAsState(initial = emptyList())
    } else {
        android.util.Log.w("NotificationsScreen", "⚠️ userId vacío, retornando lista vacía")
        remember { mutableStateOf(emptyList()) }
    }
    
    LaunchedEffect(notifications) {
        android.util.Log.d("NotificationsScreen", "🔄 notifications cambió: ${notifications.size} items")
        notifications.forEach { notification ->
            android.util.Log.d("NotificationsScreen", "   - ${notification.message}")
        }
    }
    
    val friendRequests by requestsViewModel.friendRequests.collectAsState()
    val groupInvitations by requestsViewModel.groupInvitations.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            ) 
                        }
                    )
                }
            }
            
            // Content
            when (selectedTabIndex) {
                0 -> NotificationsTab(notifications, notificationViewModel)
                1 -> RequestsTab(friendRequests, groupInvitations, requestsViewModel)
            }
        }
    }
}

@Composable
fun NotificationsTab(
    notifications: List<com.example.silenceapp.data.local.entity.Notification>,
    notificationViewModel: NotificationViewModel
) {
    if (notifications.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No tienes notificaciones",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onNotificationClicked = { 
                        notificationViewModel.markAsRead(notification.id) 
                    }
                )
            }
        }
    }
}

@Composable
fun RequestsTab(
    friendRequests: List<FriendRequestResponse>,
    groupInvitations: List<GroupInvitationResponse>,
    requestsViewModel: RequestsViewModel
) {
    android.util.Log.d("RequestsTab", "🎨 Renderizando RequestsTab")
    android.util.Log.d("RequestsTab", "   friendRequests.size: ${friendRequests.size}")
    android.util.Log.d("RequestsTab", "   groupInvitations.size: ${groupInvitations.size}")
    
    if (friendRequests.isEmpty() && groupInvitations.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No tienes solicitudes pendientes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Solicitudes de amistad
            items(friendRequests) { request ->
                FriendRequestItem(
                    request = request,
                    onAccept = { requestsViewModel.acceptFriendRequest(request.id) },
                    onReject = { requestsViewModel.rejectFriendRequest(request.id) }
                )
            }
            
            // Invitaciones a grupos
            items(groupInvitations) { invitation ->
                GroupInvitationItem(
                    invitation = invitation,
                    onAccept = { requestsViewModel.acceptGroupInvitation(invitation.id) },
                    onReject = { requestsViewModel.rejectGroupInvitation(invitation.id) }
                )
            }
        }
    }
}

@Composable
fun FriendRequestItem(
    request: FriendRequestResponse,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    // Como solo tenemos IDs, mostramos información básica
    // TODO: Hacer populate en el backend para obtener datos completos del usuario
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = R.mipmap.ic_launcher
                    ),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Solicitud de amistad",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Solicitud de amistad",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.size(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Aceptar")
                }
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}

@Composable
fun GroupInvitationItem(
    invitation: GroupInvitationResponse,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val groupImage = invitation.group.imagen?.takeIf { it.isNotEmpty() }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = groupImage ?: R.mipmap.ic_launcher
                    ),
                    contentDescription = "Group Avatar",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = invitation.group.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Invitación a grupo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.size(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Aceptar")
                }
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    com.example.silenceapp.ui.theme.SilenceAppTheme {
        NotificationsScreen()
    }
}