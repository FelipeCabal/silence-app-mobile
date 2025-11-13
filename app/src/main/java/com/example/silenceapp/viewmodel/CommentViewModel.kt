package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.repository.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentViewModel(application: Application): AndroidViewModel(application){

    private val commentDao = DatabaseProvider.getDatabase(application).commentDao()

    private val repository = CommentRepository(commentDao)


    fun getComments(postId: Int, onResult:(List<Comment>) -> Unit){
       viewModelScope.launch {
           val commentList = withContext(Dispatchers.IO){
               repository.getComments(postId)
           }
           onResult(commentList)
       }
    }
}
