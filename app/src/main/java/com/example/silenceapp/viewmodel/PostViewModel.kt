package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel(application: Application): AndroidViewModel(application){
    private val postDao = DatabaseProvider.getDatabase(application).postDao()

    private val repository = PostRepository(postDao)

    fun getPosts(onResult: (List<Post>) -> Unit){
        viewModelScope.launch {
            val postList = withContext(Dispatchers.IO) {
                repository.getPosts()
            }
            onResult(postList)
        }
    }
}