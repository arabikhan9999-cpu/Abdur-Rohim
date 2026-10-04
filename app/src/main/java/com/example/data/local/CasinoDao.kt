package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CasinoDao {
    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    fun getWallet(): Flow<UserWalletEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: UserWalletEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 50): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("SELECT * FROM game_stats")
    fun getAllGameStats(): Flow<List<GameStatsEntity>>

    @Query("SELECT * FROM game_stats WHERE gameKey = :gameKey LIMIT 1")
    suspend fun getGameStats(gameKey: String): GameStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: GameStatsEntity)
}
