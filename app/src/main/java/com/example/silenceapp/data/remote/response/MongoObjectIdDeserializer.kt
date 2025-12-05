package com.example.silenceapp.data.remote.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Deserializador para ObjectIds de MongoDB
 * Maneja tanto {"$oid": "..."} como strings directos
 */
class MongoObjectIdDeserializer : JsonDeserializer<MongoObjectId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MongoObjectId {
        return when {
            json == null || json.isJsonNull -> MongoObjectId("")
            // Si es string directo (backend hizo populate o transformación)
            json.isJsonPrimitive -> MongoObjectId(json.asString)
            // Si es objeto {"$oid": "..."}
            json.isJsonObject -> {
                val obj = json.asJsonObject
                if (obj.has("\$oid")) {
                    MongoObjectId(obj.get("\$oid").asString)
                } else {
                    MongoObjectId("")
                }
            }
            else -> MongoObjectId("")
        }
    }
}

/**
 * Deserializador para Dates de MongoDB
 * Maneja tanto {"$date": "..."} como strings directos
 */
class MongoDateDeserializer : JsonDeserializer<MongoDate> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MongoDate {
        return when {
            json == null || json.isJsonNull -> MongoDate("")
            // Si es string directo
            json.isJsonPrimitive -> MongoDate(json.asString)
            // Si es objeto {"$date": "..."}
            json.isJsonObject -> {
                val obj = json.asJsonObject
                if (obj.has("\$date")) {
                    MongoDate(obj.get("\$date").asString)
                } else {
                    MongoDate("")
                }
            }
            else -> MongoDate("")
        }
    }
}
