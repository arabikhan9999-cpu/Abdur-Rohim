package com.example.model

enum class PlayerStatus {
    ACTIVE,
    PACKED,
    WON,
    LOST
}

data class TeenPattiPlayer(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val avatarEmoji: String,
    val cards: List<Card> = emptyList(),
    val isSeen: Boolean = false,
    val status: PlayerStatus = PlayerStatus.ACTIVE,
    val isHuman: Boolean = false,
    val currentBet: Long = 0L
)

enum class TeenPattiPhase {
    WAITING_FOR_DEAL,
    PLAYING,
    SHOWDOWN,
    ROUND_OVER
}

data class TeenPattiState(
    val phase: TeenPattiPhase = TeenPattiPhase.WAITING_FOR_DEAL,
    val bootAmount: Long = 500L,
    val currentChaalAmount: Long = 500L,
    val pot: Long = 0L,
    val players: List<TeenPattiPlayer> = emptyList(),
    val currentTurnIndex: Int = 0,
    val winnerName: String = "",
    val winningHandRank: String = "",
    val winningPayout: Long = 0L,
    val roundMessage: String = ""
)
