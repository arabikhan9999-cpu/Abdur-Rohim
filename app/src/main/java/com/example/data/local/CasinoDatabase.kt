package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserBalanceEntity::class,
        BalanceTransactionEntity::class,
        UserWalletEntity::class,
        TransactionEntity::class,
        GameStatsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CasinoDatabase : RoomDatabase() {
    abstract fun casinoDao(): CasinoDao
    abstract fun balanceDao(): BalanceDao

    companion object {
        @Volatile
        private var INSTANCE: CasinoDatabase? = null

        fun getInstance(context: Context): CasinoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CasinoDatabase::class.java,
                    "dhaka_royale_casino.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
