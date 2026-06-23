package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.seed.FinanceSeedData
import com.flla.zenspend.core.database.source.CategoryLocalDataSource
import com.flla.zenspend.core.domain.repository.CategoryRepository
import com.flla.zenspend.core.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCategoryRepository
    @Inject
    constructor(
        private val localDataSource: CategoryLocalDataSource,
    ) : CategoryRepository {
        init {
            runBlocking { seedIfEmpty() }
        }

        override fun observeCategories(): Flow<List<Category>> {
            runBlocking { seedIfEmpty() }
            return localDataSource.observeCategories()
        }

        override suspend fun saveCategory(category: Category) {
            localDataSource.upsertCategory(category)
        }

        private suspend fun seedIfEmpty() {
            if (localDataSource.isEmpty()) {
                localDataSource.upsertCategories(FinanceSeedData.categories)
            }
        }
    }
