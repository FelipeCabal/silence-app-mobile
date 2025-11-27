package com.example.silenceapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.data.local.entity.Members
import com.example.silenceapp.data.local.entity.Message
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatDao = DatabaseProvider.getDatabase(application).chatDao()
    private val messageDao = DatabaseProvider.getDatabase(application).messageDao()
    private val membersDao = DatabaseProvider.getDatabase(application).membersDao()
    private val chatService = ApiClient.chatService
    private val repository = ChatRepository(chatDao, messageDao, membersDao, chatService)
    private val authDataStore = AuthDataStore(application)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    companion object {
        private const val TAG = "ChatViewModel"
    }

    // ============ SYNC OPERATIONS ============

    /**
     * Sincroniza todos los chats disponibles desde el servidor
     * NOTA: Chats privados omitidos - API no disponible aún
     */
    fun syncAllChats(token: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.syncAllChats(token)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Grupos y comunidades sincronizados exitosamente")
                    onResult(true)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al sincronizar chats", exception)
                    _error.value = exception.message
                    onResult(false)
                }
            )
        }
    }

    // PENDIENTE - API no disponible aún
    // TODO: Descomentar cuando el endpoint /chat-privado esté disponible
    /*
    /**
     * Sincroniza solo chats privados
     */
    fun syncPrivateChats(token: String, currentUserId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.syncPrivateChats(token, currentUserId)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chats ->
                    Log.d(TAG, "${chats.size} chats privados sincronizados")
                    onResult(true)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al sincronizar chats privados", exception)
                    _error.value = exception.message
                    onResult(false)
                }
            )
        }
    }
    */

    /**
     * Sincroniza solo comunidades
     */
    fun syncCommunities(token: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.syncCommunities(token)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chats ->
                    Log.d(TAG, "${chats.size} comunidades sincronizadas")
                    onResult(true)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al sincronizar comunidades", exception)
                    _error.value = exception.message
                    onResult(false)
                }
            )
        }
    }

    /**
     * Sincroniza solo grupos
     */
    fun syncGroups(token: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.syncGroups(token)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chats ->
                    Log.d(TAG, "${chats.size} grupos sincronizados")
                    onResult(true)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al sincronizar grupos", exception)
                    _error.value = exception.message
                    onResult(false)
                }
            )
        }
    }

    // ============ CHAT OPERATIONS ============

    /**
     * Crea un nuevo grupo
     */
    fun createGroup(token: String, nombre: String, descripcion: String, imagen: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.createGroup(token, nombre, descripcion, imagen)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chatId ->
                    Log.d(TAG, "Grupo creado exitosamente: $chatId")
                    onResult(Result.success(chatId))
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al crear grupo", exception)
                    _error.value = exception.message
                    onResult(Result.failure(exception))
                }
            )
        }
    }

    /**
     * Crea una nueva comunidad
     */
    fun createCommunity(token: String, nombre: String, descripcion: String, imagen: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.createCommunity(token, nombre, descripcion, imagen)
            }
            
            _isLoading.value = false
            result.fold(
                onSuccess = { chatId ->
                    Log.d(TAG, "Comunidad creada exitosamente: $chatId")
                    onResult(Result.success(chatId))
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al crear comunidad", exception)
                    _error.value = exception.message
                    onResult(Result.failure(exception))
                }
            )
        }
    }

    /**
     * Crea un nuevo grupo manejando la autenticación internamente
     */
    fun createGroupWithAuth(nombre: String, descripcion: String, imagen: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val token = authDataStore.getToken().first()
            if (token.isBlank()) {
                _error.value = "No se encontró token de autenticación"
                onResult(Result.failure(Exception("No autenticado")))
                return@launch
            }
            createGroup(token, nombre, descripcion, imagen, onResult)
        }
    }

    /**
     * Crea una nueva comunidad manejando la autenticación internamente
     */
    fun createCommunityWithAuth(nombre: String, descripcion: String, imagen: String, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val token = authDataStore.getToken().first()
            if (token.isBlank()) {
                _error.value = "No se encontró token de autenticación"
                onResult(Result.failure(Exception("No autenticado")))
                return@launch
            }
            createCommunity(token, nombre, descripcion, imagen, onResult)
        }
    }

    fun getAllChats(): Flow<List<Chat>> {
        return repository.getAllChats()
    }

    fun getChatsByType(type: String): Flow<List<Chat>> {
        return repository.getChatsByType(type)
    }

    fun searchChats(query: String): Flow<List<Chat>> {
        return repository.searchChats(query)
    }

    fun insertChat(chat: Chat, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // No activar isLoading para operaciones locales rápidas
            val success = withContext(Dispatchers.IO) {
                repository.insertChat(chat)
            }
            if (success) {
                Log.d(TAG, "Chat insertado exitosamente: ${chat.id}")
            } else {
                Log.e(TAG, "Error al insertar chat")
            }
            onResult(success)
        }
    }

    fun insertChats(chats: List<Chat>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // No activar isLoading para no bloquear UI con datos de prueba
            val success = withContext(Dispatchers.IO) {
                repository.insertChats(chats)
            }
            if (success) {
                Log.d(TAG, "${chats.size} chats insertados exitosamente")
            } else {
                Log.e(TAG, "Error al insertar chats")
            }
            onResult(success)
        }
    }

    fun updateChat(chat: Chat, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.updateChat(chat)
            }
            if (success) {
                Log.d(TAG, "Chat actualizado: ${chat.id}")
            }
            onResult(success)
        }
    }

    fun deleteChat(chat: Chat, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.deleteChat(chat)
            }
            if (success) {
                Log.d(TAG, "Chat eliminado: ${chat.id}")
            }
            onResult(success)
        }
    }

    // ============ MESSAGE OPERATIONS ============

    fun getMessagesByChatId(chatId: String): Flow<List<Message>> {
        return repository.getMessagesByChatId(chatId)
    }

    fun getUnreadMessages(chatId: String): Flow<List<Message>> {
        return repository.getUnreadMessages(chatId)
    }

    fun insertMessage(message: Message, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.insertMessage(message)
            }
            if (success) {
                Log.d(TAG, "Mensaje insertado: ${message.id}")
            }
            onResult(success)
        }
    }

    fun insertMessages(messages: List<Message>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.insertMessages(messages)
            }
            if (success) {
                Log.d(TAG, "${messages.size} mensajes insertados")
            }
            onResult(success)
        }
    }

    fun updateMessage(message: Message, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.updateMessage(message)
            }
            onResult(success)
        }
    }

    fun deleteMessage(message: Message, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.deleteMessage(message)
            }
            onResult(success)
        }
    }

    fun markAllMessagesAsRead(chatId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.markAllMessagesAsRead(chatId)
            }
            if (success) {
                Log.d(TAG, "Mensajes marcados como leídos en chat: $chatId")
            }
            onResult(success)
        }
    }

    fun getUnreadMessageCount(chatId: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                repository.getUnreadMessageCount(chatId)
            }
            onResult(count)
        }
    }

    // ============ MEMBERS OPERATIONS ============

    fun getChatMembers(chatId: String): Flow<List<Members>> {
        return repository.getChatMembers(chatId)
    }

    fun getUserChats(userId: String): Flow<List<Members>> {
        return repository.getUserChats(userId)
    }

    fun getMembersByRole(chatId: String, role: String): Flow<List<Members>> {
        return repository.getMembersByRole(chatId, role)
    }

    fun getMemberCount(chatId: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                repository.getMemberCount(chatId)
            }
            onResult(count)
        }
    }

    fun insertMember(member: Members, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.insertMember(member)
            }
            if (success) {
                Log.d(TAG, "Miembro añadido: ${member.userId} al chat ${member.chatId}")
            }
            onResult(success)
        }
    }

    fun insertMembers(members: List<Members>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.insertMembers(members)
            }
            if (success) {
                Log.d(TAG, "${members.size} miembros insertados")
            }
            onResult(success)
        }
    }

    fun updateMember(member: Members, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.updateMember(member)
            }
            onResult(success)
        }
    }

    fun deleteMember(member: Members, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.deleteMember(member)
            }
            if (success) {
                Log.d(TAG, "Miembro eliminado: ${member.userId} del chat ${member.chatId}")
            }
            onResult(success)
        }
    }

    fun clearError() {
        _error.value = null
    }
}
