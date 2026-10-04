package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserWalletEntity
import com.example.model.VipTier
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldCard
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldDark
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun VipScreen(
    wallet: UserWalletEntity,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val lang = wallet.language
    val currentTier = VipTier.fromLevel(wallet.level)
    val nextTier = VipTier.values().firstOrNull { it.level == wallet.level + 1 }
    val nextPoints = nextTier?.minPoints ?: 10000
    val progress = (wallet.vipPoints.toFloat() / nextPoints).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoEmeraldDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // VIP Crown Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261905)),
                border = androidx.compose.foundation.BorderStroke(2.dp, CasinoGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF382306), Color(0xFF140D02))
                            )
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(CasinoGold, CasinoGoldDark)
                                )
                            )
                            .border(2.dp, CasinoGoldLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = currentTier.badgeEmoji, fontSize = 40.sp)
                    }

                    Text(
                        text = if (lang == "BN") currentTier.nameBn else currentTier.nameEn,
                        color = CasinoGoldLight,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = if (lang == "BN") "লেভেল ${currentTier.level} ক্লাব সদস্য" else "Level ${currentTier.level} Club Member",
                        color = Color(0xFFC7B183),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // XP Progress bar
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${wallet.vipPoints} VIP XP",
                                color = CasinoGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (nextTier != null)
                                    "পরবর্তী: ${if (lang == "BN") nextTier.nameBn else nextTier.nameEn} ($nextPoints XP)"
                                else
                                    "সর্বোচ্চ স্তর অর্জিত!",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CasinoGold,
                            trackColor = Color(0xFF362812)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = if (lang == "BN") "👑 ভিআইপি স্তর ও বিশেষ সুবিধাসমূহ" else "👑 VIP Tiers & Exclusive Perks",
                color = CasinoGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Tiers list
        items(VipTier.values().toList()) { tier ->
            val isCurrent = tier.level == currentTier.level
            val isUnlocked = wallet.level >= tier.level

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        if (isCurrent) 2.dp else 1.dp,
                        if (isCurrent) CasinoGold else CasinoEmeraldBorder,
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) Color(0xFF1F3827) else CasinoEmeraldCard
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) Color(0xFF2C4434) else Color(0xFF1B2420)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = tier.badgeEmoji, fontSize = 24.sp)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (lang == "BN") tier.nameBn else tier.nameEn,
                                color = if (isCurrent) CasinoGold else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isCurrent) {
                                Spacer(modifier = Modifier.padding(2.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CasinoGold)
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = if (lang == "BN") "চলতি" else "Active",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (lang == "BN") tier.perkBn else tier.perkEn,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Text(
                            text = "দৈনিক বোনাস: ৳${NumberFormat.getNumberInstance(Locale.US).format(tier.dailyBonus)}",
                            color = CasinoGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
