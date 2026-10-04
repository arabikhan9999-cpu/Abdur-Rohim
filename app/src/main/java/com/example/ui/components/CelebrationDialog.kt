package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.TextPrimary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CelebrationDialog(
    amount: Long,
    titleBn: String = "অসাধারণ জয়!",
    titleEn: String = "BIG WIN!",
    isJackpot: Boolean = false,
    language: String = "BN",
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(amount)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .border(2.dp, CasinoGold, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoEmeraldDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF241604), CasinoEmeraldDark)
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Crown / Trophy emoji with animated scale
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(CasinoGold, Color(0xFFB8860B))
                            )
                        )
                        .border(2.dp, CasinoGoldLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isJackpot) "👑" else "🏆",
                        fontSize = 44.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (language == "BN") {
                        if (isJackpot) "মেগা জ্যাকপট জয়!" else titleBn
                    } else {
                        if (isJackpot) "MEGA JACKPOT!" else titleEn
                    },
                    color = CasinoGoldLight,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = if (language == "BN") "অভিনন্দন! আপনার মোট লাভ" else "Congratulations! Total Won",
                    color = Color(0xFF9EBFAC),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Payout Display
                Text(
                    text = "+৳$formattedAmount",
                    color = CasinoGold,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .testTag("collect_win_button")
                        .fillMaxWidth(0.8f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CasinoGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (language == "BN") "সংগ্রহ করুন (Collect)" else "Collect Coins",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
