package com.flla.zenspend.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.flla.zenspend.core.database.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY isPrimary DESC, name ASC")
    fun observeAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY isPrimary DESC, name ASC")
    suspend fun getAccounts(): List<AccountEntity>

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun countAccounts(): Int

    @Upsert
    suspend fun upsertAccount(account: AccountEntity)

    @Upsert
    suspend fun upsertAccounts(accounts: List<AccountEntity>)

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()
}
