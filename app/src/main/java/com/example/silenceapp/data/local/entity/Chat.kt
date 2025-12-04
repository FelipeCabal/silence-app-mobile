package com.example.silenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class Chat (
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val image: String,
    val description: String,
    val lastMessageDate: String,
    val lastMessage: String,
)