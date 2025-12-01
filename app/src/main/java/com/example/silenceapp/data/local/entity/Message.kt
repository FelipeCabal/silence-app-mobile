package com.example.silenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = Chat::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
        // Foreign key de userId removida temporalmente por incompatibilidad de tipos
        // UserEntity.id es Int, pero Message.userId es String
    ],
    indices = [Index("chatId"), Index("userId")]
)
data class Message(
    @PrimaryKey
    val id: String,
    val userId: String,
    val chatId: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean,
    val type: String,
    val userName: String? = null // Nombre del usuario que envió el mensaje
)