package com.example.silenceapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.Notification
import com.example.silenceapp.data.remote.socket.SocketEvent
import com.example.silenceapp.data.remote.socket.SocketIOManager
import com.example.silenceapp.data.repository.NotificationRepository
import com.example.silenceapp.utils.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationDao = DatabaseProvider.getDatabase(application).notificationDao()
    private val repository = NotificationRepository(notificationDao)
    private val socketManager: SocketIOManager = SocketIOManager.getInstance("https://silence-app-back-production.up.railway.app/api/")
    private val context = application.applicationContext
    private val authDataStore = AuthDataStore(application)

    companion object {
        private const val TAG = "NotificationViewModel"
    }

    init {
        // Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(context)
        
        // Escuchar eventos de notificaciones del WebSocket
        listenToNotifications()
    }

    /**
     * Obtener todas las notificaciones de un usuario
     */
    fun getNotifications(userId: String): Flow<List<Notification>> {
        Log.d(TAG, "📋 getNotifications() llamado")
        Log.d(TAG, "   userId: $userId")
        Log.d(TAG, "   userId.length: ${userId.length}")
        Log.d(TAG, "   userId.isEmpty: ${userId.isEmpty()}")
        
        val flow = repository.getAllNotifications(userId)
        
        // Log para ver qué datos llegan del Flow
        viewModelScope.launch {
            flow.collect { notifications ->
                Log.d(TAG, "📊 Flow emitió ${notifications.size} notificaciones")
                notifications.forEachIndexed { index, notification ->
                    Log.d(TAG, "   [$index] ID: ${notification.id}")
                    Log.d(TAG, "       Mensaje: ${notification.message}")
                    Log.d(TAG, "       Receiver: ${notification.receiverId}")
                    Log.d(TAG, "       Sender: ${notification.senderName}")
                }
            }
        }
        
        return flow
    }

    /**
     * Obtener contador de notificaciones no leídas
     */
    fun getUnreadCount(userId: String): StateFlow<Int> {
        return repository.getUnreadCount(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )
    }

    /**
     * Marcar notificación como leída (en servidor y BD local)
     */
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                // Obtener token
                val token = authDataStore.getToken().first()
                
                if (token.isBlank()) {
                    Log.w(TAG, "⚠️ No hay token, solo marcando en BD local")
                    repository.markAsRead(notificationId)
                    return@launch
                }
                
                Log.d(TAG, "📖 Marcando notificación como leída: $notificationId")
                repository.markAsReadOnServer(notificationId, token)
                Log.d(TAG, "✅ Notificación marcada como leída")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al marcar notificación como leída", e)
            }
        }
    }

    /**
     * Conectar al WebSocket de notificaciones y sincronizar notificaciones existentes
     */
    fun connectToNotifications(token: String) {
        Log.d(TAG, "🔔 connectToNotifications() llamado")
        Log.d(TAG, "   Token: ${token.take(50)}...")
        Log.d(TAG, "   Token length: ${token.length}")
        
        // Primero sincronizar notificaciones existentes desde el servidor
        viewModelScope.launch {
            // Obtener el userId actual
            val currentUserId = authDataStore.getUserId().first()
            Log.d(TAG, "👤 UserId actual: $currentUserId")
            
            // Ver cuántas notificaciones hay en BD antes de sincronizar
            val countBefore = notificationDao.getNotificationCount()
            val countForUserBefore = notificationDao.getNotificationCountForUser(currentUserId)
            Log.d(TAG, "📊 Notificaciones en BD ANTES de sync:")
            Log.d(TAG, "   Total: $countBefore")
            Log.d(TAG, "   Para usuario $currentUserId: $countForUserBefore")
            
            Log.d(TAG, "🔄 Sincronizando notificaciones históricas...")
            repository.syncNotificationsFromServer(token)
            
            // Ver cuántas notificaciones hay después de sincronizar
            val countAfter = notificationDao.getNotificationCount()
            val countForUserAfter = notificationDao.getNotificationCountForUser(currentUserId)
            Log.d(TAG, "📊 Notificaciones en BD DESPUÉS de sync:")
            Log.d(TAG, "   Total: $countAfter")
            Log.d(TAG, "   Para usuario $currentUserId: $countForUserAfter")
        }
        
        // Luego conectar al WebSocket para notificaciones en tiempo real
        socketManager.connectToNotifications(token)
        Log.d(TAG, "   socketManager.connectToNotifications() ejecutado")
    }
    
    /**
     * Desconectar del WebSocket de notificaciones
     */
    fun disconnect() {
        Log.d(TAG, "🚪 Desconectando notificaciones...")
        socketManager.disconnectNotifications()
    }

    /**
     * Escuchar eventos de notificaciones del WebSocket
     */
    private fun listenToNotifications() {
        viewModelScope.launch {
            socketManager.notificationEvents.collect { event ->
                when (event) {
                    is SocketEvent.NotificationReceived -> {
                        Log.d(TAG, "🔔 Nueva notificación recibida del WebSocket")
                        Log.d(TAG, "   Mensaje: ${event.message}")
                        Log.d(TAG, "   Sender: ${event.senderName} (${event.senderId})")
                        Log.d(TAG, "   Receiver: ${event.receiverName} (${event.receiverId})")
                        
                        // IMPORTANTE: Verificar que la notificación sea para el usuario actual
                        val currentUserId = authDataStore.getUserId().first()
                        
                        if (currentUserId.isBlank()) {
                            Log.w(TAG, "⚠️ No hay userId en sesión, ignorando notificación")
                            return@collect
                        }
                        
                        if (event.receiverId != currentUserId) {
                            Log.w(TAG, "⚠️ Notificación NO es para este usuario")
                            Log.w(TAG, "   Usuario actual: $currentUserId")
                            Log.w(TAG, "   Receiver de notificación: ${event.receiverId}")
                            Log.w(TAG, "   ❌ IGNORANDO notificación")
                            return@collect
                        }
                        
                        Log.d(TAG, "✅ Notificación ES para este usuario, procesando...")
                        
                        // Crear notificación local
                        val notification = Notification(
                            id = event.id,
                            message = event.message,
                            senderId = event.senderId,
                            senderName = event.senderName,
                            senderImage = event.senderImage,
                            receiverId = event.receiverId,
                            receiverName = event.receiverName,
                            type = event.type,
                            isRead = event.isRead,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        
                        // Guardar en base de datos
                        try {
                            repository.insertNotification(notification)
                            Log.d(TAG, "✅ Notificación guardada en BD")
                            
                            // Mostrar notificación push
                            showPushNotification(notification)
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error al guardar notificación", e)
                        }
                    }
                    else -> {
                        // Otros eventos no nos interesan aquí
                    }
                }
            }
        }
    }
    
    /**
     * Mostrar notificación push
     */
    private fun showPushNotification(notification: Notification) {
        // Verificar permiso
        if (!NotificationHelper.hasNotificationPermission(context)) {
            Log.w(TAG, "⚠️ No hay permiso de notificaciones")
            return
        }
        
        // Determinar título según el tipo
        val title = when (notification.type) {
            1 -> "❤️ Nuevo like"
            2 -> "💬 Nuevo comentario"
            3 -> "👥 Solicitud de amistad"
            else -> "🔔 Nueva notificación"
        }
        
        // Mostrar notificación
        NotificationHelper.showNotification(
            context = context,
            title = title,
            message = notification.message,
            notificationId = notification.id.hashCode()
        )
        
        Log.d(TAG, "📱 Push notification mostrada")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "🧹 NotificationViewModel cleared")
    }
}