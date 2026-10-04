package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BalanceOperationType
import com.example.data.local.BalanceTransactionEntity
import com.example.data.local.CasinoDatabase
import com.example.data.local.UserBalanceEntity
import com.example.data.repository.BalanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * UI State representation of the user's BDT balance.
 */
data class BalanceUiState(
    val balanceBdt: Long = 25000L,
    val currencyCode: String = "BDT",
    val currencySymbol: String = "৳",
    val formattedBalance: String = "25,000",
    val displayWithCurrency: String = "৳25,000",
    val totalWonBdt: Long = 0L,
    val totalWageredBdt: Long = 0L,
    val vipPoints: Int = 100,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel to manage a user's BDT balance and synchronize balance updates across different game activities.
 * (e.g., Teen Patti, Crash/Aviator, Andar Bahar, Slots, Lucky Wheel, Wallet).
 */
class GameBalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BalanceRepository

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _lastTransaction = MutableStateFlow<BalanceTransactionEntity?>(null)
    val lastTransaction: StateFlow<BalanceTransactionEntity?> = _lastTransaction.asStateFlow()

    init {
        val database = CasinoDatabase.getInstance(application)
        repository = BalanceRepository(database.balanceDao())

        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    /**
     * Reactive StateFlow of the user's current BDT balance, formatted numbers, and stats.
     */
    val balanceUiState: StateFlow<BalanceUiState> = repository.observeBalance()
        .map { entity ->
            val formatted = NumberFormat.getNumberInstance(Locale.US).format(entity.balanceBdt)
            BalanceUiState(
                balanceBdt = entity.balanceBdt,
                currencyCode = entity.currencyCode,
                currencySymbol = entity.currencySymbol,
                formattedBalance = formatted,
                displayWithCurrency = "${entity.currencySymbol}$formatted",
                totalWonBdt = entity.totalWonBdt,
                totalWageredBdt = entity.totalWageredBdt,
                vipPoints = entity.vipPoints,
                isLoading = false,
                errorMessage = _errorMessage.value
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = BalanceUiState()
        )

    /**
     * Reactive list of recent balance transactions across all game activities.
     */
    val recentTransactions: StateFlow<List<BalanceTransactionEntity>> = repository.observeRecentTransactions(limit = 50)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    /**
     * Atomically debits a bet for a given game activity.
     *
     * @param gameActivity Name of the game (e.g. "Teen Patti", "Crash", "Slots").
     * @param amountBdt Amount to deduct in ৳ BDT.
     * @param note Optional descriptive note.
     * @param onResult Callback with success status and optional error message.
     */
    fun placeBet(
        gameActivity: String,
        amountBdt: Long,
        note: String = "",
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) = viewModelScope.launch {
        if (amountBdt <= 0L) {
            val error = "Bet amount must be greater than ৳0"
            _errorMessage.value = error
            onResult(false, error)
            return@launch
        }

        val result = repository.placeBet(gameActivity, amountBdt, note)
        result.fold(
            onSuccess = { tx ->
                _lastTransaction.value = tx
                _errorMessage.value = null
                onResult(true, null)
            },
            onFailure = { error ->
                val msg = error.message ?: "Failed to place bet of ৳$amountBdt on $gameActivity"
                _errorMessage.value = msg
                onResult(false, msg)
            }
        )
    }

    /**
     * Atomically credits a win payout for a given game activity.
     *
     * @param gameActivity Name of the game.
     * @param winAmountBdt Winning payout in ৳ BDT.
     * @param note Optional description (e.g. "Jackpot 🐯", "Trail of Aces").
     * @param onResult Callback with result.
     */
    fun awardWin(
        gameActivity: String,
        winAmountBdt: Long,
        note: String = "",
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) = viewModelScope.launch {
        if (winAmountBdt <= 0L) {
            onResult(true, null)
            return@launch
        }

        val result = repository.awardWin(gameActivity, winAmountBdt, note)
        result.fold(
            onSuccess = { tx ->
                _lastTransaction.value = tx
                _errorMessage.value = null
                onResult(true, null)
            },
            onFailure = { error ->
                val msg = error.message ?: "Failed to credit win of ৳$winAmountBdt"
                _errorMessage.value = msg
                onResult(false, msg)
            }
        )
    }

    /**
     * Atomically refunds a wager if a round is canceled or drawn.
     */
    fun refundBet(
        gameActivity: String,
        amountBdt: Long,
        reason: String = ""
    ) = viewModelScope.launch {
        if (amountBdt <= 0L) return@launch
        val result = repository.refundBet(gameActivity, amountBdt, reason)
        result.onSuccess { _lastTransaction.value = it }
    }

    /**
     * Credits daily bonus to user's BDT balance.
     */
    fun claimDailyBonus(bonusAmountBdt: Long) = viewModelScope.launch {
        val result = repository.addCredits(
            source = "Daily Bonus",
            amountBdt = bonusAmountBdt,
            type = BalanceOperationType.DAILY_BONUS,
            note = "Daily Gift (দৈনিক উপহার) ৳$bonusAmountBdt"
        )
        result.onSuccess { _lastTransaction.value = it }
    }

    /**
     * Credits simulated reload (bKash, Nagad, Rocket).
     */
    fun addSimulatedReload(channel: String, baseAmountBdt: Long, bonusAmountBdt: Long = 0L) = viewModelScope.launch {
        val totalAmount = baseAmountBdt + bonusAmountBdt
        val result = repository.addCredits(
            source = channel,
            amountBdt = totalAmount,
            type = BalanceOperationType.SIMULATED_RELOAD,
            note = "$channel Reload (৳$baseAmountBdt + ৳$bonusAmountBdt bonus)"
        )
        result.onSuccess { _lastTransaction.value = it }
    }

    /**
     * Emergency free coin faucet grant (+৳10,000) when balance is depleted.
     */
    fun claimEmergencyFaucet() = viewModelScope.launch {
        val result = repository.addCredits(
            source = "Free Faucet",
            amountBdt = 10000L,
            type = BalanceOperationType.FAUCET,
            note = "Free Faucet (জরুরি ফ্রি কয়েন ৳১০,০০০)"
        )
        result.onSuccess { _lastTransaction.value = it }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
