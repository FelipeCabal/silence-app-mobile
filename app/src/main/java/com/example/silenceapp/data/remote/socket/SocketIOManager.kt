package com.example.silenceapp.data.remote.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException

/**
 * Gestor de conexión Socket.IO
 * Maneja la conexión, emisión y recepción de eventos
 * SINGLETON - Solo una instancia en toda la app
 */
class SocketIOManager private constructor(
    private val baseUrl: String
) {
    private var chatSocket: Socket? = null
    private var notificationSocket: Socket? = null
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    private val _chatEvents = MutableSharedFlow<SocketEvent>(replay = 0)
    val chatEvents: SharedFlow<SocketEvent> = _chatEvents
    
    private val scope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        private const val TAG = "SocketIOManager"
        private const val CHAT_NAMESPACE = "/chats"
        private const val NOTIFICATION_NAMESPACE = "/notifications"
        
        @Volatile
        private var INSTANCE: SocketIOManager? = null
        
        /**
         * Obtener la instancia única de SocketIOManager
         */
        fun getInstance(baseUrl: String): SocketIOManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SocketIOManager(baseUrl).also { 
                    INSTANCE = it
                    Log.d(TAG, "✨ Nueva instancia de SocketIOManager creada")
                }
            }
        }
    }
    
    /**
     * Conectar al namespace de chats
     * @param token JWT token para autenticación
     */
    fun connectToChats(token: String) {
        try {
            // Si ya hay un socket conectado, no crear uno nuevo
            if (chatSocket?.connected() == true) {
                Log.d(TAG, "✅ Socket ya está conectado. Socket ID: ${chatSocket?.id()}")
                _connectionState.value = ConnectionState.Connected(System.currentTimeMillis())
                return
            }
            
            // Si hay un socket pero no está conectado, desconectarlo primero
            if (chatSocket != null) {
                Log.d(TAG, "🔌 Limpiando socket anterior...")
                chatSocket?.off() // Remover todos los listeners
                chatSocket?.disconnect()
                chatSocket = null
            }
            
            Log.d(TAG, "🔌 Iniciando conexión Socket.IO con token: ${token.take(20)}...")
            Log.d(TAG, "🔌 BASE_URL original: $baseUrl")
            
            // Remover /api/ del BASE_URL para Socket.IO
            // Socket.IO se conecta directamente al dominio, no a /api/
            val socketBaseUrl = baseUrl.replace("/api/", "/").replace("/api", "")
            Log.d(TAG, "🔌 Socket BASE_URL (sin /api): $socketBaseUrl")
            
            // Verificar que el token sea un JWT válido (debe empezar con "eyJ")
            Log.d(TAG, "🔑 Token primeros 10 chars: '${token.take(10)}'")
            Log.d(TAG, "🔑 ¿Es JWT válido? ${token.startsWith("eyJ")}")
            
            // Configurar opciones de Socket.IO
            val options = IO.Options().apply {
                // El backend busca: socket.handshake.query.token || socket.handshake.headers.authorization
                // Enviamos el token SIN "Bearer " porque el backend hace .replace('Bearer ', '')
                
                // Opción 1: Query parameter (lo que el backend revisa primero)
                query = "token=$token"
                
                Log.d(TAG, "🔌 Query string completo: 'token=${token}'")
                Log.d(TAG, "🔌 Token length: ${token.length}")
                
                // Configuración de reconexión
                reconnection = true
                reconnectionAttempts = Integer.MAX_VALUE
                reconnectionDelay = 1000
                reconnectionDelayMax = 5000
                
                // Solo usar WebSocket transport (igual que el backend)
                transports = arrayOf("websocket")
                
                // Timeouts
                timeout = 20000
            }
            
            // Construir URL completa sin /api/
            val url = if (socketBaseUrl.endsWith("/")) {
                "${socketBaseUrl.dropLast(1)}$CHAT_NAMESPACE"
            } else {
                "$socketBaseUrl$CHAT_NAMESPACE"
            }
            
            Log.d(TAG, "🔌 Conectando a Socket.IO: $url")
            Log.d(TAG, "🔌 Namespace: $CHAT_NAMESPACE")
            
            // Crear socket
            chatSocket = IO.socket(url, options).apply {
                // Registrar todos los listeners
                registerChatListeners()
                
                // Conectar
                connect()
            }
            
            _connectionState.value = ConnectionState.Connecting
            
        } catch (e: URISyntaxException) {
            Log.e(TAG, "❌ Error en URI: ${e.message}")
            _connectionState.value = ConnectionState.Error(e.message ?: "URI inválida", true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al conectar: ${e.message}")
            _connectionState.value = ConnectionState.Error(e.message ?: "Error desconocido", true)
        }
    }
    
    /**
     * Registrar todos los listeners de eventos del chat
     */
    private fun Socket.registerChatListeners() {
        // ==================== EVENTOS DE SISTEMA ====================
        
        on(Socket.EVENT_CONNECT) {
            Log.d(TAG, "✅ Conectado a Socket.IO")
            Log.d(TAG, "✅ Socket ID: ${this.id()}")
            Log.d(TAG, "✅ Connected: ${this.connected()}")
            _connectionState.value = ConnectionState.Connected(System.currentTimeMillis())
        }
        
        on(Socket.EVENT_DISCONNECT) { args ->
            val reason = args.firstOrNull()
            Log.d(TAG, "❌ Desconectado de Socket.IO. Razón: $reason")
            _connectionState.value = ConnectionState.Disconnected
            emitEvent(SocketEvent.Disconnected)
        }
        
        on(Socket.EVENT_CONNECT_ERROR) { args ->
            val error = args.firstOrNull()
            Log.e(TAG, "❌ Error de conexión: $error")
            Log.e(TAG, "❌ Detalles: ${args.joinToString()}")
            _connectionState.value = ConnectionState.Error(error.toString(), true)
        }
        
        on("reconnect") { args ->
            val attempt = (args.firstOrNull() as? Int) ?: 0
            Log.d(TAG, "🔄 Reconectando... intento $attempt")
            _connectionState.value = ConnectionState.Reconnecting(attempt)
        }
        
        on("reconnect_attempt") { args ->
            val attempt = (args.firstOrNull() as? Int) ?: 0
            Log.d(TAG, "🔄 Intento de reconexión $attempt")
        }
        
        on("reconnect_error") { args ->
            val error = args.firstOrNull()
            Log.e(TAG, "❌ Error de reconexión: $error")
        }
        
        on("reconnect_failed") {
            Log.d(TAG, "❌ Reconexión fallida después de múltiples intentos")
            _connectionState.value = ConnectionState.Error("No se pudo reconectar", false)
        }
        
        // ==================== EVENTOS DEL SERVIDOR ====================
        
        // Evento: connected (del backend)
        on("connected", onConnected)
        
        // Evento: joinedChat
        on("joinedChat", onJoinedChat)
        
        // Evento: messageReceived
        on("messageReceived", onMessageReceived)
        
        // Evento: userJoined
        on("userJoined", onUserJoined)
        
        // Evento: userLeft
        on("userLeft", onUserLeft)
        
        // Evento: userTyping
        on("userTyping", onUserTyping)
        
        // Evento: messagesRead
        on("messagesRead", onMessagesRead)
        
        // Evento: error (del servidor)
        on("error", onError)
    }
    
    // ==================== LISTENERS DE EVENTOS ====================
    
    private val onConnected = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val event = SocketEvent.Connected(
                userId = data.getString("userId"),
                socketId = data.getString("socketId"),
                timestamp = data.getString("timestamp")
            )
            emitEvent(event)
            Log.d(TAG, "📡 Connected: userId=${event.userId}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando 'connected': ${e.message}")
        }
    }
    
    private val onJoinedChat = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val event = SocketEvent.JoinedChat(
                chatId = data.getString("chatId"),
                chatType = ChatType.fromString(data.getString("chatType")),
                roomName = data.getString("roomName"),
                timestamp = data.getString("timestamp")
            )
            emitEvent(event)
            Log.d(TAG, "📥 Joined chat: ${event.chatId} (${event.chatType})")
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando 'joinedChat': ${e.message}")
        }
    }
    
    private val onMessageReceived = Emitter.Listener { args ->
        try {
            Log.d(TAG, "📨 onMessageReceived LISTENER ACTIVADO")
            Log.d(TAG, "📨 Args recibidos: ${args.size} elementos")
            
            val data = args[0] as JSONObject
            Log.d(TAG, "📨 JSON completo: $data")
            
            // El chatId está en el nivel superior
            val chatId = data.getString("chatId")
            val chatTypeStr = data.optString("chatType", "group")
            
            // El mensaje está dentro de "message"
            val messageObj = data.getJSONObject("message")
            Log.d(TAG, "📨 Message object: $messageObj")
            
            // Los campos vienen en español del backend:
            // - remitente (no userId)
            // - mensaje (no content)
            // - fecha (no timestamp)
            val messageId = messageObj.getString("_id")
            val userId = messageObj.getString("remitente")
            val content = messageObj.getString("mensaje")
            val fechaStr = messageObj.getString("fecha")
            
            // Convertir fecha ISO a timestamp
            val timestamp = try {
                val instant = java.time.Instant.parse(fechaStr)
                instant.toEpochMilli()
            } catch (e: Exception) {
                Log.e(TAG, "Error parseando fecha: $fechaStr", e)
                System.currentTimeMillis()
            }
            
            Log.d(TAG, "📨 Datos parseados:")
            Log.d(TAG, "   - messageId: $messageId")
            Log.d(TAG, "   - chatId: $chatId")
            Log.d(TAG, "   - userId (remitente): $userId")
            Log.d(TAG, "   - content (mensaje): $content")
            Log.d(TAG, "   - timestamp (fecha): $timestamp")
            
            val messageData = MessageData(
                _id = messageId,
                chatId = chatId,
                userId = userId,
                content = content,
                timestamp = timestamp,
                type = messageObj.optString("tipo", "text"),
                isRead = messageObj.optBoolean("leido", false)
            )
            
            val event = SocketEvent.MessageReceived(
                chatId = chatId,
                chatType = ChatType.fromString(chatTypeStr),
                message = messageData,
                timestamp = data.optString("timestamp", System.currentTimeMillis().toString())
            )
            
            Log.d(TAG, "📨 Emitiendo evento al flow...")
            emitEvent(event)
            Log.d(TAG, "💬 Message received: ${messageData._id} from ${messageData.userId}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando 'messageReceived': ${e.message}")
        }
    }
    
    private val onUserJoined = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val event = SocketEvent.UserJoined(
                userId = data.getString("userId"),
                chatId = data.getString("chatId"),
                chatType = ChatType.fromString(data.getString("chatType")),
                timestamp = data.getString("timestamp")
            )
            emitEvent(event)
            Log.d(TAG, "👋 User joined: ${event.userId}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando 'userJoined': ${e.message}")
        }
    }
    
    private val onUserLeft = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val event = SocketEvent.UserLeft(
                userId = data.getString("userId"),
                chatId = data.getString("chatId"),
                chatType = ChatType.fromString(data.getString("chatType")),
                timestamp = data.getString("timestamp")
            )
            emitEvent(event)
            Log.d(TAG, "👋 User left: ${event.userId}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando 'userLeft': ${e.message}")
        }
    }
    
    private val onUserTyping = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val event = SocketEvent.UserTyping(
                userId = data.getString("userId"),
                chatId = data.getString("chatId"),
                chatType = ChatType.fromString(data.getString("chatType")),
                isTyping = data.getBoolean("isTyping"),
                timestamp = data.getString("timestamp")
            )
            emitEvent(event)
            Log.d(TAG, "⌨️ User typing: ${event.userId} isTyping=${event.isTyping}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando 'userTyping': ${e.message}")
        }
    }
    
    private val onMessagesRead = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val messageIdsArray = data.optJSONArray("messageIds")
            val messageIds = mutableListOf<String>()
            
            messageIdsArray?.let {
                for (i in 0 until it.length()) {
                    messageIds.add(it.getString(i))
                }
            }
            
            val event = SocketEvent.MessagesRead(
                userId = data.getString("userId"),
                chatId = data.getString("chatId"),
                chatType = ChatType.fromString(data.getString("chatType")),
                messageIds = messageIds.ifEmpty { null },
                timestamp = data.getString("timestamp")
            )
            emitEvent(event)
            Log.d(TAG, "✓✓ Messages read by: ${event.userId}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando 'messagesRead': ${e.message}")
        }
    }
    
    private val onError = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val event = SocketEvent.Error(
                event = data.getString("event"),
                message = data.getString("message")
            )
            emitEvent(event)
            Log.e(TAG, "❌ Error del servidor en '${event.event}': ${event.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando 'error': ${e.message}")
        }
    }
    
    // ==================== MÉTODOS DE EMISIÓN ====================
    
    /**
     * Unirse a un chat
     */
    fun joinChat(chatId: String, chatType: ChatType) {
        Log.d(TAG, "📤 joinChat llamado: chatId=$chatId, type=$chatType")
        Log.d(TAG, "📤 Socket conectado? ${chatSocket?.connected()}")
        Log.d(TAG, "📤 Socket ID: ${chatSocket?.id()}")
        
        if (chatSocket?.connected() != true) {
            Log.w(TAG, "⚠️ Socket no conectado al intentar joinChat")
            return
        }
        
        val data = JSONObject().apply {
            put("chatId", chatId)
            put("chatType", chatType.toString())
        }
        chatSocket?.emit("joinChat", data)
        Log.d(TAG, "📤 Emitiendo joinChat: chatId=$chatId, type=$chatType")
    }
    
    /**
     * Salir de un chat
     */
    fun leaveChat(chatId: String, chatType: ChatType) {
        val data = JSONObject().apply {
            put("chatId", chatId)
            put("chatType", chatType.toString())
        }
        chatSocket?.emit("leaveChat", data)
        Log.d(TAG, "📤 Emitiendo leaveChat: chatId=$chatId")
    }
    
    /**
     * Enviar mensaje
     */
    fun sendMessage(chatId: String, message: String, chatType: ChatType) {
        Log.d(TAG, "📤 sendMessage llamado:")
        Log.d(TAG, "   chatId: $chatId")
        Log.d(TAG, "   message: ${message.take(50)}${if(message.length > 50) "..." else ""}")
        Log.d(TAG, "   chatType: $chatType")
        Log.d(TAG, "   Socket conectado? ${chatSocket?.connected()}")
        Log.d(TAG, "   Socket ID: ${chatSocket?.id()}")
        
        if (chatSocket?.connected() != true) {
            Log.w(TAG, "⚠️ Socket no conectado al intentar enviar mensaje")
            return
        }
        
        val data = JSONObject().apply {
            put("chatId", chatId)
            put("message", message)
            put("chatType", chatType.toString())
        }
        
        Log.d(TAG, "📤 JSON a enviar: $data")
        chatSocket?.emit("sendMessage", data)
        Log.d(TAG, "✅ Mensaje emitido al servidor")
    }
    
    /**
     * Indicador de escritura
     */
    fun setTyping(chatId: String, chatType: ChatType, isTyping: Boolean) {
        val data = JSONObject().apply {
            put("chatId", chatId)
            put("chatType", chatType.toString())
            put("isTyping", isTyping)
        }
        chatSocket?.emit("typing", data)
        // No logueamos esto porque se emite muy frecuentemente
    }
    
    /**
     * Marcar mensajes como leídos
     */
    fun markAsRead(chatId: String, chatType: ChatType, messageIds: List<String>? = null) {
        val data = JSONObject().apply {
            put("chatId", chatId)
            put("chatType", chatType.toString())
            messageIds?.let { 
                put("messageIds", JSONArray(it))
            }
        }
        chatSocket?.emit("markAsRead", data)
        Log.d(TAG, "📤 Emitiendo markAsRead: chatId=$chatId")
    }
    
    /**
     * Obtener usuarios activos en un chat
     */
    fun getActiveUsers(chatId: String, chatType: ChatType, callback: (List<String>) -> Unit) {
        if (chatSocket?.connected() != true) {
            Log.w(TAG, "⚠️ Socket no conectado")
            callback(emptyList())
            return
        }
        
        val data = JSONObject().apply {
            put("chatId", chatId)
            put("chatType", chatType.toString())
        }
        
        // Emitir evento - la respuesta llegará por listener 'activeUsersResponse'
        chatSocket?.emit("getActiveUsers", data)
        Log.d(TAG, "📤 Emitiendo getActiveUsers: chatId=$chatId")
    }
    
    /**
     * Desconectar Socket.IO
     */
    fun disconnect() {
        Log.d(TAG, "🔌 Desconectando Socket.IO")
        
        chatSocket?.apply {
            // Remover todos los listeners
            off()
            disconnect()
        }
        chatSocket = null
        
        notificationSocket?.apply {
            off()
            disconnect()
        }
        notificationSocket = null
        
        _connectionState.value = ConnectionState.Disconnected
    }
    
    /**
     * Verificar si está conectado
     */
    fun isConnected(): Boolean {
        return chatSocket?.connected() == true
    }
    
    /**
     * Emitir evento al flow para que lo observen los ViewModels
     */
    private fun emitEvent(event: SocketEvent) {
        scope.launch {
            _chatEvents.emit(event)
        }
    }
}
