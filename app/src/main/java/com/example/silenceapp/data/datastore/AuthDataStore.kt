package com.example.silenceapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("auth_prefs")

class AuthDataStore(private val context: Context) {

    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    fun getToken() = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY] ?: ""
    }
    
    /**
     * Guardar el ID del usuario
     */
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }
    
    /**
     * Obtener el ID del usuario
     */
    fun getUserId() = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY] ?: ""
    }
    
    /**
     * Guardar el nombre del usuario
     */
    suspend fun saveUserName(userName: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = userName
        }
    }
    
    /**
     * Obtener el nombre del usuario
     */
    fun getUserName() = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY] ?: ""
    }
}
