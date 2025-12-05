package com.example.silenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silenceapp.data.local.entity.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<Chat>)

    @Update
    suspend fun updateChat(chat: Chat)

    @Delete
    suspend fun deleteChat(chat: Chat)

    @Query("SELECT * FROM chats ORDER BY lastMessageDate DESC")
    fun getAllChats(): Flow<List<Chat>>

    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    suspend fun getChatById(chatId: String): Chat?

    @Query("SELECT * FROM chats WHERE type = :type ORDER BY lastMessageDate DESC")
    fun getChatsByType(type: String): Flow<List<Chat>>

    @Query("DELETE FROM chats")
    suspend fun deleteAllChats()

    @Query("SELECT * FROM chats WHERE name LIKE '%' || :query || '%' ORDER BY lastMessageDate DESC")
    fun searchChats(query: String): Flow<List<Chat>>
}
