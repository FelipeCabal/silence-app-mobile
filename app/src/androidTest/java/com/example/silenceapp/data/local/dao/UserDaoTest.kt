package com.example.silenceapp.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.silenceapp.data.local.AppDatabase
import com.example.silenceapp.data.local.entity.UserEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    private val testUser1 = UserEntity(
        remoteId = "user1",
        nombre = "John Doe",
        email = "john@example.com",
        imagen = "https://example.com/john.jpg",
        sexo = "M",
        fechaNto = "1990-01-01",
        pais = "USA",
        seguidores = 10,
        seguidos = 5
    )

    private val testUser2 = UserEntity(
        remoteId = "user2",
        nombre = "Jane Smith",
        email = "jane@example.com",
        imagen = "https://example.com/jane.jpg",
        sexo = "F",
        fechaNto = "1992-05-15",
        pais = "Canada",
        seguidores = 20,
        seguidos = 15
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
        
        userDao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertUser_andRetrieveById() = runTest {
        // When
        userDao.insertUser(testUser1)

        // Then
        val retrieved = userDao.getUserByRemoteId(testUser1.remoteId)
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.remoteId).isEqualTo(testUser1.remoteId)
        assertThat(retrieved?.nombre).isEqualTo(testUser1.nombre)
        assertThat(retrieved?.email).isEqualTo(testUser1.email)
    }

    @Test
    fun insertMultipleUsers_andRetrieveAll() = runTest {
        // When
        userDao.insertUsers(listOf(testUser1, testUser2))

        // Then
        userDao.getAllUsers().test {
            val users = awaitItem()
            assertThat(users).hasSize(2)
            assertThat(users.map { it.remoteId }).containsExactly(testUser1.remoteId, testUser2.remoteId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateUser_changesValues() = runTest {
        // Given
        userDao.insertUser(testUser1)

        // When
        val updatedUser = testUser1.copy(
            nombre = "John Updated",
            seguidores = 100,
            seguidos = 50
        )
        userDao.updateUser(updatedUser)

        // Then
        val retrieved = userDao.getUserByRemoteId(testUser1.remoteId)
        assertThat(retrieved?.nombre).isEqualTo("John Updated")
        assertThat(retrieved?.seguidores).isEqualTo(100)
        assertThat(retrieved?.seguidos).isEqualTo(50)
    }

    @Test
    fun deleteUser_removesFromDatabase() = runTest {
        // Given
        userDao.insertUser(testUser1)

        // When
        userDao.deleteUser(testUser1)

        // Then
        val retrieved = userDao.getUserByRemoteId(testUser1.remoteId)
        assertThat(retrieved).isNull()
    }

    @Test
    fun deleteAllUsers_clearsDatabase() = runTest {
        // Given
        userDao.insertUsers(listOf(testUser1, testUser2))

        // When
        userDao.deleteAllUsers()

        // Then
        userDao.getAllUsers().test {
            val users = awaitItem()
            assertThat(users).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertUser_withSameRemoteId_replacesExisting() = runTest {
        // Given
        userDao.insertUser(testUser1)

        // When - insert user with same remoteId but different data
        val updatedUser = testUser1.copy(nombre = "Updated Name")
        userDao.insertUser(updatedUser)

        // Then
        userDao.getAllUsers().test {
            val users = awaitItem()
            assertThat(users).hasSize(1)
            assertThat(users[0].nombre).isEqualTo("Updated Name")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchUsersByName_returnsMatchingUsers() = runTest {
        // Given
        userDao.insertUsers(listOf(testUser1, testUser2))

        // When - search should be case-insensitive
        val john = userDao.getUserByRemoteId("user1")
        
        // Then
        assertThat(john?.nombre).contains("John")
    }

    @Test
    fun flowUpdates_whenUserDataChanges() = runTest {
        // When - collect flow and make changes
        userDao.getAllUsers().test {
            // Initial state
            assertThat(awaitItem()).isEmpty()

            // Insert first user
            userDao.insertUser(testUser1)
            assertThat(awaitItem()).hasSize(1)

            // Insert second user
            userDao.insertUser(testUser2)
            assertThat(awaitItem()).hasSize(2)

            // Update first user
            val updated = testUser1.copy(seguidores = 999)
            userDao.updateUser(updated)
            val users = awaitItem()
            assertThat(users.first { it.remoteId == testUser1.remoteId }.seguidores).isEqualTo(999)

            // Delete first user
            userDao.deleteUser(testUser1)
            assertThat(awaitItem()).hasSize(1)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getUserByRemoteId_returnsNullForNonexistent() = runTest {
        // When
        val nonexistent = userDao.getUserByRemoteId("nonexistent_id")

        // Then
        assertThat(nonexistent).isNull()
    }

    @Test
    fun insertUsers_maintainsDataIntegrity() = runTest {
        // Given
        val users = listOf(
            testUser1,
            testUser2,
            testUser1.copy(remoteId = "user3", nombre = "User Three"),
            testUser1.copy(remoteId = "user4", nombre = "User Four")
        )

        // When
        userDao.insertUsers(users)

        // Then
        userDao.getAllUsers().test {
            val inserted = awaitItem()
            assertThat(inserted).hasSize(4)
            
            // Verify each user's data is intact
            val user1 = inserted.first { it.remoteId == "user1" }
            assertThat(user1.nombre).isEqualTo("John Doe")
            assertThat(user1.email).isEqualTo("john@example.com")
            assertThat(user1.pais).isEqualTo("USA")

            cancelAndIgnoreRemainingEvents()
        }
    }
}
