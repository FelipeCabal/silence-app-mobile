package com.example.silenceapp.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.silenceapp.data.local.AppDatabase
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.UserEntity
import com.example.silenceapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = DatabaseProvider.getDatabase(application).userDao()

    private val repository = UserRepository(userDao)

    fun registerUser(name: String, email: String, password: String, phoneNumber: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.registerUser(UserEntity(name = name, email = email, password = password, phoneNumber = phoneNumber))
            }
            onResult(success)
        }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) {
                repository.loginUser(email, password)
            }
            onResult(user != null)
        }
    }

    fun updateUserProfile(user: UserEntity, onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO){
                repository.updateUserProfile(user)
            }
            onResult(success)
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