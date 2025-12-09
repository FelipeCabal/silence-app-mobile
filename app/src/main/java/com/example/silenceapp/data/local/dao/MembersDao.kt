package com.example.silenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silenceapp.data.local.entity.Members
import kotlinx.coroutines.flow.Flow

@Dao
interface MembersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Members)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<Members>)

    @Update
    suspend fun updateMember(member: Members)

    @Delete
    suspend fun deleteMember(member: Members)

    @Query("SELECT * FROM members WHERE chatId = :chatId")
    fun getMembersByChatId(chatId: String): Flow<List<Members>>

    @Query("SELECT * FROM members WHERE userId = :userId")
    fun getMembersByUserId(userId: String): Flow<List<Members>>

    @Query("SELECT * FROM members WHERE chatId = :chatId AND userId = :userId LIMIT 1")
    suspend fun getMember(chatId: String, userId: String): Members?

    @Query("DELETE FROM members WHERE chatId = :chatId")
    suspend fun deleteMembersByChatId(chatId: String)

    @Query("DELETE FROM members WHERE userId = :userId")
    suspend fun deleteMembersByUserId(userId: String)

    @Query("SELECT COUNT(*) FROM members WHERE chatId = :chatId")
    suspend fun getMemberCount(chatId: String): Int

    @Query("SELECT * FROM members WHERE chatId = :chatId AND role = :role")
    fun getMembersByRole(chatId: String, role: String): Flow<List<Members>>
}
