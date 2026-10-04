package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Card
import com.example.model.CardDeck
import com.example.model.HandType
import com.example.model.Rank
import com.example.model.Suit
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dhaka Royale", appName)
  }

  @Test
  fun `evaluate teen patti trail of aces`() {
    val trio = listOf(
        Card(Suit.SPADES, Rank.ACE),
        Card(Suit.HEARTS, Rank.ACE),
        Card(Suit.DIAMONDS, Rank.ACE)
    )
    val result = CardDeck.evaluateTeenPatti(trio)
    assertEquals(HandType.TRAIL, result.type)
  }

  @Test
  fun `evaluate teen patti sequence`() {
    val seq = listOf(
        Card(Suit.SPADES, Rank.FOUR),
        Card(Suit.HEARTS, Rank.FIVE),
        Card(Suit.DIAMONDS, Rank.SIX)
    )
    val result = CardDeck.evaluateTeenPatti(seq)
    assertEquals(HandType.SEQUENCE, result.type)
  }
}
