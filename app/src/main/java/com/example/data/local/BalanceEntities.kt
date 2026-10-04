package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity storing a user's BDT (Bangladeshi Taka) balance.
 * Tracks current balance, total statistics, VIP tier progress, and update timestamp.
 */
@Entity(tableName = "user_bdt_balance")
data class UserBalanceEntity(
    @PrimaryKey val userId: String = PRIMARY_USER_ID,
    @ColumnInfo(name = "balance_bdt") val balanceBdt: Long = 25000L, // Default welcome bonus: ৳25,000 BDT
    @ColumnInfo(name = "currency_code") val currencyCode: String = "BDT",
    @ColumnInfo(name = "currency_symbol") val currencySymbol: String = "৳",
    @ColumnInfo(name = "total_won_bdt") val totalWonBdt: Long = 0L,
    @ColumnInfo(name = "total_wagered_bdt") val totalWageredBdt: Long = 0L,
    @ColumnInfo(name = "vip_points") val vipPoints: Int = 100,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val PRIMARY_USER_ID = "primary_user"
    }
}

/**
 * Classification of balance transactions across games.
 */
enum class BalanceOperationType {
    BET,
    WIN,
    REFUND,
    DAILY_BONUS,
    SIMULATED_RELOAD,
    FAUCET
}

/**
 * Room Entity recording every balance modification event across different game activities.
 */
@Entity(tableName = "balance_transactions")
data class BalanceTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "user_id") val userId: String = UserBalanceEntity.PRIMARY_USER_ID,
    @ColumnInfo(name = "game_activity") val gameActivity: String, // e.g., "Crash", "Teen Patti", "Andar Bahar", "Slots", "Lucky Wheel", "Wallet"
    @ColumnInfo(name = "operation_type") val operationType: BalanceOperationType,
    @ColumnInfo(name = "amount_bdt") val amountBdt: Long, // Negative for debit, positive for credit
    @ColumnInfo(name = "balance_before_bdt") val balanceBeforeBdt: Long,
    @ColumnInfo(name = "balance_after_bdt") val balanceAfterBdt: Long,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "description") val description: String = ""
)
