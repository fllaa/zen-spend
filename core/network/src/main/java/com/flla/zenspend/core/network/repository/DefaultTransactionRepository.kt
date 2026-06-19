package com.flla.zenspend.core.network.repository

import com.flla.zenspend.core.domain.repository.TransactionRepository
import com.flla.zenspend.core.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTransactionRepository
    @Inject
    constructor() : TransactionRepository {
        
        private val transactionsFlow: MutableStateFlow<List<Transaction>>

        init {
            val today = LocalDate.now()
            val zoneId = ZoneId.systemDefault()

            val today1230 = today.atTime(12, 30).atZone(zoneId).toInstant().toEpochMilli()
            val today0900 = today.atTime(9, 0).atZone(zoneId).toInstant().toEpochMilli()

            val yesterday = today.minusDays(1)
            val yesterday1815 = yesterday.atTime(18, 15).atZone(zoneId).toInstant().toEpochMilli()
            val yesterday1045 = yesterday.atTime(10, 45).atZone(zoneId).toInstant().toEpochMilli()
            val yesterday0800 = yesterday.atTime(8, 0).atZone(zoneId).toInstant().toEpochMilli()
            val yesterday0700 = yesterday.atTime(7, 0).atZone(zoneId).toInstant().toEpochMilli()
            val yesterday0600 = yesterday.atTime(6, 0).atZone(zoneId).toInstant().toEpochMilli()

            val initialTransactions =
                listOf(
                    Transaction(
                        id = "1",
                        title = "Makan Siang Zen",
                        amount = 85000L,
                        category = "Makanan",
                        account = "BCA",
                        isIncome = false,
                        timestamp = today1230,
                    ),
                    Transaction(
                        id = "2",
                        title = "Gaji Bulanan",
                        amount = 12000000L,
                        category = "Pendapatan",
                        account = "Mandiri",
                        isIncome = true,
                        timestamp = today0900,
                    ),
                    Transaction(
                        id = "3",
                        title = "Bensin Pertamax",
                        amount = 250000L,
                        category = "Transportasi",
                        account = "Tunai",
                        isIncome = false,
                        timestamp = yesterday1815,
                    ),
                    Transaction(
                        id = "4",
                        title = "Belanja Mingguan",
                        amount = 450000L,
                        category = "Kebutuhan",
                        account = "Mandiri",
                        isIncome = false,
                        timestamp = yesterday1045,
                    ),
                    Transaction(
                        id = "5",
                        title = "Tagihan Listrik",
                        amount = 1200000L,
                        category = "Utilitas",
                        account = "BCA",
                        isIncome = false,
                        timestamp = yesterday0800,
                    ),
                    Transaction(
                        id = "6",
                        title = "Sewa Apartemen",
                        amount = 2000000L,
                        category = "Utilitas",
                        account = "BCA",
                        isIncome = false,
                        timestamp = yesterday0700,
                    ),
                    Transaction(
                        id = "7",
                        title = "Langganan Netflix",
                        amount = 265000L,
                        category = "Kebutuhan",
                        account = "BCA",
                        isIncome = false,
                        timestamp = yesterday0600,
                    ),
                )
            transactionsFlow = MutableStateFlow(initialTransactions)
        }

        override fun observeTransactions(): Flow<List<Transaction>> = transactionsFlow

        override suspend fun saveTransaction(transaction: Transaction) {
            transactionsFlow.update { currentList ->
                listOf(transaction) + currentList
            }
        }
    }
