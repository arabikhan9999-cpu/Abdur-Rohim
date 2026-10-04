package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AndarBaharPhase
import com.example.model.AndarBaharSide
import com.example.model.AndarBaharState
import com.example.model.Strings
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.ChipSelector
import com.example.ui.components.PlayingCardView
import com.example.ui.theme.CasinoCrimson
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldCard
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldDark
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.CasinoNeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AndarBaharScreen(
    state: AndarBaharState,
    balance: Long,
    language: String,
    onPlaceBetAndar: (Long) -> Unit,
    onPlaceBetBahar: (Long) -> Unit,
    onDealRound: () -> Unit,
    onClearBets: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedBet by remember { mutableStateOf(1000L) }
    var showWinDialog by remember { mutableStateOf(false) }

    if (state.phase == AndarBaharPhase.FINISHED && state.totalWon > 0 && !showWinDialog) {
        showWinDialog = true
    }

    if (showWinDialog && state.totalWon > 0) {
        CelebrationDialog(
            amount = state.totalWon,
            titleBn = if (state.winningSide == AndarBaharSide.ANDAR) "আন্দর জয়লাভ করেছে!" else "বাহার জয়লাভ করেছে!",
            titleEn = if (state.winningSide == AndarBaharSide.ANDAR) "ANDAR WON!" else "BAHAR WON!",
            isJackpot = false,
            language = language,
            onDismiss = { showWinDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoEmeraldDark)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // History Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (language == "BN") "পূর্ববর্তী জয়:" else "History:",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            state.history.takeLast(10).reversed().forEach { side ->
                val isAndar = side == AndarBaharSide.ANDAR
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isAndar) Color(0xFF0077B6) else CasinoCrimson)
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isAndar) "A" else "B",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Joker Card Showcase in Center
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, CasinoGold, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B291A))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (language == "BN") "👑 মূল জোকার কার্ড" else "👑 JOKER CARD",
                        color = CasinoGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (language == "BN") "এই র‍্যাংকটি খুঁজুন" else "Match this Rank",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (state.jokerCard != null) {
                    PlayingCardView(
                        card = state.jokerCard,
                        isFaceUp = true,
                        width = 62.dp,
                        height = 92.dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(62.dp, 92.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, CasinoGoldDark, RoundedCornerShape(8.dp))
                            .background(Color(0xFF143B27)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎴", fontSize = 28.sp)
                    }
                }
            }
        }

        // Andar (Left) & Bahar (Right) Piles & Bet Boxes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ANDAR Box
            val isAndarWinning = state.winningSide == AndarBaharSide.ANDAR
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        if (isAndarWinning) 2.5.dp else 1.dp,
                        if (isAndarWinning) CasinoGold else Color(0xFF0077B6),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = state.phase != AndarBaharPhase.DEALING) {
                        onPlaceBetAndar(selectedBet)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isAndarWinning) Color(0xFF0F3D24) else Color(0xFF08202D)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (language == "BN") "আন্দর (ANDAR)" else "ANDAR",
                            color = Color(0xFF48CAE4),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "1.95x Payout",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    // Andar Cards Pile (Horizontal Scroll)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.andarCards) { card ->
                            val isMatch = state.jokerCard?.rank == card.rank
                            PlayingCardView(
                                card = card,
                                isFaceUp = true,
                                width = 42.dp,
                                height = 62.dp,
                                modifier = if (isMatch) Modifier.border(2.dp, CasinoGold, RoundedCornerShape(8.dp)) else Modifier
                            )
                        }
                    }

                    // Bet placed on Andar badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A2E3F))
                            .border(1.dp, Color(0xFF0096C7), RoundedCornerShape(8.dp))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "বাজি: ৳${NumberFormat.getNumberInstance(Locale.US).format(state.andarBet)}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // BAHAR Box
            val isBaharWinning = state.winningSide == AndarBaharSide.BAHAR
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        if (isBaharWinning) 2.5.dp else 1.dp,
                        if (isBaharWinning) CasinoGold else CasinoCrimson,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = state.phase != AndarBaharPhase.DEALING) {
                        onPlaceBetBahar(selectedBet)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isBaharWinning) Color(0xFF0F3D24) else Color(0xFF2C0B12)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (language == "BN") "বাহার (BAHAR)" else "BAHAR",
                            color = CasinoCrimson,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "2.00x Payout",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    // Bahar Cards Pile
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.baharCards) { card ->
                            val isMatch = state.jokerCard?.rank == card.rank
                            PlayingCardView(
                                card = card,
                                isFaceUp = true,
                                width = 42.dp,
                                height = 62.dp,
                                modifier = if (isMatch) Modifier.border(2.dp, CasinoGold, RoundedCornerShape(8.dp)) else Modifier
                            )
                        }
                    }

                    // Bet placed on Bahar badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3F0A15))
                            .border(1.dp, CasinoCrimson, RoundedCornerShape(8.dp))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "বাজি: ৳${NumberFormat.getNumberInstance(Locale.US).format(state.baharBet)}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Chip Selector
        ChipSelector(
            selectedAmount = selectedBet,
            onSelectAmount = { selectedBet = it },
            userBalance = balance,
            onDouble = {
                val doubled = selectedBet * 2
                if (doubled <= balance) selectedBet = doubled
            },
            onHalf = {
                selectedBet = (selectedBet / 2).coerceAtLeast(100L)
            },
            onMax = {
                selectedBet = (balance / 100 * 100).coerceAtLeast(100L).coerceAtMost(50000L)
            }
        )

        // Deal Button
        val totalBetPlaced = state.andarBet + state.baharBet
        val canDeal = (totalBetPlaced > 0 || balance >= selectedBet) && state.phase != AndarBaharPhase.DEALING

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (totalBetPlaced > 0 && state.phase != AndarBaharPhase.DEALING) {
                Button(
                    onClick = onClearBets,
                    modifier = Modifier
                        .testTag("ab_clear_btn")
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3A1C1C),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (language == "BN") "মুছুন" else "Clear", fontSize = 13.sp)
                }
            }

            Button(
                onClick = onDealRound,
                enabled = canDeal,
                modifier = Modifier
                    .testTag("ab_deal_btn")
                    .weight(2f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CasinoGold,
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xFF1B3B30),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (state.phase == AndarBaharPhase.DEALING)
                        if (language == "BN") "কার্ড বণ্টন হচ্ছে..." else "Dealing Cards..."
                    else if (totalBetPlaced > 0)
                        if (language == "BN") "শুরু করুন (Deal Cards)" else "Deal Cards"
                    else
                        if (language == "BN") "আন্দর বা বাহারে বাজি ধরুন" else "Bet on Andar or Bahar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
