package com.example.silenceapp.data.remote.response

import com.google.gson.annotations.SerializedName

// Helper para parsear ObjectId de MongoDB
data class MongoObjectId(
    @SerializedName("\$oid") val oid: String
)

data class NotificationResponse(
    @SerializedName("_id") val id: MongoObjectId,
    val message: String,
    val sender: NotificationSender,
    val receiver: NotificationReceiver,
    val type: Int,
    val read: Boolean = false,
    val createdAt: MongoDate,
    val updatedAt: MongoDate
)

data class MongoDate(
    @SerializedName("\$date") val date: String
)

data class NotificationSender(
    @SerializedName("_id") val id: MongoObjectId,
    val nombre: String,
    val imagen: String?
)

data class NotificationReceiver(
    @SerializedName("_id") val id: MongoObjectId,
    val nombre: String,
    val imagen: String?,
    @SerializedName("userId") val userIdObj: MongoObjectId? = null
)
