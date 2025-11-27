package com.example.silenceapp.data.repository

import com.example.silenceapp.data.local.dao.NotificationDao
import com.example.silenceapp.data.local.entity.Notification
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {

    fun getAllNotifications(): Flow<List<Notification>> = notificationDao.getAllNotifications("1")

    suspend fun insertAll(notifications: List<Notification>) {
        notificationDao.insertAll(notifications)
    }

    suspend fun insert(notification: Notification) {
        notificationDao.insert(notification)
    }

    suspend fun markAsSeen(notificationId: Int) {
        notificationDao.markAsSeen(notificationId)
    }

    suspend fun hasNotifications(): Boolean {
        return notificationDao.getNotificationsCount("1") > 0
    }
}