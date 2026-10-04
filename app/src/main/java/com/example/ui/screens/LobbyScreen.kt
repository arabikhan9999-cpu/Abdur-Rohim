package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserWalletEntity
import com.example.model.Strings
import com.example.model.VipTier
import com.example.ui.theme.CasinoCrimson
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldCard
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoEmeraldMedium
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldDark
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.CasinoNeonCyan
import com.example.ui.theme.CasinoPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LobbyScreen(
    wallet: UserWalletEntity,
    canClaimDaily: Boolean,
    onClaimDaily: () -> Unit,
    onNavigateToCrash: () -> Unit,
    onNavigateToTeenPatti: () -> Unit,
    onNavigateToAndarBahar: () -> Unit,
    onNavigateToSlots: () -> Unit,
    onNavigateToWheel: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToVip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = wallet.language
    val vipTier = VipTier.fromLevel(wallet.level)
    val nextTier = VipTier.values().firstOrNull { it.level == wallet.level + 1 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoEmeraldDark)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, CasinoGoldDark, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CasinoEmeraldMedium)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.bg_casino_hero),
                        contentDescription = "Dhaka Royale Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xCC05130D))
                                )
                            )
                    )

                    // Text overlay on banner
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (lang == "BN") "👑 ঢাকা রয়্যাল ক্লাব" else "👑 DHAKA ROYALE CLUB",
                                color = CasinoGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CasinoCrimson)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "BDT ৳",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = if (lang == "BN") "খাঁটি বাংলাদেশি বিনোদন ক্যাসিনো • ফ্রি কয়েন ও জ্যাকপট"
                            else "Premier Bangladeshi Social Casino • 100% Free Virtual Fun",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Live Winner Ticker Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F2B1D))
                    .border(1.dp, CasinoEmeraldBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🔥", fontSize = 14.sp)
                    Text(
                        text = if (lang == "BN")
                            "লাইভ জয়: শাকিব ৳১,২০,০০০ জিতেছেন ক্র্যাশে! • তানভীর ৳৪৫,০০০ আন্দর বাহারে!"
                        else
                            "Recent Winners: Shakib won ৳120,000 on Crash! • Tanvir won ৳45,000 on Andar Bahar!",
                        color = CasinoGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }

        // Daily Bonus & VIP Progress Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Daily Gift Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, CasinoGoldDark, RoundedCornerShape(16.dp))
                        .clickable(enabled = canClaimDaily) { onClaimDaily() },
                    colors = CardDefaults.cardColors(
                        containerColor = if (canClaimDaily) Color(0xFF1E3827) else CasinoEmeraldCard
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = Strings.dailyBonus(lang),
                                color = CasinoGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Daily Gift",
                                tint = if (canClaimDaily) CasinoGold else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        val bonusAmt = 10000L + (wallet.level * 2500L)
                        Text(
                            text = "+৳${NumberFormat.getNumberInstance(Locale.US).format(bonusAmt)}",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Button(
                            onClick = onClaimDaily,
                            enabled = canClaimDaily,
                            modifier = Modifier
                                .testTag("lobby_daily_claim_button")
                                .fillMaxWidth()
                                .height(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CasinoGold,
                                contentColor = Color.Black,
                                disabledContainerColor = Color(0xFF233B2E),
                                disabledContentColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (canClaimDaily) Strings.claim(lang) else Strings.claimed(lang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // VIP Nawab Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, CasinoEmeraldBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToVip() },
                    colors = CardDefaults.cardColors(containerColor = CasinoEmeraldCard)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (lang == "BN") "নবাব স্ট্যাটাস" else "VIP Status",
                                color = CasinoGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = vipTier.badgeEmoji, fontSize = 16.sp)
                        }

                        Text(
                            text = if (lang == "BN") vipTier.nameBn else vipTier.nameEn,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        val nextPoints = nextTier?.minPoints ?: 10000
                        val progress = (wallet.vipPoints.toFloat() / nextPoints).coerceIn(0f, 1f)

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CasinoGold,
                            trackColor = Color(0xFF1E3827)
                        )

                        Text(
                            text = "${wallet.vipPoints} / $nextPoints XP",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Section Title: Featured Games
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lang == "BN") "জনপ্রিয় গেমস (Featured Games)" else "Featured Games",
                    color = CasinoGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (lang == "BN") "৫টি গেম চালু" else "5 Games Live",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // 1. Crash / Aviator Card (Primary spotlight)
        item {
            GameCard(
                title = Strings.crashTitle(lang),
                subtitle = Strings.crashSubtitle(lang),
                tag = "HOT • 100x+",
                tagColor = CasinoCrimson,
                iconEmoji = "🚀",
                badgeColor = Color(0xFF8B0000),
                accentColor = CasinoCrimson,
                testTag = "game_card_crash",
                onClick = onNavigateToCrash
            )
        }

        // 2. Teen Patti Card
        item {
            GameCard(
                title = Strings.teenPattiTitle(lang),
                subtitle = Strings.teenPattiSubtitle(lang),
                tag = if (lang == "BN") "রয়্যাল ৩-কার্ড" else "Royal 3-Card",
                tagColor = CasinoGold,
                iconEmoji = "🎴",
                badgeColor = Color(0xFF3D2C04),
                accentColor = CasinoGold,
                testTag = "game_card_teen_patti",
                onClick = onNavigateToTeenPatti
            )
        }

        // 3. Andar Bahar Card
        item {
            GameCard(
                title = Strings.andarBaharTitle(lang),
                subtitle = Strings.andarBaharSubtitle(lang),
                tag = "FAST 50:50",
                tagColor = CasinoNeonCyan,
                iconEmoji = "🃏",
                badgeColor = Color(0xFF07332C),
                accentColor = CasinoNeonCyan,
                testTag = "game_card_andar_bahar",
                onClick = onNavigateToAndarBahar
            )
        }

        // 4. Dhaka 777 Slots Card
        item {
            GameCard(
                title = Strings.slotsTitle(lang),
                subtitle = Strings.slotsSubtitle(lang),
                tag = "JACKPOT",
                tagColor = CasinoPurple,
                iconEmoji = "🎰",
                badgeColor = Color(0xFF2F0B4A),
                accentColor = CasinoPurple,
                testTag = "game_card_slots",
                onClick = onNavigateToSlots
            )
        }

        // 5. Lucky Wheel Card
        item {
            GameCard(
                title = Strings.wheelTitle(lang),
                subtitle = Strings.wheelSubtitle(lang),
                tag = if (lang == "BN") "দৈনিক স্পিন" else "Daily Spin",
                tagColor = Color(0xFFFFB703),
                iconEmoji = "🎡",
                badgeColor = Color(0xFF382602),
                accentColor = Color(0xFFFFB703),
                testTag = "game_card_wheel",
                onClick = onNavigateToWheel
            )
        }

        // Disclaimer Banner at bottom
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF081810))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Disclaimer",
                        tint = CasinoGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = Strings.disclaimer(lang),
                        color = Color(0xFF8AA396),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun GameCard(
    title: String,
    subtitle: String,
    tag: String,
    tagColor: Color,
    iconEmoji: String,
    badgeColor: Color,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, CasinoEmeraldBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CasinoEmeraldCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Game Emoji Icon with glowing background
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(badgeColor)
                        .border(1.2.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconEmoji, fontSize = 28.sp)
                }

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(tagColor.copy(alpha = 0.2f))
                                .border(0.8.dp, tagColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                color = tagColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Text(
                        text = subtitle,
                        color = Color(0xFF8AA396),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Play",
                tint = CasinoGold,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
