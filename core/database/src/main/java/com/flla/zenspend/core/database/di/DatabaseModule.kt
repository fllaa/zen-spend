package com.flla.zenspend.core.database.di

import android.content.Context
import androidx.room.Room
import com.flla.zenspend.core.common.AppConstants
import com.flla.zenspend.core.database.ZenSpendDatabase
import com.flla.zenspend.core.database.dao.AccountDao
import com.flla.zenspend.core.database.dao.BudgetDao
import com.flla.zenspend.core.database.dao.CategoryDao
import com.flla.zenspend.core.database.dao.TransactionDao
import com.flla.zenspend.core.database.dao.UserDao
import com.flla.zenspend.core.database.migration.DatabaseMigrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    @Suppress("SpreadOperator")
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): ZenSpendDatabase =
        Room.databaseBuilder(context, ZenSpendDatabase::class.java, AppConstants.DATABASE_NAME)
            .addMigrations(*DatabaseMigrations.ALL)
            .build()

    @Provides
    fun provideAccountDao(database: ZenSpendDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideBudgetDao(database: ZenSpendDatabase): BudgetDao = database.budgetDao()

    @Provides
    fun provideCategoryDao(database: ZenSpendDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTransactionDao(database: ZenSpendDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideUserDao(database: ZenSpendDatabase): UserDao = database.userDao()
}
