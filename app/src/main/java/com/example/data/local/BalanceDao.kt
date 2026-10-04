package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User BDT Balance and Game Activity Transactions.
 */
@Dao
interface BalanceDao {

    @Query("SELECT * FROM user_bdt_balance WHERE userId = :userId LIMIT 1")
    fun observeBalance(userId: String = UserBalanceEntity.PRIMARY_USER_ID): Flow<UserBalanceEntity?>

    @Query("SELECT * FROM user_bdt_balance WHERE userId = :userId LIMIT 1")
    suspend fun getBalanceDirect(userId: String = UserBalanceEntity.PRIMARY_USER_ID): UserBalanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBalance(balance: UserBalanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: BalanceTransactionEntity)

    @Query("SELECT * FROM balance_transactions WHERE user_id = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentTransactions(
        userId: String = UserBalanceEntity.PRIMARY_USER_ID,
        limit: Int = 50
    ): Flow<List<BalanceTransactionEntity>>

    @Query("SELECT * FROM balance_transactions WHERE user_id = :userId AND game_activity = :gameActivity ORDER BY timestamp DESC LIMIT :limit")
    fun observeGameTransactions(
        gameActivity: String,
        userId: String = UserBalanceEntity.PRIMARY_USER_ID,
        limit: Int = 30
    ): Flow<List<BalanceTransactionEntity>>

    @Query("DELETE FROM balance_transactions WHERE user_id = :userId")
    suspend fun clearTransactions(userId: String = UserBalanceEntity.PRIMARY_USER_ID)

    /**
     * Atomically executes a balance update and records the audit transaction in a single SQLite transaction.
     * Prevents partial updates or balance underflows.
     *
     * @param userId The ID of the user.
     * @param gameActivity The game activity name triggering this update (e.g., "Teen Patti", "Crash").
     * @param type Operation type (BET, WIN, REFUND, DAILY_BONUS, SIMULATED_RELOAD).
     * @param deltaAmount Positive for additions (wins/reloads), negative for deductions (bets).
     * @param description Human-readable description.
     */
    @Transaction
    suspend fun executeAtomicBalanceUpdate(
        userId: String = UserBalanceEntity.PRIMARY_USER_ID,
        gameActivity: String,
        type: BalanceOperationType,
        deltaAmount: Long,
        description: String = ""
    ): BalanceTransactionEntity {
        val current = getBalanceDirect(userId) ?: UserBalanceEntity(userId = userId)

        // For debits (negative delta), verify sufficient balance
        if (deltaAmount < 0 && current.balanceBdt < -deltaAmount) {
            throw IllegalArgumentException(
                "Insufficient BDT balance: Current=৳${current.balanceBdt}, Required=৳${-deltaAmount}"
            )
        }

        val balanceBefore = current.balanceBdt
        val balanceAfter = balanceBefore + deltaAmount

        val updatedWagered = if (deltaAmount < 0) {
            current.totalWageredBdt + (-deltaAmount)
        } else {
            current.totalWageredBdt
        }

        val updatedWon = if (deltaAmount > 0 && type == BalanceOperationType.WIN) {
            current.totalWonBdt + deltaAmount
        } else {
            current.totalWonBdt
        }

        val addedVipPoints = when {
            deltaAmount < 0 -> ((-deltaAmount) / 500).toInt().coerceAtLeast(1)
            deltaAmount > 0 && type == BalanceOperationType.WIN -> (deltaAmount / 250).toInt().coerceAtLeast(1)
            else -> 0
        }

        val updatedEntity = current.copy(
            balanceBdt = balanceAfter,
            totalWageredBdt = updatedWagered,
            totalWonBdt = updatedWon,
            vipPoints = current.vipPoints + addedVipPoints,
            updatedAt = System.currentTimeMillis()
        )

        insertOrUpdateBalance(updatedEntity)

        val transaction = BalanceTransactionEntity(
            userId = userId,
            gameActivity = gameActivity,
            operationType = type,
            amountBdt = deltaAmount,
            balanceBeforeBdt = balanceBefore,
            balanceAfterBdt = balanceAfter,
            timestamp = System.currentTimeMillis(),
            description = description.ifEmpty {
                if (deltaAmount >= 0) "$gameActivity $type +৳$deltaAmount" else "$gameActivity $type -৳${-deltaAmount}"
            }
        )

        insertTransaction(transaction)
        return transaction
    }
}
