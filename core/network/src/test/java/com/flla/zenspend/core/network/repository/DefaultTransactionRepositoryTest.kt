package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.source.AccountLocalDataSource
import com.flla.zenspend.core.database.source.CategoryLocalDataSource
import com.flla.zenspend.core.database.source.TransactionLocalDataSource
import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.model.Transaction
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DefaultTransactionRepositoryTest {
    private val accountLocalDataSource = mockk<AccountLocalDataSource>()
    private val categoryLocalDataSource = mockk<CategoryLocalDataSource>()
    private val transactionLocalDataSource = mockk<TransactionLocalDataSource>(relaxed = true)
    private val financeLocalInitializer = mockk<FinanceLocalInitializer>()
    private val repository =
        DefaultTransactionRepository(
            accountLocalDataSource = accountLocalDataSource,
            categoryLocalDataSource = categoryLocalDataSource,
            localDataSource = transactionLocalDataSource,
            financeLocalInitializer = financeLocalInitializer,
        )

    @Test
    fun saveTransaction_normalizesDisplayFieldsFromCanonicalLocalModels() =
        runTest {
            val savedTransaction = slot<Transaction>()
            val input =
                Transaction(
                    id = "txn-1",
                    title = "Lunch",
                    amount = 50_000L,
                    categoryId = "cat_makan",
                    categoryName = "Old Category",
                    accountId = "acc_bca",
                    accountName = "Old Account",
                    isIncome = true,
                    timestamp = 1234L,
                )
            coEvery { financeLocalInitializer.ensureInitialized() } just Runs
            coEvery { accountLocalDataSource.getAccounts() } returns
                listOf(Account("acc_bca", "BCA Personal", 1_000_000L, "Bank", "BCA", isVisible = true, isPrimary = true))
            coEvery { categoryLocalDataSource.getCategories() } returns
                listOf(Category("cat_makan", "Makanan", "restaurant", "#E65100", isIncome = false, isPrimary = true))
            coEvery { transactionLocalDataSource.upsertTransaction(any()) } just Runs

            repository.saveTransaction(input)

            coVerify { transactionLocalDataSource.upsertTransaction(capture(savedTransaction)) }
            assertEquals("BCA Personal", savedTransaction.captured.accountName)
            assertEquals("Makanan", savedTransaction.captured.categoryName)
            assertFalse(savedTransaction.captured.isIncome)
        }
}
