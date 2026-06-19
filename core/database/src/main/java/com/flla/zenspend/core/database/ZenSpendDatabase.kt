package com.flla.zenspend.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flla.zenspend.core.database.dao.UserDao
import com.flla.zenspend.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class ZenSpendDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
