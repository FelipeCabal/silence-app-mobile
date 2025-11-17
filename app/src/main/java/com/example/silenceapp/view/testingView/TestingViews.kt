package com.example.silenceapp.view.testingView

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.local.entity.UserEntity
import com.example.silenceapp.ui.components.CommentCard
import com.example.silenceapp.ui.components.PostCard
import java.sql.Timestamp
import java.util.Date

@Composable
fun TestingViews(){
    val user = listOf(
        UserEntity(id = 1, remoteId = "user1", nombre = "Sanji", email = "user@correo.com", sexo = "Male", fechaNto = "1990-03-02", pais = "Japan"),
        UserEntity(id = 2, remoteId = "user2", nombre = "Luffy D. Monkey", email = "user2@correo.com", sexo = "Male", fechaNto = "1994-05-05", pais = "Brazil")

    )
    val posts = listOf(
        Post(id = 1, user = user[1], description =  "¿Pero por qué somos tan pobres?", createdAt = Timestamp(Date().time)),
        Post(id = 2, user = user[0], description = "Que no lo vea Roronoa", imagen = "https://i.ytimg.com/vi/aac9iYHwwpc/maxresdefault.jpg", createdAt = Timestamp(Date().time))
    )
    val comments = listOf(
        Comment(id = 2, usuario = user[0], postId = 1, comentario = "Es por tu comidaaa!!", CreatedAt = Timestamp(Date().time))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(posts){
            post ->
            PostCard(post)
        }
        items(comments){
            comment ->
            CommentCard(comment)
        }
    }
}