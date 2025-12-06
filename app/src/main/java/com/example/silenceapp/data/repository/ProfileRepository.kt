package com.example.silenceapp.data.repository

import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.remote.dto.FriendRequestDto
import com.example.silenceapp.data.remote.dto.ReportUserRequest
import com.example.silenceapp.data.remote.response.PostResponse
import com.example.silenceapp.data.remote.response.ProfileResponse
import com.example.silenceapp.data.remote.response.RelationshipStatusResponse
import com.example.silenceapp.data.remote.service.AuthService
import com.example.silenceapp.data.remote.service.ProfileService
import kotlinx.coroutines.flow.first

class ProfileRepository(
    private val profileService: ProfileService,
    private val authService: AuthService,
    private val store: AuthDataStore
) {

    private suspend fun getAuthHeader(): String {
        val token = store.getToken().first()
        return "Bearer $token"
    }

    suspend fun getCurrentUserProfile(): ProfileResponse {
        return authService.profile(getAuthHeader())
    }

    suspend fun getUserProfile(id: String): ProfileResponse {
        return profileService.getUserProfile(getAuthHeader(), id)
    }

    suspend fun getUserPosts(id: String): List<PostResponse> {
        return profileService.getUserPosts(getAuthHeader(), id)
    }

    suspend fun getRelationshipStatus(id: String): RelationshipStatusResponse {
        return profileService.getRelationshipStatus(getAuthHeader(), id)
    }

    suspend fun sendFriendRequest(id: String): RelationshipStatusResponse {
        return profileService.sendFriendRequest(getAuthHeader(), FriendRequestDto(id))
    }

    suspend fun cancelFriendRequest(id: String): RelationshipStatusResponse {
        return profileService.cancelFriendRequest(getAuthHeader(), id)
    }

    suspend fun removeFriend(id: String): RelationshipStatusResponse {
        return profileService.removeFriend(getAuthHeader(), id)
    }

    suspend fun reportUser(id: String, reason: String? = null) {
        profileService.reportUser(getAuthHeader(), ReportUserRequest(id, reason))
    }
}