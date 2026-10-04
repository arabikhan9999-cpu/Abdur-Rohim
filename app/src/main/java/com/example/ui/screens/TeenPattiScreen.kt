package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.CardDeck
import com.example.model.PlayerStatus
import com.example.model.Strings
import com.example.model.TeenPattiPhase
import com.example.model.TeenPattiState
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.PlayingCardView
import com.example.ui.theme.CasinoCrimson
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldCard
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TeenPattiScreen(
    state: TeenPattiState,
    balance: Long,
    language: String,
    onStartGame: (Long) -> Unit,
    onChaal: () -> Unit,
    onPack: () -> Unit,
    onShow: () -> Unit,
    onSeeCards: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedBoot by remember { mutableStateOf(500L) }
    var showWinDialog by remember { mutableStateOf(false) }

    val humanPlayer = state.players.firstOrNull { it.isHuman }
    val bot1 = state.players.getOrNull(0)?.takeIf { !it.isHuman } ?: state.players.getOrNull(1)
    val bot2 = state.players.getOrNull(2)

    val isHumanWinner = state.phase == TeenPattiPhase.ROUND_OVER && state.winnerName.contains("You")

    if (isHumanWinner && !showWinDialog && state.winningPayout > 0) {
        showWinDialog = true
    }

    if (showWinDialog && state.winningPayout > 0) {
        CelebrationDialog(
            amount = state.winningPayout,
            titleBn = "তিন পাত্তি বিজয়!",
            titleEn = "TEEN PATTI VICTORY!",
            isJackpot = state.winningHandRank.contains("Trail") || state.winningHandRank.contains("Pure"),
            language = language,
            onDismiss = { showWinDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoEmeraldDark)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Table Top: Bot Opponents
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bot 1 (Left)
            if (bot1 != null) {
                OpponentView(
                    name = if (language == "BN") bot1.nameBn else bot1.nameEn,
                    emoji = bot1.avatarEmoji,
                    isPacked = bot1.status == PlayerStatus.PACKED,
                    isSeen = bot1.isSeen,
                    cards = bot1.cards,
                    revealCards = state.phase == TeenPattiPhase.ROUND_OVER
                )
            }

            // Pot Center Display
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF2E2004), Color(0xFF0F2618))
                        )
                    )
                    .border(1.5.dp, CasinoGold, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (language == "BN") "মোট পট (Pot)" else "Total Pot",
                        color = CasinoGoldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "৳${NumberFormat.getNumberInstance(Locale.US).format(state.pot)}",
                        color = CasinoGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Bot 2 (Right)
            if (bot2 != null) {
                OpponentView(
                    name = if (language == "BN") bot2.nameBn else bot2.nameEn,
                    emoji = bot2.avatarEmoji,
                    isPacked = bot2.status == PlayerStatus.PACKED,
                    isSeen = bot2.isSeen,
                    cards = bot2.cards,
                    revealCards = state.phase == TeenPattiPhase.ROUND_OVER
                )
            }
        }

        // Center Table Felt Commentary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF072418)),
            border = androidx.compose.foundation.BorderStroke(1.dp, CasinoEmeraldBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (state.roundMessage.isNotEmpty()) state.roundMessage
                    else if (language == "BN") "রয়্যাল ৩-কার্ড টেবিলে স্বাগতম!"
                    else "Welcome to Royal 3-Card Table!",
                    color = CasinoGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                if (state.phase == TeenPattiPhase.ROUND_OVER && state.winningHandRank.isNotEmpty()) {
                    Text(
                        text = state.winningHandRank,
                        color = Color(0xFF00F5D4),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Human Player Section (Bottom)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (humanPlayer != null) {
                // Hand rank evaluation hint
                if (humanPlayer.isSeen && humanPlayer.cards.size == 3) {
                    val handResult = remember(humanPlayer.cards) {
                        CardDeck.evaluateTeenPatti(humanPlayer.cards)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1D382B))
                            .border(1.dp, CasinoGold, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (language == "BN") handResult.type.nameBn else handResult.type.nameEn,
                            color = CasinoGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Player's 3 Cards
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    humanPlayer.cards.forEach { card ->
                        PlayingCardView(
                            card = card,
                            isFaceUp = humanPlayer.isSeen || state.phase == TeenPattiPhase.ROUND_OVER,
                            width = 68.dp,
                            height = 100.dp
                        )
                    }
                }

                // "See Cards" toggle if playing blind
                if (!humanPlayer.isSeen && state.phase == TeenPattiPhase.PLAYING && humanPlayer.status == PlayerStatus.ACTIVE) {
                    Button(
                        onClick = onSeeCards,
                        modifier = Modifier.testTag("tp_see_cards_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1F4A37),
                            contentColor = CasinoGold
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = Strings.seeCards(language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Action Controls (Chaal, Pack, Show OR New Game Deal)
        if (state.phase == TeenPattiPhase.WAITING_FOR_DEAL || state.phase == TeenPattiPhase.ROUND_OVER) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Boot selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == "BN") "বুট বাজি (Boot Bet):" else "Boot Bet:",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(200L, 500L, 1000L, 2500L, 5000L).forEach { amt ->
                            val isSel = selectedBoot == amt
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CasinoGold else CasinoEmeraldCard)
                                    .border(1.dp, CasinoEmeraldBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedBoot = amt }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "৳$amt",
                                    color = if (isSel) Color.Black else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { onStartGame(selectedBoot) },
                    enabled = balance >= selectedBoot,
                    modifier = Modifier
                        .testTag("tp_deal_btn")
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CasinoGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (language == "BN")
                            "নতুন হাত শুরু করুন (Deal ৳$selectedBoot)"
                        else
                            "Deal New Hand (৳$selectedBoot)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        } else {
            // In-Game Play Actions (Pack, Chaal, Show)
            val isMyTurn = state.currentTurnIndex == state.players.indexOfFirst { it.isHuman }
            val activePlayersCount = state.players.count { it.status == PlayerStatus.ACTIVE }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pack button
                Button(
                    onClick = onPack,
                    enabled = isMyTurn,
                    modifier = Modifier
                        .testTag("tp_pack_btn")
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CasinoCrimson,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = Strings.pack(language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Show button (only if 2 players remain)
                if (activePlayersCount <= 2) {
                    Button(
                        onClick = onShow,
                        enabled = isMyTurn && balance >= state.currentChaalAmount,
                        modifier = Modifier
                            .testTag("tp_show_btn")
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00B4D8),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = Strings.show(language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Chaal button
                val chaalCost = if (humanPlayer?.isSeen == true) state.currentChaalAmount * 2 else state.currentChaalAmount
                Button(
                    onClick = onChaal,
                    enabled = isMyTurn && balance >= chaalCost,
                    modifier = Modifier
                        .testTag("tp_chaal_btn")
                        .weight(1.5f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CasinoGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${Strings.chaal(language)} ৳$chaalCost",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun OpponentView(
    name: String,
    emoji: String,
    isPacked: Boolean,
    isSeen: Boolean,
    cards: List<com.example.model.Card>,
    revealCards: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Text(
                text = name,
                color = if (isPacked) Color.Gray else TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            if (isPacked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CasinoCrimson)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(text = "PACK", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isSeen) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E4633))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(text = "SEEN", color = CasinoGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Opponent cards (mini)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            cards.forEach { card ->
                PlayingCardView(
                    card = card,
                    isFaceUp = revealCards,
                    width = 38.dp,
                    height = 54.dp
                )
            }
        }
    }
}
