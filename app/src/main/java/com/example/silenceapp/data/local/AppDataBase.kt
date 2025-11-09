package com.example.silenceapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.silenceapp.data.local.converter.Converters
import com.example.silenceapp.data.local.dao.CommentDao
import com.example.silenceapp.data.local.dao.PostDao
import com.example.silenceapp.data.local.dao.UserDao
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.local.entity.UserEntity

@Database(entities = [UserEntity::class, Post::class, Comment::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao

    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "silence_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
