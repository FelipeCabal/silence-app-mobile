package com.example.silenceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.remote.dto.FriendDto
import com.example.silenceapp.data.repository.GroupInvitationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupInvitationViewModel : ViewModel() {
    
    private val repository = GroupInvitationRepository(ApiClient.groupInvitationService)
    
    private val _friends = MutableStateFlow<List<FriendDto>>(emptyList())
    val friends: StateFlow<List<FriendDto>> = _friends.asStateFlow()
    
    private val _memberIds = MutableStateFlow<Set<String>>(emptySet())
    private val _pendingInvitationIds = MutableStateFlow<Set<String>>(emptySet())
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _invitationSent = MutableStateFlow<String?>(null)
    val invitationSent: StateFlow<String?> = _invitationSent.asStateFlow()
    
    fun isMember(userId: String): Boolean = _memberIds.value.contains(userId)
    fun hasPendingInvitation(userId: String): Boolean = _pendingInvitationIds.value.contains(userId)
    
    fun loadFriendsWithGroupData(token: String, groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val friendsResult = withContext(Dispatchers.IO) {
                repository.getFriends(token)
            }
            
            val membersResult = withContext(Dispatchers.IO) {
                repository.getGroupMembers(token, groupId)
            }
            
            val pendingResult = withContext(Dispatchers.IO) {
                repository.getPendingInvitations(token, groupId)
            }
            
            friendsResult.fold(
                onSuccess = { friendsList ->
                    _friends.value = friendsList
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
            
            membersResult.fold(
                onSuccess = { members ->
                    _memberIds.value = members.map { it.id }.toSet()
                },
                onFailure = { /* Ignorar si falla */ }
            )
            
            pendingResult.fold(
                onSuccess = { pending ->
                    _pendingInvitationIds.value = pending.map { it.id }.toSet()
                },
                onFailure = { /* Ignorar si falla */ }
            )
            
            _isLoading.value = false
        }
    }
    
    fun loadFriends(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.getFriends(token)
            }
            
            result.fold(
                onSuccess = { friendsList ->
                    _friends.value = friendsList
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun sendInvitation(token: String, groupId: String, receiverId: String, receiverName: String) {
        viewModelScope.launch {
            _error.value = null
            
            val result = withContext(Dispatchers.IO) {
                repository.sendGroupInvitation(token, groupId, receiverId)
            }
            
            result.fold(
                onSuccess = {
                    _invitationSent.value = receiverName
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
        }
    }
    
    fun clearInvitationSent() {
        _invitationSent.value = null
    }
    
    fun clearError() {
        _error.value = null
    }
}
