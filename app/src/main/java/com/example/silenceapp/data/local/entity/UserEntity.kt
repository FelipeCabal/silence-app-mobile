package com.example.silenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: String,
    val nombre: String,
    val email: String,
    val sexo: String,
    val fechaNto: String,
    val pais: String
)
