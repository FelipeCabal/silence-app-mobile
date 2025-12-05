package com.example.silenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silenceapp.data.local.entity.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Update
    suspend fun updateMessage(message: Message)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesByChatId(chatId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessageByChatId(chatId: String): Message?

    @Query("SELECT * FROM messages WHERE id = :messageId LIMIT 1")
    suspend fun getMessageById(messageId: String): Message?

    @Query("SELECT * FROM messages WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMessagesByUserId(userId: String): Flow<List<Message>>

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesByChatId(chatId: String)

    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId AND isRead = 0")
    suspend fun getUnreadMessageCount(chatId: String): Int

    @Query("UPDATE messages SET isRead = 1 WHERE chatId = :chatId")
    suspend fun markAllMessagesAsRead(chatId: String)

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isRead = 0")
    fun getUnreadMessages(chatId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND type = :type ORDER BY timestamp ASC")
    fun getMessagesByType(chatId: String, type: String): Flow<List<Message>>

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}
