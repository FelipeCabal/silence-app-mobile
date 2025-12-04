package com.example.silenceapp.data.remote.socket

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Clase de prueba para verificar la conexión Socket.IO
 * USO: Crear una instancia y llamar a testConnection() con un token válido
 */
class SocketIOTest(
    private val baseUrl: String
) {
    private val socketManager = SocketIOManager.getInstance(baseUrl)
    private val scope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        private const val TAG = "SocketIOTest"
    }
    
    /**
     * Probar conexión básica
     */
    fun testConnection(token: String) {
        Log.d(TAG, "========================================")
        Log.d(TAG, "🧪 Iniciando prueba de Socket.IO")
        Log.d(TAG, "URL: $baseUrl")
        Log.d(TAG, "========================================")
        
        // Observar estado de conexión
        scope.launch {
            socketManager.connectionState.collectLatest { state ->
                when (state) {
                    is ConnectionState.Disconnected -> {
                        Log.d(TAG, "📊 Estado: DESCONECTADO")
                    }
                    is ConnectionState.Connecting -> {
                        Log.d(TAG, "📊 Estado: CONECTANDO...")
                    }
                    is ConnectionState.Connected -> {
                        Log.d(TAG, "📊 Estado: ✅ CONECTADO (${state.connectedAt})")
                        // Cuando se conecte, probar unirse a un chat
                        delay(1000)
                        testJoinChat()
                    }
                    is ConnectionState.Reconnecting -> {
                        Log.d(TAG, "📊 Estado: 🔄 RECONECTANDO (intento ${state.attempt})")
                    }
                    is ConnectionState.Error -> {
                        Log.e(TAG, "📊 Estado: ❌ ERROR - ${state.message}")
                        Log.e(TAG, "   Puede reintentar: ${state.canRetry}")
                    }
                }
            }
        }
        
        // Observar eventos
        scope.launch {
            socketManager.chatEvents.collectLatest { event ->
                when (event) {
                    is SocketEvent.Connected -> {
                        Log.d(TAG, "🎉 Evento Connected:")
                        Log.d(TAG, "   userId: ${event.userId}")
                        Log.d(TAG, "   socketId: ${event.socketId}")
                        Log.d(TAG, "   timestamp: ${event.timestamp}")
                    }
                    
                    is SocketEvent.JoinedChat -> {
                        Log.d(TAG, "🎉 Evento JoinedChat:")
                        Log.d(TAG, "   chatId: ${event.chatId}")
                        Log.d(TAG, "   chatType: ${event.chatType}")
                        Log.d(TAG, "   roomName: ${event.roomName}")
                    }
                    
                    is SocketEvent.MessageReceived -> {
                        Log.d(TAG, "💬 Evento MessageReceived:")
                        Log.d(TAG, "   messageId: ${event.message._id}")
                        Log.d(TAG, "   from: ${event.message.userId}")
                        Log.d(TAG, "   content: ${event.message.content}")
                    }
                    
                    is SocketEvent.UserJoined -> {
                        Log.d(TAG, "👋 Evento UserJoined:")
                        Log.d(TAG, "   userId: ${event.userId}")
                    }
                    
                    is SocketEvent.UserTyping -> {
                        Log.d(TAG, "⌨️ Evento UserTyping:")
                        Log.d(TAG, "   userId: ${event.userId}")
                        Log.d(TAG, "   isTyping: ${event.isTyping}")
                    }
                    
                    is SocketEvent.Error -> {
                        Log.e(TAG, "❌ Evento Error:")
                        Log.e(TAG, "   event: ${event.event}")
                        Log.e(TAG, "   message: ${event.message}")
                    }
                    
                    else -> {
                        Log.d(TAG, "📡 Evento: ${event::class.simpleName}")
                    }
                }
            }
        }
        
        // Conectar
        socketManager.connectToChats(token)
    }
    
    /**
     * Probar unirse a un chat de prueba
     */
    private fun testJoinChat() {
        Log.d(TAG, "")
        Log.d(TAG, "🧪 Probando joinChat...")
        
        // Cambia estos valores por un chat real de tu sistema
        val testChatId = "test_chat_123"
        val testChatType = ChatType.GROUP
        
        socketManager.joinChat(testChatId, testChatType)
    }
    
    /**
     * Probar envío de mensaje
     */
    fun testSendMessage(chatId: String, chatType: ChatType, message: String) {
        Log.d(TAG, "")
        Log.d(TAG, "🧪 Probando sendMessage...")
        socketManager.sendMessage(chatId, message, chatType)
    }
    
    /**
     * Probar indicador de escritura
     */
    fun testTyping(chatId: String, chatType: ChatType) {
        Log.d(TAG, "")
        Log.d(TAG, "🧪 Probando typing indicator...")
        
        scope.launch {
            // Simular escritura
            socketManager.setTyping(chatId, chatType, true)
            delay(3000)
            socketManager.setTyping(chatId, chatType, false)
        }
    }
    
    /**
     * Desconectar
     */
    fun disconnect() {
        Log.d(TAG, "")
        Log.d(TAG, "🧪 Desconectando...")
        socketManager.disconnect()
    }
}
