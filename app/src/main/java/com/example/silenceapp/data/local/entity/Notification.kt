package com.example.silenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

enum class NotificationType {
    LIKE,
    COMMENT,
    FRIEND_REQUEST,
    GROUP_INVITE
}

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // We can enhance this later to be a foreign key to a User entity
    val action: String,
    val user: String,
    val time: Timestamp,
    val avatar: String? = null, // Storing image as a URL or resource identifier
    val type: NotificationType,
    val alreadySeen: Boolean = false,
    val data: String? = null // Optional metadata such as groupId or friendRequestId
)