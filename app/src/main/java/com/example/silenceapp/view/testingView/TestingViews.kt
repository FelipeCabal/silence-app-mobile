package com.example.silenceapp.view.testingView

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.ui.components.PostCard
import com.google.gson.Gson

@Composable
fun TestingViews(){
    val gson = Gson()
    
    // Posts de prueba con la nueva estructura
    val posts = listOf(
        Post(
            id = 1,
            userId = 1,
            userName = "Luffy D. Monkey",
            description = "¿Pero por qué somos tan pobres?",
            images = null,
            cantLikes = 5,
            cantComentarios = 2,
            esAnonimo = false,
            createdAt = System.currentTimeMillis() - 3600000 // Hace 1 hora
        ),
        Post(
            id = 2,
            userId = 2,
            userName = "Sanji",
            description = "Que no lo vea Roronoa",
            images = gson.toJson(listOf("https://i.ytimg.com/vi/aac9iYHwwpc/maxresdefault.jpg")),
            cantLikes = 12,
            cantComentarios = 4,
            esAnonimo = false,
            createdAt = System.currentTimeMillis() - 7200000 // Hace 2 horas
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(posts){ post ->
            PostCard(post)
        }
    }
}