package com.flla.zenspend.feature.profile

import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.SaveCategoryUseCase
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeCategoryRepository
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
class CategoriesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val categoryRepository = FakeCategoryRepository()
    private val observeCategoriesUseCase = ObserveCategoriesUseCase(categoryRepository)
    private val saveCategoryUseCase = SaveCategoryUseCase(categoryRepository)

    private lateinit var viewModel: CategoriesViewModel

    @Before
    fun setUp() {
        val initialCategories =
            listOf(
                Category("1", "Makan", "restaurant", "#E65100", isIncome = false, isPrimary = true),
                Category("2", "Gaji", "account_balance_wallet", "#006064", isIncome = true, isPrimary = true),
            )
        categoryRepository.setCategories(initialCategories)
        viewModel =
            CategoriesViewModel(
                observeCategoriesUseCase = observeCategoriesUseCase,
                saveCategoryUseCase = saveCategoryUseCase,
            )
    }

    @Test
    fun loadCategories_loadsInitialData() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(2, state.categories.size)
            assertFalse(state.selectedTabIsIncome)
        }

    @Test
    fun selectTab_updatesSelectedTab() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            viewModel.selectTab(true)
            runCurrent()

            val state = viewModel.uiState.value
            assertTrue(state.selectedTabIsIncome)
        }

    @Test
    fun saveCategory_addsNewCategory() =
        runTest {
            backgroundScope.launch { viewModel.uiState.collect {} }
            runCurrent()

            viewModel.saveCategory(
                name = "Bonus",
                iconName = "military_tech",
                colorHex = "#F57F17",
                isIncome = true,
                isPrimary = false,
            )
            runCurrent()

            val state = viewModel.uiState.value
            assertEquals(3, state.categories.size)
            assertTrue(state.categories.any { it.name == "Bonus" && it.isIncome })
        }
}
