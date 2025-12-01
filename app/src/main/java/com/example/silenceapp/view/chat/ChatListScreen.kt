package com.example.silenceapp.view.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.silenceapp.R
import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.ui.components.ChatItem
import com.example.silenceapp.ui.theme.backgroundColor
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.secondaryColor
import com.example.silenceapp.view.chat.components.ConnectionIndicator
import com.example.silenceapp.viewmodel.AuthViewModel
import com.example.silenceapp.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Todos", "Privados", "Grupos", "Comunidades")
    val profileImageUrl by remember { mutableStateOf<String?>(null) }
    
    val isLoading by chatViewModel.isLoading.collectAsState()
    val error by chatViewModel.error.collectAsState()
    val allChats by chatViewModel.getAllChats().collectAsState(initial = emptyList())
    val connectionState by chatViewModel.socketConnectionState.collectAsState()
    
    var hasSynced by remember { mutableStateOf(false) }
    var hasConnectedSocket by remember { mutableStateOf(false) }
    
    // Conectar Socket.IO una vez
    LaunchedEffect(Unit) {
        if (!hasConnectedSocket) {
            hasConnectedSocket = true
            chatViewModel.connectSocket()
        }
    }
    
    // Sincronizar chats desde la API solo una vez
    LaunchedEffect(Unit) {
        if (!hasSynced) {
            hasSynced = true
            authViewModel.loadToken { token ->
                if (token.isNotBlank()) {
                    chatViewModel.syncAllChats(token) { success ->
                    }
                }
            }
        }
    }
    
    // Filtrar chats según el tipo seleccionado
    val filteredChats = remember(selectedTab, allChats) {
        when (selectedTab) {
            0 -> allChats // Todos
            1 -> allChats.filter { it.type == "private" }
            2 -> allChats.filter { it.type == "group" }
            3 -> allChats.filter { it.type == "community" }
            else -> allChats
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Header con icono y título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de la app",
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Chats",
                    style = MaterialTheme.typography.headlineMedium,
                    color = onBackgroundColor,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Indicador de conexión Socket.IO
                ConnectionIndicator(state = connectionState)
            }
            
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileImageUrl ?: R.drawable.avatar_placeholder)
                    .crossfade(true)
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .build(),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = backgroundColor,
            contentColor = secondaryColor,
            edgePadding = 10.dp,
            indicator = { },
            divider = { }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier
                        .padding(horizontal = 1.dp)
                        .height(36.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            if (selectedTab == index) secondaryColor
                            else Color.Transparent
                        ),
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = onBackgroundColor.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = onBackgroundColor.copy(alpha = 0.1f)
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        // Lista de chats con FAB
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when {
                isLoading -> {
                    // Estado de carga
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = secondaryColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando chats...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = onBackgroundColor.copy(alpha = 0.6f)
                        )
                    }
                }
                filteredChats.isEmpty() -> {
                    // Estado vacío
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No hay chats",
                            style = MaterialTheme.typography.bodyLarge,
                            color = onBackgroundColor.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toca + para iniciar una conversación",
                            style = MaterialTheme.typography.bodyMedium,
                            color = onBackgroundColor.copy(alpha = 0.4f)
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = filteredChats,
                            key = { chat -> chat.id }
                        ) { chat ->
                            // TODO: Implementar caché de unreadCount si es necesario
                            // Por ahora, no consultamos DB en cada item para mejor rendimiento
                            ChatItem(
                                chat = chat,
                                unreadCount = 0, // Optimización: remover consulta DB costosa
                                onClick = {
                                    // Navegar al chat individual con Socket.IO
                                    navController.navigate("chat/${chat.id}/${chat.name}/${chat.type}")
                                }
                            )
                        }
                    }
                }
            }

            // FAB flotante posicionado en la esquina inferior derecha
            FloatingActionButton(
                onClick = {
                    navController.navigate("create-chat")
                },
                containerColor = secondaryColor,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear chat",
                    tint = Color.Black
                )
            }
        }
    }
}

// Función para insertar chats de prueba sin bloquear UI
private fun insertSampleChatsAsync(chatViewModel: ChatViewModel) {
    val sampleChats = listOf(
        Chat(
            id = "1",
            name = "Amaranta",
            type = "private",
            image = "",
            description = "Chat privado con Amaranta",
            lastMessageDate = System.currentTimeMillis().toString(),
            lastMessage = "Hola, como estas?"
        ),
        Chat(
            id = "2",
            name = "Leopoldo",
            type = "private",
            image = "",
            description = "Chat privado con Leopoldo",
            lastMessageDate = (System.currentTimeMillis() - 3600000).toString(),
            lastMessage = "Puedes mandarme el link de la reunión?.."
        ),
        Chat(
            id = "3",
            name = "Leopoldo",
            type = "private",
            image = "",
            description = "Chat privado con Leopoldo",
            lastMessageDate = (System.currentTimeMillis() - 86400000 * 2).toString(),
            lastMessage = "Puedes mandarme el link de la reunión?"
        ),
        Chat(
            id = "4",
            name = "GRUPO SANCOCHO DOMINGO",
            type = "group",
            image = "",
            description = "Grupo de amigos",
            lastMessageDate = (System.currentTimeMillis() - 86400000 * 3).toString(),
            lastMessage = "@milaneso: Yo o la olla pal salcocho"
        ),
        Chat(
            id = "5",
            name = "COMUNIDAD PUEBLO LINDO",
            type = "community",
            image = "",
            description = "Comunidad del barrio",
            lastMessageDate = (System.currentTimeMillis() - 86400000 * 14).toString(),
            lastMessage = "+2 mensajes nuevos"
        )
    )
    
    chatViewModel.insertChats(sampleChats) { }
}
