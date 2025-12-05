package com.example.silenceapp.data.mappers

import com.example.silenceapp.data.local.entity.Notification
import com.example.silenceapp.data.remote.response.NotificationResponse
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun NotificationResponse.toLocalNotification(): Notification {
    return Notification(
        id = this.id.oid,  // Extraer el oid del MongoObjectId
        message = this.message,
        senderId = this.sender.id.oid,
        senderName = this.sender.nombre,
        senderImage = this.sender.imagen,
        receiverId = this.receiver.id.oid,
        receiverName = this.receiver.nombre,
        type = this.type,
        isRead = this.read,
        createdAt = parseDate(this.createdAt.date),  // Extraer el date de MongoDate
        updatedAt = parseDate(this.updatedAt.date)
    )
}

private fun parseDate(dateString: String): Long {
    return try {
        dateFormat.parse(dateString)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        e.printStackTrace()
        System.currentTimeMillis()
    }
}
