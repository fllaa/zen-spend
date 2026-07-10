package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.database.source.CategoryLocalDataSource
import com.flla.zenspend.core.domain.repository.CategoryRepository
import com.flla.zenspend.core.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCategoryRepository
    @Inject
    constructor(
        private val localDataSource: CategoryLocalDataSource,
        private val financeLocalInitializer: FinanceLocalInitializer,
    ) : CategoryRepository {
        override fun observeCategories(): Flow<List<Category>> {
            return localDataSource.observeCategories().onStart {
                financeLocalInitializer.ensureInitialized()
            }
        }

        override suspend fun saveCategory(category: Category) {
            financeLocalInitializer.ensureInitialized()
            localDataSource.upsertCategory(category)
        }
    }
