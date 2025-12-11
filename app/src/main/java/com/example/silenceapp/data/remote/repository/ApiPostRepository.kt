package com.example.silenceapp.data.remote.repository

import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.mappers.toLocalPost
import com.example.silenceapp.data.mappers.toLocalPostDetail
import com.example.silenceapp.data.remote.dto.CommentRequest
import com.example.silenceapp.data.remote.dto.PostRequest
import com.example.silenceapp.data.remote.response.ComentarioResponse
import com.example.silenceapp.data.remote.response.PostResponse
import com.example.silenceapp.data.remote.service.PostService
import com.example.silenceapp.data.remote.service.UserService
import kotlinx.coroutines.flow.first

class ApiPostRepository (
    private val api : PostService,
    private val store : AuthDataStore

) {

    suspend fun getAllPosts(currentUserId: String? = null): List<Post> {
        val response = api.getAllPosts()
        android.util.Log.d("ApiPostRepository", "📡 Raw API response: ${response.size} posts")
        response.forEach { postResponse ->
            android.util.Log.d(
                "ApiPostRepository",
                "   - Raw id: '${postResponse.id}', desc: ${postResponse.description?.take(20)}"
            )
        }
        return response
            .filter { it.id != null } // Filtrar posts sin ID
            .map { it.toLocalPost(currentUserId) }
    }

    suspend fun getPostById(id: String, currentUserId: String? = null): Post {
        val response = api.getPostById(id)
        return response.toLocalPostDetail(currentUserId)
    }

    suspend fun createPost(post: Post, currentUserId: String? = null): Post {
        val token = store.getToken().first()
        val postRequest = PostRequest(
            description = post.description,
            imagen = post.images,
            esAnonimo = post.esAnonimo
        )
        val response = api.createPost("Bearer $token", post = postRequest)
        return response.toLocalPostDetail(currentUserId)
    }

    suspend fun getPostDetailWithComments(
        id: String,
        currentUserId: String? = null
    ): Pair<Post, List<ComentarioResponse>> {
        val response = api.getPostById(id) // <- esto es PostDetailResponse
        val post = response.toLocalPostDetail(currentUserId) // ya lo usas
        return post to response.comentarios
    }

    suspend fun addCommentToPost(
        postId: String,
        comment: String,
    ): ComentarioResponse {
        val token = store.getToken().first()
        val commentRequest = CommentRequest(comentario = comment)

        return api.addComment(
            token = "Bearer $token",
            postId = postId,
            comment = commentRequest
        )
    }
}
