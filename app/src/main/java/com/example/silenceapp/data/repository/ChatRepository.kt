package com.example.silenceapp.data.repository

import com.example.silenceapp.data.local.dao.ChatDao
import com.example.silenceapp.data.local.dao.MembersDao
import com.example.silenceapp.data.local.dao.MessageDao
import com.example.silenceapp.data.local.dao.UserDao
import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.data.local.entity.Members
import com.example.silenceapp.data.local.entity.Message
import com.example.silenceapp.data.remote.dto.CreateChatDto
import com.example.silenceapp.data.remote.mapper.ChatMapper
import com.example.silenceapp.data.remote.service.ChatService
import com.example.silenceapp.data.remote.socket.ChatType
import com.example.silenceapp.data.remote.socket.ConnectionState
import com.example.silenceapp.data.remote.socket.SocketEvent
import com.example.silenceapp.data.remote.socket.SocketIOManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow
import android.util.Log

class ChatRepository(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val membersDao: MembersDao,
    private val userDao: UserDao,
    private val chatService: ChatService,
    private val socketIOManager: SocketIOManager
) {
    
    companion object {
        private const val TAG = "ChatRepository"
    }

    // ============ SOCKET.IO OBSERVABLES ============
    
    /**
     * Observable del estado de conexión Socket.IO
     */
    val socketConnectionState: StateFlow<ConnectionState> = socketIOManager.connectionState
    
    /**
     * Observable de eventos Socket.IO
     */
    val socketEvents: SharedFlow<SocketEvent> = socketIOManager.chatEvents

    // ============ SOCKET.IO CONNECTION ============
    
    /**
     * Conecta al servidor Socket.IO con el token JWT
     */
    fun connectSocket(token: String) {
        Log.d(TAG, "🔌 Conectando Socket.IO...")
        socketIOManager.connectToChats(token)
    }
    
    /**
     * Desconecta del servidor Socket.IO
     */
    fun disconnectSocket() {
        Log.d(TAG, "🔌 Desconectando Socket.IO...")
        socketIOManager.disconnect()
    }
    
    /**
     * Verifica si está conectado
     */
    fun isSocketConnected(): Boolean {
        return socketIOManager.isConnected()
    }

    // ============ SOCKET.IO CHAT ACTIONS ============
    
    /**
     * Unirse a un chat mediante Socket.IO
     */
    fun joinChatRoom(chatId: String, chatType: String) {
        Log.d(TAG, "🚪 Uniéndose a chat: $chatId ($chatType)")
        val type = when (chatType.lowercase()) {
            "private" -> ChatType.PRIVATE
            "group" -> ChatType.GROUP
            "community" -> ChatType.COMMUNITY
            else -> ChatType.GROUP
        }
        socketIOManager.joinChat(chatId, type)
    }
    
    /**
     * Salir de un chat mediante Socket.IO
     */
    fun leaveChatRoom(chatId: String, chatType: String) {
        Log.d(TAG, "👋 Saliendo de chat: $chatId ($chatType)")
        val type = when (chatType.lowercase()) {
            "private" -> ChatType.PRIVATE
            "group" -> ChatType.GROUP
            "community" -> ChatType.COMMUNITY
            else -> ChatType.GROUP
        }
        socketIOManager.leaveChat(chatId, type)
    }
    
    /**
     * Enviar mensaje mediante Socket.IO (con actualización optimista)
     * 1. Primero guarda en Room
     * 2. Luego emite al servidor
     */
    suspend fun sendMessageViaSocket(
        chatId: String,
        content: String,
        chatType: String,
        userId: String,
        userName: String? = null
    ): Result<Message> {
        return try {
            Log.d(TAG, "💬 Enviando mensaje vía Socket.IO...")
            
            // 1. Verificar si el chat existe en Room
            val chatExists = chatDao.getChatById(chatId) != null
            Log.d(TAG, "🔍 Chat existe en Room: $chatExists")
            
            if (!chatExists) {
                Log.d(TAG, "⚠️ Chat no existe en Room, creando placeholder...")
                // Crear un chat placeholder para cumplir con la foreign key
                val placeholderChat = Chat(
                    id = chatId,
                    name = "Chat",
                    type = chatType,
                    image = "",
                    description = "",
                    lastMessageDate = System.currentTimeMillis().toString(),
                    lastMessage = ""
                )
                chatDao.insertChat(placeholderChat)
                Log.d(TAG, "✅ Chat placeholder creado")
            }
            
            // 2. Crear mensaje local (actualización optimista)
            val localMessage = Message(
                id = "temp_${System.currentTimeMillis()}", // ID temporal
                chatId = chatId,
                content = content,
                userId = userId,
                timestamp = System.currentTimeMillis(),
                type = "text",
                isRead = false,
                userName = userName
            )
            
            // 3. Guardar en Room
            messageDao.insertMessage(localMessage)
            updateChatLastMessage(chatId, localMessage)
            Log.d(TAG, "✅ Mensaje guardado localmente: ${localMessage.id}")
            
            // 3. Emitir al servidor vía Socket.IO
            val type = when (chatType.lowercase()) {
                "private" -> ChatType.PRIVATE
                "group" -> ChatType.GROUP
                "community" -> ChatType.COMMUNITY
                else -> ChatType.GROUP
            }
            socketIOManager.sendMessage(chatId, content, type)
            Log.d(TAG, "📡 Mensaje emitido al servidor")
            
            Result.success(localMessage)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al enviar mensaje", e)
            Result.failure(e)
        }
    }
    
    /**
     * Actualizar mensaje local cuando llega confirmación del servidor
     */
    suspend fun updateMessageWithServerId(tempId: String, serverId: String): Boolean {
        return try {
            messageDao.getMessageById(tempId)?.let { message ->
                val updatedMessage = message.copy(id = serverId)
                messageDao.deleteMessage(message) // Eliminar temp
                messageDao.insertMessage(updatedMessage) // Insertar con ID real
                Log.d(TAG, "✅ Mensaje actualizado: $tempId -> $serverId")
                true
            } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al actualizar mensaje", e)
            false
        }
    }
    
    /**
     * Enviar indicador de escritura
     */
    fun sendTypingIndicator(chatId: String, chatType: String, isTyping: Boolean) {
        val type = when (chatType.lowercase()) {
            "private" -> ChatType.PRIVATE
            "group" -> ChatType.GROUP
            "community" -> ChatType.COMMUNITY
            else -> ChatType.GROUP
        }
        socketIOManager.setTyping(chatId, type, isTyping)
    }
    
    /**
     * Marcar mensajes como leídos vía Socket.IO
     */
    suspend fun markMessagesAsReadViaSocket(chatId: String, chatType: String, messageIds: List<String>): Boolean {
        return try {
            // 1. Actualizar en Room
            messageIds.forEach { messageId ->
                messageDao.getMessageById(messageId)?.let { message ->
                    messageDao.updateMessage(message.copy(isRead = true))
                }
            }
            
            // 2. Notificar al servidor
            val type = when (chatType.lowercase()) {
                "private" -> ChatType.PRIVATE
                "group" -> ChatType.GROUP
                "community" -> ChatType.COMMUNITY
                else -> ChatType.GROUP
            }
            socketIOManager.markAsRead(chatId, type, messageIds)
            Log.d(TAG, "✅ Mensajes marcados como leídos: ${messageIds.size}")
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al marcar mensajes como leídos", e)
            false
        }
    }
    
    /**
     * Sincronizar mensajes del servidor (cargar chat con mensajes embebidos)
     */
    suspend fun syncMessagesFromServer(chatId: String, chatType: String, token: String): Result<List<Message>> {
        return try {
            Log.d(TAG, "🔄 Sincronizando mensajes del servidor para chat: $chatId, tipo: $chatType")
            Log.d(TAG, "🔑 Token: ${token.take(20)}...")
            
            // Llamar al endpoint con chatType como query parameter
            val response = chatService.getChatMessages(
                token = "Bearer $token",
                chatId = chatId,
                chatType = chatType
            )
            
            Log.d(TAG, "📡 Response code: ${response.code()}")
            Log.d(TAG, "📡 Response successful: ${response.isSuccessful}")
            Log.d(TAG, "📡 Response body null: ${response.body() == null}")
            
            if (response.isSuccessful && response.body() != null) {
                val messagesResponse = response.body()!!
                Log.d(TAG, "📦 Response recibido: chatId=${messagesResponse.chatId}, chatType=${messagesResponse.chatType}")
                Log.d(TAG, "📦 Total de mensajes: ${messagesResponse.total}")
                Log.d(TAG, "📦 Mensajes en respuesta: ${messagesResponse.messages.size}")
                Log.d(TAG, "📦 Miembros en respuesta: ${messagesResponse.miembros?.size ?: 0}")
                
                // Guardar miembros si vienen en la respuesta
                messagesResponse.miembros?.let { membersDto ->
                    Log.d(TAG, "👥 Guardando ${membersDto.size} miembros en Room...")
                    membersDto.forEach { memberDto ->
                        try {
                            val userEntity = com.example.silenceapp.data.local.entity.UserEntity(
                                remoteId = memberDto.id,
                                nombre = memberDto.nombre ?: "Usuario",
                                email = memberDto.email ?: "",
                                sexo = "",
                                fechaNto = "",
                                pais = "",
                                imagen = memberDto.imagen
                            )
                            // Usar un DAO de usuario si está disponible
                            userDao.insertUser(userEntity)
                            Log.d(TAG, "✅ Usuario guardado: ${memberDto.nombre} (${memberDto.id})")
                        } catch (e: Exception) {
                            Log.w(TAG, "⚠️ Error al guardar usuario ${memberDto.id}: ${e.message}")
                        }
                    }
                }
                
                val messages = mutableListOf<Message>()
                
                // Convertir mensajes del DTO a entidades Room
                messagesResponse.messages.forEachIndexed { index, messageDto ->
                    val contentPreview = messageDto.content?.take(30) ?: "[sin contenido]"
                    Log.d(TAG, "📨 Mensaje $index: id=${messageDto.id}, content='$contentPreview', userId=${messageDto.userId}")
                    
                    // Validar que el mensaje tenga datos mínimos requeridos
                    if (messageDto.userId == null) {
                        Log.w(TAG, "⚠️ Mensaje ${messageDto.id} no tiene userId, se omite")
                        return@forEachIndexed
                    }
                    
                    // Convertir fecha ISO 8601 a timestamp Long
                    val timestampLong = try {
                        java.time.Instant.parse(messageDto.timestamp).toEpochMilli()
                    } catch (e: Exception) {
                        Log.w(TAG, "⚠️ Error al parsear fecha: ${messageDto.timestamp}, usando timestamp actual")
                        System.currentTimeMillis()
                    }
                    
                    val message = Message(
                        id = messageDto.id,
                        chatId = chatId,
                        userId = messageDto.userId,
                        content = messageDto.content ?: "",
                        timestamp = timestampLong,
                        type = messageDto.type ?: "text",
                        isRead = messageDto.isRead
                    )
                    messages.add(message)
                    
                    // Guardar en Room (insert or replace)
                    messageDao.insertMessage(message)
                    Log.d(TAG, "💾 Mensaje guardado en Room: ${message.id}")
                }
                
                Log.d(TAG, "✅ ${messages.size} mensajes sincronizados desde el servidor")
                Result.success(messages)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                val error = "Error al cargar mensajes: ${response.code()} - $errorBody"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al sincronizar mensajes", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Obtener usuarios activos en un chat
     */
    fun getActiveUsers(chatId: String, chatType: String) {
        val type = when (chatType.lowercase()) {
            "private" -> ChatType.PRIVATE
            "group" -> ChatType.GROUP
            "community" -> ChatType.COMMUNITY
            else -> ChatType.GROUP
        }
        socketIOManager.getActiveUsers(chatId, type) {}
    }

    // ============ CHAT OPERATIONS ============
    
    fun getAllChats(): Flow<List<Chat>> {
        return chatDao.getAllChats()
    }

    suspend fun getChatById(chatId: String): Chat? {
        return chatDao.getChatById(chatId)
    }

    fun getChatsByType(type: String): Flow<List<Chat>> {
        return chatDao.getChatsByType(type)
    }

    fun searchChats(query: String): Flow<List<Chat>> {
        return chatDao.searchChats(query)
    }

    suspend fun insertChat(chat: Chat): Boolean {
        return try {
            chatDao.insertChat(chat)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun insertChats(chats: List<Chat>): Boolean {
        return try {
            chatDao.insertChats(chats)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateChat(chat: Chat): Boolean {
        return try {
            chatDao.updateChat(chat)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteChat(chat: Chat): Boolean {
        return try {
            chatDao.deleteChat(chat)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Elimina un chat privado del servidor y localmente
     */
    suspend fun deletePrivateChat(token: String, chatId: String): Result<Unit> {
        return try {
            val response = chatService.deletePrivateChat("Bearer $token", chatId)
            
            if (response.isSuccessful) {
                // Eliminar localmente también
                chatDao.getChatById(chatId)?.let { chat ->
                    chatDao.deleteChat(chat)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar chat privado: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllChats(): Boolean {
        return try {
            chatDao.deleteAllChats()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ============ SYNC OPERATIONS FROM REMOTE ==========

    /**
     * Sincroniza chats privados desde el servidor
     */
    suspend fun syncPrivateChats(token: String, currentUserId: String): Result<List<Chat>> {
        return try {
            val response = chatService.getPrivateChats("Bearer $token")
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.error == false && apiResponse.data != null) {
                    val chats = ChatMapper.fromPrivateChatDtoList(apiResponse.data, currentUserId)
                    chatDao.insertChats(chats)
                    Result.success(chats)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Error desconocido"))
                }
            } else {
                Result.failure(Exception("Error al sincronizar chats privados: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza comunidades desde el servidor
     */
    suspend fun syncCommunities(token: String): Result<List<Chat>> {
        return try {
            val response = chatService.getCommunities("Bearer $token")
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.error == false && apiResponse.data != null) {
                    val chats = ChatMapper.fromCommunityDtoList(apiResponse.data)
                    chatDao.insertChats(chats)
                    Result.success(chats)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Error desconocido"))
                }
            } else {
                Result.failure(Exception("Error al sincronizar comunidades: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza grupos desde el servidor
     */
    suspend fun syncGroups(token: String): Result<List<Chat>> {
        return try {
            val response = chatService.getGroups("Bearer $token")
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.error == false && apiResponse.data != null) {
                    val chats = ChatMapper.fromGroupDtoList(apiResponse.data)
                    chatDao.insertChats(chats)
                    Result.success(chats)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Error desconocido"))
                }
            } else {
                Result.failure(Exception("Error al sincronizar grupos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza todos los chats disponibles (chats privados, grupos y comunidades)
     */
    suspend fun syncAllChats(token: String, currentUserId: String): Result<Unit> {
        return try {
            val privateResult = syncPrivateChats(token, currentUserId)
            val communitiesResult = syncCommunities(token)
            val groupsResult = syncGroups(token)

            // Si alguna falla, devolver el error
            when {
                privateResult.isFailure -> Result.failure(privateResult.exceptionOrNull()!!)
                communitiesResult.isFailure -> Result.failure(communitiesResult.exceptionOrNull()!!)
                groupsResult.isFailure -> Result.failure(groupsResult.exceptionOrNull()!!)
                else -> Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ CREATE CHAT OPERATIONS ==========
    
    /**
     * Crea un nuevo chat privado
     */
    suspend fun createPrivateChat(token: String, userRecibeId: String, currentUserId: String): Result<Chat> {
        return try {
            val chatData = com.example.silenceapp.data.remote.dto.CreatePrivateChatDto(userRecibeId)
            val response = chatService.createPrivateChat("Bearer $token", chatData)
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.error == false && apiResponse.data != null) {
                    // Convertir el DTO a Chat entity
                    val chat = ChatMapper.fromPrivateChatDto(apiResponse.data, currentUserId)
                    // Insertar el chat localmente
                    chatDao.insertChat(chat)
                    Result.success(chat)
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Error al crear chat privado"))
                }
            } else {
                Result.failure(Exception("Error al crear chat privado: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea un nuevo grupo
     */
    suspend fun createGroup(token: String, nombre: String, descripcion: String, imagen: String): Result<String> {
        return try {
            val chatData = CreateChatDto(nombre, descripcion, imagen)
            val response = chatService.createGroup("Bearer $token", chatData)
            
            if (response.isSuccessful) {
                val createResponse = response.body()
                if (createResponse?.error == false && createResponse.data != null) {
                    // Insertar el chat localmente
                    val newChat = Chat(
                        id = createResponse.data.id,
                        name = createResponse.data.nombre,
                        type = "group",
                        image = createResponse.data.imagen ?: "",
                        description = createResponse.data.descripcion,
                        lastMessageDate = System.currentTimeMillis().toString(),
                        lastMessage = ""
                    )
                    chatDao.insertChat(newChat)
                    Result.success(createResponse.data.id)
                } else {
                    Result.failure(Exception(createResponse?.message ?: "Error al crear grupo"))
                }
            } else {
                Result.failure(Exception("Error al crear grupo: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Crea una nueva comunidad
     */
    suspend fun createCommunity(token: String, nombre: String, descripcion: String, imagen: String): Result<String> {
        return try {
            val chatData = CreateChatDto(nombre, descripcion, imagen)
            val response = chatService.createCommunity("Bearer $token", chatData)
            
            if (response.isSuccessful) {
                val createResponse = response.body()
                if (createResponse?.error == false && createResponse.data != null) {
                    // Insertar el chat localmente
                    val newChat = Chat(
                        id = createResponse.data.id,
                        name = createResponse.data.nombre,
                        type = "community",
                        image = createResponse.data.imagen ?: "",
                        description = createResponse.data.descripcion,
                        lastMessageDate = System.currentTimeMillis().toString(),
                        lastMessage = ""
                    )
                    chatDao.insertChat(newChat)
                    Result.success(createResponse.data.id)
                } else {
                    Result.failure(Exception(createResponse?.message ?: "Error al crear comunidad"))
                }
            } else {
                Result.failure(Exception("Error al crear comunidad: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ MESSAGE OPERATIONS ============

    fun getMessagesByChatId(chatId: String): Flow<List<Message>> {
        return messageDao.getMessagesByChatId(chatId)
    }

    suspend fun getLastMessage(chatId: String): Message? {
        return messageDao.getLastMessageByChatId(chatId)
    }

    suspend fun getUnreadMessageCount(chatId: String): Int {
        return messageDao.getUnreadMessageCount(chatId)
    }

    fun getUnreadMessages(chatId: String): Flow<List<Message>> {
        return messageDao.getUnreadMessages(chatId)
    }

    suspend fun insertMessage(message: Message): Boolean {
        return try {
            messageDao.insertMessage(message)
            // Actualizar último mensaje del chat
            updateChatLastMessage(message.chatId, message)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun insertMessages(messages: List<Message>): Boolean {
        return try {
            messageDao.insertMessages(messages)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateMessage(message: Message): Boolean {
        return try {
            messageDao.updateMessage(message)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteMessage(message: Message): Boolean {
        return try {
            messageDao.deleteMessage(message)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun markAllMessagesAsRead(chatId: String): Boolean {
        return try {
            messageDao.markAllMessagesAsRead(chatId)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteMessagesByChatId(chatId: String): Boolean {
        return try {
            messageDao.deleteMessagesByChatId(chatId)
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun updateChatLastMessage(chatId: String, message: Message) {
        chatDao.getChatById(chatId)?.let { chat ->
            val updatedChat = chat.copy(
                lastMessage = message.content,
                lastMessageDate = message.timestamp.toString()
            )
            chatDao.updateChat(updatedChat)
        }
    }

    // ============ MEMBERS OPERATIONS ============

    fun getChatMembers(chatId: String): Flow<List<Members>> {
        return membersDao.getMembersByChatId(chatId)
    }

    fun getUserChats(userId: String): Flow<List<Members>> {
        return membersDao.getMembersByUserId(userId)
    }

    suspend fun getMember(chatId: String, userId: String): Members? {
        return membersDao.getMember(chatId, userId)
    }

    suspend fun getMemberCount(chatId: String): Int {
        return membersDao.getMemberCount(chatId)
    }

    fun getMembersByRole(chatId: String, role: String): Flow<List<Members>> {
        return membersDao.getMembersByRole(chatId, role)
    }

    suspend fun insertMember(member: Members): Boolean {
        return try {
            membersDao.insertMember(member)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun insertMembers(members: List<Members>): Boolean {
        return try {
            membersDao.insertMembers(members)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateMember(member: Members): Boolean {
        return try {
            membersDao.updateMember(member)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteMember(member: Members): Boolean {
        return try {
            membersDao.deleteMember(member)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteMembersByChatId(chatId: String): Boolean {
        return try {
            membersDao.deleteMembersByChatId(chatId)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteMembersByUserId(userId: String): Boolean {
        return try {
            membersDao.deleteMembersByUserId(userId)
            true
        } catch (e: Exception) {
            false
        }
    }
}
