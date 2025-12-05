package com.example.silenceapp.data.repository

import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.remote.response.Community
import com.example.silenceapp.data.remote.response.User
import com.example.silenceapp.data.remote.service.SearchService
import kotlinx.coroutines.flow.first
import com.example.silenceapp.data.remote.response.FriendRequestResponse
import com.example.silenceapp.data.remote.response.Community_Member_RequestResponse
import com.example.silenceapp.data.remote.response.FriendRequest
import com.example.silenceapp.data.remote.response.CommunityRequest
class SearchRepository (
    private val store: AuthDataStore,
    private val api: SearchService,
){

    // Obtener todos los usuarios desde la API
    suspend fun getUsers(): List<User> {
        val token = store.getToken().first()
        val response = api.getAllUsers("Bearer $token")
        return response
    }

    // Obtener todas las comunidades desde la API
    suspend fun getCommunities(): List<Community> {
        val response = api.getAllCommunities()
        return response.results
    }

    // Buscar usuarios usando los datos cargados
    suspend fun searchUsers(query: String): List<User> {
        if (query.isBlank()) return getUsers()
        return getUsers().filter { it.nombre.contains(query, ignoreCase = true) }
    }

    // Buscar comunidades usando los datos cargados
    suspend fun searchCommunities(query: String): List<Community> {
        if (query.isBlank()) return getCommunities()
        return getCommunities().filter { it.nombre.contains(query, ignoreCase = true) }
    }

    suspend fun sendFriendRequest(query: String): FriendRequestResponse {
        val token = store.getToken().first()
        val response = api.postFriend_Request(query, "Bearer $token")
        return response
    }

    suspend fun sendCommunityRequest(query: String): Community_Member_RequestResponse {
        val token = store.getToken().first()
        val response = api.postCommunity_Request(query,"Bearer $token")
        return response
    }

    suspend fun getSentFriendsRequests(): List<FriendRequest> {
        val token = store.getToken().first()
        val response =  api.getMyFriendRequests("Bearer $token")
        return response
    }

    suspend fun getSentCommunitiesRequests(): CommunityRequest {
        val token = store.getToken().first()
        val response =  api.getMyCommunitiesRequests("Bearer $token")
        return response
    }


}
