package com.example.data.repository

import com.example.data.local.CasinoDao
import com.example.data.local.GameStatsEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserWalletEntity
import com.example.data.local.BalanceDao
import com.example.data.local.BalanceOperationType
import com.example.data.local.BalanceTransactionEntity
import com.example.data.local.UserBalanceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.max

class CasinoRepository(
    private val dao: CasinoDao,
    private val balanceDao: BalanceDao? = null
) {

    val walletFlow: Flow<UserWalletEntity?> = dao.getWallet()
    val transactionsFlow: Flow<List<TransactionEntity>> = dao.getRecentTransactions(50)
    val gameStatsFlow: Flow<List<GameStatsEntity>> = dao.getAllGameStats()

    suspend fun ensureWalletInitialized(): UserWalletEntity {
        val existing = dao.getWallet().firstOrNull()
        if (existing != null) {
            return existing
        }
        val initial = UserWalletEntity(
            balance = 25000L, // Welcome bonus of ৳25,000
            totalWon = 0L,
            totalWagered = 0L,
            vipPoints = 100,
            level = 1,
            lastDailyClaim = 0L,
            language = "BN",
            soundEnabled = true
        )
        dao.insertOrUpdateWallet(initial)
        dao.insertTransaction(
            TransactionEntity(
                type = "REWARD",
                game = "Lobby",
                amount = 25000L,
                balanceAfter = 25000L,
                note = "Welcome Bonus (স্বাগতম বোনাস)"
            )
        )
        return initial
    }

    suspend fun placeBet(gameName: String, amount: Long): Boolean {
        val current = dao.getWallet().firstOrNull() ?: ensureWalletInitialized()
        if (current.balance < amount) return false

        val newBalance = current.balance - amount
        val newWagered = current.totalWagered + amount
        val addedVip = (amount / 500).toInt().coerceAtLeast(1)
        val newVip = current.vipPoints + addedVip
        val newLevel = calculateLevel(newVip)

        dao.insertOrUpdateWallet(
            current.copy(
                balance = newBalance,
                totalWagered = newWagered,
                vipPoints = newVip,
                level = newLevel
            )
        )
        dao.insertTransaction(
            TransactionEntity(
                type = "BET",
                game = gameName,
                amount = -amount,
                balanceAfter = newBalance,
                note = "Bet Placed (বাজি ধরা হয়েছে)"
            )
        )
        return true
    }

    suspend fun recordWin(gameKey: String, gameName: String, payout: Long, netProfit: Long) {
        val current = dao.getWallet().firstOrNull() ?: ensureWalletInitialized()
        val newBalance = current.balance + payout
        val newWon = current.totalWon + netProfit
        val addedVip = (netProfit / 200).toInt().coerceAtLeast(2)
        val newVip = current.vipPoints + addedVip

        dao.insertOrUpdateWallet(
            current.copy(
                balance = newBalance,
                totalWon = newWon,
                vipPoints = newVip,
                level = calculateLevel(newVip)
            )
        )
        dao.insertTransaction(
            TransactionEntity(
                type = "WIN",
                game = gameName,
                amount = payout,
                balanceAfter = newBalance,
                note = if (netProfit > 10000) "Big Win (বড় জয়)!" else "Win (বিজয়)"
            )
        )

        // Update stats
        val existingStats = dao.getGameStats(gameKey) ?: GameStatsEntity(gameKey)
        val updatedStats = existingStats.copy(
            roundsPlayed = existingStats.roundsPlayed + 1,
            roundsWon = existingStats.roundsWon + 1,
            biggestWin = max(existingStats.biggestWin, payout)
        )
        dao.insertOrUpdateStats(updatedStats)
    }

    suspend fun recordLoss(gameKey: String) {
        val existingStats = dao.getGameStats(gameKey) ?: GameStatsEntity(gameKey)
        val updatedStats = existingStats.copy(
            roundsPlayed = existingStats.roundsPlayed + 1
        )
        dao.insertOrUpdateStats(updatedStats)
    }

    suspend fun claimDailyBonus(): Pair<Boolean, Long> {
        val current = dao.getWallet().firstOrNull() ?: ensureWalletInitialized()
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        if (now - current.lastDailyClaim < oneDayMillis && current.lastDailyClaim != 0L) {
            return Pair(false, 0L)
        }

        // Daily bonus scales with VIP level!
        val bonus = 10000L + (current.level * 2500L)
        val newBalance = current.balance + bonus
        dao.insertOrUpdateWallet(
            current.copy(
                balance = newBalance,
                lastDailyClaim = now
            )
        )
        dao.insertTransaction(
            TransactionEntity(
                type = "DAILY_BONUS",
                game = "Daily Gift",
                amount = bonus,
                balanceAfter = newBalance,
                note = "Daily Reward Level ${current.level} (দৈনিক উপহার)"
            )
        )
        return Pair(true, bonus)
    }

    suspend fun addSimulatedReload(channel: String, amount: Long, bonus: Long = 0L) {
        val current = dao.getWallet().firstOrNull() ?: ensureWalletInitialized()
        val totalAdded = amount + bonus
        val newBalance = current.balance + totalAdded
        val newVip = current.vipPoints + (amount / 200).toInt()
        dao.insertOrUpdateWallet(
            current.copy(
                balance = newBalance,
                vipPoints = newVip,
                level = calculateLevel(newVip)
            )
        )
        dao.insertTransaction(
            TransactionEntity(
                type = "SIMULATED_DEPOSIT",
                game = channel,
                amount = totalAdded,
                balanceAfter = newBalance,
                note = "Virtual Top-up $channel (সিমুলেটেড রিচার্জ)"
            )
        )
    }

    suspend fun toggleLanguage() {
        val current = dao.getWallet().firstOrNull() ?: ensureWalletInitialized()
        val newLang = if (current.language == "BN") "EN" else "BN"
        dao.insertOrUpdateWallet(current.copy(language = newLang))
    }

    suspend fun toggleSound() {
        val current = dao.getWallet().firstOrNull() ?: ensureWalletInitialized()
        dao.insertOrUpdateWallet(current.copy(soundEnabled = !current.soundEnabled))
    }

    private fun calculateLevel(vipPoints: Int): Int {
        return when {
            vipPoints >= 10000 -> 5 // Royal Nawab
            vipPoints >= 5000 -> 4  // Platinum
            vipPoints >= 2000 -> 3  // Gold
            vipPoints >= 500 -> 2   // Silver
            else -> 1               // Bronze
        }
    }
}
