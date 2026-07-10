package com.flla.zenspend.core.database.seed

import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.model.Budget
import com.flla.zenspend.core.model.BudgetPeriod
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.model.Transaction
import java.time.LocalDate
import java.time.ZoneId

object FinanceSeedData {
    val accounts =
        listOf(
            Account(
                id = "acc_bca_personal",
                name = "BCA Personal",
                balance = 12_400_000L,
                type = "Bank",
                iconType = "BCA",
                isVisible = true,
                isPrimary = true,
            ),
            Account(
                id = "acc_mandiri_tabungan",
                name = "Mandiri Tabungan",
                balance = 8_150_000L,
                type = "Bank",
                iconType = "Mandiri",
                isVisible = true,
                isPrimary = false,
            ),
            Account(
                id = "acc_gopay",
                name = "GoPay",
                balance = 2_200_000L,
                type = "Wallet",
                iconType = "GoPay",
                isVisible = true,
                isPrimary = false,
            ),
            Account(
                id = "acc_tunai",
                name = "Tunai",
                balance = 1_500_000L,
                type = "Cash",
                iconType = "Tunai",
                isVisible = true,
                isPrimary = false,
            ),
        )

    val categories =
        listOf(
            Category("cat_makan", "Makanan", "restaurant", "#E65100", isIncome = false, isPrimary = true),
            Category("cat_transport", "Transportasi", "directions_car", "#0D47A1", isIncome = false, isPrimary = true),
            Category("cat_belanja", "Belanja", "shopping_bag", "#4A148C", isIncome = false, isPrimary = true),
            Category("cat_tagihan", "Tagihan", "receipt_long", "#B71C1C", isIncome = false, isPrimary = true),
            Category("cat_kesehatan", "Kesehatan", "medical_services", "#1B5E20", isIncome = false, isPrimary = false),
            Category("cat_hiburan", "Hiburan", "movie", "#004D40", isIncome = false, isPrimary = false),
            Category("cat_pendidikan", "Pendidikan", "school", "#FF8F00", isIncome = false, isPrimary = false),
            Category("cat_lainnya_expense", "Lainnya", "more_horiz", "#37474F", isIncome = false, isPrimary = false),
            Category("cat_gaji", "Gaji", "account_balance_wallet", "#006064", isIncome = true, isPrimary = true),
            Category("cat_bonus", "Bonus", "military_tech", "#F57F17", isIncome = true, isPrimary = true),
            Category("cat_freelance", "Freelance", "laptop_mac", "#006064", isIncome = true, isPrimary = false),
            Category("cat_investasi", "Investasi", "trending_up", "#1A237E", isIncome = true, isPrimary = false),
            Category("cat_hadiah", "Hadiah", "card_giftcard", "#880E4F", isIncome = true, isPrimary = false),
            Category("cat_lainnya_income", "Lainnya", "add", "#37474F", isIncome = true, isPrimary = false),
        )

    val budgets =
        listOf(
            Budget("b_makan", "cat_makan", 3_000_000L, BudgetPeriod.MONTHLY),
            Budget("b_transport", "cat_transport", 2_000_000L, BudgetPeriod.MONTHLY),
            Budget("b_hiburan", "cat_hiburan", 1_500_000L, BudgetPeriod.MONTHLY),
            Budget("b_belanja", "cat_belanja", 2_000_000L, BudgetPeriod.MONTHLY),
        )

    val transactions: List<Transaction>
        get() {
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val zoneId = ZoneId.systemDefault()

            fun timeOf(
                date: LocalDate,
                hour: Int,
                minute: Int,
            ) = date.atTime(hour, minute).atZone(zoneId).toInstant().toEpochMilli()

            return listOf(
                Transaction(
                    id = "txn_1",
                    title = "Makan Siang Zen",
                    amount = 85_000L,
                    categoryId = "cat_makan",
                    categoryName = "Makanan",
                    accountId = "acc_bca_personal",
                    accountName = "BCA Personal",
                    isIncome = false,
                    timestamp = timeOf(today, 12, 30),
                    note = "Makan siang",
                ),
                Transaction(
                    id = "txn_2",
                    title = "Gaji Bulanan",
                    amount = 12_000_000L,
                    categoryId = "cat_gaji",
                    categoryName = "Gaji",
                    accountId = "acc_mandiri_tabungan",
                    accountName = "Mandiri Tabungan",
                    isIncome = true,
                    timestamp = timeOf(today, 9, 0),
                    note = "Gaji bulan ini",
                ),
                Transaction(
                    id = "txn_3",
                    title = "Bensin Pertamax",
                    amount = 250_000L,
                    categoryId = "cat_transport",
                    categoryName = "Transportasi",
                    accountId = "acc_tunai",
                    accountName = "Tunai",
                    isIncome = false,
                    timestamp = timeOf(yesterday, 18, 15),
                    note = "Isi bensin",
                ),
                Transaction(
                    id = "txn_4",
                    title = "Belanja Mingguan",
                    amount = 450_000L,
                    categoryId = "cat_belanja",
                    categoryName = "Belanja",
                    accountId = "acc_mandiri_tabungan",
                    accountName = "Mandiri Tabungan",
                    isIncome = false,
                    timestamp = timeOf(yesterday, 10, 45),
                    note = "Belanja mingguan",
                ),
                Transaction(
                    id = "txn_5",
                    title = "Tagihan Listrik",
                    amount = 1_200_000L,
                    categoryId = "cat_tagihan",
                    categoryName = "Tagihan",
                    accountId = "acc_bca_personal",
                    accountName = "BCA Personal",
                    isIncome = false,
                    timestamp = timeOf(yesterday, 8, 0),
                    note = "Listrik bulanan",
                ),
                Transaction(
                    id = "txn_6",
                    title = "Sewa Apartemen",
                    amount = 2_000_000L,
                    categoryId = "cat_tagihan",
                    categoryName = "Tagihan",
                    accountId = "acc_bca_personal",
                    accountName = "BCA Personal",
                    isIncome = false,
                    timestamp = timeOf(yesterday, 7, 0),
                    note = "Sewa apartemen",
                ),
                Transaction(
                    id = "txn_7",
                    title = "Langganan Netflix",
                    amount = 265_000L,
                    categoryId = "cat_hiburan",
                    categoryName = "Hiburan",
                    accountId = "acc_bca_personal",
                    accountName = "BCA Personal",
                    isIncome = false,
                    timestamp = timeOf(yesterday, 6, 0),
                    note = "Tagihan hiburan",
                ),
            )
        }
}
