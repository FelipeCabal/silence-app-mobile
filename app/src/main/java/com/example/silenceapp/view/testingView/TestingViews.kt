package com.example.silenceapp.view.testingView

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.silenceapp.data.local.entity.UserEntity
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.ui.components.CommentCard
import com.example.silenceapp.ui.components.PostCard

@Composable
fun TestingViews(){
    val user = listOf(
        UserEntity(1, "Sanji", "user@correo.com", "123","1234560", "", "","https://i.pinimg.com/736x/f3/af/98/f3af98f4fd136039a5775b53e76b272a.jpg"),
        UserEntity(2, "Luffy D. Monkey", "user@correo.com", "123","1234560","", "", "")
    )
    val posts = listOf(
        Post(1, user = user[1], description =  "¿Pero por qué somos tan pobres?"),
        Post(2, user = user[0], description = "Que no lo vea Roronoa", imagen = "https://i.ytimg.com/vi/aac9iYHwwpc/maxresdefault.jpg")
    )
    val comments = listOf(
        Comment(2, usuario = user[0], postId = 1, comentario = "Es por tu comidaaa!!")
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