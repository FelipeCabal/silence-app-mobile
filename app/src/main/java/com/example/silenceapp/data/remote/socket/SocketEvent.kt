package com.example.silenceapp.data.remote.socket

/**
 * Enum para tipos de chat (debe coincidir con el backend)
 */
enum class ChatType {
    PRIVATE,
    GROUP,
    COMMUNITY;
    
    override fun toString(): String = name.lowercase()
    
    companion object {
        fun fromString(value: String): ChatType {
            return when (value.lowercase()) {
                "private" -> PRIVATE
                "group" -> GROUP
                "community" -> COMMUNITY
                else -> throw IllegalArgumentException("Unknown chat type: $value")
            }
        }
    }
}

/**
 * Modelo de datos para mensajes recibidos del servidor
 */
data class MessageData(
    val _id: String,
    val chatId: String,
    val userId: String,
    val content: String,
    val timestamp: Long,
    val type: String = "text",
    val isRead: Boolean = false
)

/**
 * Eventos de Socket.IO
 * Sealed class que representa todos los eventos posibles
 */
sealed class SocketEvent {
    
    // ==================== EVENTOS DE SALIDA (Cliente → Servidor) ====================
    
    /**
     * Unirse a un chat
     */
    data class JoinChat(
        val chatId: String,
        val chatType: ChatType
    ) : SocketEvent()
    
    /**
     * Salir de un chat
     */
    data class LeaveChat(
        val chatId: String,
        val chatType: ChatType
    ) : SocketEvent()
    
    /**
     * Enviar mensaje
     */
    data class SendMessage(
        val chatId: String,
        val message: String,
        val chatType: ChatType
    ) : SocketEvent()
    
    /**
     * Indicador de escritura
     */
    data class Typing(
        val chatId: String,
        val chatType: ChatType,
        val isTyping: Boolean = true
    ) : SocketEvent()
    
    /**
     * Marcar mensajes como leídos
     */
    data class MarkAsRead(
        val chatId: String,
        val chatType: ChatType,
        val messageIds: List<String>? = null
    ) : SocketEvent()
    
    /**
     * Obtener usuarios activos en un chat
     */
    data class GetActiveUsers(
        val chatId: String,
        val chatType: ChatType
    ) : SocketEvent()
    
    // ==================== EVENTOS DE ENTRADA (Servidor → Cliente) ====================
    
    /**
     * Conexión exitosa al servidor
     */
    data class Connected(
        val userId: String,
        val socketId: String,
        val timestamp: String
    ) : SocketEvent()
    
    /**
     * Se unió al chat exitosamente
     */
    data class JoinedChat(
        val chatId: String,
        val chatType: ChatType,
        val roomName: String,
        val timestamp: String
    ) : SocketEvent()
    
    /**
     * Otro usuario se unió al chat
     */
    data class UserJoined(
        val userId: String,
        val chatId: String,
        val chatType: ChatType,
        val timestamp: String
    ) : SocketEvent()
    
    /**
     * Otro usuario salió del chat
     */
    data class UserLeft(
        val userId: String,
        val chatId: String,
        val chatType: ChatType,
        val timestamp: String
    ) : SocketEvent()
    
    /**
     * Nuevo mensaje recibido
     */
    data class MessageReceived(
        val chatId: String,
        val chatType: ChatType,
        val message: MessageData,
        val timestamp: String
    ) : SocketEvent()
    
    /**
     * Usuario está escribiendo
     */
    data class UserTyping(
        val userId: String,
        val chatId: String,
        val chatType: ChatType,
        val isTyping: Boolean,
        val timestamp: String
    ) : SocketEvent()
    
    /**
     * Mensajes marcados como leídos por un usuario
     */
    data class MessagesRead(
        val userId: String,
        val chatId: String,
        val chatType: ChatType,
        val messageIds: List<String>?,
        val timestamp: String
    ) : SocketEvent()
    
    /**
     * Respuesta de usuarios activos
     */
    data class ActiveUsersResponse(
        val success: Boolean,
        val activeUsers: List<String>,
        val count: Int
    ) : SocketEvent()
    
    // ==================== EVENTOS DE SISTEMA ====================
    
    /**
     * Desconectado del servidor
     */
    object Disconnected : SocketEvent()
    
    /**
     * Error del servidor
     */
    data class Error(
        val event: String,
        val message: String
    ) : SocketEvent()
}
