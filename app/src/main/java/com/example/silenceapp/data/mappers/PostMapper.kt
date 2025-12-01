package com.example.silenceapp.data.mappers

import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.remote.response.PostDetailResponse
import com.example.silenceapp.data.remote.response.PostResponse
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
fun PostResponse.toLocalPost(): Post {
    val gson = Gson()

    // Convertir la lista de imágenes a JSON, filtrando nulls
    val imagesJson = when {
        imagen.isNullOrEmpty() -> null
        else -> {
            val cleanImages = imagen.filterNotNull().filter { it.isNotBlank() }
            if (cleanImages.isEmpty()) null else gson.toJson(cleanImages)
        }
    }

    return Post(
        remoteId = this.id,
        userId = this.owner?.id ?: "",
        userName = this.owner?.nombre ?: "Usuario",
        userImageProfile = this.owner?.imagen,
        description = this.description,
        images = imagesJson,
        cantLikes = this.cantLikes,
        cantComentarios = this.cantComentarios,
        esAnonimo = this.esAnonimo,
        createdAt = parseDate(this.createdAt)
    )
}

fun PostDetailResponse.toLocalPostDetail(): Post {
    val gson = Gson()

    // Convertir la lista de imágenes a JSON, filtrando nulls
    val imagesJson = when {
        imagen.isNullOrEmpty() -> null
        else -> {
            val cleanImages = imagen.filterNotNull().filter { it.isNotBlank() }
            if (cleanImages.isEmpty()) null else gson.toJson(cleanImages)
        }
    }

    return Post(
        remoteId = id,
        userId = owner?.id ?: "",
        userName = owner?.nombre ?: "Usuario",
        userImageProfile = owner?.imagen,
        description = description,
        images = imagesJson,
        cantLikes = cantLikes,
        cantComentarios = cantComentarios,
        //comentarios = comentarios,
        esAnonimo = esAnonimo,
        createdAt = parseDate(createdAt)
    )
}

// Función auxiliar para parsear fechas de la API
private fun parseDate(dateString: String): Long {
    return try {
        dateFormat.parse(dateString)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        e.printStackTrace()
        System.currentTimeMillis()
    }
}
