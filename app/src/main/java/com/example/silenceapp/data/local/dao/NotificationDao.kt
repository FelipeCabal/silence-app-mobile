package com.example.silenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.silenceapp.data.local.entity.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY time DESC")
    fun getAllNotifications(userId: String): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<Notification>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: Notification)

    @Query("UPDATE notifications SET alreadySeen = 1 WHERE id = :notificationId")
    suspend fun markAsSeen(notificationId: Int)

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND alreadySeen = 0")
    suspend fun getNotificationsCount(userId: String): Int
}
