package com.example.silenceapp.data.repository

import com.example.silenceapp.data.local.dao.NotificationDao
import com.example.silenceapp.data.local.entity.Notification
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {

    fun getAllNotifications(): Flow<List<Notification>> = notificationDao.getAllNotifications()

    suspend fun insertAll(notifications: List<Notification>) {
        notificationDao.insertAll(notifications)
    }

    suspend fun markAsSeen(notificationId: Int) {
        notificationDao.markAsSeen(notificationId)
    }
}
