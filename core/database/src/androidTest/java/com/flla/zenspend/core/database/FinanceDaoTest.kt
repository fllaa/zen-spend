package com.flla.zenspend.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.flla.zenspend.core.database.entity.AccountEntity
import com.flla.zenspend.core.database.entity.BudgetEntity
import com.flla.zenspend.core.database.entity.CategoryEntity
import com.flla.zenspend.core.database.entity.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FinanceDaoTest {
    private lateinit var database: ZenSpendDatabase

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ZenSpendDatabase::class.java,
            )
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun accountDao_observeAccounts_returnsOrderedPrimaryAccounts() =
        runTest {
            database.accountDao().upsertAccounts(
                listOf(
                    AccountEntity("acc_2", "GoPay", 100L, "Wallet", "GoPay", true, false, 2L),
                    AccountEntity("acc_1", "BCA Personal", 200L, "Bank", "BCA", true, true, 1L),
                ),
            )

            val accounts = database.accountDao().observeAccounts().first()

            assertEquals(listOf("acc_1", "acc_2"), accounts.map { it.id })
        }

    @Test
    fun categoryDao_observeCategories_returnsPrimaryCategoriesFirst() =
        runTest {
            database.categoryDao().upsertCategories(
                listOf(
                    CategoryEntity("cat_2", "Transportasi", "directions_car", "#0D47A1", false, false, 2L),
                    CategoryEntity("cat_1", "Makanan", "restaurant", "#E65100", false, true, 1L),
                    CategoryEntity("cat_3", "Gaji", "account_balance_wallet", "#006064", true, true, 3L),
                ),
            )

            val categories = database.categoryDao().observeCategories().first()

            assertEquals(listOf("cat_1", "cat_3", "cat_2"), categories.map { it.id })
        }

    @Test
    fun budgetDao_deleteBudget_removesRequestedRow() =
        runTest {
            database.budgetDao().upsertBudgets(
                listOf(
                    BudgetEntity("budget_1", "cat_1", 200_000L, "MONTHLY", 1L),
                    BudgetEntity("budget_2", "cat_2", 300_000L, "WEEKLY", 2L),
                ),
            )

            database.budgetDao().deleteBudget("budget_1")

            val budgets = database.budgetDao().observeBudgets().first()
            assertEquals(listOf("budget_2"), budgets.map { it.id })
        }

    @Test
    fun transactionDao_observeTransactions_returnsNewestFirst() =
        runTest {
            database.transactionDao().upsertTransactions(
                listOf(
                    TransactionEntity(
                        id = "txn_older",
                        title = "Older",
                        amount = 10_000L,
                        categoryId = "cat_1",
                        categoryName = "Makanan",
                        accountId = "acc_1",
                        accountName = "BCA Personal",
                        isIncome = false,
                        timestamp = 1_000L,
                        note = null,
                        updatedAtMillis = 1L,
                    ),
                    TransactionEntity(
                        id = "txn_newer",
                        title = "Newer",
                        amount = 20_000L,
                        categoryId = "cat_1",
                        categoryName = "Makanan",
                        accountId = "acc_1",
                        accountName = "BCA Personal",
                        isIncome = false,
                        timestamp = 2_000L,
                        note = "latest",
                        updatedAtMillis = 2L,
                    ),
                ),
            )

            val transactions = database.transactionDao().observeTransactions().first()

            assertEquals(listOf("txn_newer", "txn_older"), transactions.map { it.id })
        }
}
