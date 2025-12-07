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

    // --- Para la lista de strings (imágenes) ---
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        if (json == null) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
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
}
