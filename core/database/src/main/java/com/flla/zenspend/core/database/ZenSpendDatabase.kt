package com.flla.zenspend.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flla.zenspend.core.database.dao.AccountDao
import com.flla.zenspend.core.database.dao.BudgetDao
import com.flla.zenspend.core.database.dao.CategoryDao
import com.flla.zenspend.core.database.dao.TransactionDao
import com.flla.zenspend.core.database.dao.UserDao
import com.flla.zenspend.core.database.entity.AccountEntity
import com.flla.zenspend.core.database.entity.BudgetEntity
import com.flla.zenspend.core.database.entity.CategoryEntity
import com.flla.zenspend.core.database.entity.TransactionEntity
import com.flla.zenspend.core.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        TransactionEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class ZenSpendDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    abstract fun budgetDao(): BudgetDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    abstract fun userDao(): UserDao
}
