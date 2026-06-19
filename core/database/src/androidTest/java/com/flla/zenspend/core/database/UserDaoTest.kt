package com.flla.zenspend.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.flla.zenspend.core.database.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserDaoTest {
    private lateinit var database: ZenSpendDatabase

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ZenSpendDatabase::class.java,
            )
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeCurrentUser_returnsUpsertedUser() =
        runTest {
            val user =
                UserEntity(
                    id = "user-1",
                    name = "Room User",
                    email = "room@example.com",
                    phone = null,
                    avatarUrl = null,
                    updatedAtMillis = 1L,
                )

            database.userDao().upsertUser(user)

            assertEquals(user, database.userDao().observeCurrentUser().first())
        }
}
