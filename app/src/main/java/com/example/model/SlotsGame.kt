package com.example.model

enum class SlotSymbol(val emoji: String, val displayName: String, val multiplier3x: Int) {
    TIGER("🐯", "Royal Tiger", 50),
    DIAMOND("💎", "Diamond", 25),
    SEVEN("7️⃣", "Lucky 7", 15),
    CROWN("👑", "Nawab Crown", 10),
    GOLD_POT("💰", "BDT Pot", 8),
    MANGO("🥭", "Rajshahi Mango", 5),
    BELL("🔔", "Golden Bell", 3),
    CHERRY("🍒", "Cherry", 2)
}

data class WinningLine(
    val lineIndex: Int, // 0: Top, 1: Middle, 2: Bottom, 3: Diag1, 4: Diag2
    val symbol: SlotSymbol,
    val payout: Long
)

data class SlotsState(
    val isSpinning: Boolean = false,
    val grid: List<List<SlotSymbol>> = listOf(
        listOf(SlotSymbol.SEVEN, SlotSymbol.CROWN, SlotSymbol.TIGER),
        listOf(SlotSymbol.GOLD_POT, SlotSymbol.TIGER, SlotSymbol.DIAMOND),
        listOf(SlotSymbol.BELL, SlotSymbol.MANGO, SlotSymbol.SEVEN)
    ),
    val betPerLine: Long = 200L,
    val activeLines: Int = 5,
    val totalBet: Long = 1000L,
    val lastWin: Long = 0L,
    val winningLines: List<WinningLine> = emptyList(),
    val isJackpot: Boolean = false,
    val autoSpin: Boolean = false
)
