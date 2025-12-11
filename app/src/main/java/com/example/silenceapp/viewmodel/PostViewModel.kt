package com.example.silenceapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.remote.repository.ApiPostRepository
import com.example.silenceapp.data.remote.response.ComentarioResponse
import com.example.silenceapp.data.repository.AuthRepository
import com.example.silenceapp.data.repository.PostRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    val comments: List<ComentarioResponse> = emptyList(),
    val error: String? = null,
    val isSendingComment: Boolean = false,
    val commentError: String? = null
)
class PostViewModel(application: Application): AndroidViewModel(application){
    private val postDao = DatabaseProvider.getDatabase(application).postDao()
    private val authDataStore = AuthDataStore(application)

    private val api = ApiClient.postService
    private val apiRepository = ApiPostRepository(api,authDataStore)
    private val repository = PostRepository(postDao)
    private val likeService = ApiClient.likeService

    private val authRepository = AuthRepository(ApiClient.authService, authDataStore)
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
                // ⚠️ TEMPORAL: Limpiar posts sin remoteId de la BD
                val localPosts = withContext(Dispatchers.IO) {
                    val allPosts = repository.getPosts()
                    // Eliminar posts sin remoteId
                    allPosts.filter { it.remoteId == null }.forEach { post ->
                        android.util.Log.d("PostViewModel", "🗑️ Eliminando post sin remoteId: id=${post.id}")
                        repository.deletePost(post.id)
                    }
                    // Retornar solo posts válidos
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
                android.util.Log.d("PostViewModel", "📥 Posts de API: ${remotePosts.size}")
                remotePosts.forEach { post ->
                    android.util.Log.d("PostViewModel", "  - Post remoteId: ${post.remoteId}, cantComentarios: ${post.cantComentarios}")
                }

                val existingPosts = repository.getPosts()
                val existingPostsMap = existingPosts.associateBy { it.remoteId }

                // Separar posts nuevos y existentes
                val newPosts = remotePosts.filter { it.remoteId !in existingPostsMap.keys }
                val postsToUpdate = remotePosts.filter { it.remoteId in existingPostsMap.keys }

                android.util.Log.d("PostViewModel", "🆕 Posts nuevos a guardar: ${newPosts.size}")
                android.util.Log.d("PostViewModel", "🔄 Posts a actualizar: ${postsToUpdate.size}")

                // Guardar posts nuevos
                newPosts.forEach { post ->
                    repository.createPost(post)
                }

                // Actualizar posts existentes
                postsToUpdate.forEach { remotePost ->
                    val localPost = existingPostsMap[remotePost.remoteId]
                    if (localPost != null) {
                        // Actualizar el post manteniendo el ID local
                        val updatedPost = remotePost.copy(id = localPost.id)
                        repository.updatePost(updatedPost)
                        android.util.Log.d("PostViewModel", "✅ Actualizado post ${remotePost.remoteId}: cantComentarios=${remotePost.cantComentarios}")
                    }
                }

                // Obtener posts actualizados de la BD local
                val updatedPosts = repository.getPosts()
                android.util.Log.d("PostViewModel", "💾 Posts en BD local: ${updatedPosts.size}")

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
                // Obtener userId del usuario actual desde el perfil
                val userId: String? = withContext(Dispatchers.IO) {
                    try {
                        authRepository.getProfile().id
                    } catch (e: Exception) {
                        null
                    }
                }

                try {
                    val post = apiRepository.getPostById(
                        id = remoteId,
                        currentUserId = userId
                    )

                    _postDetailState.value = PostDetailUiState(
                        isLoading = false,
                        post = post,
                        comments = post.comentarios
                    )
                } catch (e: Exception){
                    _postDetailState.value = PostDetailUiState(
                        isLoading = false,
                        error = "Error al cargar los comentarios: ${e.message}"
                    )
                }

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
            try {
                // Crear el post local temporal
                val post = Post(
                    userId = userId,
                    userName = userName,
                    description = description,
                    images = imageUris,
                    esAnonimo = esAnonimo,
                    createdAt = System.currentTimeMillis()
                )

                val createdPost = withContext(Dispatchers.IO) {
                    apiRepository.createPost(post, userId)
                }

                withContext(Dispatchers.IO) {
                    repository.createPost(createdPost)
                }

                loadPosts()

                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("PostViewModel", "Error creating post: ${e.message}")
                onResult(false)
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            android.util.Log.d("PostViewModel", "🔥 toggleLike called with postId: $postId")

            // Buscar el post en el detalle O en la lista del home
            val currentPost = _postDetailState.value.post?.takeIf { it.remoteId == postId }
                ?: _uiState.value.posts.find { it.remoteId == postId }

            if (currentPost == null) {
                android.util.Log.e("PostViewModel", "❌ Post not found with id: $postId")
                return@launch
            }

            android.util.Log.d("PostViewModel", "📍 Post found: hasLiked=${currentPost.hasLiked}, likes=${currentPost.cantLikes}")

            try {

                // Actualizar UI inmediatamente (optimistic update)
                val newHasLiked = !currentPost.hasLiked
                val newLikeCount = if (newHasLiked) currentPost.cantLikes + 1 else currentPost.cantLikes - 1

                // Actualizar el detalle si el post está ahí
                if (_postDetailState.value.post?.remoteId == postId) {
                    val updatedPost = currentPost.copy(
                        hasLiked = newHasLiked,
                        cantLikes = maxOf(0, newLikeCount)
                    )
                    _postDetailState.value = _postDetailState.value.copy(post = updatedPost)
                }

                // Actualizar la lista de posts en el home
                val updatedPosts = _uiState.value.posts.map { post ->
                    if (post.remoteId == postId) {
                        post.copy(hasLiked = newHasLiked, cantLikes = maxOf(0, newLikeCount))
                    } else {
                        post
                    }
                }
                _uiState.value = _uiState.value.copy(posts = updatedPosts)

                android.util.Log.d("PostViewModel", "💡 UI updated optimistically: hasLiked=$newHasLiked, likes=$newLikeCount")

                // Obtener el token de autenticación
                val token = withContext(Dispatchers.IO) {
                    authDataStore.getToken().first()
                }

                if (token.isNullOrEmpty()) {
                    android.util.Log.e("PostViewModel", "❌ No authentication token available")
                    // Revertir el cambio optimista en ambos lugares
                    if (_postDetailState.value.post?.remoteId == postId) {
                        _postDetailState.value = _postDetailState.value.copy(post = currentPost)
                    }
                    val revertedPosts = _uiState.value.posts.map { post ->
                        if (post.remoteId == postId) currentPost else post
                    }
                    _uiState.value = _uiState.value.copy(posts = revertedPosts)
                    return@launch
                }

                android.util.Log.d("PostViewModel", "🔑 Token obtained, making API call...")

                // Llamar al endpoint correcto según el estado
                withContext(Dispatchers.IO) {
                    if (newHasLiked) {
                        likeService.likePost("Bearer $token", postId)
                    } else {
                        likeService.unlikePost("Bearer $token", postId)
                    }

                    // Persistir el estado en la base de datos local
                    val localPost = repository.getPosts().find { it.remoteId == postId }
                    if (localPost != null) {
                        val updatedLocalPost = localPost.copy(
                            hasLiked = newHasLiked,
                            cantLikes = maxOf(0, newLikeCount)
                        )
                        repository.updatePost(updatedLocalPost)
                        android.util.Log.d("PostViewModel", "💾 Post state persisted: hasLiked=$newHasLiked, likes=$newLikeCount")
                    }
                }

                android.util.Log.d("PostViewModel", "✅ Like request successful - Estado ya actualizado optimísticamente")

            } catch (e: Exception) {
                android.util.Log.e("PostViewModel", "❌ Error toggling like: ${e.message}", e)
                e.printStackTrace()

                // Revertir cambios optimistas en caso de error
                if (_postDetailState.value.post?.remoteId == postId) {
                    _postDetailState.value = _postDetailState.value.copy(post = currentPost)
                }
                val revertedPosts = _uiState.value.posts.map { post ->
                    if (post.remoteId == postId) currentPost else post
                }
                _uiState.value = _uiState.value.copy(posts = revertedPosts)
            }
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

    fun sendComment(postRemoteId: String, text: String){
        if(text.isBlank()) return

        viewModelScope.launch {
            _postDetailState.value = _postDetailState.value.copy(
                isSendingComment = true,
                commentError = null
            )

            try {
                // Enviar el comentario
                withContext(Dispatchers.IO){
                    apiRepository.addCommentToPost(
                        postRemoteId,
                        text
                    )
                }

                // Recargar el post completo con los comentarios actualizados
                val userId = withContext(Dispatchers.IO) {
                    try {
                        authRepository.getProfile().id
                    } catch (e: Exception) {
                        null
                    }
                }

                val updatedPost = withContext(Dispatchers.IO) {
                    apiRepository.getPostById(
                        id = postRemoteId,
                        currentUserId = userId
                    )
                }

                _postDetailState.value = _postDetailState.value.copy(
                    isSendingComment = false,
                    post = updatedPost,
                    comments = updatedPost.comentarios,
                    commentError = null
                )

                // Actualizar también el post en la lista del home
                withContext(Dispatchers.IO) {
                    repository.updatePost(updatedPost)
                }
                val updatedPosts = _uiState.value.posts.map { post ->
                    if (post.remoteId == postRemoteId) updatedPost else post
                }
                _uiState.value = _uiState.value.copy(posts = updatedPosts)

            }catch (e: Exception){
                _postDetailState.value = _postDetailState.value.copy(
                    isSendingComment = false,
                    commentError = e.message ?: "Error al enviar comentario"
                )
            }
        }
    }
}