package com.example.silenceapp.data.remote.socket

/**
 * Estados de conexión de Socket.IO
 */
sealed class ConnectionState {
    /**
     * No conectado
     */
    object Disconnected : ConnectionState()
    
    /**
     * Intentando conectar
     */
    object Connecting : ConnectionState()
    
    /**
     * Conectado exitosamente
     * @param connectedAt Timestamp de cuando se conectó
     */
    data class Connected(val connectedAt: Long) : ConnectionState()
    
    /**
     * Intentando reconectar después de error
     * @param attempt Número de intento de reconexión
     */
    data class Reconnecting(val attempt: Int) : ConnectionState()
    
    /**
     * Error de conexión
     * @param message Mensaje de error
     * @param canRetry Si se puede intentar reconectar
     */
    data class Error(val message: String, val canRetry: Boolean) : ConnectionState()
}
