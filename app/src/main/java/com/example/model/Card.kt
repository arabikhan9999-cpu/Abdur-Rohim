package com.example.model

enum class Suit(val symbol: String, val isRed: Boolean) {
    SPADES("♠", false),
    HEARTS("♥", true),
    DIAMONDS("♦", true),
    CLUBS("♣", false)
}

enum class Rank(val value: Int, val display: String) {
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K"),
    ACE(14, "A")
}

data class Card(
    val suit: Suit,
    val rank: Rank,
    val isFaceUp: Boolean = true
) {
    val displayShort: String = "${rank.display}${suit.symbol}"
}

enum class HandType(val rankWeight: Int, val nameBn: String, val nameEn: String) {
    TRAIL(6, "ট্রেইল (তিনের জোড়া)", "Trail / Trio (Three of a Kind)"),
    PURE_SEQUENCE(5, "পিওর সিকোয়েন্স", "Pure Sequence (Straight Flush)"),
    SEQUENCE(4, "সিকোয়েন্স (রান)", "Sequence (Straight)"),
    COLOR(3, "কালার (ফ্লাশ)", "Color (Flush)"),
    PAIR(2, "পেয়ার (জোড়া)", "Pair"),
    HIGH_CARD(1, "হাই কার্ড", "High Card")
}

data class HandResult(
    val type: HandType,
    val score: Int,
    val description: String
)

object CardDeck {
    fun createDeck(shuffled: Boolean = true): MutableList<Card> {
        val list = mutableListOf<Card>()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                list.add(Card(suit, rank))
            }
        }
        if (shuffled) {
            list.shuffle()
        }
        return list
    }

    /**
     * Evaluates a 3-card Teen Patti hand
     */
    fun evaluateTeenPatti(cards: List<Card>): HandResult {
        if (cards.size < 3) {
            return HandResult(HandType.HIGH_CARD, 0, "High Card")
        }
        val sorted = cards.take(3).sortedByDescending { it.rank.value }
        val r0 = sorted[0].rank.value
        val r1 = sorted[1].rank.value
        val r2 = sorted[2].rank.value

        val isSameSuit = cards[0].suit == cards[1].suit && cards[1].suit == cards[2].suit
        val isTrio = (r0 == r1 && r1 == r2)

        // Straight check: standard consecutive (e.g. A-K-Q or 5-4-3) OR A-2-3
        val isConsecutive = (r0 == r1 + 1 && r1 == r2 + 1) || (r0 == 14 && r1 == 3 && r2 == 2)

        return when {
            isTrio -> HandResult(
                HandType.TRAIL,
                6000000 + r0 * 100,
                "Trail of ${sorted[0].rank.display}s"
            )
            isConsecutive && isSameSuit -> HandResult(
                HandType.PURE_SEQUENCE,
                5000000 + (if (r0 == 14 && r1 == 3) 3 else r0) * 100 + r1,
                "Pure Sequence ${sorted[0].rank.display}-${sorted[1].rank.display}-${sorted[2].rank.display}"
            )
            isConsecutive -> HandResult(
                HandType.SEQUENCE,
                4000000 + (if (r0 == 14 && r1 == 3) 3 else r0) * 100 + r1,
                "Sequence ${sorted[0].rank.display}-${sorted[1].rank.display}-${sorted[2].rank.display}"
            )
            isSameSuit -> HandResult(
                HandType.COLOR,
                3000000 + r0 * 1000 + r1 * 50 + r2,
                "Color (${cards[0].suit.symbol})"
            )
            r0 == r1 -> HandResult(
                HandType.PAIR,
                2000000 + r0 * 100 + r2,
                "Pair of ${sorted[0].rank.display}s"
            )
            r1 == r2 -> HandResult(
                HandType.PAIR,
                2000000 + r1 * 100 + r0,
                "Pair of ${sorted[1].rank.display}s"
            )
            r0 == r2 -> HandResult(
                HandType.PAIR,
                2000000 + r0 * 100 + r1,
                "Pair of ${sorted[0].rank.display}s"
            )
            else -> HandResult(
                HandType.HIGH_CARD,
                1000000 + r0 * 1000 + r1 * 50 + r2,
                "High Card ${sorted[0].rank.display}"
            )
        }
    }
}
