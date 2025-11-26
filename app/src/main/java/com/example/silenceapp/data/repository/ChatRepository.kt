package com.example.silenceapp.data.repository

import com.example.silenceapp.data.local.dao.ChatDao
import com.example.silenceapp.data.local.dao.MembersDao
import com.example.silenceapp.data.local.dao.MessageDao
import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.data.local.entity.Members
import com.example.silenceapp.data.local.entity.Message
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val membersDao: MembersDao
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
