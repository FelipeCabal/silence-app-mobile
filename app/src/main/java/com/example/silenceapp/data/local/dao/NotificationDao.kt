package com.example.silenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.silenceapp.data.local.entity.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    
    @Query("SELECT * FROM notifications WHERE receiverId = :userId ORDER BY createdAt DESC")
    fun getAllNotifications(userId: String): Flow<List<Notification>>
    
    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun getNotificationCount(): Int
    
    @Query("SELECT COUNT(*) FROM notifications WHERE receiverId = :userId")
    suspend fun getNotificationCountForUser(userId: String): Int
    
    @Query("SELECT * FROM notifications WHERE receiverId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotifications(userId: String): Flow<List<Notification>>
    
    @Query("SELECT COUNT(*) FROM notifications WHERE receiverId = :userId AND isRead = 0")
    fun getUnreadCount(userId: String): Flow<Int>
    
    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    suspend fun getNotificationById(notificationId: String): Notification?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<Notification>)
    
    @Update
    suspend fun updateNotification(notification: Notification)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE receiverId = :userId")
    suspend fun markAllAsRead(userId: String)
    
    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: String)
    
    @Query("DELETE FROM notifications WHERE receiverId = :userId")
    suspend fun deleteAllNotifications(userId: String)
}
