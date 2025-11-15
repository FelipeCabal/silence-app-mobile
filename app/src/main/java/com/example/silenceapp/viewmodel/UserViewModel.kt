package com.example.silenceapp.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.AppDatabase
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.UserEntity
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = DatabaseProvider.getDatabase(application).userDao()
    private val api = ApiClient.authService
    private val store = AuthDataStore(application)

    private val repository = UserRepository(userDao, api, store)

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess = _authSuccess.asStateFlow()

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val success = repository.loginUser(email, password)
                _authSuccess.value = success
            } catch (e: Exception) {
                _authSuccess.value = false
            }
        }
    }

    fun clearAuthSuccess() {
        _authSuccess.value = false
    }

    fun registerUser(
        nombre: String,
        email: String,
        password: String,
        sexo: String,
        fechaNto: String,
        pais: String
    ) {
        viewModelScope.launch {
            try {
                val success = repository.registerUser(nombre, email, password, sexo, fechaNto, pais)
                _authSuccess.value = success
            } catch (e: Exception) {
                _authSuccess.value = false
            }
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