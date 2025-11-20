package com.example.silenceapp.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.remote.response.UploadImageResponse
import com.example.silenceapp.data.remote.service.FirebaseService
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class FirebaseRepository(
    private val api: FirebaseService,
    private val store: AuthDataStore,
    private val context: Context
) {

    /**
     * Sube una imagen a Firebase Storage mediante el endpoint backend.
     * @param imageUri URI de la imagen seleccionada
     * @param folder Carpeta opcional donde guardar (default: "images")
     * @return UploadImageResponse con la URL de la imagen subida
     */
    suspend fun uploadImage(imageUri: Uri, folder: String = "images"): UploadImageResponse {
        val token = store.getToken().first()
        
        // Convertir URI a File temporal
        val file = uriToFile(imageUri)
        
        Log.d("FirebaseRepo", "Archivo a subir: ${file.name}, Tamaño: ${file.length()} bytes")
        Log.d("FirebaseRepo", "Folder: $folder")
        
        // Crear RequestBody para la imagen con tipo MIME específico
        val mimeType = when {
            file.name.endsWith(".jpg", true) || file.name.endsWith(".jpeg", true) -> "image/jpeg"
            file.name.endsWith(".png", true) -> "image/png"
            file.name.endsWith(".webp", true) -> "image/webp"
            else -> "image/jpeg"
        }
        
        Log.d("FirebaseRepo", "MIME Type: $mimeType")
        
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        
        // Crear MultipartBody.Part con el nombre "image" (debe coincidir con @UploadedFile del backend)
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        
        // Crear RequestBody para el folder
        val folderBody = folder.toRequestBody("text/plain".toMediaTypeOrNull())
        
        Log.d("FirebaseRepo", "Iniciando upload...")
        
        // Llamar al servicio
        val response = try {
            api.uploadImage("Bearer $token", imagePart, folderBody)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Error en upload: ${e.message}", e)
            throw e
        }
        
        Log.d("FirebaseRepo", "Upload exitoso: ${response.data.url}")
        
        // Limpiar archivo temporal
        file.delete()
        
        return response
    }

    /**
     * Convierte un Uri en un File temporal
     */
    private fun uriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("No se pudo abrir el URI: $uri")
        
        val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        
        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        
        return tempFile
    }
}
