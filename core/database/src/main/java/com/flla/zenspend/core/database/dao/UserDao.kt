package com.flla.zenspend.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.flla.zenspend.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun observeCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
