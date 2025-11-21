package com.example.silenceapp.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.remote.response.ProfileResponse
import com.example.silenceapp.data.repository.AuthRepository
import retrofit2.HttpException
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val api = ApiClient.authService
    private val store = AuthDataStore(application)

    private val repository = AuthRepository( api, store)

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess = _authSuccess.asStateFlow()

    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated.asStateFlow()

    init {
        viewModelScope.launch {
            store.getToken().collect { token ->
                _isAuthenticated.value = token.isNotBlank()
            }
        }
    }

    //UTILS
    fun clearAuthSuccess() {
        _authSuccess.value = false
    }
    fun loadToken(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val token = store.getToken().first()
            onResult(token)
        }
    }

    //AUTH
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val success = repository.loginUser(email, password)
                _authSuccess.value = success
            } catch (e: Exception) {
                e.printStackTrace() // Imprimir el error en logcat
                _authSuccess.value = false
            }
        }
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
    fun logout() {
        viewModelScope.launch {
            store.saveToken("")
        }
    }
    fun getProfile(onResult: (ProfileResponse?) -> Unit) {
        viewModelScope.launch {
            try {
                val profile = withContext(Dispatchers.IO) { repository.getProfile() }
                onResult(profile)
            } catch (e: HttpException) {
                // 401 u otros errores: devolver null para que la UI maneje reautenticación
                onResult(null)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}