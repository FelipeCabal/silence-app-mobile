package com.example.silenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey val id: String, // _id del backend
    val message: String,
    val senderId: String, // sender._id
    val senderName: String, // sender.nombre
    val senderImage: String?, // sender.imagen (puede ser null)
    val receiverId: String, // receiver._id
    val receiverName: String, // receiver.nombre
    val type: Int, // 1 = like, 2 = comment, 3 = follow, etc.
    val isRead: Boolean = false,
    val createdAt: Long, // timestamp en millis
    val updatedAt: Long
)