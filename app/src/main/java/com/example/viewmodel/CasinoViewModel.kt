package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CasinoDatabase
import com.example.data.local.TransactionEntity
import com.example.data.local.UserWalletEntity
import com.example.data.repository.CasinoRepository
import com.example.model.AndarBaharPhase
import com.example.model.AndarBaharSide
import com.example.model.AndarBaharState
import com.example.model.Card
import com.example.model.CardDeck
import com.example.model.CrashState
import com.example.model.CrashStatus
import com.example.model.PlayerStatus
import com.example.model.SlotSymbol
import com.example.model.SlotsState
import com.example.model.TeenPattiPhase
import com.example.model.TeenPattiPlayer
import com.example.model.TeenPattiState
import com.example.model.WinningLine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

enum class CasinoScreen {
    LOBBY,
    CRASH,
    TEEN_PATTI,
    ANDAR_BAHAR,
    SLOTS,
    WHEEL,
    WALLET,
    VIP
}

class CasinoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CasinoRepository
    private val vibrator: Vibrator?

    init {
        val db = CasinoDatabase.getInstance(application)
        repository = CasinoRepository(db.casinoDao(), db.balanceDao())

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        viewModelScope.launch {
            repository.ensureWalletInitialized()
        }
    }

    // Navigation
    private val _currentScreen = MutableStateFlow(CasinoScreen.LOBBY)
    val currentScreen: StateFlow<CasinoScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: CasinoScreen) {
        _currentScreen.value = screen
    }

    // Room Flows
    val wallet: StateFlow<UserWalletEntity> = repository.walletFlow
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            UserWalletEntity()
        )

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactionsFlow
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )

    // Game States
    private val _crashState = MutableStateFlow(CrashState())
    val crashState: StateFlow<CrashState> = _crashState.asStateFlow()

    private val _teenPattiState = MutableStateFlow(TeenPattiState())
    val teenPattiState: StateFlow<TeenPattiState> = _teenPattiState.asStateFlow()

    private val _andarBaharState = MutableStateFlow(AndarBaharState())
    val andarBaharState: StateFlow<AndarBaharState> = _andarBaharState.asStateFlow()

    private val _slotsState = MutableStateFlow(SlotsState())
    val slotsState: StateFlow<SlotsState> = _slotsState.asStateFlow()

    private var crashJob: Job? = null
    private var andarBaharJob: Job? = null

    // Haptic feedback
    private fun triggerHaptic(type: String = "tap") {
        if (wallet.value.soundEnabled && vibrator != null && vibrator.hasVibrator()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (type) {
                        "win" -> vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 200), -1))
                        "crash" -> vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
                        else -> vibrator.vibrate(VibrationEffect.createOneShot(40, 120))
                    }
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(50)
                }
            } catch (_: Exception) {}
        }
    }

    // ================= AVIATOR / CRASH LOGIC =================
    fun startCrashRound(betAmount: Long, autoCashOut: Float) {
        viewModelScope.launch {
            val success = repository.placeBet("Crash", betAmount)
            if (!success) return@launch

            triggerHaptic("tap")

            // Weighted crash calculation:
            // 70% between 1.1x and 3.5x
            // 20% between 3.5x and 10x
            // 10% between 10x and 60x
            val r = Random.nextFloat()
            val crashPoint = when {
                r < 0.12f -> 1.05f + Random.nextFloat() * 0.3f
                r < 0.70f -> 1.35f + Random.nextFloat() * 2.2f
                r < 0.90f -> 3.5f + Random.nextFloat() * 6.5f
                else -> 10.0f + Random.nextFloat() * 50.0f
            }

            _crashState.value = _crashState.value.copy(
                status = CrashStatus.FLYING,
                currentMultiplier = 1.00f,
                crashPoint = crashPoint,
                betAmount = betAmount,
                isBetActive = true,
                autoCashOutAt = autoCashOut,
                hasCashedOut = false,
                profit = 0L
            )

            crashJob?.cancel()
            crashJob = viewModelScope.launch {
                var currentMult = 1.00f
                while (currentMult < crashPoint) {
                    delay(70)
                    val speed = 0.02f + (currentMult * 0.015f)
                    currentMult += speed

                    if (autoCashOut > 1.0f && currentMult >= autoCashOut && !_crashState.value.hasCashedOut) {
                        cashOutCrash(autoCashOut)
                    }

                    _crashState.value = _crashState.value.copy(
                        currentMultiplier = currentMult
                    )
                }

                // Crashed!
                triggerHaptic("crash")
                val historyList = _crashState.value.history.toMutableList().apply {
                    add(crashPoint)
                }
                _crashState.value = _crashState.value.copy(
                    status = CrashStatus.CRASHED,
                    currentMultiplier = crashPoint,
                    history = historyList
                )

                if (!_crashState.value.hasCashedOut) {
                    repository.recordLoss("crash")
                }
            }
        }
    }

    fun cashOutCrash(specificMultiplier: Float = 0f) {
        val state = _crashState.value
        if (state.status != CrashStatus.FLYING || !state.isBetActive || state.hasCashedOut) return

        val mult = if (specificMultiplier > 0f) specificMultiplier else state.currentMultiplier
        val payout = (state.betAmount * mult).toLong()
        val profit = payout - state.betAmount

        _crashState.value = state.copy(
            hasCashedOut = true,
            cashedOutMultiplier = mult,
            profit = profit
        )

        triggerHaptic("win")

        viewModelScope.launch {
            repository.recordWin("crash", "Crash", payout, profit)
        }
    }

    // ================= TEEN PATTI LOGIC =================
    fun startTeenPatti(bootAmount: Long) {
        viewModelScope.launch {
            val success = repository.placeBet("Teen Patti", bootAmount)
            if (!success) return@launch

            triggerHaptic("tap")

            val deck = CardDeck.createDeck(shuffled = true)
            val playerCards = listOf(deck.removeAt(0), deck.removeAt(0), deck.removeAt(0))
            val bot1Cards = listOf(deck.removeAt(0), deck.removeAt(0), deck.removeAt(0))
            val bot2Cards = listOf(deck.removeAt(0), deck.removeAt(0), deck.removeAt(0))

            val players = listOf(
                TeenPattiPlayer("p1", "আপনি (You)", "You", "🤴", playerCards, isSeen = false, isHuman = true, currentBet = bootAmount),
                TeenPattiPlayer("b1", "নবাব শাকিল", "Nawab Shakil", "👳‍♂️", bot1Cards, isSeen = false, isHuman = false, currentBet = bootAmount),
                TeenPattiPlayer("b2", "রানী প্রিয়া", "Rani Priya", "👸", bot2Cards, isSeen = false, isHuman = false, currentBet = bootAmount)
            )

            _teenPattiState.value = TeenPattiState(
                phase = TeenPattiPhase.PLAYING,
                bootAmount = bootAmount,
                currentChaalAmount = bootAmount,
                pot = bootAmount * 3,
                players = players,
                currentTurnIndex = 0,
                winnerName = "",
                winningHandRank = "",
                winningPayout = 0L,
                roundMessage = "নতুন খেলা শুরু হয়েছে! আপনার চাল দিন।"
            )
        }
    }

    fun seeCardsTeenPatti() {
        val state = _teenPattiState.value
        val updatedPlayers = state.players.map {
            if (it.isHuman) it.copy(isSeen = true) else it
        }
        _teenPattiState.value = state.copy(players = updatedPlayers)
        triggerHaptic("tap")
    }

    fun chaalTeenPatti() {
        val state = _teenPattiState.value
        val humanIndex = state.players.indexOfFirst { it.isHuman }
        if (state.currentTurnIndex != humanIndex || state.phase != TeenPattiPhase.PLAYING) return

        val human = state.players[humanIndex]
        val cost = if (human.isSeen) state.currentChaalAmount * 2 else state.currentChaalAmount

        viewModelScope.launch {
            val ok = repository.placeBet("Teen Patti", cost)
            if (!ok) return@launch

            triggerHaptic("tap")

            val newPot = state.pot + cost
            val updatedPlayers = state.players.toMutableList()
            updatedPlayers[humanIndex] = human.copy(currentBet = human.currentBet + cost)

            _teenPattiState.value = state.copy(
                pot = newPot,
                players = updatedPlayers,
                currentTurnIndex = 1,
                roundMessage = "আপনি ৳$cost চাল দিয়েছেন। নবাব শাকিল চিন্তা করছেন..."
            )

            // Let bots take turns
            runBotTurns()
        }
    }

    fun packTeenPatti() {
        val state = _teenPattiState.value
        val humanIndex = state.players.indexOfFirst { it.isHuman }
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[humanIndex] = updatedPlayers[humanIndex].copy(status = PlayerStatus.PACKED)

        _teenPattiState.value = state.copy(
            players = updatedPlayers,
            phase = TeenPattiPhase.ROUND_OVER,
            winnerName = "নবাব শাকিল",
            roundMessage = "আপনি প্যাক করেছেন! এই রাউন্ডে জয়ী নবাব শাকিল।"
        )
        viewModelScope.launch { repository.recordLoss("teen_patti") }
    }

    fun showTeenPatti() {
        val state = _teenPattiState.value
        val human = state.players.first { it.isHuman }
        val cost = state.currentChaalAmount

        viewModelScope.launch {
            val ok = repository.placeBet("Teen Patti", cost)
            if (!ok) return@launch

            val newPot = state.pot + cost
            val activePlayers = state.players.filter { it.status == PlayerStatus.ACTIVE }

            var bestPlayer = activePlayers.first()
            var bestEvaluation = CardDeck.evaluateTeenPatti(bestPlayer.cards)

            for (p in activePlayers.drop(1)) {
                val eval = CardDeck.evaluateTeenPatti(p.cards)
                if (eval.score > bestEvaluation.score) {
                    bestPlayer = p
                    bestEvaluation = eval
                }
            }

            val humanWon = bestPlayer.isHuman
            if (humanWon) {
                triggerHaptic("win")
                repository.recordWin("teen_patti", "Teen Patti", newPot, newPot - human.currentBet - cost)
            } else {
                repository.recordLoss("teen_patti")
            }

            _teenPattiState.value = state.copy(
                phase = TeenPattiPhase.ROUND_OVER,
                pot = newPot,
                winnerName = if (humanWon) "আপনি (You)" else bestPlayer.nameBn,
                winningHandRank = bestEvaluation.type.nameBn,
                winningPayout = if (humanWon) newPot else 0L,
                roundMessage = "${if (humanWon) "অভিনন্দন! আপনি জিতেছেন" else "${bestPlayer.nameBn} জিতেছেন"} (${bestEvaluation.type.nameBn})!"
            )
        }
    }

    private fun runBotTurns() {
        viewModelScope.launch {
            delay(800)
            val state = _teenPattiState.value
            if (state.phase != TeenPattiPhase.PLAYING) return@launch

            val updatedPlayers = state.players.toMutableList()
            var currentPot = state.pot

            // Bot 1
            val bot1 = updatedPlayers[1]
            if (bot1.status == PlayerStatus.ACTIVE) {
                val bot1Score = CardDeck.evaluateTeenPatti(bot1.cards).score
                if (bot1Score < 1050000 && Random.nextFloat() < 0.25f) {
                    updatedPlayers[1] = bot1.copy(status = PlayerStatus.PACKED)
                } else {
                    val botChaal = state.currentChaalAmount
                    currentPot += botChaal
                    updatedPlayers[1] = bot1.copy(currentBet = bot1.currentBet + botChaal)
                }
            }

            delay(700)

            // Bot 2
            val bot2 = updatedPlayers[2]
            if (bot2.status == PlayerStatus.ACTIVE) {
                val bot2Score = CardDeck.evaluateTeenPatti(bot2.cards).score
                if (bot2Score < 1050000 && Random.nextFloat() < 0.28f) {
                    updatedPlayers[2] = bot2.copy(status = PlayerStatus.PACKED)
                } else {
                    val botChaal = state.currentChaalAmount
                    currentPot += botChaal
                    updatedPlayers[2] = bot2.copy(currentBet = bot2.currentBet + botChaal)
                }
            }

            // Check if only human remains
            val active = updatedPlayers.filter { it.status == PlayerStatus.ACTIVE }
            if (active.size == 1 && active.first().isHuman) {
                triggerHaptic("win")
                val human = active.first()
                repository.recordWin("teen_patti", "Teen Patti", currentPot, currentPot - human.currentBet)
                _teenPattiState.value = state.copy(
                    phase = TeenPattiPhase.ROUND_OVER,
                    players = updatedPlayers,
                    pot = currentPot,
                    winnerName = "আপনি (You)",
                    winningPayout = currentPot,
                    roundMessage = "সব বট প্যাক করেছে! আপনি পুরো পট জিতে নিয়েছেন!"
                )
                return@launch
            }

            _teenPattiState.value = state.copy(
                pot = currentPot,
                players = updatedPlayers,
                currentTurnIndex = 0,
                roundMessage = "বটদের চাল সম্পন্ন। এবার আপনার চাল দিন!"
            )
        }
    }

    // ================= ANDAR BAHAR LOGIC =================
    fun betAndar(amount: Long) {
        viewModelScope.launch {
            val ok = repository.placeBet("Andar Bahar", amount)
            if (ok) {
                triggerHaptic("tap")
                _andarBaharState.value = _andarBaharState.value.copy(
                    andarBet = _andarBaharState.value.andarBet + amount
                )
            }
        }
    }

    fun betBahar(amount: Long) {
        viewModelScope.launch {
            val ok = repository.placeBet("Andar Bahar", amount)
            if (ok) {
                triggerHaptic("tap")
                _andarBaharState.value = _andarBaharState.value.copy(
                    baharBet = _andarBaharState.value.baharBet + amount
                )
            }
        }
    }

    fun clearAndarBaharBets() {
        _andarBaharState.value = _andarBaharState.value.copy(andarBet = 0L, baharBet = 0L)
    }

    fun dealAndarBaharRound() {
        val totalBet = _andarBaharState.value.andarBet + _andarBaharState.value.baharBet
        if (totalBet == 0L || _andarBaharState.value.phase == AndarBaharPhase.DEALING) return

        andarBaharJob?.cancel()
        andarBaharJob = viewModelScope.launch {
            val deck = CardDeck.createDeck(shuffled = true)
            val joker = deck.removeAt(0)

            _andarBaharState.value = _andarBaharState.value.copy(
                phase = AndarBaharPhase.DEALING,
                jokerCard = joker,
                andarCards = emptyList(),
                baharCards = emptyList(),
                winningSide = null,
                winningCard = null,
                totalWon = 0L,
                message = "জোকার কার্ড ডিল হয়েছে: ${joker.rank.display}${joker.suit.symbol}"
            )

            delay(600)

            var isAndarTurn = true
            var matched = false
            var winningSide: AndarBaharSide? = null
            var matchedCard: Card? = null

            val andarList = mutableListOf<Card>()
            val baharList = mutableListOf<Card>()

            while (deck.isNotEmpty() && !matched) {
                delay(400)
                val dealt = deck.removeAt(0)
                triggerHaptic("tap")

                if (isAndarTurn) {
                    andarList.add(dealt)
                    _andarBaharState.value = _andarBaharState.value.copy(andarCards = andarList.toList())
                } else {
                    baharList.add(dealt)
                    _andarBaharState.value = _andarBaharState.value.copy(baharCards = baharList.toList())
                }

                if (dealt.rank == joker.rank) {
                    matched = true
                    matchedCard = dealt
                    winningSide = if (isAndarTurn) AndarBaharSide.ANDAR else AndarBaharSide.BAHAR
                }

                isAndarTurn = !isAndarTurn
            }

            // Evaluate payout
            val andarBet = _andarBaharState.value.andarBet
            val baharBet = _andarBaharState.value.baharBet
            var payout = 0L
            if (winningSide == AndarBaharSide.ANDAR && andarBet > 0) {
                payout = (andarBet * 1.95f).toLong()
            } else if (winningSide == AndarBaharSide.BAHAR && baharBet > 0) {
                payout = (baharBet * 2.0f).toLong()
            }

            val history = _andarBaharState.value.history.toMutableList()
            if (winningSide != null) {
                history.add(winningSide)
            }

            if (payout > 0) {
                triggerHaptic("win")
                repository.recordWin("andar_bahar", "Andar Bahar", payout, payout - totalBet)
            } else {
                repository.recordLoss("andar_bahar")
            }

            _andarBaharState.value = _andarBaharState.value.copy(
                phase = AndarBaharPhase.FINISHED,
                winningSide = winningSide,
                winningCard = matchedCard,
                totalWon = payout,
                andarBet = 0L,
                baharBet = 0L,
                history = history,
                message = "${if (winningSide == AndarBaharSide.ANDAR) "আন্দর" else "বাহার"} জয়ী হয়েছে!"
            )
        }
    }

    // ================= SLOTS LOGIC =================
    fun spinSlots(betAmount: Long) {
        if (_slotsState.value.isSpinning) return

        viewModelScope.launch {
            val ok = repository.placeBet("Slots", betAmount)
            if (!ok) return@launch

            triggerHaptic("tap")
            _slotsState.value = _slotsState.value.copy(
                isSpinning = true,
                totalBet = betAmount,
                lastWin = 0L,
                winningLines = emptyList(),
                isJackpot = false
            )

            delay(1200)

            val symbols = SlotSymbol.values()
            val newGrid = List(3) {
                List(3) { symbols[Random.nextInt(symbols.size)] }
            }

            // Evaluate paylines
            val lines = mutableListOf<WinningLine>()
            val lineIndices = listOf(
                listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2)), // Line 0: Top
                listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2)), // Line 1: Middle
                listOf(Pair(2, 0), Pair(2, 1), Pair(2, 2)), // Line 2: Bottom
                listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2)), // Line 3: Diagonal 1
                listOf(Pair(2, 0), Pair(1, 1), Pair(0, 2))  // Line 4: Diagonal 2
            )

            var totalPayout = 0L
            var jackpotHit = false

            lineIndices.forEachIndexed { idx, coords ->
                val s1 = newGrid[coords[0].first][coords[0].second]
                val s2 = newGrid[coords[1].first][coords[1].second]
                val s3 = newGrid[coords[2].first][coords[2].second]

                if (s1 == s2 && s2 == s3) {
                    val linePayout = (betAmount / 5) * s1.multiplier3x
                    totalPayout += linePayout
                    lines.add(WinningLine(idx, s1, linePayout))
                    if (s1 == SlotSymbol.TIGER) {
                        jackpotHit = true
                    }
                }
            }

            if (jackpotHit) {
                totalPayout += 250000L // Grand Jackpot Bonus
            }

            if (totalPayout > 0) {
                triggerHaptic("win")
                repository.recordWin("slots", "Slots", totalPayout, totalPayout - betAmount)
            } else {
                repository.recordLoss("slots")
            }

            _slotsState.value = _slotsState.value.copy(
                isSpinning = false,
                grid = newGrid,
                lastWin = totalPayout,
                winningLines = lines,
                isJackpot = jackpotHit
            )

            // Auto-spin next round if enabled
            if (_slotsState.value.autoSpin && wallet.value.balance >= betAmount) {
                delay(1500)
                if (_slotsState.value.autoSpin) {
                    spinSlots(betAmount)
                }
            }
        }
    }

    fun toggleAutoSpin() {
        val newState = !_slotsState.value.autoSpin
        _slotsState.value = _slotsState.value.copy(autoSpin = newState)
        if (newState && !_slotsState.value.isSpinning) {
            spinSlots(_slotsState.value.totalBet)
        }
    }

    // ================= WHEEL & REWARDS =================
    fun claimSpinReward(amount: Long) {
        triggerHaptic("win")
        viewModelScope.launch {
            repository.recordWin("wheel", "Lucky Wheel", amount, amount - 1000L)
        }
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            val (success, amt) = repository.claimDailyBonus()
            if (success) {
                triggerHaptic("win")
            }
        }
    }

    fun addSimulatedReload(channel: String, baseAmount: Long, bonus: Long) {
        viewModelScope.launch {
            triggerHaptic("win")
            repository.addSimulatedReload(channel, baseAmount, bonus)
        }
    }

    fun claimEmergencyFaucet() {
        viewModelScope.launch {
            triggerHaptic("win")
            repository.addSimulatedReload("Free Faucet", 10000L, 0L)
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            repository.toggleLanguage()
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            repository.toggleSound()
        }
    }
}
