package com.example.silenceapp.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.silenceapp.data.local.AppDatabase
import com.example.silenceapp.data.local.entity.Post
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PostDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var postDao: PostDao

    private val testPost1 = Post(
        remoteId = "post1",
        usuarioId = "user1",
        contenido = "Test post 1",
        imagen = listOf("image1.jpg"),
        numeroLikes = 10,
        numeroComentarios = 5,
        numeroPosts = 1,
        timestamp = System.currentTimeMillis(),
        hasLiked = false
    )

    private val testPost2 = Post(
        remoteId = "post2",
        usuarioId = "user2",
        contenido = "Test post 2",
        imagen = listOf("image2.jpg"),
        numeroLikes = 20,
        numeroComentarios = 10,
        numeroPosts = 2,
        timestamp = System.currentTimeMillis() + 1000,
        hasLiked = true
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        
        postDao = database.postDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertPost_andRetrieveById() = runTest {
        // When
        postDao.insertPost(testPost1)

        // Then
        val retrieved = postDao.getPostByRemoteId(testPost1.remoteId)
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.remoteId).isEqualTo(testPost1.remoteId)
        assertThat(retrieved?.contenido).isEqualTo(testPost1.contenido)
        assertThat(retrieved?.numeroLikes).isEqualTo(testPost1.numeroLikes)
    }

    @Test
    fun insertMultiplePosts_andRetrieveAll() = runTest {
        // When
        postDao.insertPosts(listOf(testPost1, testPost2))

        // Then
        postDao.getAllPosts().test {
            val posts = awaitItem()
            assertThat(posts).hasSize(2)
            assertThat(posts.map { it.remoteId }).containsExactly(testPost1.remoteId, testPost2.remoteId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getPostsByUserId_returnsOnlyUserPosts() = runTest {
        // Given
        val user1Posts = listOf(
            testPost1,
            testPost1.copy(remoteId = "post3", usuarioId = "user1")
        )
        val user2Posts = listOf(testPost2)

        postDao.insertPosts(user1Posts + user2Posts)

        // When
        postDao.getPostsByUserId("user1").test {
            val posts = awaitItem()
            
            // Then
            assertThat(posts).hasSize(2)
            assertThat(posts.all { it.usuarioId == "user1" }).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updatePost_changesValues() = runTest {
        // Given
        postDao.insertPost(testPost1)

        // When
        val updatedPost = testPost1.copy(
            numeroLikes = 50,
            numeroComentarios = 25,
            hasLiked = true
        )
        postDao.updatePost(updatedPost)

        // Then
        val retrieved = postDao.getPostByRemoteId(testPost1.remoteId)
        assertThat(retrieved?.numeroLikes).isEqualTo(50)
        assertThat(retrieved?.numeroComentarios).isEqualTo(25)
        assertThat(retrieved?.hasLiked).isTrue()
    }

    @Test
    fun deletePost_removesFromDatabase() = runTest {
        // Given
        postDao.insertPost(testPost1)

        // When
        postDao.deletePost(testPost1)

        // Then
        val retrieved = postDao.getPostByRemoteId(testPost1.remoteId)
        assertThat(retrieved).isNull()
    }

    @Test
    fun deleteAllPosts_clearsDatabase() = runTest {
        // Given
        postDao.insertPosts(listOf(testPost1, testPost2))

        // When
        postDao.deleteAllPosts()

        // Then
        postDao.getAllPosts().test {
            val posts = awaitItem()
            assertThat(posts).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertPost_withSameRemoteId_replacesExisting() = runTest {
        // Given
        postDao.insertPost(testPost1)

        // When - insert post with same remoteId but different content
        val updatedPost = testPost1.copy(contenido = "Updated content")
        postDao.insertPost(updatedPost)

        // Then
        postDao.getAllPosts().test {
            val posts = awaitItem()
            assertThat(posts).hasSize(1)
            assertThat(posts[0].contenido).isEqualTo("Updated content")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun flowUpdates_whenDataChanges() = runTest {
        // When - collect flow and make changes
        postDao.getAllPosts().test {
            // Initial state
            assertThat(awaitItem()).isEmpty()

            // Insert first post
            postDao.insertPost(testPost1)
            assertThat(awaitItem()).hasSize(1)

            // Insert second post
            postDao.insertPost(testPost2)
            assertThat(awaitItem()).hasSize(2)

            // Delete first post
            postDao.deletePost(testPost1)
            assertThat(awaitItem()).hasSize(1)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getPostsByUserId_reactsToInserts() = runTest {
        // When
        postDao.getPostsByUserId("user1").test {
            // Initial state
            assertThat(awaitItem()).isEmpty()

            // Insert post for user1
            postDao.insertPost(testPost1)
            assertThat(awaitItem()).hasSize(1)

            // Insert post for user2 - should not trigger update for user1 flow
            postDao.insertPost(testPost2)
            expectNoEvents()

            // Insert another post for user1
            val anotherPost = testPost1.copy(remoteId = "post3")
            postDao.insertPost(anotherPost)
            assertThat(awaitItem()).hasSize(2)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
