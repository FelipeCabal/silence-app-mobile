package com.example.silenceapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.repository.PostRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostViewModel(application: Application): AndroidViewModel(application){
    private val postDao = DatabaseProvider.getDatabase(application).postDao()
    private val repository = PostRepository(postDao)
    private val gson = Gson()

    fun getPosts(onResult: (List<Post>) -> Unit){
        viewModelScope.launch {
            val postList = withContext(Dispatchers.IO) {
                repository.getPosts()
            }
            onResult(postList)
        }
    }
    
    fun createPost(
        userId: String,
        userName: String,
        description: String?,
        imageUris: List<String>,
        esAnonimo: Boolean = false,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            // Copiar imágenes a almacenamiento permanente
            val permanentUris = withContext(Dispatchers.IO) {
                imageUris.mapNotNull { uriString ->
                    try {
                        copyImageToPermanentStorage(uriString.toUri())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
            
            val imagesJson = if (permanentUris.isNotEmpty()) gson.toJson(permanentUris) else null
            val post = Post(
                userId = userId,
                userName = userName,
                description = description,
                images = imagesJson,
                esAnonimo = esAnonimo,
                createdAt = System.currentTimeMillis()
            )
            val success = withContext(Dispatchers.IO) {
                repository.createPost(post)
            }
            onResult(success)
        }
    }
    
    private fun copyImageToPermanentStorage(sourceUri: Uri): String {
        val context = getApplication<Application>()
        
        // Crear directorio para imágenes de posts si no existe
        val imagesDir = File(context.filesDir, "post_images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        
        // Generar nombre único para el archivo
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(imagesDir, "IMG_${timeStamp}_${System.currentTimeMillis()}.jpg")
        
        // Copiar el contenido de la URI temporal al archivo permanente
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            imageFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        // Retornar la ruta absoluta del archivo
        return imageFile.absolutePath
    }
}