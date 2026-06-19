package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.domain.repository.CategoryRepository
import com.flla.zenspend.core.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCategoryRepository
    @Inject
    constructor() : CategoryRepository {
        private val categoriesFlow: MutableStateFlow<List<Category>>

        init {
            val initialCategories =
                listOf(
                    // Expenses
                    Category(
                        id = "cat_makan",
                        name = "Makan",
                        iconName = "restaurant",
                        colorHex = "#E65100",
                        isIncome = false,
                        isPrimary = true,
                    ),
                    Category(
                        id = "cat_transport",
                        name = "Transport",
                        iconName = "directions_car",
                        colorHex = "#0D47A1",
                        isIncome = false,
                        isPrimary = true,
                    ),
                    Category(
                        id = "cat_belanja",
                        name = "Belanja",
                        iconName = "shopping_bag",
                        colorHex = "#4A148C",
                        isIncome = false,
                        isPrimary = true,
                    ),
                    Category(
                        id = "cat_tagihan",
                        name = "Tagihan",
                        iconName = "receipt_long",
                        colorHex = "#B71C1C",
                        isIncome = false,
                        isPrimary = true,
                    ),
                    Category(
                        id = "cat_kesehatan",
                        name = "Kesehatan",
                        iconName = "medical_services",
                        colorHex = "#1B5E20",
                        isIncome = false,
                        isPrimary = false,
                    ),
                    Category(
                        id = "cat_hiburan",
                        name = "Hiburan",
                        iconName = "movie",
                        colorHex = "#004D40",
                        isIncome = false,
                        isPrimary = false,
                    ),
                    Category(
                        id = "cat_edukasi",
                        name = "Edukasi",
                        iconName = "school",
                        colorHex = "#FF8F00",
                        isIncome = false,
                        isPrimary = false,
                    ),
                    Category(
                        id = "cat_lainnya_expense",
                        name = "Lainnya",
                        iconName = "more_horiz",
                        colorHex = "#37474F",
                        isIncome = false,
                        isPrimary = false,
                    ),
                    // Income
                    Category(
                        id = "cat_gaji",
                        name = "Gaji",
                        iconName = "account_balance_wallet",
                        colorHex = "#006064",
                        isIncome = true,
                        isPrimary = true,
                    ),
                    Category(
                        id = "cat_bonus",
                        name = "Bonus",
                        iconName = "military_tech",
                        colorHex = "#F57F17",
                        isIncome = true,
                        isPrimary = true,
                    ),
                    Category(
                        id = "cat_freelance",
                        name = "Freelance",
                        iconName = "laptop_mac",
                        colorHex = "#006064",
                        isIncome = true,
                        isPrimary = false,
                    ),
                    Category(
                        id = "cat_investasi",
                        name = "Investasi",
                        iconName = "trending_up",
                        colorHex = "#1A237E",
                        isIncome = true,
                        isPrimary = false,
                    ),
                    Category(
                        id = "cat_hadiah",
                        name = "Hadiah",
                        iconName = "card_giftcard",
                        colorHex = "#880E4F",
                        isIncome = true,
                        isPrimary = false,
                    ),
                    Category(
                        id = "cat_lainnya_income",
                        name = "Lainnya",
                        iconName = "add",
                        colorHex = "#37474F",
                        isIncome = true,
                        isPrimary = false,
                    ),
                )
            categoriesFlow = MutableStateFlow(initialCategories)
        }

        override fun observeCategories(): Flow<List<Category>> = categoriesFlow

        override suspend fun saveCategory(category: Category) {
            categoriesFlow.update { currentList ->
                // Check if category already exists, replace it, otherwise append.
                val existingIndex =
                    currentList.indexOfFirst {
                        it.id == category.id ||
                            (
                                it.name.equals(category.name, ignoreCase = true) &&
                                    it.isIncome == category.isIncome
                            )
                    }
                if (existingIndex >= 0) {
                    currentList.toMutableList().apply {
                        set(existingIndex, category)
                    }
                } else {
                    currentList + category
                }
            }
        }
    }
