package com.example.silenceapp.data.repository

import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.dao.UserDao
import com.example.silenceapp.data.local.entity.UserEntity
import com.example.silenceapp.data.remote.dto.LoginRequest
import com.example.silenceapp.data.remote.dto.RegisterRequest
import com.example.silenceapp.data.remote.response.AuthResponse
import com.example.silenceapp.data.remote.service.AuthService
import retrofit2.Response

class UserRepository(
    private val userDao: UserDao,
    private val api: AuthService,
    private val store: AuthDataStore

) {

    suspend fun loginUser(email: String, password: String): Boolean {
        val response = api.login(LoginRequest(email, password))
        store.saveToken(response.access_token)
        return true
    }

    suspend fun registerUser(nombre: String, email: String, password: String, sexo: String, fechaNto: String, pais: String): Boolean {
        val request = RegisterRequest(
            nombre = nombre,
            email = email,
            password = password,
            sexo = sexo,
            fechaNto = fechaNto,
            pais = pais
        )

        val response = api.register(request)

        store.saveToken(response.access_token)

        return true
    }

    suspend fun updateUserProfile(user: UserEntity): Boolean {
        userDao.updateUser(user)
        return true
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }
}
