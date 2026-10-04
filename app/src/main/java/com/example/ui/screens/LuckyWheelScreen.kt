package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CelebrationDialog
import com.example.ui.theme.CasinoCrimson
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.random.Random

data class WheelPrize(
    val label: String,
    val amount: Long,
    val color: Color,
    val isMystery: Boolean = false
)

val WHEEL_PRIZES = listOf(
    WheelPrize("৳500", 500L, Color(0xFF1E382B)),
    WheelPrize("৳1,000", 1000L, Color(0xFF8B263E)),
    WheelPrize("৳2,500", 2500L, Color(0xFF0F4C5C)),
    WheelPrize("৳5,000", 5000L, Color(0xFF5E2B8C)),
    WheelPrize("৳10,000", 10000L, Color(0xFFB8860B)),
    WheelPrize("৳25,000", 25000L, Color(0xFF0D5C3A)),
    WheelPrize("৳50,000", 50000L, Color(0xFFD4AF37)),
    WheelPrize("🎁 নবাব বক্স", 35000L, Color(0xFF9E2A2B), isMystery = true)
)

@Composable
fun LuckyWheelScreen(
    balance: Long,
    language: String,
    onSpinReward: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val coroutineScope = rememberCoroutineScope()
    val rotationAnim = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonPrize by remember { mutableStateOf<WheelPrize?>(null) }
    var showWinDialog by remember { mutableStateOf(false) }

    val spinCost = 1000L

    fun spinWheel() {
        if (isSpinning || balance < spinCost) return
        isSpinning = true
        wonPrize = null

        val winningIndex = Random.nextInt(WHEEL_PRIZES.size)
        val selectedPrize = WHEEL_PRIZES[winningIndex]
        val sectorAngle = 360f / WHEEL_PRIZES.size
        // Indicator is at top (270 degrees). Target stop angle
        val targetSectorCenter = winningIndex * sectorAngle + sectorAngle / 2f
        val fullRotations = (5 + Random.nextInt(3)) * 360f
        val targetAngle = fullRotations + (360f - targetSectorCenter + 270f) % 360f

        coroutineScope.launch {
            rotationAnim.snapTo(rotationAnim.value % 360f)
            rotationAnim.animateTo(
                targetValue = rotationAnim.value + fullRotations + 360f - (rotationAnim.value % 360f) + (360f - targetSectorCenter + 270f) % 360f,
                animationSpec = tween(
                    durationMillis = 4000,
                    easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
                )
            )
            isSpinning = false
            wonPrize = selectedPrize
            showWinDialog = true
            onSpinReward(selectedPrize.amount)
        }
    }

    if (showWinDialog && wonPrize != null) {
        CelebrationDialog(
            amount = wonPrize!!.amount,
            titleBn = if (wonPrize!!.isMystery) "নবাব মিস্ট্রি বক্স আনলক!" else "ভাগ্য চাকা বিজয়!",
            titleEn = if (wonPrize!!.isMystery) "MYSTERY BOX UNLOCKED!" else "LUCKY WHEEL WIN!",
            isJackpot = wonPrize!!.amount >= 25000L,
            language = language,
            onDismiss = { showWinDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoEmeraldDark)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Title Banner
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (language == "BN") "🎡 ভাগ্য চাকা (Lucky Fortune Wheel)" else "🎡 Lucky Fortune Wheel",
                color = CasinoGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = if (language == "BN") "প্রতি স্পিনে জিতুন ৳৫০,০০০ পর্যন্ত জ্যাকপট!" else "Win up to ৳50,000 every single spin!",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Wheel Visual Container
        Box(
            modifier = Modifier
                .size(290.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background gold rim shadow
            Box(
                modifier = Modifier
                    .size(285.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF382404))
                    .border(4.dp, CasinoGold, CircleShape)
            )

            // Canvas Wheel
            Canvas(modifier = Modifier.size(265.dp)) {
                val radius = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                val sectorAngle = 360f / WHEEL_PRIZES.size

                rotate(rotationAnim.value, pivot = center) {
                    WHEEL_PRIZES.forEachIndexed { i, prize ->
                        val startAngle = i * sectorAngle
                        drawArc(
                            color = prize.color,
                            startAngle = startAngle,
                            sweepAngle = sectorAngle,
                            useCenter = true,
                            size = size
                        )
                        // Sector border
                        drawArc(
                            color = Color(0x55FFD700),
                            startAngle = startAngle,
                            sweepAngle = sectorAngle,
                            useCenter = true,
                            size = size,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Draw labels with Android Native Canvas
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 28f
                        isFakeBoldText = true
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }

                    WHEEL_PRIZES.forEachIndexed { i, prize ->
                        val angle = Math.toRadians((i * sectorAngle + sectorAngle / 2f).toDouble())
                        val textRadius = radius * 0.78f
                        val x = (center.x + textRadius * Math.cos(angle)).toFloat()
                        val y = (center.y + textRadius * Math.sin(angle)).toFloat()

                        drawContext.canvas.nativeCanvas.save()
                        drawContext.canvas.nativeCanvas.rotate(
                            (i * sectorAngle + sectorAngle / 2f).toFloat(),
                            x,
                            y
                        )
                        drawContext.canvas.nativeCanvas.drawText(prize.label, x, y + 10f, paint)
                        drawContext.canvas.nativeCanvas.restore()
                    }
                }

                // Inner gold hub
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(CasinoGoldLight, Color(0xFF9E7806)),
                        center = center,
                        radius = 24.dp.toPx()
                    ),
                    radius = 24.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = Color.White,
                    radius = 8.dp.toPx(),
                    center = center
                )
            }

            // Top Pointer Needle
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(24.dp, 28.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(size.width / 2f, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(path, color = CasinoCrimson)
                    drawPath(path, color = Color.White, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
                }
            }
        }

        // Spin Button Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B1D)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CasinoEmeraldBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (language == "BN") "স্পিন ফি: ৳১,০০০" else "Spin Fee: ৳1,000",
                    color = CasinoGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { spinWheel() },
                    enabled = !isSpinning && balance >= spinCost,
                    modifier = Modifier
                        .testTag("wheel_spin_button")
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CasinoGold,
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFF1B3B30),
                        disabledContentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (isSpinning)
                            if (language == "BN") "চাকা ঘুরছে..." else "Spinning Wheel..."
                        else
                            if (language == "BN") "ভাগ্য চাকা ঘুরান (SPIN)" else "SPIN FORTUNE WHEEL",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
