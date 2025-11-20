package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.AppDatabase
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.UserEntity
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = DatabaseProvider.getDatabase(application).userDao()
    private val store = AuthDataStore(application)
    private val api = ApiClient.userService
    private val repository = UserRepository(userDao, api, store)

    fun updateUserProfile(user: UserEntity, onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            try {
                val success = withContext(Dispatchers.IO){
                    repository.updateUserProfile(user)
                }
                onResult(success)
            }catch (e: Exception){
                onResult(false)
            }
        }
    }
    fun getUserByEmail(email: String, onResult: (UserEntity?) -> Unit){
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO){
                repository.getUserByEmail(email)
            }
            onResult(user)
        }
    }
}