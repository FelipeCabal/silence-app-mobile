package com.example.silenceapp.view.posts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.ui.components.CommentCard
import com.example.silenceapp.ui.theme.PaleMint
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.ui.theme.primaryColor
import com.example.silenceapp.viewmodel.PostViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun PostDetailScreen(
    postId: String,
    postViewModel: PostViewModel = viewModel()
) {
    val state by postViewModel.postDetailState.collectAsState()

    // Cargar detalle al entrar
    LaunchedEffect(postId) {
        postViewModel.loadPostDetail(postId)
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.error}")
            }
        }

        state.post != null -> {
            Scaffold(
                bottomBar = {
                    CommentInput(
                        comment = "",
                        onCommentChange = {},
                        onSendClick = {}
                    )
                }
            ) { inner ->
                // Contenido principal dentro de un LazyColumn para permitir scroll
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        PostDetailContent(post = state.post!!)
                    }
                }
            }
        }

        else -> {
            // Estado inicial sin nada
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No hay información del post")
            }
        }
    }
}

@Composable
fun PostDetailContent(post: Post) {
    val gson = Gson()

    // Parseo imágenes (post.images es JSON string o null)
    val images: List<String> = try {
        if (!post.images.isNullOrEmpty()) {
            gson.fromJson(post.images, Array<String>::class.java).toList()
        } else emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    // Parseo comentarios (post.comentarios es JSON string o null)
    val comentarios: List<Comment> = try {
        if (!post.comentarios.isNullOrEmpty()) {
            // Usamos TypeToken para evitar problemas si Comment tiene tipos complejos
            val listType = object : TypeToken<List<Comment>>() {}.type
            gson.fromJson<List<Comment>>(post.comentarios, listType)
        } else emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            text = "Publicación",
            style = MaterialTheme.typography.titleMedium
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PaleMint)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (post.userImageProfile.isNullOrEmpty()) {
                Text(
                    text = post.userName.firstOrNull()?.uppercase() ?: "U",
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineSmall
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(post.userImageProfile),
                    contentDescription = "",
                    modifier = Modifier
                        .height(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        // Header: autor / anónimo
        Text(
            text = if (post.esAnonimo) "Anónimo" else post.userName,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Contenido: imágenes o texto
        if (images.isNotEmpty()) {
            ImageCarousel(imagePaths = images)
            Text(
                text = post.description ?: "",
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            Text(
                text = post.description ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Métricas
        Text("${post.cantLikes} Likes • ${comentarios.size} Comentarios",
            style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(12.dp))

        // Sección comentarios (titulo + lista)
        Text("Comentarios", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (comentarios.isEmpty()) {
            Text("Sé el primero en comentar", style = MaterialTheme.typography.bodySmall)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(comentarios) { comment ->
                    CommentCard(comment = comment)
                }
            }
        }
    }
}

@Composable
fun ImageCarousel(imagePaths: List<String>, imageHeight: Dp = 500.dp) {
    val listState = rememberLazyListState()
    val firstVisible by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            itemsIndexed(imagePaths) { _, path ->
                // Coil puede cargar URLs y también paths de archivos locales si se usan con scheme "file://"
                val painter = rememberAsyncImagePainter(
                    model = if (path.startsWith("/")) "file://$path" else path

                )

                Image(
                    painter = painter,
                    contentDescription = "Imagen del post",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(end = 8.dp)
                )
            }
        }

        // Indicadores simples
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            imagePaths.forEachIndexed { index, _ ->
                val alpha = if (index == firstVisible) 1f else 0.4f
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .alpha(alpha)
                )
            }
        }
    }
}

@Composable
fun CommentInput(
    comment: String,
    onCommentChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                modifier = Modifier.weight(1f),
                placeholder = {Text(
                    "Escribe un comentario..."
                )},
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = onBackgroundColor,
                    unfocusedTextColor = Color.Gray.copy(alpha = 0.8f),
                ),
                singleLine = false
            )

            IconButton(
                onClick = onSendClick,
                enabled = comment.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if(comment.isNotBlank()) primaryColor
                        else Color.Gray.copy(alpha = 0.3f)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar comentario",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
}

