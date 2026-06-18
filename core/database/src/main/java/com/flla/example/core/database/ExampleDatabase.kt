package com.flla.example.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flla.example.core.database.dao.UserDao
import com.flla.example.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class ExampleDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
