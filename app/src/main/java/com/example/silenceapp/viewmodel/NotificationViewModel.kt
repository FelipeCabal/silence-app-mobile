package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.Notification
import com.example.silenceapp.data.local.entity.NotificationType
import com.example.silenceapp.data.repository.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.sql.Timestamp

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationDao = DatabaseProvider.getDatabase(application).notificationDao()
    private val repository = NotificationRepository(notificationDao)

    val notifications: StateFlow<List<Notification>> = repository.getAllNotifications()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getDataTest() {
        viewModelScope.launch {
            if (repository.hasNotifications()) return@launch

            val sampleNotifications = listOf(
                Notification(
                    user = "Esperanza0",
                    action = "le gusto tu publicacion",
                    time = Timestamp(System.currentTimeMillis() - 10000),
                    type = NotificationType.LIKE
                ),
                Notification(
                    user = "GinaPao",
                    action = "comento tu publicacion",
                    time = Timestamp(System.currentTimeMillis() - 20000),
                    type = NotificationType.COMMENT
                ),
                Notification(
                    user = "RezeBoom",
                    action = "quiere ser tu amigo:",
                    time = Timestamp(System.currentTimeMillis() - 300000),
                    type = NotificationType.FRIEND_REQUEST,
                    avatar = "ic_launcher",
                    data = "friend_request_1"
                ),
                Notification(
                    user = "JuanDC",
                    action = "le gusto tu publicacion",
                    time = Timestamp(System.currentTimeMillis() - 600000),
                    type = NotificationType.LIKE
                ),
                Notification(
                    user = "MariaR",
                    action = "comento tu publicacion",
                    time = Timestamp(System.currentTimeMillis() - 900000),
                    type = NotificationType.COMMENT,
                    alreadySeen = true,
                    data = "comment_1"
                )
            )
            repository.insertAll(sampleNotifications)
        }
    }

    fun markAsSeen(notificationId: Int) {
        viewModelScope.launch {
            repository.markAsSeen(notificationId)
        }
    }

    fun addNotification(notification: Notification) {
        viewModelScope.launch {
            repository.insert(notification)
        }
    }
}