package com.example.silenceapp.data.mappers

import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.remote.response.ComentarioResponse

fun ComentarioResponse.toDomain(postId: Int) = Comment(
    remoteId =  id,
    comentario = comentario,
    usuarioRemoteId = usuario._id,
    nombreUsuario = usuario.nombre,
    imagenUsuario = usuario.imagen,
    postId = postId,
    createdAt = createdAt
)