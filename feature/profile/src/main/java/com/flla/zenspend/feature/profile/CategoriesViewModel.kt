package com.flla.zenspend.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flla.zenspend.core.domain.usecase.ObserveCategoriesUseCase
import com.flla.zenspend.core.domain.usecase.SaveCategoryUseCase
import com.flla.zenspend.core.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val selectedTabIsIncome: Boolean = false,
)

@HiltViewModel
class CategoriesViewModel
    @Inject
    constructor(
        observeCategoriesUseCase: ObserveCategoriesUseCase,
        private val saveCategoryUseCase: SaveCategoryUseCase,
    ) : ViewModel() {
        private val selectedTabIsIncomeFlow = MutableStateFlow(false)

        val uiState: StateFlow<CategoriesUiState> =
            combine(
                observeCategoriesUseCase(),
                selectedTabIsIncomeFlow,
            ) { categories, selectedTabIsIncome ->
                CategoriesUiState(
                    categories = categories,
                    selectedTabIsIncome = selectedTabIsIncome,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CategoriesUiState(),
            )

        fun selectTab(isIncome: Boolean) {
            selectedTabIsIncomeFlow.value = isIncome
        }

        fun saveCategory(
            name: String,
            iconName: String,
            colorHex: String,
            isIncome: Boolean,
            isPrimary: Boolean,
        ) {
            viewModelScope.launch {
                val newCategory =
                    Category(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        iconName = iconName,
                        colorHex = colorHex,
                        isIncome = isIncome,
                        isPrimary = isPrimary,
                    )
                saveCategoryUseCase(newCategory)
            }
        }
    }
