package com.flla.zenspend.core.domain.repository

import com.flla.zenspend.core.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>

    suspend fun saveCategory(category: Category)
}
