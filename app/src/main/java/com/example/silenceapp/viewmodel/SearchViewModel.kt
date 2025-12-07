package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.repository.SearchRepository
import com.example.silenceapp.data.remote.response.Community
import com.example.silenceapp.data.remote.response.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val store = AuthDataStore(application)
    private val api = ApiClient.searchService
    private val repository: SearchRepository = SearchRepository(store, api)

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities: StateFlow<List<Community>> = _communities

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _sentRequestsIds = MutableStateFlow<List<String>>(emptyList())
    val sentRequestsIds: StateFlow<List<String>> = _sentRequestsIds
    private val _sentRequestsCommunityIds = MutableStateFlow<List<String>>(emptyList())
    val sentRequestsCommunityIds: StateFlow<List<String>> = _sentRequestsCommunityIds

    // Método para limpiar datos cuando se hace logout
    fun clearData() {
        _users.value = emptyList()
        _communities.value = emptyList()
        _sentRequestsIds.value = emptyList()
        _sentRequestsCommunityIds.value = emptyList()
        _error.value = null
    }

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                loadSentRequests()

                // Luego usuarios y comunidades
                _users.value = repository.getUsers()
                _communities.value = repository.getCommunities()

            } catch (e: Exception) {
                _error.value = e.message ?: "Unexpected error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSentRequests() {
        viewModelScope.launch {
            try {
                val requests = repository.getSentFriendsRequests()
                _sentRequestsIds.value = requests.map { it.userRecibe._id }


                val requestsc = repository.getSentCommunitiesRequests()
                _sentRequestsCommunityIds.value = requestsc.data.map { it.id }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun search(query: String, tab: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                if (tab == 0) {
                    _users.value = repository.searchUsers(query)
                } else {
                    _communities.value = repository.searchCommunities(query)
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.sendFriendRequest(userId)
                _error.value = null

                _sentRequestsIds.value = _sentRequestsIds.value + userId

            } catch (e: Exception) {
                _error.value = "Error sending request: ${e.message}"
            }
        }
    }


    fun sendCommunityRequest(communityId: String) {
        viewModelScope.launch {
            try {
                val response = repository.sendCommunityRequest(communityId)
                _error.value = null

                _sentRequestsCommunityIds.value = _sentRequestsCommunityIds.value + communityId
            } catch (e: Exception) {
                _error.value = "Error sending request: ${e.message}"
            }
        }
    }


}
