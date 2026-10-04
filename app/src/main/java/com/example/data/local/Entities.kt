package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_wallet")
data class UserWalletEntity(
    @PrimaryKey val id: Int = 1,
    val balance: Long = 25000L, // Initial gift: ৳25,000 BDT
    val totalWon: Long = 0L,
    val totalWagered: Long = 0L,
    val vipPoints: Int = 150,
    val level: Int = 1,
    val lastDailyClaim: Long = 0L,
    val language: String = "BN", // "BN" for Bangla, "EN" for English
    val soundEnabled: Boolean = true
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String, // "BET", "WIN", "DAILY_BONUS", "SIMULATED_DEPOSIT", "REWARD"
    val game: String, // "Teen Patti", "Crash", "Andar Bahar", "Slots", "Wheel", "Wallet"
    val amount: Long, // Positive for gain, negative for wager
    val balanceAfter: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey val gameKey: String, // "teen_patti", "crash", "andar_bahar", "slots", "wheel"
    val roundsPlayed: Int = 0,
    val roundsWon: Int = 0,
    val biggestWin: Long = 0L
)
