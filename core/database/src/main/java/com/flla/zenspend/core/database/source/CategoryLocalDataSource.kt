package com.flla.zenspend.core.database.source

import com.flla.zenspend.core.database.dao.CategoryDao
import com.flla.zenspend.core.database.entity.asEntity
import com.flla.zenspend.core.database.entity.asExternalModel
import com.flla.zenspend.core.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryLocalDataSource
    @Inject
    constructor(
        private val categoryDao: CategoryDao,
    ) {
        fun observeCategories(): Flow<List<Category>> =
            categoryDao.observeCategories().map { list ->
                list.map { it.asExternalModel() }
            }

        suspend fun getCategories(): List<Category> = categoryDao.getCategories().map { it.asExternalModel() }

        suspend fun isEmpty(): Boolean = categoryDao.countCategories() == 0

        suspend fun upsertCategory(category: Category) {
            categoryDao.upsertCategory(category.asEntity())
        }

        suspend fun upsertCategories(categories: List<Category>) {
            categoryDao.upsertCategories(categories.map { it.asEntity() })
        }

        suspend fun clear() {
            categoryDao.deleteAll()
        }
    }
