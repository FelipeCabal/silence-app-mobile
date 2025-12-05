package com.example.silenceapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.remote.response.FriendRequestResponse
import com.example.silenceapp.data.remote.response.GroupInvitationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RequestsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authDataStore = AuthDataStore(application)
    private val friendRequestService = ApiClient.friendRequestService
    private val groupInvitationService = ApiClient.groupInvitationService
    
    private val _friendRequests = MutableStateFlow<List<FriendRequestResponse>>(emptyList())
    val friendRequests: StateFlow<List<FriendRequestResponse>> = _friendRequests
    
    private val _groupInvitations = MutableStateFlow<List<GroupInvitationResponse>>(emptyList())
    val groupInvitations: StateFlow<List<GroupInvitationResponse>> = _groupInvitations
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    companion object {
        private const val TAG = "RequestsViewModel"
    }
    
    /**
     * Cargar solicitudes de amistad
     */
    fun loadFriendRequests() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = authDataStore.getToken().first()
                
                if (token.isBlank()) {
                    Log.w(TAG, "⚠️ No hay token")
                    return@launch
                }
                
                Log.d(TAG, "🔄 Cargando solicitudes de amistad...")
                Log.d(TAG, "   Token: Bearer ${token.take(30)}...")
                val response = friendRequestService.getFriendRequests("Bearer $token")
                
                Log.d(TAG, "📥 Response code: ${response.code()}")
                Log.d(TAG, "📥 Response successful: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val requests = response.body() ?: emptyList()
                    Log.d(TAG, "📦 Total solicitudes recibidas: ${requests.size}")
                    requests.forEach { request ->
                        Log.d(TAG, "   - ID: ${request.id}, Status: ${request.status}, SenderId: ${request.senderId}, ReceiverId: ${request.receiverId}")
                    }
                    // Filtrar solo las pendientes (status = "P")
                    _friendRequests.value = requests.filter { it.status == "P" }
                    Log.d(TAG, "✅ ${_friendRequests.value.size} solicitudes de amistad pendientes (status P)")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "❌ Error: ${response.code()} - ${response.message()}")
                    Log.e(TAG, "❌ Error body: $errorBody")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al cargar solicitudes", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Cargar invitaciones a grupos
     */
    fun loadGroupInvitations() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = authDataStore.getToken().first()
                
                if (token.isBlank()) {
                    Log.w(TAG, "⚠️ No hay token")
                    return@launch
                }
                
                Log.d(TAG, "🔄 Cargando invitaciones a grupos...")
                Log.d(TAG, "   Token: Bearer ${token.take(30)}...")
                val response = groupInvitationService.getGroupInvitations("Bearer $token")
                
                Log.d(TAG, "📥 Response code: ${response.code()}")
                Log.d(TAG, "📥 Response successful: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val invitations = response.body() ?: emptyList()
                    Log.d(TAG, "📦 Total invitaciones recibidas: ${invitations.size}")
                    invitations.forEach { invitation ->
                        Log.d(TAG, "   - ID: ${invitation.id}, Status: ${invitation.status}, Group: ${invitation.group.nombre}, User: ${invitation.user.nombre}")
                    }
                    // Filtrar solo las pendientes (status = "P")
                    _groupInvitations.value = invitations.filter { it.status == "P" }
                    Log.d(TAG, "✅ ${_groupInvitations.value.size} invitaciones a grupos pendientes (status P)")
                } else {
                    Log.e(TAG, "❌ Error: ${response.code()} - ${response.message()}")
                    Log.e(TAG, "❌ Error body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al cargar invitaciones", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Aceptar solicitud de amistad
     */
    fun acceptFriendRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val token = authDataStore.getToken().first()
                Log.d(TAG, "✅ Aceptando solicitud: $requestId")
                
                val response = friendRequestService.acceptFriendRequest(requestId, "Bearer $token")
                
                if (response.isSuccessful) {
                    Log.d(TAG, "✅ Solicitud aceptada")
                    loadFriendRequests() // Recargar lista
                } else {
                    Log.e(TAG, "❌ Error al aceptar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al aceptar solicitud", e)
            }
        }
    }
    
    /**
     * Rechazar solicitud de amistad
     */
    fun rejectFriendRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val token = authDataStore.getToken().first()
                Log.d(TAG, "❌ Rechazando solicitud: $requestId")
                
                val response = friendRequestService.rejectFriendRequest(requestId, "Bearer $token")
                
                if (response.isSuccessful) {
                    Log.d(TAG, "✅ Solicitud rechazada")
                    loadFriendRequests() // Recargar lista
                } else {
                    Log.e(TAG, "❌ Error al rechazar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al rechazar solicitud", e)
            }
        }
    }
    
    /**
     * Aceptar invitación a grupo
     */
    fun acceptGroupInvitation(invitationId: String) {
        viewModelScope.launch {
            try {
                val token = authDataStore.getToken().first()
                Log.d(TAG, "✅ Aceptando invitación: $invitationId")
                
                val response = groupInvitationService.acceptGroupInvitation(invitationId, "Bearer $token")
                
                if (response.isSuccessful) {
                    Log.d(TAG, "✅ Invitación aceptada")
                    loadGroupInvitations() // Recargar lista
                } else {
                    Log.e(TAG, "❌ Error al aceptar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al aceptar invitación", e)
            }
        }
    }
    
    /**
     * Rechazar invitación a grupo
     */
    fun rejectGroupInvitation(invitationId: String) {
        viewModelScope.launch {
            try {
                val token = authDataStore.getToken().first()
                Log.d(TAG, "❌ Rechazando invitación: $invitationId")
                
                val response = groupInvitationService.rejectGroupInvitation(invitationId, "Bearer $token")
                
                if (response.isSuccessful) {
                    Log.d(TAG, "✅ Invitación rechazada")
                    loadGroupInvitations() // Recargar lista
                } else {
                    Log.e(TAG, "❌ Error al rechazar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al rechazar invitación", e)
            }
        }
    }
}
