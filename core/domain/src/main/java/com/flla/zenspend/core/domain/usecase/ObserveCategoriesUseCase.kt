package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.CategoryRepository
import com.flla.zenspend.core.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCategoriesUseCase
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
    ) {
        operator fun invoke(): Flow<List<Category>> {
            return categoryRepository.observeCategories()
        }
    }
