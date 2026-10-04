package com.example.data.repository

import com.example.data.local.BalanceDao
import com.example.data.local.BalanceOperationType
import com.example.data.local.BalanceTransactionEntity
import com.example.data.local.UserBalanceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull

/**
 * Repository for managing user BDT balance operations, bets, wins, and audits across game activities.
 */
class BalanceRepository(private val balanceDao: BalanceDao) {

    fun observeBalance(userId: String = UserBalanceEntity.PRIMARY_USER_ID): Flow<UserBalanceEntity> {
        return balanceDao.observeBalance(userId).filterNotNull()
    }

    fun observeRecentTransactions(
        userId: String = UserBalanceEntity.PRIMARY_USER_ID,
        limit: Int = 50
    ): Flow<List<BalanceTransactionEntity>> {
        return balanceDao.observeRecentTransactions(userId, limit)
    }

    fun observeGameTransactions(
        gameActivity: String,
        userId: String = UserBalanceEntity.PRIMARY_USER_ID
    ): Flow<List<BalanceTransactionEntity>> {
        return balanceDao.observeGameTransactions(gameActivity, userId)
    }

    suspend fun ensureInitialized(userId: String = UserBalanceEntity.PRIMARY_USER_ID): UserBalanceEntity {
        val existing = balanceDao.getBalanceDirect(userId)
        if (existing != null) return existing

        val initial = UserBalanceEntity(
            userId = userId,
            balanceBdt = 25000L,
            currencyCode = "BDT",
            currencySymbol = "৳",
            totalWonBdt = 0L,
            totalWageredBdt = 0L,
            vipPoints = 100,
            updatedAt = System.currentTimeMillis()
        )
        balanceDao.insertOrUpdateBalance(initial)
        balanceDao.insertTransaction(
            BalanceTransactionEntity(
                userId = userId,
                gameActivity = "Lobby",
                operationType = BalanceOperationType.DAILY_BONUS,
                amountBdt = 25000L,
                balanceBeforeBdt = 0L,
                balanceAfterBdt = 25000L,
                description = "Welcome Bonus (স্বাগতম উপহার ৳২৫,০০০)"
            )
        )
        return initial
    }

    /**
     * Atomically debits a bet for a specific game activity.
     */
    suspend fun placeBet(
        gameActivity: String,
        amountBdt: Long,
        note: String = "",
        userId: String = UserBalanceEntity.PRIMARY_USER_ID
    ): Result<BalanceTransactionEntity> {
        ensureInitialized(userId)
        return try {
            val tx = balanceDao.executeAtomicBalanceUpdate(
                userId = userId,
                gameActivity = gameActivity,
                type = BalanceOperationType.BET,
                deltaAmount = -amountBdt,
                description = note.ifEmpty { "Bet placed on $gameActivity" }
            )
            Result.success(tx)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atomically credits a win payout from a specific game activity.
     */
    suspend fun awardWin(
        gameActivity: String,
        winAmountBdt: Long,
        note: String = "",
        userId: String = UserBalanceEntity.PRIMARY_USER_ID
    ): Result<BalanceTransactionEntity> {
        ensureInitialized(userId)
        return try {
            val tx = balanceDao.executeAtomicBalanceUpdate(
                userId = userId,
                gameActivity = gameActivity,
                type = BalanceOperationType.WIN,
                deltaAmount = winAmountBdt,
                description = note.ifEmpty { "Win payout from $gameActivity" }
            )
            Result.success(tx)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atomically refunds a canceled bet.
     */
    suspend fun refundBet(
        gameActivity: String,
        amountBdt: Long,
        reason: String = "",
        userId: String = UserBalanceEntity.PRIMARY_USER_ID
    ): Result<BalanceTransactionEntity> {
        ensureInitialized(userId)
        return try {
            val tx = balanceDao.executeAtomicBalanceUpdate(
                userId = userId,
                gameActivity = gameActivity,
                type = BalanceOperationType.REFUND,
                deltaAmount = amountBdt,
                description = reason.ifEmpty { "Bet refund for $gameActivity" }
            )
            Result.success(tx)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atomically credits a daily bonus, simulated reload, or faucet grant.
     */
    suspend fun addCredits(
        source: String,
        amountBdt: Long,
        type: BalanceOperationType = BalanceOperationType.DAILY_BONUS,
        note: String = "",
        userId: String = UserBalanceEntity.PRIMARY_USER_ID
    ): Result<BalanceTransactionEntity> {
        ensureInitialized(userId)
        return try {
            val tx = balanceDao.executeAtomicBalanceUpdate(
                userId = userId,
                gameActivity = source,
                type = type,
                deltaAmount = amountBdt,
                description = note.ifEmpty { "Added ৳$amountBdt via $source" }
            )
            Result.success(tx)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
