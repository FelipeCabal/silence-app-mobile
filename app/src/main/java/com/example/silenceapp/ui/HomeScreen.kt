package com.example.silenceapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.silenceapp.ui.components.PostItem

@Composable
fun HomeScreen(viewModel: FeedViewModel) {
    val posts = viewModel.postsFlow.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        when (posts.loadState.refresh) {
            is LoadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is LoadState.Error -> {
                val e = posts.loadState.refresh as LoadState.Error
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error: ${e.error.localizedMessage ?: "desconocido"}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { posts.retry() }) {
                        Text("Reintentar")
                    }
                }
            }
            is LoadState.NotLoading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    val itemCount = posts.itemCount
                    items(itemCount) { index ->
                        val post = posts[index]
                        post?.let { PostItem(post = it) }
                    }

                    // Estado de carga adicional
                    item {
                        when (posts.loadState.append) {
                            is LoadState.Loading -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is LoadState.Error -> {
                                val err = posts.loadState.append as LoadState.Error
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Error cargando más: ${err.error.localizedMessage}")
                                    Spacer(Modifier.height(6.dp))
                                    Button(onClick = { posts.retry() }) {
                                        Text("Reintentar")
                                    }
                                }
                            }
                            else -> { Spacer(Modifier.height(8.dp)) }
                        }
                    }
                }
            }
        }
    }
}
