package com.example.silenceapp.data.remote.mapper

import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.data.remote.dto.CommunityDto
import com.example.silenceapp.data.remote.dto.GroupDto
// import com.example.silenceapp.data.remote.dto.PrivateChatDto // PENDIENTE - API no disponible
import java.text.SimpleDateFormat
import java.util.*

object ChatMapper {

    // PENDIENTE - API no disponible aún
    // TODO: Descomentar cuando el endpoint /chat-privado esté disponible
    /*
    /**
     * Convierte un PrivateChatDto a Chat entity
     * Selecciona el nombre e imagen del otro participante (no el usuario actual)
     */
    fun fromPrivateChatDto(dto: PrivateChatDto, currentUserId: String): Chat {
        // Encontrar el otro participante (no el usuario actual)
        val otherParticipant = dto.participants.find { it.id != currentUserId }
            ?: dto.participants.firstOrNull()
        
        return Chat(
            id = dto.id,
            name = otherParticipant?.username ?: "Usuario desconocido",
            type = "private",
            image = otherParticipant?.profileImage ?: "",
            description = "Chat privado",
            lastMessageDate = parseDate(dto.lastMessageDate),
            lastMessage = dto.lastMessage ?: ""
        )
    }

    /**
     * Convierte una lista de PrivateChatDto a lista de Chat entities
     */
    fun fromPrivateChatDtoList(dtos: List<PrivateChatDto>, currentUserId: String): List<Chat> {
        return dtos.map { fromPrivateChatDto(it, currentUserId) }
    }
    */

    /**
     * Convierte un CommunityDto a Chat entity
     */
    fun fromCommunityDto(dto: CommunityDto): Chat {
        return Chat(
            id = dto.id,
            name = dto.nombre,
            type = "community",
            image = dto.imagen ?: "",
            description = "",
            lastMessageDate = parseDate(dto.lastMessageDate),
            lastMessage = dto.lastMessage ?: ""
        )
    }

    /**
     * Convierte una lista de CommunityDto a lista de Chat entities
     */
    fun fromCommunityDtoList(dtos: List<CommunityDto>): List<Chat> {
        return dtos.map { fromCommunityDto(it) }
    }

    /**
     * Convierte un GroupDto a Chat entity
     */
    fun fromGroupDto(dto: GroupDto): Chat {
        return Chat(
            id = dto.id,
            name = dto.nombre,
            type = "group",
            image = dto.imagen ?: "",
            description = "",
            lastMessageDate = parseDate(dto.lastMessageDate),
            lastMessage = dto.lastMessage ?: ""
        )
    }

    /**
     * Convierte una lista de GroupDto a lista de Chat entities
     */
    fun fromGroupDtoList(dtos: List<GroupDto>): List<Chat> {
        return dtos.map { fromGroupDto(it) }
    }

    /**
     * Convierte una fecha ISO string a timestamp en milisegundos
     */
    private fun parseDate(dateString: String): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            val date = format.parse(dateString)
            date?.time?.toString() ?: System.currentTimeMillis().toString()
        } catch (e: Exception) {
            System.currentTimeMillis().toString()
        }
    }
}
