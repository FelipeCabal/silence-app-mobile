package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.compose.runtime.tooling.parseSourceInformation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.silenceapp.data.local.AppDatabase
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.repository.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentViewModel(application: Application): AndroidViewModel(application){

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "silence_db"
    ).build()

    private val repository = CommentRepository(db.commentDao())

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments

    fun loadComments(postId: Int){
       viewModelScope.launch {
           val commentList = withContext(Dispatchers.IO){
               repository.getComments(postId)
           }
           _comments.value = commentList
       }
    }
}
