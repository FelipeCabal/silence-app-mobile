package com.example.silenceapp.data.repository

import com.example.silenceapp.data.local.dao.UserDao
import com.example.silenceapp.data.local.entity.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: UserEntity): Boolean {
        val existingUser = userDao.getUserByEmail(user.email)
        if (existingUser != null) return false
        userDao.insertUser(user)
        return true
    }

    suspend fun loginUser(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
    }
}
