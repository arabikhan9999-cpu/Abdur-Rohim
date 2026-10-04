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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.TransactionEntity
import com.example.data.local.UserWalletEntity
import com.example.model.Strings
import com.example.ui.theme.BkashPink
import com.example.ui.theme.CasinoCrimson
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldCard
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    wallet: UserWalletEntity,
    transactions: List<TransactionEntity>,
    onAddSimulatedReload: (String, Long, Long) -> Unit,
    onFaucetClaim: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val lang = wallet.language
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Top-up, 1: History
    var selectedChannel by remember { mutableStateOf("bKash") }
    var showReloadSuccess by remember { mutableStateOf(false) }
    var lastAddedAmount by remember { mutableStateOf(0L) }

    val formattedBalance = NumberFormat.getNumberInstance(Locale.US).format(wallet.balance)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoEmeraldDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Balance Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2419)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CasinoGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == "BN") "ভার্চুয়াল ওয়ালেট ব্যালেন্স" else "Virtual Wallet Balance",
                        color = Color(0xFF8BAE9B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E382A))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "BDT ৳",
                            color = CasinoGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "৳$formattedBalance",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (lang == "BN") "মোট জয়: ৳${NumberFormat.getNumberInstance(Locale.US).format(wallet.totalWon)}"
                        else "Total Won: ৳${NumberFormat.getNumberInstance(Locale.US).format(wallet.totalWon)}",
                        color = Color(0xFF00F5D4),
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (lang == "BN") "মোট বাজি: ৳${NumberFormat.getNumberInstance(Locale.US).format(wallet.totalWagered)}"
                        else "Total Wagered: ৳${NumberFormat.getNumberInstance(Locale.US).format(wallet.totalWagered)}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Faucet Emergency Claim Banner if balance is low (< ৳2,000)
        if (wallet.balance < 2000L) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1A04)),
                border = androidx.compose.foundation.BorderStroke(1.dp, CasinoGold)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == "BN") "কম ব্যালেন্স সহায়তা!" else "Emergency Free Refill!",
                            color = CasinoGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == "BN") "তাত্ক্ষণিক ফ্রি ৳১০,০০০ কয়েন নিন" else "Claim instant free ৳10,000 coins",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = onFaucetClaim,
                        modifier = Modifier.testTag("wallet_faucet_claim_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CasinoGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = Strings.claim(lang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Tabs: Top-up Simulator vs Transaction History
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF081C13),
            contentColor = CasinoGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CasinoGold
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.testTag("wallet_tab_reload"),
                text = {
                    Text(
                        text = if (lang == "BN") "সিমুলেটেড রিচার্জ (ফ্রি)" else "Simulated Top-up (Free)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.testTag("wallet_tab_history"),
                text = {
                    Text(
                        text = if (lang == "BN") "লেনদেনের ইতিহাস" else "History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            )
        }

        if (selectedTab == 0) {
            // Simulated Reload Screen
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = if (lang == "BN") "পেমেন্ট মাধ্যম বেছে নিন (সম্পূর্ণ ফ্রি বিনোদন):" else "Select Simulated Channel (100% Free):",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Channel Pickers (bKash, Nagad, Rocket)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentChannelChip("bKash", "বিকাশ", BkashPink, selectedChannel == "bKash") { selectedChannel = "bKash" }
                        PaymentChannelChip("Nagad", "নগদ", NagadOrange, selectedChannel == "Nagad") { selectedChannel = "Nagad" }
                        PaymentChannelChip("Rocket", "রকেট", RocketPurple, selectedChannel == "Rocket") { selectedChannel = "Rocket" }
                    }
                }

                item {
                    Text(
                        text = if (lang == "BN") "ফ্রি কয়েন প্যাকেজ সমূহ:" else "Available Free Packages:",
                        color = CasinoGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Package Cards
                val packages = listOf(
                    Triple(5000L, 0L, "Starter Free Pack"),
                    Triple(15000L, 2000L, "Popular Pack + ৳2,000 Bonus"),
                    Triple(50000L, 10000L, "High Roller + ৳10,000 Bonus"),
                    Triple(100000L, 30000L, "Royal Nawab + ৳30,000 Bonus")
                )

                items(packages) { (baseAmt, bonusAmt, label) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, CasinoEmeraldBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = CasinoEmeraldCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "৳${NumberFormat.getNumberInstance(Locale.US).format(baseAmt)}",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                if (bonusAmt > 0) {
                                    Text(
                                        text = "+৳${NumberFormat.getNumberInstance(Locale.US).format(bonusAmt)} ফ্রি বোনাস!",
                                        color = CasinoGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(text = label, color = TextSecondary, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    onAddSimulatedReload(selectedChannel, baseAmt, bonusAmt)
                                    lastAddedAmount = baseAmt + bonusAmt
                                    showReloadSuccess = true
                                },
                                modifier = Modifier.testTag("reload_pack_${baseAmt}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedChannel == "bKash") BkashPink
                                    else if (selectedChannel == "Nagad") NagadOrange
                                    else RocketPurple,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (lang == "BN") "রিচার্জ নিন (ফ্রি)" else "Get Free",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Disclaimer in Wallet
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF06140E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = CasinoGold, modifier = Modifier.size(18.dp))
                            Text(
                                text = Strings.disclaimer(lang),
                                color = Color(0xFF8FA799),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Transaction History List
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lang == "BN") "এখনও কোনো লেনদেনের ইতিহাস নেই" else "No transactions recorded yet",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { tx ->
                        TransactionItem(tx = tx, lang = lang)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentChannelChip(
    channelKey: String,
    channelBn: String,
    brandColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) brandColor else Color(0xFF132B20))
            .border(
                1.5.dp,
                if (isSelected) Color.White else CasinoEmeraldBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$channelKey ($channelBn)",
            color = if (isSelected) Color.White else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TransactionItem(tx: TransactionEntity, lang: String) {
    val isPositive = tx.amount > 0
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(tx.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CasinoEmeraldCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CasinoEmeraldBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "${tx.game} • ${tx.type}",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tx.note.ifEmpty { formattedDate },
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isPositive) "+৳${NumberFormat.getNumberInstance(Locale.US).format(tx.amount)}"
                    else "-৳${NumberFormat.getNumberInstance(Locale.US).format(-tx.amount)}",
                    color = if (isPositive) Color(0xFF00F5D4) else CasinoCrimson,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "ব্যালেন্স: ৳${NumberFormat.getNumberInstance(Locale.US).format(tx.balanceAfter)}",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}
