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

    // Cached data
    private var cachedUsers: List<User>? = null
    private var cachedCommunities: List<Community>? = null
    // Obtener todos los usuarios desde la API o cache
    suspend fun getUsers(): List<User> {
        if (cachedUsers != null) return cachedUsers!!
        val token = store.getToken().first()
        val response = api.getAllUsers("Bearer $token")
        cachedUsers = response
        return response
    }
    // Obtener todas las comunidades desde la API o cache
    suspend fun getCommunities(): List<Community> {
        if (cachedCommunities != null) return cachedCommunities!!
        val response = api.getAllCommunities()
        cachedCommunities = response.results
        return response.results
    }
    // Buscar usuarios usando los datos cargados
    suspend fun searchUsers(query: String): List<User> {
        val users = getUsers()
        if (query.isBlank()) return users
        return users.filter { it.nombre.contains(query, ignoreCase = true) }
    }
    // Buscar comunidades usando los datos cargados
    suspend fun searchCommunities(query: String): List<Community> {
        val communities = getCommunities()
        if (query.isBlank()) return communities
        return communities.filter { it.nombre.contains(query, ignoreCase = true) }
    }

    suspend fun sendFriendRequest(query: String): FriendRequestResponse {
        val token = store.getToken().first()
        val response = api.postFriendRequest(query, "Bearer $token")
        return response
    }

    suspend fun sendCommunityRequest(query: String): Community_Member_RequestResponse {
        val token = store.getToken().first()
        val response = api.postCommunityRequest(query,"Bearer $token")
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
