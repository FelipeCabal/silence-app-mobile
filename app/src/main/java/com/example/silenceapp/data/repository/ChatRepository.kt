package com.example.silenceapp.data.repository

import com.example.silenceapp.data.local.dao.ChatDao
import com.example.silenceapp.data.local.dao.MembersDao
import com.example.silenceapp.data.local.dao.MessageDao
import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.data.local.entity.Members
import com.example.silenceapp.data.local.entity.Message
import com.example.silenceapp.data.remote.dto.CreateChatDto
import com.example.silenceapp.data.remote.mapper.ChatMapper
import com.example.silenceapp.data.remote.service.ChatService
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val membersDao: MembersDao,
    private val chatService: ChatService
) {

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

    suspend fun deleteAllChats(): Boolean {
        return try {
            chatDao.deleteAllChats()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ============ SYNC OPERATIONS FROM REMOTE ==========

    // PENDIENTE - API no disponible aún
    // TODO: Descomentar cuando el endpoint /chat-privado esté disponible
    /*
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
    */

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
     * Sincroniza todos los chats disponibles (grupos y comunidades)
     * NOTA: Chats privados omitidos - API no disponible aún
     */
    suspend fun syncAllChats(token: String): Result<Unit> {
        return try {
            // TODO: Descomentar cuando /chat-privado esté disponible
            // val privateResult = syncPrivateChats(token, currentUserId)
            
            val communitiesResult = syncCommunities(token)
            val groupsResult = syncGroups(token)

            // Si alguna falla, devolver el error
            when {
                // privateResult.isFailure -> Result.failure(privateResult.exceptionOrNull()!!)
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
