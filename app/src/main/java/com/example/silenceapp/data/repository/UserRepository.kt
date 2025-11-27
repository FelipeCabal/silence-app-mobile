package com.example.silenceapp.data.repository

import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.dao.UserDao
import com.example.silenceapp.data.local.entity.UserEntity
import com.example.silenceapp.data.remote.dto.UpdateProfileRequest
import com.example.silenceapp.data.remote.service.UserService
import kotlinx.coroutines.flow.first

class UserRepository (
    private val userDao: UserDao,
    private val api: UserService,
    private val store: AuthDataStore
){

    suspend fun updateUserProfile(user: UserEntity): Boolean {
        val token = store.getToken().first()
        val userRequest = UpdateProfileRequest(
            nombre = user.nombre,
            //email = user.email,
            //fechaNto = user.fechaNto,
            //sexo = user.sexo,
            pais = user.pais,
            imagen = user.imagen
        )
        try {
            api.update("Bearer $token", user.remoteId, userRequest)
            userDao.updateUser(user)
        }catch (e: Exception){

        }

        return true
    }

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }
}