package com.example.silenceapp.data.local.converter

import androidx.room.TypeConverter
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.local.entity.UserEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.sql.Timestamp

class Converters {

    private val gson = Gson()

    // --- Para el campo 'user' ---
    @TypeConverter
    fun fromUser(user: UserEntity?): String? {
        return gson.toJson(user)
    }

    @TypeConverter
    fun toUser(userJson: String?): UserEntity? {
        if (userJson == null) return null
        val type = object : TypeToken<UserEntity>() {}.type
        return gson.fromJson(userJson, type)
    }

    // --- Para la lista 'comentarios' ---
    @TypeConverter
    fun fromComments(comments: List<Comment>?): String? {
        return gson.toJson(comments)
    }

    @TypeConverter
    fun toComments(commentsJson: String?): List<Comment>? {
        if (commentsJson == null) return emptyList()
        val type = object : TypeToken<List<Comment>>() {}.type
        return gson.fromJson(commentsJson, type)
    }

    // --- Para 'createdAt' (Timestamp) ---
    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.time
    }

    @TypeConverter
    fun toTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it) }
    }

    // Convertir List<String> → String (JSON)
    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return Gson().toJson(list)
    }

    // Convertir String (JSON) → List<String>
    @TypeConverter
    fun toStringList(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()

        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, type)
    }
}
