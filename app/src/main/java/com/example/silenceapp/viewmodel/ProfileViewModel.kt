package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.R
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.remote.response.LikeResponse
import com.example.silenceapp.data.remote.response.PostResponse
import com.example.silenceapp.data.remote.response.ProfileResponse
import com.example.silenceapp.data.repository.ProfileRepository
import kotlinx.coroutines.launch

enum class RelationshipStatus {
    NONE, PENDING, ACCEPTED;

    companion object {
        fun from(value: String?): RelationshipStatus {
            return when (value?.lowercase()) {
                "pending" -> PENDING
                "accepted", "friends" -> ACCEPTED
                else -> NONE
            }
        }
    }
}

data class ProfileUiState(
    val profile: ProfileResponse? = null,
    val posts: List<PostResponse> = emptyList(),
    val likedPosts: List<PostResponse> = emptyList(),
    val relationshipStatus: RelationshipStatus = RelationshipStatus.NONE,
    val isOwnProfile: Boolean = false,
    val isLoadingProfile: Boolean = false,
    val isLoadingPosts: Boolean = false,
    val isLoadingLikedPosts: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(
        ApiClient.profileService,
        ApiClient.authService,
        AuthDataStore(application)
    )

    private var currentUserId: String? = null
    private var activeUserId: String? = null

    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoadingProfile = true,
                isLoadingPosts = true,
                errorMessage = null
            )
            try {
                val ownId = ensureCurrentUserId()
                val targetUserId = resolveTargetUserId(userId, ownId)
                val isOwn = targetUserId == ownId

                val profileResponse = if (isOwn) {
                    repository.getCurrentUserProfile()
                } else {
                    repository.getUserProfile(targetUserId)
                }

                val relationship = if (isOwn) {
                    RelationshipStatus.ACCEPTED
                } else {
                    val status = profileResponse.relationshipStatus?.status
                        ?: repository.getRelationshipStatus(targetUserId).status
                    RelationshipStatus.from(status)
                }

                activeUserId = targetUserId
                val (postsWithOwner, postsError) = try {
                    resolveUserPosts(profileResponse, targetUserId) to null
                } catch (e: Exception) {
                    emptyList<PostResponse>() to (
                            e.message ?: getApplication<Application>().getString(R.string.error_loading_posts)
                            )
                }
                uiState = uiState.copy(
                    profile = profileResponse,
                    likedPosts = mapLikesToPosts(profileResponse.likes),
                    posts = postsWithOwner,
                    isOwnProfile = isOwn,
                    relationshipStatus = relationship,
                    isLoadingProfile = false,
                    isLoadingPosts = false,
                    errorMessage = postsError
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoadingProfile = false,
                    isLoadingPosts = false,
                    errorMessage =
                        e.message
                            ?: getApplication<Application>().getString(R.string.error_loading_profile)
                )
            }
        }
    }

    fun loadLikedPosts(userId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingLikedPosts = true, errorMessage = null)
            try {
                val ownId = ensureCurrentUserId()
                val targetUserId = resolveTargetUserId(userId, ownId)
                activeUserId = targetUserId

                // Recargar el perfil para obtener los likes actualizados
                val profileResponse = if (targetUserId == ownId) {
                    repository.getCurrentUserProfile()
                } else {
                    repository.getUserProfile(targetUserId)
                }

                val likedPosts = mapLikesToPosts(profileResponse.likes)
                uiState = uiState.copy(
                    likedPosts = likedPosts,
                    isLoadingLikedPosts = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoadingLikedPosts = false,
                    errorMessage =
                        e.message
                            ?: getApplication<Application>().getString(R.string.error_loading_liked_posts)
                )
            }
        }
    }

    fun sendFriendRequest() {
        val targetUserId = activeUserId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(errorMessage = null)
            try {
                val response = repository.sendFriendRequest(targetUserId)
                uiState =
                    uiState.copy(relationshipStatus = RelationshipStatus.from(response.status))
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }

    fun cancelFriendRequest() {
        val targetUserId = activeUserId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(errorMessage = null)
            try {
                val response = repository.cancelFriendRequest(targetUserId)
                uiState =
                    uiState.copy(relationshipStatus = RelationshipStatus.from(response.status))
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }

    fun removeFriend() {
        val targetUserId = activeUserId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(errorMessage = null)
            try {
                val response = repository.removeFriend(targetUserId)
                uiState =
                    uiState.copy(relationshipStatus = RelationshipStatus.from(response.status))
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }

    fun reportUser(reason: String? = null) {
        val targetUserId = activeUserId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(errorMessage = null)
            try {
                repository.reportUser(targetUserId, reason)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }

    fun shareProfile() {
        // Pendiente de integrar con la capa de compartición
    }

    private suspend fun ensureCurrentUserId(): String {
        if (currentUserId == null) {
            currentUserId = repository.getCurrentUserProfile().id
        }
        return currentUserId
            ?: throw IllegalStateException("Unable to fetch current user id")
    }

    private fun resolveTargetUserId(requestedUserId: String, ownId: String): String {
        return when (requestedUserId.lowercase()) {
            "self", "me" -> ownId
            else -> requestedUserId
        }
    }

    private fun mapLikesToPosts(likes: List<LikeResponse>): List<PostResponse> {
        return likes.map { like ->
            PostResponse(
                id = like.id,
                owner = null,
                description = like.description,
                imagen = like.imagen,
                cantLikes = like.cantLikes,
                cantComentarios = like.cantComentarios,
                esAnonimo = like.esAnonimo,
                createdAt = like.createdAt,
            )
        }
    }

    private suspend fun resolveUserPosts(
        profileResponse: ProfileResponse,
        targetUserId: String
    ): List<PostResponse> {
        val postsFromProfile = profileResponse.publicaciones
        return if (postsFromProfile.isNotEmpty()) {
            attachOwnerToPosts(postsFromProfile, profileResponse)
        } else {
            val remotePosts = repository.getUserPosts(targetUserId)
            attachOwnerToPosts(remotePosts, profileResponse)
        }
    }

    private fun attachOwnerToPosts(
        posts: List<PostResponse>,
        owner: ProfileResponse
    ): List<PostResponse> {
        return posts.map { post ->
            if (post.owner == null) post.copy(owner = owner) else post
        }
    }
}