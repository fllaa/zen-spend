package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.CategoryRepository
import com.flla.zenspend.core.model.Category
import javax.inject.Inject

class SaveCategoryUseCase
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
    ) {
        suspend operator fun invoke(category: Category) {
            categoryRepository.saveCategory(category)
        }
    }
