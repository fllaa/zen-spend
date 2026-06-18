package com.flla.example.core.database.di

import android.content.Context
import androidx.room.Room
import com.flla.example.core.common.AppConstants
import com.flla.example.core.database.ExampleDatabase
import com.flla.example.core.database.dao.UserDao
import com.flla.example.core.database.migration.DatabaseMigrations
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
    ): ExampleDatabase =
        Room.databaseBuilder(context, ExampleDatabase::class.java, AppConstants.DATABASE_NAME)
            .addMigrations(*DatabaseMigrations.ALL)
            .build()

    @Provides
    fun provideUserDao(database: ExampleDatabase): UserDao = database.userDao()
}
