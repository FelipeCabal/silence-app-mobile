package com.example.silenceapp.data.remote.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

// Deserializador que maneja tanto String como Array
class ImagenDeserializer : JsonDeserializer<List<String?>?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<String?>? {
        if (json == null || json.isJsonNull) {
            return null
        }

        return when {
            // Si es un array: ["img1.jpg", "img2.jpg"]
            json.isJsonArray -> {
                val list = mutableListOf<String?>()
                json.asJsonArray.forEach { element ->
                    list.add(if (element.isJsonNull) null else element.asString)
                }
                list
            }
            // Si es un string simple: "img1.jpg"
            json.isJsonPrimitive && json.asJsonPrimitive.isString -> {
                val str = json.asString
                if (str.isBlank()) {
                    emptyList()
                } else {
                    listOf(str)
                }
            }
            // Cualquier otro caso
            else -> emptyList()
        }
    }
}

data class PostResponse(
    @SerializedName("_id") val id: String,
    val owner: ProfileResponse?,
    val description: String,
    @JsonAdapter(ImagenDeserializer::class)
    val imagen: List<String?>?,
    val cantLikes: Int,
    val cantComentarios: Int,
    val esAnonimo: Boolean,
    val createdAt: String,
)

data class PostDetailResponse(
    val id: String,
    val owner: ProfileResponse?,
    val description: String,
    @JsonAdapter(ImagenDeserializer::class)
    val imagen: List<String?>?,
    val comentarios: List<ComentarioResponse>,
    val cantLikes: Int,
    val cantComentarios: Int,
    val esAnonimo: Boolean,
    val createdAt: String,
    val likes: List<String>,
    val updatedAt: String
)