package com.example.silenceapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.remote.repository.ApiPostRepository
import com.example.silenceapp.data.repository.PostRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PostsUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val error: String? = null
)
class PostViewModel(application: Application): AndroidViewModel(application){
    private val postDao = DatabaseProvider.getDatabase(application).postDao()
    private val apiRepository = ApiPostRepository()
    private val repository = PostRepository(postDao)
    private val gson = Gson()

    private val _uiState = MutableStateFlow(PostsUiState())
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    private val _postDetailState = MutableStateFlow(PostDetailUiState())
    val postDetailState: StateFlow<PostDetailUiState> = _postDetailState.asStateFlow()


    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Primero cargar posts locales
                val localPosts = withContext(Dispatchers.IO) {
                    repository.getPosts()
                }
                _uiState.value = _uiState.value.copy(posts = localPosts, isLoading = false)

                // Luego sincronizar con la API
                syncPostsFromApi()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar posts: ${e.message}"
                )
            }
        }
    }


    private suspend fun syncPostsFromApi() {
        withContext(Dispatchers.IO) {
            try {
                // Obtener posts de la API
                val remotePosts = apiRepository.getAllPosts()
                val existinPosts = repository.getPosts()
                val existingIds = existinPosts.map{it.remoteId}.toSet()

                val newPosts = remotePosts.filter {it.remoteId !in existingIds}

                // Guardar en base de datos local
                newPosts.forEach { post ->
                    repository.createPost(post)
                }

                // Actualizar UI con los posts locales
                val updatedPosts = repository.getPosts()
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        posts = updatedPosts,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Error de conexión: ${e.message}",
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }
    fun loadPostDetail(remoteId: String) {
        viewModelScope.launch {
            _postDetailState.value = PostDetailUiState(isLoading = true)

            try {
                val post = withContext(Dispatchers.IO){
                    apiRepository.getPostById(remoteId)
                }

                _postDetailState.value = PostDetailUiState(
                    isLoading = false,
                    post = post
                )
            } catch (e: Exception){
                _postDetailState.value = PostDetailUiState(
                    isLoading = false,
                    error = "Error al cargar el post: ${e.message}"
                )
            }
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