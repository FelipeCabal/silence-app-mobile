package com.example.silenceapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.BuildConfig
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.data.local.entity.Members
import com.example.silenceapp.data.local.entity.Message
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.remote.socket.ConnectionState
import com.example.silenceapp.data.remote.socket.SocketEvent
import com.example.silenceapp.data.remote.socket.SocketIOManager
import com.example.silenceapp.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatDao = DatabaseProvider.getDatabase(application).chatDao()
    private val messageDao = DatabaseProvider.getDatabase(application).messageDao()
    private val membersDao = DatabaseProvider.getDatabase(application).membersDao()
    private val userDao = DatabaseProvider.getDatabase(application).userDao()
    private val chatService = ApiClient.chatService
    
    // Socket.IO Manager - Usar la instancia única (Singleton)
    private val socketIOManager = SocketIOManager.getInstance(BuildConfig.BASE_URL)
    private val repository = ChatRepository(chatDao, messageDao, membersDao, userDao, chatService, socketIOManager)
    
    private val authDataStore = AuthDataStore(application)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // ============ SOCKET.IO STATE ============
    
    /**
     * Estado de conexión Socket.IO
     */
    val socketConnectionState: StateFlow<ConnectionState> = repository.socketConnectionState
    
    /**
     * Usuarios escribiendo por chat
     * Map<chatId, Set<userId>>
     */
    private val _typingUsers = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val typingUsers: StateFlow<Map<String, Set<String>>> = _typingUsers
    
    /**
     * Nombres de usuarios escribiendo por chat
     * Map<chatId, Set<userName>>
     */
    private val _typingUserNames = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val typingUserNames: StateFlow<Map<String, Set<String>>> = _typingUserNames
    
    /**
     * Usuarios activos en el chat actual
     */
    private val _activeUsers = MutableStateFlow<Set<String>>(emptySet())
    val activeUsers: StateFlow<Set<String>> = _activeUsers
    
    /**
     * ID del chat actual (para manejar eventos)
     */
    private var currentChatId: String? = null

    companion object {
        private const val TAG = "ChatViewModel"
    }
    
    init {
        // Observar eventos Socket.IO
        observeSocketEvents()
    }
    
    // ============ SOCKET.IO OPERATIONS ============
    
    /**
     * Conectar a Socket.IO usando el token almacenado
     */
    fun connectSocket() {
        viewModelScope.launch {
            try {
                var token = authDataStore.getToken().first()
                
                Log.d(TAG, "🔑 Token RAW del DataStore: '${token.take(30)}...'")
                Log.d(TAG, "🔑 Token length original: ${token.length}")
                Log.d(TAG, "🔑 ¿Empieza con 'Bearer '? ${token.startsWith("Bearer ")}")
                Log.d(TAG, "🔑 ¿Empieza con 'Bearer'? ${token.startsWith("Bearer")}")
                
                // Limpiar el token: remover "Bearer " o "Bearer" si existe
                token = token.trim()
                    .removePrefix("Bearer ")
                    .removePrefix("Bearer")
                    .trim()
                
                Log.d(TAG, "🔑 Token LIMPIO: '${token.take(30)}...'")
                Log.d(TAG, "🔑 Token length limpio: ${token.length}")
                
                if (token.isNotBlank()) {
                    Log.d(TAG, "🔌 Conectando Socket.IO con token limpio...")
                    repository.connectSocket(token)
                } else {
                    Log.e(TAG, "❌ No hay token disponible para conectar Socket.IO")
                    _error.value = "No autenticado"
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al conectar Socket.IO", e)
                _error.value = e.message
            }
        }
    }
    
    /**
     * Desconectar de Socket.IO
     */
    fun disconnectSocket() {
        Log.d(TAG, "🔌 Desconectando Socket.IO...")
        repository.disconnectSocket()
    }
    
    /**
     * Unirse a un chat room
     */
    fun joinChatRoom(chatId: String, chatType: String) {
        currentChatId = chatId
        Log.d(TAG, "🚪 Uniéndose a chat: $chatId")
        repository.joinChatRoom(chatId, chatType)
        
        // Solicitar usuarios activos
        repository.getActiveUsers(chatId, chatType)
    }
    
    /**
     * Sincronizar mensajes del servidor al entrar al chat
     */
    fun syncChatMessages(chatId: String, chatType: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "🔄 Sincronizando mensajes del chat: $chatId, tipo: $chatType")
                val token = authDataStore.getToken().first()
                
                if (token.isBlank()) {
                    Log.e(TAG, "❌ Token vacío, no se pueden sincronizar mensajes")
                    onComplete(false)
                    return@launch
                }
                
                val result = withContext(Dispatchers.IO) {
                    repository.syncMessagesFromServer(chatId, chatType, token)
                }
                
                result.fold(
                    onSuccess = { messages ->
                        Log.d(TAG, "✅ ${messages.size} mensajes sincronizados")
                        onComplete(true)
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "❌ Error al sincronizar mensajes", exception)
                        onComplete(false)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en syncChatMessages", e)
                onComplete(false)
            }
        }
    }
    
    /**
     * Salir de un chat room
     */
    fun leaveChatRoom(chatId: String, chatType: String) {
        if (currentChatId == chatId) {
            currentChatId = null
        }
        Log.d(TAG, "👋 Saliendo de chat: $chatId")
        repository.leaveChatRoom(chatId, chatType)
    }
    
    /**
     * Enviar mensaje vía Socket.IO (actualización optimista)
     */
    fun sendMessage(chatId: String, content: String, chatType: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "🎯 INICIO sendMessage - chatId=$chatId, mensaje='${content.take(50)}', tipo=$chatType")
        viewModelScope.launch {
            try {
                Log.d(TAG, "💬 Dentro de coroutine, obteniendo userId...")
                val userId = authDataStore.getUserId().first()
                val userName = authDataStore.getUserName().first()
                Log.d(TAG, "👤 userId obtenido: $userId")
                Log.d(TAG, "👤 userName obtenido: $userName")
                if (userId.isBlank()) {
                    _error.value = "Usuario no autenticado"
                    onResult(false)
                    return@launch
                }
                
                val result = withContext(Dispatchers.IO) {
                    repository.sendMessageViaSocket(chatId, content, chatType, userId, userName)
                }
                
                result.fold(
                    onSuccess = { message ->
                        Log.d(TAG, "✅ Mensaje enviado: ${message.id}")
                        onResult(true)
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "❌ Error al enviar mensaje", exception)
                        _error.value = exception.message
                        onResult(false)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al enviar mensaje", e)
                _error.value = e.message
                onResult(false)
            }
        }
    }
    
    /**
     * Enviar indicador de escritura
     */
    fun setTyping(chatId: String, chatType: String, isTyping: Boolean) {
        repository.sendTypingIndicator(chatId, chatType, isTyping)
    }
    
    /**
     * Marcar mensajes como leídos
     */
    fun markMessagesAsRead(chatId: String, chatType: String, messageIds: List<String>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.markMessagesAsReadViaSocket(chatId, chatType, messageIds)
            }
            onResult(success)
        }
    }
    
    /**
     * Observar eventos Socket.IO
     */
    private fun observeSocketEvents() {
        viewModelScope.launch {
            repository.socketEvents.collect { event ->
                handleSocketEvent(event)
            }
        }
    }
    
    /**
     * Manejar eventos Socket.IO entrantes
     */
    private suspend fun handleSocketEvent(event: SocketEvent) {
        when (event) {
            is SocketEvent.Connected -> {
                Log.d(TAG, "✅ Conectado: userId=${event.userId}, socketId=${event.socketId}")
            }
            
            is SocketEvent.JoinedChat -> {
                Log.d(TAG, "✅ Unido a chat: ${event.chatId}")
            }
            
            is SocketEvent.MessageReceived -> {
                Log.d(TAG, "📨 ====== MENSAJE RECIBIDO DE SOCKET.IO ======")
                Log.d(TAG, "📨 ID: ${event.message._id}")
                Log.d(TAG, "📨 ChatId del evento: ${event.chatId}")
                Log.d(TAG, "📨 ChatId del mensaje: ${event.message.chatId}")
                Log.d(TAG, "📨 ChatType: ${event.chatType}")
                Log.d(TAG, "📨 De userId: ${event.message.userId}")
                Log.d(TAG, "📨 Contenido: ${event.message.content.take(50)}")
                
                // 🔍 Log especial para chats privados
                if (event.chatType == com.example.silenceapp.data.remote.socket.ChatType.PRIVATE) {
                    Log.d(TAG, "🔐 ¡ES UN MENSAJE DE CHAT PRIVADO!")
                }
                
                // Obtener userId actual para no duplicar mensajes propios
                val currentUserId = authDataStore.getUserId().first()
                Log.d(TAG, "📨 Mi userId: $currentUserId")
                Log.d(TAG, "📨 ¿Es mi mensaje? ${event.message.userId == currentUserId}")
                
                withContext(Dispatchers.IO) {
                    // Verificar si el mensaje ya existe (por ID del servidor)
                    val existingMessage = messageDao.getMessageById(event.message._id)
                    if (existingMessage != null) {
                        Log.w(TAG, "⚠️ Mensaje ya existe en Room: ${event.message._id}, IGNORANDO")
                        return@withContext
                    }
                    
                    // Si es mi propio mensaje, buscar y actualizar el temporal
                    if (event.message.userId == currentUserId) {
                        Log.d(TAG, "✅ Es mi propio mensaje, buscando mensaje temporal...")
                        
                        // Buscar mensajes temporales recientes (últimos 10 segundos)
                        val recentMessages = messageDao.getMessagesByChatId(event.message.chatId)
                            .first() // Get first emission from Flow
                        
                        val tempMessage = recentMessages
                            .filter { it.id.startsWith("temp_") && it.userId == currentUserId }
                            .filter { it.content == event.message.content }
                            .maxByOrNull { it.timestamp }
                        
                        if (tempMessage != null) {
                            Log.d(TAG, "✅ Mensaje temporal encontrado: ${tempMessage.id}")
                            Log.d(TAG, "🔄 Actualizando con ID real: ${event.message._id}")
                            
                            // Eliminar mensaje temporal
                            messageDao.deleteMessage(tempMessage)
                            
                            // Insertar con ID real del servidor
                            val realMessage = tempMessage.copy(
                                id = event.message._id,
                                timestamp = event.message.timestamp,
                                isRead = event.message.isRead
                            )
                            messageDao.insertMessage(realMessage)
                            
                            Log.d(TAG, "✅ Mensaje actualizado de temporal a real")
                            return@withContext
                        } else {
                            Log.w(TAG, "⚠️ No se encontró mensaje temporal, guardando como nuevo")
                        }
                    }
                    
                    // Si NO es mi mensaje o no se encontró el temporal, guardar como nuevo
                    Log.d(TAG, "✅ Guardando mensaje de otro usuario en Room...")
                    
                    // Verificar que el chat existe
                    val chatExists = chatDao.getChatById(event.message.chatId) != null
                    if (!chatExists) {
                        Log.w(TAG, "⚠️ Chat no existe, creando placeholder...")
                        val placeholderChat = Chat(
                            id = event.message.chatId,
                            name = "Chat",
                            type = event.chatType.toString().lowercase(),
                            image = "",
                            description = "",
                            lastMessageDate = System.currentTimeMillis().toString(),
                            lastMessage = ""
                        )
                        chatDao.insertChat(placeholderChat)
                    }
                    
                    // Obtener el nombre del usuario
                    val userName = userDao.getUserByRemoteId(event.message.userId)?.nombre
                    
                    // Guardar mensaje
                    val message = Message(
                        id = event.message._id,
                        chatId = event.message.chatId,
                        content = event.message.content,
                        userId = event.message.userId,
                        timestamp = event.message.timestamp,
                        type = event.message.type,
                        isRead = false, // Marcar como no leído inicialmente
                        userName = userName // Agregar el nombre del usuario
                    )
                    
                    Log.d(TAG, "💾 Guardando mensaje en Room:")
                    Log.d(TAG, "   - id: ${message.id}")
                    Log.d(TAG, "   - chatId: ${message.chatId}")
                    Log.d(TAG, "   - userId: ${message.userId}")
                    Log.d(TAG, "   - content: ${message.content.take(30)}")
                    Log.d(TAG, "   - timestamp: ${message.timestamp}")
                    
                    messageDao.insertMessage(message)
                    Log.d(TAG, "✅ Mensaje guardado exitosamente")
                    
                    // Actualizar último mensaje del chat
                    chatDao.getChatById(event.message.chatId)?.let { chat ->
                        val updatedChat = chat.copy(
                            lastMessage = message.content,
                            lastMessageDate = message.timestamp.toString()
                        )
                        chatDao.updateChat(updatedChat)
                    }
                }
            }
            
            is SocketEvent.UserJoined -> {
                Log.d(TAG, "👋 Usuario unido: ${event.userId}")
                // Actualizar lista de usuarios activos
                _activeUsers.value = _activeUsers.value + event.userId
            }
            
            is SocketEvent.UserLeft -> {
                Log.d(TAG, "👋 Usuario salió: ${event.userId}")
                // Remover de usuarios activos
                _activeUsers.value = _activeUsers.value - event.userId
                
                // Remover de usuarios escribiendo
                _typingUsers.value = _typingUsers.value.mapValues { (_, users) ->
                    users - event.userId
                }.filterValues { it.isNotEmpty() }
            }
            
            is SocketEvent.UserTyping -> {
                Log.d(TAG, "⌨️ Usuario escribiendo: ${event.userId} = ${event.isTyping}")
                
                // No mostrar mi propio indicador de escritura
                val currentUserId = authDataStore.getUserId().first()
                if (event.userId != currentUserId) {
                    // Obtener el nombre del usuario desde Room
                    viewModelScope.launch(Dispatchers.IO) {
                        val userFromDb = userDao.getUserByRemoteId(event.userId)
                        // Si no está en DB, usar "Alguien" en lugar del ID
                        val userName = userFromDb?.nombre
                        
                        Log.d(TAG, "👤 Usuario escribiendo: userId=${event.userId}, userName=$userName, encontrado en DB=${userFromDb != null}")
                        
                        withContext(Dispatchers.Main) {
                            val currentUsers = _typingUsers.value[event.chatId] ?: emptySet()
                            val currentUserNames = _typingUserNames.value[event.chatId] ?: emptySet()
                            
                            _typingUsers.value = _typingUsers.value.toMutableMap().apply {
                                if (event.isTyping) {
                                    put(event.chatId, currentUsers + event.userId)
                                } else {
                                    put(event.chatId, currentUsers - event.userId)
                                    if (get(event.chatId)?.isEmpty() == true) {
                                        remove(event.chatId)
                                    }
                                }
                            }
                            
                            // Solo agregar el nombre si existe en DB
                            if (userName != null) {
                                _typingUserNames.value = _typingUserNames.value.toMutableMap().apply {
                                    if (event.isTyping) {
                                        put(event.chatId, currentUserNames + userName)
                                    } else {
                                        put(event.chatId, currentUserNames - userName)
                                        if (get(event.chatId)?.isEmpty() == true) {
                                            remove(event.chatId)
                                        }
                                    }
                                }
                            } else {
                                // Si no hay nombre, usar un contador genérico
                                _typingUserNames.value = _typingUserNames.value.toMutableMap().apply {
                                    if (event.isTyping) {
                                        put(event.chatId, currentUserNames + "Alguien")
                                    } else {
                                        put(event.chatId, currentUserNames - "Alguien")
                                        if (get(event.chatId)?.isEmpty() == true) {
                                            remove(event.chatId)
                                        }
                                    }
                                }
                            }
                            
                            Log.d(TAG, "📝 TypingUserNames actualizado para chat ${event.chatId}: ${_typingUserNames.value[event.chatId]}")
                        }
                    }
                } else {
                    Log.d(TAG, "⚠️ Es mi propio indicador, ignorando")
                }
            }
            
            is SocketEvent.MessagesRead -> {
                Log.d(TAG, "✅ Mensajes leídos: ${event.messageIds?.size ?: 0}")
                
                // Actualizar mensajes en Room
                event.messageIds?.let { ids ->
                    viewModelScope.launch(Dispatchers.IO) {
                        ids.forEach { messageId ->
                            messageDao.getMessageById(messageId)?.let { message ->
                                messageDao.updateMessage(message.copy(isRead = true))
                            }
                        }
                    }
                }
            }
            
            is SocketEvent.ActiveUsersResponse -> {
                Log.d(TAG, "👥 Usuarios activos: ${event.activeUsers.size}")
                _activeUsers.value = event.activeUsers.toSet()
            }
            
            is SocketEvent.Disconnected -> {
                Log.d(TAG, "❌ Desconectado")
                _activeUsers.value = emptySet()
                _typingUsers.value = emptyMap()
                _typingUserNames.value = emptyMap()
            }
            
            is SocketEvent.Error -> {
                Log.e(TAG, "❌ Error Socket.IO [${event.event}]: ${event.message}")
                _error.value = event.message
            }
            
            else -> {
                Log.d(TAG, "📡 Evento no manejado: ${event::class.simpleName}")
            }
        }
    }

    // ============ SYNC OPERATIONS ============

    /**
     * Sincroniza todos los chats disponibles desde el servidor (chats privados, grupos y comunidades)
     */
    fun syncAllChats(token: String, currentUserId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.syncAllChats(token, currentUserId)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Todos los chats sincronizados exitosamente")
                    onResult(true)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al sincronizar chats", exception)
                    _error.value = exception.message
                    onResult(false)
                }
            )
        }
    }

    /**
     * Sincroniza solo chats privados
     */
    fun syncPrivateChats(token: String, currentUserId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.syncPrivateChats(token, currentUserId)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chats ->
                    Log.d(TAG, "${chats.size} chats privados sincronizados")
                    onResult(true)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al sincronizar chats privados", exception)
                    _error.value = exception.message
                    onResult(false)
                }
            )
        }
    }

    /**
     * Sincroniza solo comunidades
     */
    fun syncCommunities(token: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.syncCommunities(token)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chats ->
                    Log.d(TAG, "${chats.size} comunidades sincronizadas")
                    onResult(true)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al sincronizar comunidades", exception)
                    _error.value = exception.message
                    onResult(false)
                }
            )
        }
    }

    /**
     * Sincroniza solo grupos
     */
    fun syncGroups(token: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.syncGroups(token)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chats ->
                    Log.d(TAG, "${chats.size} grupos sincronizados")
                    onResult(true)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al sincronizar grupos", exception)
                    _error.value = exception.message
                    onResult(false)
                }
            )
        }
    }

    // ============ CHAT OPERATIONS ============

    // ============ CREATE CHAT OPERATIONS ============
    
    /**
     * Crea un nuevo chat privado
     */
    fun createPrivateChat(token: String, userRecibeId: String, currentUserId: String, onResult: (Result<Chat>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.createPrivateChat(token, userRecibeId, currentUserId)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chat ->
                    Log.d(TAG, "Chat privado creado exitosamente: ${chat.id}")
                    onResult(Result.success(chat))
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al crear chat privado", exception)
                    _error.value = exception.message
                    onResult(Result.failure(exception))
                }
            )
        }
    }
    
    /**
     * Crea un nuevo grupo
     */
    fun createGroup(token: String, nombre: String, descripcion: String, imagen: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.createGroup(token, nombre, descripcion, imagen)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chatId ->
                    Log.d(TAG, "Grupo creado exitosamente: $chatId")
                    onResult(Result.success(chatId))
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al crear grupo", exception)
                    _error.value = exception.message
                    onResult(Result.failure(exception))
                }
            )
        }
    }

    /**
     * Crea una nueva comunidad
     */
    fun createCommunity(token: String, nombre: String, descripcion: String, imagen: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.createCommunity(token, nombre, descripcion, imagen)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chatId ->
                    Log.d(TAG, "Comunidad creada exitosamente: $chatId")
                    onResult(Result.success(chatId))
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al crear comunidad", exception)
                    _error.value = exception.message
                    onResult(Result.failure(exception))
                }
            )
        }
    }

    /**
     * Crea un nuevo grupo manejando la autenticación internamente
     */
    fun createGroupWithAuth(nombre: String, descripcion: String, imagen: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val token = authDataStore.getToken().first()
            if (token.isBlank()) {
                _error.value = "No se encontró token de autenticación"
                onResult(Result.failure(Exception("No autenticado")))
                return@launch
            }
            createGroup(token, nombre, descripcion, imagen, onResult)
        }
    }

    /**
     * Crea una nueva comunidad manejando la autenticación internamente
     */
    fun createCommunityWithAuth(nombre: String, descripcion: String, imagen: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val token = authDataStore.getToken().first()
            if (token.isBlank()) {
                _error.value = "No se encontró token de autenticación"
                onResult(Result.failure(Exception("No autenticado")))
                return@launch
            }
            createCommunity(token, nombre, descripcion, imagen, onResult)
        }
    }

    fun getAllChats(): Flow<List<Chat>> {
        return repository.getAllChats()
    }

    fun getChatsByType(type: String): Flow<List<Chat>> {
        return repository.getChatsByType(type)
    }

    fun searchChats(query: String): Flow<List<Chat>> {
        return repository.searchChats(query)
    }

    fun insertChat(chat: Chat, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // No activar isLoading para operaciones locales rápidas
            val success = withContext(Dispatchers.IO) {
                repository.insertChat(chat)
            }
            if (success) {
                Log.d(TAG, "Chat insertado exitosamente: ${chat.id}")
            } else {
                Log.e(TAG, "Error al insertar chat")
            }
            onResult(success)
        }
    }

    fun insertChats(chats: List<Chat>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // No activar isLoading para no bloquear UI con datos de prueba
            val success = withContext(Dispatchers.IO) {
                repository.insertChats(chats)
            }
            if (success) {
                Log.d(TAG, "${chats.size} chats insertados exitosamente")
            } else {
                Log.e(TAG, "Error al insertar chats")
            }
            onResult(success)
        }
    }

    fun updateChat(chat: Chat, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.updateChat(chat)
            }
            if (success) {
                Log.d(TAG, "Chat actualizado: ${chat.id}")
            }
            onResult(success)
        }
    }

    fun deleteChat(chat: Chat, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.deleteChat(chat)
            }
            if (success) {
                Log.d(TAG, "Chat eliminado: ${chat.id}")
            }
            onResult(success)
        }
    }

    // ============ MESSAGE OPERATIONS ============

    fun getMessagesByChatId(chatId: String): Flow<List<Message>> {
        return repository.getMessagesByChatId(chatId)
    }

    fun getUnreadMessages(chatId: String): Flow<List<Message>> {
        return repository.getUnreadMessages(chatId)
    }

    fun insertMessage(message: Message, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.insertMessage(message)
            }
            if (success) {
                Log.d(TAG, "Mensaje insertado: ${message.id}")
            }
            onResult(success)
        }
    }

    fun insertMessages(messages: List<Message>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.insertMessages(messages)
            }
            if (success) {
                Log.d(TAG, "${messages.size} mensajes insertados")
            }
            onResult(success)
        }
    }

    fun updateMessage(message: Message, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.updateMessage(message)
            }
            onResult(success)
        }
    }

    fun deleteMessage(message: Message, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.deleteMessage(message)
            }
            onResult(success)
        }
    }

    fun markAllMessagesAsRead(chatId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.markAllMessagesAsRead(chatId)
            }
            if (success) {
                Log.d(TAG, "Mensajes marcados como leídos en chat: $chatId")
            }
            onResult(success)
        }
    }

    fun getUnreadMessageCount(chatId: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                repository.getUnreadMessageCount(chatId)
            }
            onResult(count)
        }
    }

    // ============ MEMBERS OPERATIONS ============

    fun getChatMembers(chatId: String): Flow<List<Members>> {
        return repository.getChatMembers(chatId)
    }

    fun getUserChats(userId: String): Flow<List<Members>> {
        return repository.getUserChats(userId)
    }

    fun getMembersByRole(chatId: String, role: String): Flow<List<Members>> {
        return repository.getMembersByRole(chatId, role)
    }

    fun getMemberCount(chatId: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                repository.getMemberCount(chatId)
            }
            onResult(count)
        }
    }

    fun insertMember(member: Members, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.insertMember(member)
            }
            if (success) {
                Log.d(TAG, "Miembro añadido: ${member.userId} al chat ${member.chatId}")
            }
            onResult(success)
        }
    }

    fun insertMembers(members: List<Members>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.insertMembers(members)
            }
            if (success) {
                Log.d(TAG, "${members.size} miembros insertados")
            }
            onResult(success)
        }
    }

    fun updateMember(member: Members, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.updateMember(member)
            }
            onResult(success)
        }
    }

    fun deleteMember(member: Members, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.deleteMember(member)
            }
            if (success) {
                Log.d(TAG, "Miembro eliminado: ${member.userId} del chat ${member.chatId}")
            }
            onResult(success)
        }
    }

    fun clearError() {
        _error.value = null
    }
}
