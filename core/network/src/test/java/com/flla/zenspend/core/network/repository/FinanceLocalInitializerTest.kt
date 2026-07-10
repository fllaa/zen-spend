package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.source.AccountLocalDataSource
import com.flla.zenspend.core.database.source.BudgetLocalDataSource
import com.flla.zenspend.core.database.source.CategoryLocalDataSource
import com.flla.zenspend.core.database.source.TransactionLocalDataSource
import com.flla.zenspend.core.domain.repository.UserPreferencesRepository
import com.flla.zenspend.core.model.UserPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FinanceLocalInitializerTest {
    private val accountLocalDataSource = mockk<AccountLocalDataSource>(relaxed = true)
    private val categoryLocalDataSource = mockk<CategoryLocalDataSource>(relaxed = true)
    private val budgetLocalDataSource = mockk<BudgetLocalDataSource>(relaxed = true)
    private val transactionLocalDataSource = mockk<TransactionLocalDataSource>(relaxed = true)
    private val preferencesFlow = MutableStateFlow(UserPreferences())
    private val userPreferencesRepository =
        mockk<UserPreferencesRepository> {
            every { preferences } returns preferencesFlow
        }

    private val initializer =
        FinanceLocalInitializer(
            accountLocalDataSource = accountLocalDataSource,
            categoryLocalDataSource = categoryLocalDataSource,
            budgetLocalDataSource = budgetLocalDataSource,
            transactionLocalDataSource = transactionLocalDataSource,
            userPreferencesRepository = userPreferencesRepository,
        )

    @Test
    fun ensureInitialized_seedsFreshInstallBeforeSetupCompletion() =
        runTest {
            coEvery { accountLocalDataSource.isEmpty() } returns true
            coEvery { categoryLocalDataSource.isEmpty() } returns true
            coEvery { budgetLocalDataSource.isEmpty() } returns true
            coEvery { transactionLocalDataSource.isEmpty() } returns true

            initializer.ensureInitialized()

            coVerify(exactly = 1) { accountLocalDataSource.upsertAccounts(any()) }
            coVerify(exactly = 1) { categoryLocalDataSource.upsertCategories(any()) }
            coVerify(exactly = 1) { budgetLocalDataSource.upsertBudgets(any()) }
            coVerify(exactly = 1) { transactionLocalDataSource.upsertTransactions(any()) }
        }

    @Test
    fun ensureInitialized_skipsSeedAfterSetupCompletion() =
        runTest {
            preferencesFlow.value = UserPreferences(hasCompletedSetup = true)
            coEvery { accountLocalDataSource.isEmpty() } returns true
            coEvery { categoryLocalDataSource.isEmpty() } returns true
            coEvery { budgetLocalDataSource.isEmpty() } returns true
            coEvery { transactionLocalDataSource.isEmpty() } returns true

            initializer.ensureInitialized()

            coVerify(exactly = 0) { accountLocalDataSource.upsertAccounts(any()) }
            coVerify(exactly = 0) { categoryLocalDataSource.upsertCategories(any()) }
            coVerify(exactly = 0) { budgetLocalDataSource.upsertBudgets(any()) }
            coVerify(exactly = 0) { transactionLocalDataSource.upsertTransactions(any()) }
        }

    @Test
    fun ensureInitialized_skipsSeedWhenLocalDataAlreadyExists() =
        runTest {
            coEvery { accountLocalDataSource.isEmpty() } returns false
            coEvery { categoryLocalDataSource.isEmpty() } returns true
            coEvery { budgetLocalDataSource.isEmpty() } returns true
            coEvery { transactionLocalDataSource.isEmpty() } returns true

            initializer.ensureInitialized()

            coVerify(exactly = 0) { accountLocalDataSource.upsertAccounts(any()) }
            coVerify(exactly = 0) { categoryLocalDataSource.upsertCategories(any()) }
            coVerify(exactly = 0) { budgetLocalDataSource.upsertBudgets(any()) }
            coVerify(exactly = 0) { transactionLocalDataSource.upsertTransactions(any()) }
        }
}
