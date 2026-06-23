package com.flla.zenspend.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.flla.zenspend.core.database.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY categoryId ASC")
    fun observeBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets ORDER BY categoryId ASC")
    suspend fun getBudgets(): List<BudgetEntity>

    @Query("SELECT COUNT(*) FROM budgets")
    suspend fun countBudgets(): Int

    @Upsert
    suspend fun upsertBudget(budget: BudgetEntity)

    @Upsert
    suspend fun upsertBudgets(budgets: List<BudgetEntity>)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudget(id: String)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}
