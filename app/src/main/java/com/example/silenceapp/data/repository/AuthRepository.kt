package com.example.silenceapp.data.repository

import android.util.Base64
import android.util.Log
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.dao.UserDao
import com.example.silenceapp.data.local.entity.UserEntity
import com.example.silenceapp.data.remote.dto.LoginRequest
import com.example.silenceapp.data.remote.dto.RegisterRequest
import com.example.silenceapp.data.remote.response.ProfileResponse
import com.example.silenceapp.data.remote.service.AuthService
import kotlinx.coroutines.flow.first
import org.json.JSONObject

class AuthRepository(
    private val api: AuthService,
    private val store: AuthDataStore
) {

    /**
     * Extrae el userId del payload del JWT
     */
    private fun extractUserIdFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e("AuthRepository", "❌ JWT inválido: no tiene 3 partes")
                return null
            }
            
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            Log.d("AuthRepository", "📄 JWT Payload: $payload")
            
            val json = JSONObject(payload)
            val userId = json.optString("id", "")
            Log.d("AuthRepository", "👤 userId extraído del JWT: $userId")
            
            if (userId.isBlank()) null else userId
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Error al decodificar JWT", e)
            null
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        val response = api.login(LoginRequest(email, password))
        store.saveToken(response.access_token)
        
        // Extraer y guardar el userId del token
        val userId = extractUserIdFromToken(response.access_token)
        if (userId != null) {
            store.saveUserId(userId)
            Log.d("AuthRepository", "✅ userId guardado: $userId")
        } else {
            Log.e("AuthRepository", "⚠️ No se pudo extraer userId del token")
        }
        
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
        
        // Extraer y guardar el userId del token
        val userId = extractUserIdFromToken(response.access_token)
        if (userId != null) {
            store.saveUserId(userId)
            Log.d("AuthRepository", "✅ userId guardado después de registro: $userId")
        } else {
            Log.e("AuthRepository", "⚠️ No se pudo extraer userId del token en registro")
        }

        return true
    }

    suspend fun getProfile(): ProfileResponse {
        val token = store.getToken().first()
        return api.profile("Bearer $token")
    }

}
