package com.example.model

enum class CrashStatus {
    IDLE,
    BETTING,
    FLYING,
    CRASHED
}

data class CrashState(
    val status: CrashStatus = CrashStatus.IDLE,
    val currentMultiplier: Float = 1.00f,
    val crashPoint: Float = 2.00f,
    val betAmount: Long = 1000L,
    val isBetActive: Boolean = false,
    val autoCashOutAt: Float = 2.00f, // 0 for manual
    val hasCashedOut: Boolean = false,
    val cashedOutMultiplier: Float = 0f,
    val profit: Long = 0L,
    val history: List<Float> = listOf(1.42f, 2.85f, 1.18f, 7.30f, 1.95f, 12.44f, 1.08f),
    val message: String = ""
)
