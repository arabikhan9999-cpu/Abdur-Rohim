package com.example.model

enum class AndarBaharSide {
    ANDAR,
    BAHAR
}

enum class AndarBaharPhase {
    IDLE,
    BETTING,
    DEALING,
    FINISHED
}

data class AndarBaharState(
    val phase: AndarBaharPhase = AndarBaharPhase.IDLE,
    val jokerCard: Card? = null,
    val andarCards: List<Card> = emptyList(),
    val baharCards: List<Card> = emptyList(),
    val andarBet: Long = 0L,
    val baharBet: Long = 0L,
    val winningSide: AndarBaharSide? = null,
    val winningCard: Card? = null,
    val totalWon: Long = 0L,
    val history: List<AndarBaharSide> = listOf(
        AndarBaharSide.ANDAR, AndarBaharSide.BAHAR, AndarBaharSide.BAHAR,
        AndarBaharSide.ANDAR, AndarBaharSide.ANDAR, AndarBaharSide.BAHAR
    ),
    val message: String = ""
)
