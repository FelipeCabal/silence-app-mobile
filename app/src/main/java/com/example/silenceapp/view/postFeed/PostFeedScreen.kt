package com.example.silenceapp.view.postFeed

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.silenceapp.data.model.Post
import com.example.silenceapp.ui.components.PostCard

@Composable
fun FeedScreen(){
    val posts = listOf(
        Post(2, "Luffy D. Monkey", "¿Pero por qué somos tan pobres?"),
        Post(4, "Luffy D. Monkey", imagenUrl = "https://i.pinimg.com/736x/8c/d3/b6/8cd3b64201b71480f2414037ac10649c.jpg"),

        Post(1, "Queen Iva", "Soy la reina de las travestis", "https://i.pinimg.com/1200x/77/14/0e/77140e0dbad40edcc6883d6c381464a2.jpg"),
        Post(3, "Franky", "Suuupeeeeeeeerrrrrrrrr", "https://static.wikia.nocookie.net/featteca/images/d/d4/Franky_Post_Timeskip.png/revision/latest/scale-to-width-down/480?cb=20220810165303&path-prefix=es")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(posts){
            post ->
            PostCard(post)
        }
    }
}