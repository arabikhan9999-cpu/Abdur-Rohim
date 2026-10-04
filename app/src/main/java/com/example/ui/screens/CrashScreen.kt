package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrashState
import com.example.model.CrashStatus
import com.example.model.Strings
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.ChipSelector
import com.example.ui.theme.CasinoCrimson
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldCard
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.CasinoNeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

@Composable
fun CrashScreen(
    state: CrashState,
    balance: Long,
    language: String,
    onStartRound: (Long, Float) -> Unit,
    onCashOut: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedBet by remember { mutableStateOf(1000L) }
    var autoCashOutEnabled by remember { mutableStateOf(false) }
    var autoCashOutValue by remember { mutableFloatStateOf(2.0f) }
    var showWinDialog by remember { mutableStateOf(false) }

    // Check win dialog trigger
    if (state.hasCashedOut && state.profit > 0 && !showWinDialog) {
        showWinDialog = true
    }

    if (showWinDialog && state.profit > 0) {
        CelebrationDialog(
            amount = state.profit,
            titleBn = "ক্যাশআউট সফল!",
            titleEn = "CASHED OUT!",
            isJackpot = state.cashedOutMultiplier >= 10f,
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
        // Multiplier History Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (language == "BN") "ইতিহাস:" else "History:",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            state.history.takeLast(10).reversed().forEach { mult ->
                val pillColor = when {
                    mult >= 10.0f -> CasinoGold
                    mult >= 2.0f -> CasinoNeonCyan
                    else -> Color(0xFFE56B6F)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F261B))
                        .border(1.dp, pillColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.2fx", mult),
                        color = pillColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Flight Arena Canvas Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, CasinoEmeraldBorder, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF061810))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Rocket flight curve drawing
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid lines
                    for (i in 1..4) {
                        val y = h * (i / 5f)
                        drawLine(
                            color = Color(0x15FFFFFF),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    if (state.status == CrashStatus.FLYING || state.status == CrashStatus.CRASHED) {
                        val progress = min(1f, (state.currentMultiplier - 1.0f) / 10f)
                        val endX = 40f + progress * (w - 120f)
                        val endY = h - 30f - progress * (h - 70f)

                        val path = Path().apply {
                            moveTo(30f, h - 30f)
                            quadraticTo(
                                30f + (endX - 30f) * 0.4f,
                                h - 30f,
                                endX,
                                endY
                            )
                        }

                        // Gradient fill under curve
                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(endX, h - 30f)
                            lineTo(30f, h - 30f)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                listOf(
                                    if (state.status == CrashStatus.CRASHED)
                                        CasinoCrimson.copy(alpha = 0.25f)
                                    else
                                        CasinoGold.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )

                        // Flight stroke
                        drawPath(
                            path = path,
                            color = if (state.status == CrashStatus.CRASHED) CasinoCrimson else CasinoGold,
                            style = Stroke(width = 4.dp.toPx())
                        )

                        // Rocket glow point
                        drawCircle(
                            color = if (state.status == CrashStatus.CRASHED) CasinoCrimson else Color(0xFFFFE680),
                            radius = 9.dp.toPx(),
                            center = Offset(endX, endY)
                        )
                    }
                }

                // Center Multiplier Overlay
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (state.status) {
                        CrashStatus.FLYING -> {
                            Text(
                                text = String.format(Locale.US, "%.2fx", state.currentMultiplier),
                                color = CasinoGoldLight,
                                fontSize = 46.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (language == "BN") "রকেট উড়ছে..." else "Flying High...",
                                color = CasinoNeonCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        CrashStatus.CRASHED -> {
                            Text(
                                text = String.format(Locale.US, "%.2fx", state.crashPoint),
                                color = CasinoCrimson,
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (language == "BN") "ক্র্যাশ হয়েছে! রকেট উড়ে গেছে!" else "CRASHED! FLEW AWAY!",
                                color = CasinoCrimson,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        CrashStatus.BETTING, CrashStatus.IDLE -> {
                            Text(
                                text = "🚀 1.00x",
                                color = CasinoGold,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (language == "BN") "বাজি ধরুন ও রকেটে চড়ুন!" else "Ready for Next Flight!",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Auto Cash Out Control Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CasinoEmeraldCard),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = autoCashOutEnabled,
                        onCheckedChange = { autoCashOutEnabled = it },
                        modifier = Modifier.testTag("crash_auto_cashout_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CasinoGold,
                            checkedTrackColor = Color(0xFF1D4D36)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "BN") "অটো ক্যাশআউট:" else "Auto Cash Out:",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (autoCashOutEnabled) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(1.5f, 2.0f, 3.0f, 5.0f).forEach { targetMult ->
                            val isSel = autoCashOutValue == targetMult
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CasinoGold else Color(0xFF143323))
                                    .border(
                                        1.dp,
                                        if (isSel) CasinoGoldLight else CasinoEmeraldBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { autoCashOutValue = targetMult }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${targetMult}x",
                                    color = if (isSel) Color.Black else CasinoGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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

        Spacer(modifier = Modifier.weight(1f))

        // Main Action Button (Place Bet vs Cash Out)
        val isFlying = state.status == CrashStatus.FLYING
        val canCashOut = isFlying && state.isBetActive && !state.hasCashedOut

        if (canCashOut) {
            val liveWin = (state.betAmount * state.currentMultiplier).toLong()
            Button(
                onClick = onCashOut,
                modifier = Modifier
                    .testTag("crash_cashout_button")
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CasinoGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (language == "BN")
                        "ক্যাশ আউট ৳${NumberFormat.getNumberInstance(Locale.US).format(liveWin)} (${String.format(Locale.US, "%.2fx", state.currentMultiplier)})"
                    else
                        "CASH OUT ৳${NumberFormat.getNumberInstance(Locale.US).format(liveWin)} (${String.format(Locale.US, "%.2fx", state.currentMultiplier)})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        } else {
            val isButtonEnabled = state.status != CrashStatus.FLYING && balance >= selectedBet
            Button(
                onClick = {
                    val autoMult = if (autoCashOutEnabled) autoCashOutValue else 0f
                    onStartRound(selectedBet, autoMult)
                },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .testTag("crash_place_bet_button")
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00B4D8),
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xFF1B3B30),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isFlying) {
                        if (state.hasCashedOut)
                            if (language == "BN") "ক্যাশআউট নেওয়া হয়েছে!" else "Cashed Out!"
                        else
                            if (language == "BN") "রাউন্ড চলছে..." else "Round in Progress..."
                    } else {
                        if (balance < selectedBet)
                            Strings.insufficientBalance(language)
                        else
                            "${Strings.placeBet(language)} ৳${NumberFormat.getNumberInstance(Locale.US).format(selectedBet)}"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
