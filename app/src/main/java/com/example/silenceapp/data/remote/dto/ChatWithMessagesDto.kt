package com.example.silenceapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de mensajes del endpoint /chats/:chatId
 */
data class ChatMessagesResponse(
    val chatId: String,
    val chatType: String,
    val messages: List<MessageDto>,
    val total: Int
)

data class MessageDto(
    @SerializedName("_id")
    val id: String,
    @SerializedName("remitente")
    val userId: String?,
    @SerializedName("mensaje")
    val content: String?,
    @SerializedName("fecha")
    val timestamp: String, // ISO 8601 date string
    @SerializedName("tipo")
    val type: String?,
    val isRead: Boolean
)
