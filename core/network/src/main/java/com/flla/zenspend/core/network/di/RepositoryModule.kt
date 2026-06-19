package com.flla.zenspend.core.network.di

import com.flla.zenspend.core.domain.repository.AccountRepository
import com.flla.zenspend.core.domain.repository.AuthRepository
import com.flla.zenspend.core.domain.repository.CategoryRepository
import com.flla.zenspend.core.domain.repository.SessionRepository
import com.flla.zenspend.core.domain.repository.TransactionRepository
import com.flla.zenspend.core.domain.repository.UserPreferencesRepository
import com.flla.zenspend.core.domain.repository.UserRepository
import com.flla.zenspend.core.network.repository.DefaultAccountRepository
import com.flla.zenspend.core.network.repository.DefaultAuthRepository
import com.flla.zenspend.core.network.repository.DefaultCategoryRepository
import com.flla.zenspend.core.network.repository.DefaultSessionRepository
import com.flla.zenspend.core.network.repository.DefaultTransactionRepository
import com.flla.zenspend.core.network.repository.DefaultUserPreferencesRepository
import com.flla.zenspend.core.network.repository.OfflineFirstUserRepository
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

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(repository: DefaultTransactionRepository): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(repository: DefaultAccountRepository): AccountRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(repository: DefaultCategoryRepository): CategoryRepository
}
