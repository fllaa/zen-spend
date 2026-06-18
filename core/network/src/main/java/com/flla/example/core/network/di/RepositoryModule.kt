package com.flla.example.core.network.di

import com.flla.example.core.domain.repository.AuthRepository
import com.flla.example.core.domain.repository.SessionRepository
import com.flla.example.core.domain.repository.UserPreferencesRepository
import com.flla.example.core.domain.repository.UserRepository
import com.flla.example.core.network.repository.DefaultAuthRepository
import com.flla.example.core.network.repository.DefaultSessionRepository
import com.flla.example.core.network.repository.DefaultUserPreferencesRepository
import com.flla.example.core.network.repository.OfflineFirstUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: DefaultAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(repository: DefaultSessionRepository): SessionRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(repository: OfflineFirstUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(repository: DefaultUserPreferencesRepository): UserPreferencesRepository
}
