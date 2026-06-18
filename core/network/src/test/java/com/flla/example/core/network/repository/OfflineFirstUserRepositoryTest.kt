package com.flla.example.core.network.repository

import com.flla.example.core.database.source.UserLocalDataSource
import com.flla.example.core.network.source.UserRemoteDataSource
import com.flla.example.core.testing.SampleData
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class OfflineFirstUserRepositoryTest {
    private val localDataSource = mockk<UserLocalDataSource>()
    private val remoteDataSource = mockk<UserRemoteDataSource>()
    private val repository = OfflineFirstUserRepository(localDataSource, remoteDataSource)

    @Test
    fun refreshCurrentUser_updatesLocalCacheFromNetwork() =
        runTest {
            coEvery { remoteDataSource.getCurrentUser() } returns SampleData.user
            coEvery { localDataSource.upsertCurrentUser(SampleData.user) } just Runs

            repository.refreshCurrentUser()

            coVerify { localDataSource.upsertCurrentUser(SampleData.user) }
        }
}
