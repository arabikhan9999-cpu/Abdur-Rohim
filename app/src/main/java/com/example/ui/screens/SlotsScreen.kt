package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.SlotSymbol
import com.example.model.SlotsState
import com.example.model.Strings
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.ChipSelector
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldCard
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.CasinoPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SlotsScreen(
    state: SlotsState,
    balance: Long,
    language: String,
    onSpin: (Long) -> Unit,
    onToggleAutoSpin: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedBet by remember { mutableStateOf(1000L) }
    var showPaytable by remember { mutableStateOf(false) }
    var showWinDialog by remember { mutableStateOf(false) }

    if (state.lastWin > 0 && !state.isSpinning && !showWinDialog) {
        showWinDialog = true
    }

    if (showWinDialog && state.lastWin > 0) {
        CelebrationDialog(
            amount = state.lastWin,
            titleBn = if (state.isJackpot) "মেগা জ্যাকপট জয়!" else "স্লট বিগ উইন!",
            titleEn = if (state.isJackpot) "MEGA JACKPOT!" else "SLOTS BIG WIN!",
            isJackpot = state.isJackpot,
            language = language,
            onDismiss = { showWinDialog = false }
        )
    }

    if (showPaytable) {
        PaytableDialog(language = language, onDismiss = { showPaytable = false })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoEmeraldDark)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Jackpot Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF281804)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CasinoGold)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👑", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (language == "BN") "রয়্যাল টাইগার জ্যাকপট" else "ROYAL TIGER JACKPOT",
                            color = CasinoGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "৳২,৫০,০০০",
                            color = CasinoGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                IconButton(
                    onClick = { showPaytable = true },
                    modifier = Modifier.testTag("slots_paytable_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Paytable",
                        tint = CasinoGold
                    )
                }
            }
        }

        // Slot Machine Reels Box (3x3 grid)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF241505), Color(0xFF071911))
                    )
                )
                .border(2.dp, CasinoGold, RoundedCornerShape(20.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.grid.forEachIndexed { rowIndex, rowSymbols ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowSymbols.forEachIndexed { colIndex, symbol ->
                            SlotCell(
                                symbol = symbol,
                                isSpinning = state.isSpinning,
                                isWinning = state.winningLines.any { line ->
                                    (line.lineIndex == rowIndex) ||
                                            (line.lineIndex == 3 && rowIndex == colIndex) ||
                                            (line.lineIndex == 4 && rowIndex == 2 - colIndex)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Winning notification bar
        if (state.lastWin > 0 && !state.isSpinning) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E462E))
                    .border(1.dp, CasinoGold, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (language == "BN")
                        "বিজয়: +৳${NumberFormat.getNumberInstance(Locale.US).format(state.lastWin)}!"
                    else
                        "WIN: +৳${NumberFormat.getNumberInstance(Locale.US).format(state.lastWin)}!",
                    color = CasinoGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Chip Selector for Bet per Spin
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

        // Spin Buttons (Auto & Manual Spin)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onToggleAutoSpin,
                modifier = Modifier
                    .testTag("slots_auto_btn")
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.autoSpin) CasinoPurple else Color(0xFF1E3827),
                    contentColor = if (state.autoSpin) Color.White else CasinoGold
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (state.autoSpin)
                        if (language == "BN") "অটো বন্ধ" else "Stop Auto"
                    else
                        if (language == "BN") "অটো স্পিন" else "Auto Spin",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { onSpin(selectedBet) },
                enabled = !state.isSpinning && balance >= selectedBet,
                modifier = Modifier
                    .testTag("slots_spin_btn")
                    .weight(2f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CasinoGold,
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xFF1B3B30),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Spin",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (state.isSpinning)
                            if (language == "BN") "ঘুরছে..." else "Spinning..."
                        else
                            "${Strings.spin(language)} (৳${NumberFormat.getNumberInstance(Locale.US).format(selectedBet)})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun SlotCell(
    symbol: SlotSymbol,
    isSpinning: Boolean,
    isWinning: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "reel_anim")
    val spinOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spin_float"
    )

    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    if (isWinning)
                        listOf(Color(0xFF3B2702), Color(0xFF163821))
                    else
                        listOf(Color(0xFF0F261A), Color(0xFF06140D))
                )
            )
            .border(
                width = if (isWinning) 2.5.dp else 1.dp,
                color = if (isWinning) CasinoGold else CasinoEmeraldBorder,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = symbol.emoji,
                fontSize = 32.sp,
                modifier = if (isSpinning) Modifier.scale(0.85f + spinOffset * 0.25f) else Modifier
            )
            Text(
                text = symbol.displayName,
                color = if (isWinning) CasinoGoldLight else TextSecondary,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PaytableDialog(language: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoEmeraldDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CasinoGold),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (language == "BN") "স্লট পে-টেবিল ও পুরস্কার" else "Slots Paytable & Multipliers",
                    color = CasinoGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                SlotSymbol.values().forEach { sym ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = sym.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = sym.displayName, color = TextPrimary, fontSize = 13.sp)
                        }
                        Text(
                            text = "3x = ${sym.multiplier3x}x বাজি",
                            color = CasinoGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CasinoGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (language == "BN") "ঠিক আছে" else "Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
