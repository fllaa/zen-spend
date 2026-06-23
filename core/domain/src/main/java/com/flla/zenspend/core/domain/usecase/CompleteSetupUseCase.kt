package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.model.Account
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

data class CompleteSetupRequest(
    val currency: String,
    val accountName: String,
    val accountType: String,
    val provider: String,
    val initialBalance: Long,
    val selectedCategoryIds: Set<String>,
)

class CompleteSetupUseCase
    @Inject
    constructor(
        private val setCurrencyUseCase: SetCurrencyUseCase,
        private val saveAccountUseCase: SaveAccountUseCase,
        private val observeCategoriesUseCase: ObserveCategoriesUseCase,
        private val saveCategoryUseCase: SaveCategoryUseCase,
        private val setSetupCompletedUseCase: SetSetupCompletedUseCase,
    ) {
        suspend operator fun invoke(request: CompleteSetupRequest) {
            setCurrencyUseCase(request.currency)

            val account =
                Account(
                    id = UUID.randomUUID().toString(),
                    name = request.accountName,
                    balance = request.initialBalance,
                    type = request.accountType,
                    iconType = request.provider,
                    isVisible = true,
                    isPrimary = true,
                )
            saveAccountUseCase(account)

            val categories = observeCategoriesUseCase().first()
            categories.forEach { category ->
                saveCategoryUseCase(
                    category.copy(
                        isPrimary = category.id in request.selectedCategoryIds,
                    ),
                )
            }

            setSetupCompletedUseCase(true)
        }
    }
