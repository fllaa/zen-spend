package com.flla.zenspend.core.testing.repository

import com.flla.zenspend.core.domain.repository.CategoryRepository
import com.flla.zenspend.core.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeCategoryRepository : CategoryRepository {
    private val categoriesFlow = MutableStateFlow<List<Category>>(emptyList())

    override fun observeCategories(): Flow<List<Category>> = categoriesFlow

    override suspend fun saveCategory(category: Category) {
        categoriesFlow.update { currentList ->
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

    fun setCategories(categories: List<Category>) {
        categoriesFlow.value = categories
    }
}
