package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BalanceOperationType
import com.example.data.local.CasinoDatabase
import com.example.data.repository.BalanceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GameBalanceViewModelTest {

    private lateinit var database: CasinoDatabase
    private lateinit var repository: BalanceRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CasinoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = BalanceRepository(database.balanceDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `initial balance starts at 25000 BDT with currency symbol`() = runBlocking {
        val userBalance = repository.ensureInitialized()
        assertEquals(25000L, userBalance.balanceBdt)
        assertEquals("BDT", userBalance.currencyCode)
        assertEquals("৳", userBalance.currencySymbol)
    }

    @Test
    fun `placeBet atomically debits balance and logs game activity`() = runBlocking {
        repository.ensureInitialized()

        // Place a bet on Crash
        val betResult = repository.placeBet(
            gameActivity = "Crash",
            amountBdt = 2000L,
            note = "Round 1 Bet"
        )

        assertTrue(betResult.isSuccess)
        val transaction = betResult.getOrNull()
        assertNotNull(transaction)
        assertEquals(25000L, transaction!!.balanceBeforeBdt)
        assertEquals(23000L, transaction.balanceAfterBdt)
        assertEquals(-2000L, transaction.amountBdt)
        assertEquals("Crash", transaction.gameActivity)
        assertEquals(BalanceOperationType.BET, transaction.operationType)

        // Verify updated entity
        val updated = repository.observeBalance().first()
        assertEquals(23000L, updated.balanceBdt)
        assertEquals(2000L, updated.totalWageredBdt)
    }

    @Test
    fun `awardWin atomically credits balance from game activity`() = runBlocking {
        repository.ensureInitialized()
        repository.placeBet("Teen Patti", 1000L)

        val winResult = repository.awardWin(
            gameActivity = "Teen Patti",
            winAmountBdt = 5000L,
            note = "Trail of Kings"
        )

        assertTrue(winResult.isSuccess)
        val tx = winResult.getOrNull()!!
        assertEquals(24000L, tx.balanceBeforeBdt)
        assertEquals(29000L, tx.balanceAfterBdt)
        assertEquals(5000L, tx.amountBdt)
        assertEquals("Teen Patti", tx.gameActivity)
        assertEquals(BalanceOperationType.WIN, tx.operationType)

        val updated = repository.observeBalance().first()
        assertEquals(29000L, updated.balanceBdt)
        assertEquals(5000L, updated.totalWonBdt)
    }

    @Test
    fun `placeBet fails safely when balance is insufficient`() = runBlocking {
        repository.ensureInitialized()

        // Attempt to bet 50,000 when balance is 25,000
        val failResult = repository.placeBet(
            gameActivity = "Slots",
            amountBdt = 50000L,
            note = "Over-budget bet"
        )

        assertTrue(failResult.isFailure)

        // Balance must remain unchanged
        val balance = repository.observeBalance().first()
        assertEquals(25000L, balance.balanceBdt)
    }

    @Test
    fun `multiple games maintain consistent transaction audit log`() = runBlocking {
        repository.ensureInitialized()

        repository.placeBet("Crash", 1000L)
        repository.awardWin("Crash", 2500L)
        repository.placeBet("Andar Bahar", 500L)
        repository.placeBet("Slots", 2000L)
        repository.awardWin("Slots", 10000L)

        val transactions = repository.observeRecentTransactions(limit = 20).first()
        // Welcome bonus + 5 operations = 6 transactions
        assertTrue(transactions.size >= 5)

        // Most recent should be Slots WIN
        val latest = transactions.first()
        assertEquals("Slots", latest.gameActivity)
        assertEquals(BalanceOperationType.WIN, latest.operationType)
        assertEquals(10000L, latest.amountBdt)
    }
}
