package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VipTier
import com.example.ui.theme.CasinoCrimson
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldCard
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldDark
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.TextPrimary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CasinoTopBar(
    title: String,
    balance: Long,
    vipLevel: Int,
    language: String,
    soundEnabled: Boolean,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    onVipClick: () -> Unit = {},
    onLanguageToggle: () -> Unit = {},
    onSoundToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val vipTier = VipTier.fromLevel(vipLevel)
    val formattedBalance = NumberFormat.getNumberInstance(Locale.US).format(balance)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF030D08),
                        CasinoEmeraldDark
                    )
                )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left section: Back button + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (showBackButton) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .testTag("topbar_back_button")
                            .size(38.dp)
                            .background(CasinoEmeraldCard, CircleShape)
                            .border(1.dp, CasinoEmeraldBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CasinoGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Column {
                    Text(
                        text = title,
                        color = CasinoGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (showBackButton) 16.sp else 18.sp,
                        maxLines = 1
                    )
                    Text(
                        text = if (language == "BN") "ঢাকা রয়্যাল ক্লাব" else "Dhaka Royale Club",
                        color = Color(0xFF6B937C),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Right section: VIP Badge + Balance Pill + Toggles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // VIP Pill
                Box(
                    modifier = Modifier
                        .testTag("topbar_vip_button")
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF261D09), Color(0xFF3B2E10))
                            )
                        )
                        .border(1.dp, CasinoGoldDark, RoundedCornerShape(14.dp))
                        .clickable { onVipClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = vipTier.badgeEmoji, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VIP ${vipTier.level}",
                            color = CasinoGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Balance Pill
                Box(
                    modifier = Modifier
                        .testTag("topbar_wallet_button")
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(CasinoEmeraldCard, Color(0xFF163E2B))
                            )
                        )
                        .border(1.2.dp, CasinoGold, RoundedCornerShape(16.dp))
                        .clickable { onWalletClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Taka symbol badge
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(CasinoGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "৳",
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Animated balance text
                        AnimatedContent(
                            targetState = formattedBalance,
                            transitionSpec = {
                                slideInVertically { height -> height } togetherWith
                                        slideOutVertically { height -> -height }
                            },
                            label = "balance_anim"
                        ) { targetText ->
                            Text(
                                text = targetText,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Plus button icon
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color(0xFF2E7D32), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add BDT",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Language toggle pill
                Box(
                    modifier = Modifier
                        .testTag("topbar_lang_button")
                        .clip(CircleShape)
                        .background(CasinoEmeraldCard)
                        .border(1.dp, CasinoEmeraldBorder, CircleShape)
                        .clickable { onLanguageToggle() }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = language,
                        color = CasinoGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Sound toggle
                IconButton(
                    onClick = onSoundToggle,
                    modifier = Modifier
                        .testTag("topbar_sound_button")
                        .size(32.dp)
                        .background(CasinoEmeraldCard, CircleShape)
                        .border(1.dp, CasinoEmeraldBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Sound toggle",
                        tint = if (soundEnabled) CasinoGold else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
