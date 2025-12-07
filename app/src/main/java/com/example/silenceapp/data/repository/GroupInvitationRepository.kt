package com.example.silenceapp.data.repository

import com.example.silenceapp.data.remote.dto.FriendDto
import com.example.silenceapp.data.remote.service.GroupInvitationService

class GroupInvitationRepository(
    private val service: GroupInvitationService
) {
    
    suspend fun getFriends(token: String): Result<List<FriendDto>> {
        return try {
            val response = service.getFriends("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener amigos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getGroupMembers(token: String, groupId: String): Result<List<FriendDto>> {
        return try {
            val response = service.getGroupMembers("Bearer $token", groupId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Error al obtener miembros: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPendingInvitations(token: String, groupId: String): Result<List<FriendDto>> {
        return try {
            val response = service.getPendingInvitations("Bearer $token", groupId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Error al obtener invitaciones pendientes: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendGroupInvitation(token: String, groupId: String, receiverId: String): Result<Unit> {
        return try {
            val response = service.sendGroupInvitation("Bearer $token", groupId, receiverId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al enviar invitación: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
