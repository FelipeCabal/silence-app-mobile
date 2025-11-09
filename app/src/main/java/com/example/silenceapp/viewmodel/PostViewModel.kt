package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.silenceapp.data.local.AppDatabase
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel(application: Application): AndroidViewModel(application){

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "silence_db"
    ).build()

    private val repository = PostRepository(db.postDao())

    private val _post = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _post

    fun fetchPosts(){
        viewModelScope.launch{
            val postList = withContext(Dispatchers.IO){
                repository.getPosts()
            }
            _post.value = postList
        }
    }
}