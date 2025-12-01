package com.example.silenceapp.view.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.entity.Message
import com.example.silenceapp.data.remote.socket.ConnectionState
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.view.chat.components.ConnectionIndicator
import com.example.silenceapp.view.chat.components.MessageBubble
import com.example.silenceapp.view.chat.components.MessageInput
import com.example.silenceapp.view.chat.components.TypingIndicator
import com.example.silenceapp.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Pantalla de conversación individual
 * 
 * @param chatId ID del chat
 * @param chatName Nombre del chat (grupo, usuario, comunidad)
 * @param chatType Tipo: "private", "group", "community"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    chatName: String,
    chatType: String,
    navController: NavController,
    viewModel: ChatViewModel = viewModel(),
    authDataStore: AuthDataStore
) {
    // Estados
    val messages by viewModel.getMessagesByChatId(chatId).collectAsState(initial = emptyList())
    val connectionState by viewModel.socketConnectionState.collectAsState()
    val typingUsers by viewModel.typingUsers.collectAsState()
    val activeUsers by viewModel.activeUsers.collectAsState()
    
    // Log cuando cambian los mensajes
    LaunchedEffect(messages.size) {
        android.util.Log.d("ChatScreen", "📋 Total mensajes en pantalla: ${messages.size}")
        messages.forEach { msg ->
            android.util.Log.d("ChatScreen", "   - ${msg.id.take(10)}: userId=${msg.userId}, content=${msg.content.take(20)}")
        }
    }
    
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Obtener userId del DataStore
    var currentUserId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        currentUserId = authDataStore.getUserId().first()
        android.util.Log.d("ChatScreen", "👤 userId del usuario actual: $currentUserId")
    }
    
    // 🚪 Unirse al chat cuando entra a la pantalla
    LaunchedEffect(chatId) {
        // Primero sincronizar mensajes del servidor
        viewModel.syncChatMessages(chatId, chatType) { success ->
            if (success) {
                android.util.Log.d("ChatScreen", "✅ Mensajes sincronizados del servidor")
            }
        }
        
        // Luego unirse al chat room de Socket.IO
        viewModel.joinChatRoom(chatId, chatType)
        
        // Marcar mensajes como leídos
        currentUserId?.let { userId ->
            val unreadMessages = messages.filter { !it.isRead && it.userId != userId }
            if (unreadMessages.isNotEmpty()) {
                val messageIds = unreadMessages.map { it.id }
                viewModel.markMessagesAsRead(chatId, chatType, messageIds) {}
            }
        }
    }
    
    // 👋 Salir del chat cuando sale de la pantalla
    DisposableEffect(chatId) {
        onDispose {
            viewModel.leaveChatRoom(chatId, chatType)
        }
    }
    
    // Auto-scroll cuando llegan mensajes nuevos
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        }
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = {
                    Column {
                        Text(
                            text = chatName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        // Mostrar estado de conexión y usuarios activos
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (connectionState) {
                                is ConnectionState.Connected -> {
                                    Text(
                                        text = "${activeUsers.size} en línea",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                is ConnectionState.Reconnecting -> {
                                    Text(
                                        text = "Reconectando...",
                                        fontSize = 12.sp,
                                        color = Color(0xFFFF9800)
                                    )
                                }
                                is ConnectionState.Disconnected -> {
                                    Text(
                                        text = "Sin conexión",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    ConnectionIndicator(
                        state = connectionState,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = onBackgroundColor.copy(alpha = 0.1f)
            )

            // Lista de mensajes
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                reverseLayout = true, // Mensajes más recientes abajo
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Indicador de escritura (primera posición = abajo)
                item {
                    TypingIndicator(
                        chatId = chatId,
                        typingUsers = typingUsers
                    )
                }
                
                // Mensajes (invertidos porque reverseLayout = true)
                items(
                    items = messages.reversed(),
                    key = { it.id }
                ) { message ->
                    // Log para debug
                    android.util.Log.d("ChatScreen", "🎨 Renderizando mensaje: id=${message.id.take(10)}, userId=${message.userId}, currentUserId=$currentUserId, isMyMessage=${message.userId == currentUserId}")
                    
                    MessageBubble(
                        message = message,
                        isMyMessage = message.userId == currentUserId
                    )
                }
            }
            
            // Input de mensaje
            MessageInput(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSend = {
                    android.util.Log.d("ChatScreen", "📨 onSend llamado, texto='$messageText', chatId='$chatId'")
                    if (messageText.isNotBlank()) {
                        android.util.Log.d("ChatScreen", "✅ Llamando viewModel.sendMessage()")
                        viewModel.sendMessage(chatId, messageText, chatType) { success ->
                            android.util.Log.d("ChatScreen", "📬 Callback recibido, success=$success")
                            if (success) {
                                messageText = ""
                            }
                        }
                    } else {
                        android.util.Log.d("ChatScreen", "❌ Mensaje vacío en ChatScreen")
                    }
                },
                onTypingChange = { isTyping ->
                    viewModel.setTyping(chatId, chatType, isTyping)
                },
                enabled = connectionState is ConnectionState.Connected
            )
        }
    }
}
