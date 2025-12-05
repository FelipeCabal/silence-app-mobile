package com.example.silenceapp.data.repository

import android.util.Log
import com.example.silenceapp.data.local.dao.NotificationDao
import com.example.silenceapp.data.local.entity.Notification
import com.example.silenceapp.data.mappers.toLocalNotification
import com.example.silenceapp.data.remote.client.ApiClient
import kotlinx.coroutines.flow.Flow

class NotificationRepository(
    private val notificationDao: NotificationDao
) {
    
    private val notificationService = ApiClient.notificationService
    
    companion object {
        private const val TAG = "NotificationRepository"
    }
    
    /**
     * Sincronizar notificaciones desde el servidor
     */
    suspend fun syncNotificationsFromServer(token: String): Result<Unit> {
        return try {
            Log.d(TAG, "🔄 Sincronizando notificaciones desde servidor...")
            Log.d(TAG, "   Token: ${token.take(50)}...")
            Log.d(TAG, "   Endpoint: GET /api/notifications (el backend filtra por JWT)")
            
            val response = notificationService.getNotifications("Bearer $token")
            
            Log.d(TAG, "📥 Response code: ${response.code()}")
            Log.d(TAG, "📥 Response successful: ${response.isSuccessful}")
            Log.d(TAG, "📥 Response message: ${response.message()}")
            
            // Log del raw response para debug
            try {
                val rawResponse = response.raw()
                Log.d(TAG, "📥 Response URL: ${rawResponse.request.url}")
                Log.d(TAG, "📥 Request Headers:")
                rawResponse.request.headers.forEach { header ->
                    if (header.first == "Authorization") {
                        Log.d(TAG, "   ${header.first}: Bearer ${header.second.take(50)}...")
                    } else {
                        Log.d(TAG, "   ${header.first}: ${header.second}")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "No se pudo obtener info del raw response", e)
            }
            
            if (response.isSuccessful) {
                val notifications = response.body() ?: emptyList()
                Log.d(TAG, "✅ ${notifications.size} notificaciones recibidas del servidor")
                
                if (notifications.isEmpty()) {
                    Log.w(TAG, "⚠️ El servidor devolvió una lista vacía")
                    Log.w(TAG, "═══════════════════════════════════════════")
                    Log.w(TAG, "PROBLEMA EN EL BACKEND:")
                    Log.w(TAG, "1. El endpoint GET /api/notifications responde 200 OK")
                    Log.w(TAG, "2. Pero devuelve array vacío []")
                    Log.w(TAG, "3. Hay notificaciones en MongoDB para este usuario")
                    Log.w(TAG, "═══════════════════════════════════════════")
                    Log.w(TAG, "SOLUCIÓN BACKEND:")
                    Log.w(TAG, "El controller debe:")
                    Log.w(TAG, "1. Extraer userId del JWT token (req.user.id)")
                    Log.w(TAG, "2. Filtrar: { receiver: userId } o { receiverId: userId }")
                    Log.w(TAG, "3. Verificar que el campo en Notification model sea 'receiver' con populate")
                    Log.w(TAG, "═══════════════════════════════════════════")
                }
                
                // Convertir y guardar en BD local
                val localNotifications = notifications.map { it.toLocalNotification() }
                Log.d(TAG, "📦 Notificaciones convertidas:")
                localNotifications.forEach { notification ->
                    Log.d(TAG, "   - ID: ${notification.id}")
                    Log.d(TAG, "     Receiver: ${notification.receiverId}")
                    Log.d(TAG, "     Message: ${notification.message}")
                }
                
                notificationDao.insertNotifications(localNotifications)
                
                // Verificar cuántas notificaciones hay en BD
                val totalCount = notificationDao.getNotificationCount()
                Log.d(TAG, "✅ Notificaciones sincronizadas en BD local")
                Log.d(TAG, "📊 Total en BD: $totalCount notificaciones")
                
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val error = "Error ${response.code()}: ${response.message()}"
                Log.e(TAG, "❌ Error al obtener notificaciones: $error")
                Log.e(TAG, "❌ Error body: $errorBody")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción al sincronizar notificaciones", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    fun getAllNotifications(userId: String): Flow<List<Notification>> {
        Log.d(TAG, "📋 getAllNotifications() llamado con userId: $userId")
        return notificationDao.getAllNotifications(userId)
    }
    
    fun getUnreadNotifications(userId: String): Flow<List<Notification>> {
        return notificationDao.getUnreadNotifications(userId)
    }
    
    fun getUnreadCount(userId: String): Flow<Int> {
        return notificationDao.getUnreadCount(userId)
    }
    
    suspend fun getNotificationById(notificationId: String): Notification? {
        return notificationDao.getNotificationById(notificationId)
    }
    
    suspend fun insertNotification(notification: Notification) {
        notificationDao.insertNotification(notification)
    }
    
    suspend fun insertNotifications(notifications: List<Notification>) {
        notificationDao.insertNotifications(notifications)
    }
    
    suspend fun updateNotification(notification: Notification) {
        notificationDao.updateNotification(notification)
    }
    
    suspend fun markAsRead(notificationId: String) {
        notificationDao.markAsRead(notificationId)
    }
    
    /**
     * Marcar notificación como leída en el servidor Y en BD local
     */
    suspend fun markAsReadOnServer(notificationId: String, token: String): Result<Unit> {
        return try {
            Log.d(TAG, "📖 Marcando notificación como leída en servidor...")
            Log.d(TAG, "   ID: $notificationId")
            
            val response = notificationService.markNotificationAsRead(notificationId, "Bearer $token")
            
            if (response.isSuccessful) {
                Log.d(TAG, "✅ Notificación marcada como leída en servidor")
                
                // Actualizar también en BD local
                notificationDao.markAsRead(notificationId)
                Log.d(TAG, "✅ Notificación marcada como leída en BD local")
                
                Result.success(Unit)
            } else {
                val error = "Error ${response.code()}: ${response.message()}"
                Log.e(TAG, "❌ Error al marcar como leída en servidor: $error")
                
                // Aún así marcar en local
                notificationDao.markAsRead(notificationId)
                
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción al marcar como leída", e)
            
            // Aún así marcar en local
            notificationDao.markAsRead(notificationId)
            
            Result.failure(e)
        }
    }
    
    suspend fun markAllAsRead(userId: String) {
        notificationDao.markAllAsRead(userId)
    }
    
    suspend fun deleteNotification(notificationId: String) {
        notificationDao.deleteNotification(notificationId)
    }
    
    suspend fun deleteAllNotifications(userId: String) {
        notificationDao.deleteAllNotifications(userId)
    }
}