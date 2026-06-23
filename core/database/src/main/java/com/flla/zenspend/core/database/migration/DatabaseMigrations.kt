package com.flla.zenspend.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 =
        object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT")
            }
        }

    val MIGRATION_2_3 =
        object : Migration(2, 3) {
            @Suppress("LongMethod")
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `accounts` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `balance` INTEGER NOT NULL,
                        `type` TEXT NOT NULL,
                        `iconType` TEXT NOT NULL,
                        `isVisible` INTEGER NOT NULL,
                        `isPrimary` INTEGER NOT NULL,
                        `updatedAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `categories` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `iconName` TEXT NOT NULL,
                        `colorHex` TEXT NOT NULL,
                        `isIncome` INTEGER NOT NULL,
                        `isPrimary` INTEGER NOT NULL,
                        `updatedAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `budgets` (
                        `id` TEXT NOT NULL,
                        `categoryId` TEXT NOT NULL,
                        `limitAmount` INTEGER NOT NULL,
                        `period` TEXT NOT NULL,
                        `updatedAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `transactions` (
                        `id` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `amount` INTEGER NOT NULL,
                        `categoryId` TEXT NOT NULL,
                        `categoryName` TEXT NOT NULL,
                        `accountId` TEXT NOT NULL,
                        `accountName` TEXT NOT NULL,
                        `isIncome` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `note` TEXT,
                        `updatedAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent(),
                )
            }
        }

    val ALL: Array<Migration> = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
}
