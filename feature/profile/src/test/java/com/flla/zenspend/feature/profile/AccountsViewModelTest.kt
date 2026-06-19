package com.flla.zenspend.feature.profile

import com.flla.zenspend.core.domain.usecase.ObserveAccountsUseCase
import com.flla.zenspend.core.domain.usecase.SaveAccountUseCase
import com.flla.zenspend.core.domain.usecase.ToggleAccountVisibilityUseCase
import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeAccountRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val accountRepository = FakeAccountRepository()
    private val observeAccountsUseCase = ObserveAccountsUseCase(accountRepository)
    private val saveAccountUseCase = SaveAccountUseCase(accountRepository)
    private val toggleAccountVisibilityUseCase = ToggleAccountVisibilityUseCase(accountRepository)

    private lateinit var viewModel: AccountsViewModel

    @Before
    fun setUp() {
        val initialAccounts =
            listOf(
                Account("1", "BCA Personal", 1000L, "BCA Personal", "BCA", isVisible = true, isPrimary = true),
                Account("2", "GoPay", 500L, "GoPay", "GoPay", isVisible = true, isPrimary = false),
            )
        accountRepository.setAccounts(initialAccounts)
        viewModel =
            AccountsViewModel(
                observeAccountsUseCase = observeAccountsUseCase,
                saveAccountUseCase = saveAccountUseCase,
                toggleAccountVisibilityUseCase = toggleAccountVisibilityUseCase,
            )
    }

    @Test
    fun loadAccounts_loadsInitialData() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(2, state.accounts.size)
            assertEquals("BCA Personal", state.accounts[0].name)
            assertEquals("GoPay", state.accounts[1].name)
        }

    @Test
    fun toggleVisibility_updatesAccountVisibility() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            viewModel.toggleVisibility("1")
            runCurrent()

            val state = viewModel.uiState.value
            assertFalse(state.accounts.first { it.id == "1" }.isVisible)
        }

    @Test
    fun saveAccount_addsNewAccount() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            viewModel.saveAccount(
                name = "Mandiri Tabungan",
                type = "Mandiri Tabungan",
                provider = "Mandiri",
                initialBalance = 1500L,
                isPrimary = false,
            )
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(3, state.accounts.size)
            assertTrue(state.accounts.any { it.name == "Mandiri Tabungan" })
        }

    @Test
    fun savePrimaryAccount_resetsPreviousPrimaryAccount() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            // Save new account as primary
            viewModel.saveAccount(
                name = "Mandiri Tabungan",
                type = "Mandiri Tabungan",
                provider = "Mandiri",
                initialBalance = 1500L,
                isPrimary = true,
            )
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(3, state.accounts.size)

            // The newly saved Mandiri account should be primary
            val newPrimary = state.accounts.first { it.name == "Mandiri Tabungan" }
            assertTrue(newPrimary.isPrimary)

            // The older BCA account (id "1") should no longer be primary
            val oldPrimary = state.accounts.first { it.id == "1" }
            assertFalse(oldPrimary.isPrimary)
        }
}
