package com.example.silenceapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.silenceapp.data.local.converter.Converters
import com.example.silenceapp.data.local.dao.ChatDao
import com.example.silenceapp.data.local.dao.CommentDao
import com.example.silenceapp.data.local.dao.MembersDao
import com.example.silenceapp.data.local.dao.MessageDao
import com.example.silenceapp.data.local.dao.NotificationDao
import com.example.silenceapp.data.local.dao.PostDao
import com.example.silenceapp.data.local.dao.UserDao
import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.local.entity.Members
import com.example.silenceapp.data.local.entity.Message
import com.example.silenceapp.data.local.entity.Notification
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        Post::class,
        Comment::class,
        Notification::class,
        Chat::class,
        Members::class,
        Message::class,
    ],
    version = 12
)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun chatDao(): ChatDao
    abstract fun membersDao(): MembersDao
    abstract fun messageDao(): MessageDao

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
