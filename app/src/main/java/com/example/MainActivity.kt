package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Strings
import com.example.ui.components.CasinoTopBar
import com.example.ui.screens.AndarBaharScreen
import com.example.ui.screens.CrashScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.LuckyWheelScreen
import com.example.ui.screens.SlotsScreen
import com.example.ui.screens.TeenPattiScreen
import com.example.ui.screens.VipScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.CasinoEmeraldDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CasinoScreen
import com.example.viewmodel.CasinoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CasinoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DhakaRoyaleApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DhakaRoyaleApp(viewModel: CasinoViewModel) {
    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    val crashState by viewModel.crashState.collectAsStateWithLifecycle()
    val teenPattiState by viewModel.teenPattiState.collectAsStateWithLifecycle()
    val andarBaharState by viewModel.andarBaharState.collectAsStateWithLifecycle()
    val slotsState by viewModel.slotsState.collectAsStateWithLifecycle()

    val lang = wallet.language
    val canClaimDaily = System.currentTimeMillis() - wallet.lastDailyClaim >= (24 * 60 * 60 * 1000L) || wallet.lastDailyClaim == 0L

    val screenTitle = when (currentScreen) {
        CasinoScreen.LOBBY -> Strings.appTitle(lang)
        CasinoScreen.CRASH -> Strings.crashTitle(lang)
        CasinoScreen.TEEN_PATTI -> Strings.teenPattiTitle(lang)
        CasinoScreen.ANDAR_BAHAR -> Strings.andarBaharTitle(lang)
        CasinoScreen.SLOTS -> Strings.slotsTitle(lang)
        CasinoScreen.WHEEL -> Strings.wheelTitle(lang)
        CasinoScreen.WALLET -> if (lang == "BN") "ওয়ালেট ও রিচার্জ" else "Wallet & Top-up"
        CasinoScreen.VIP -> if (lang == "BN") "নবাব ভিআইপি ক্লাব" else "Royal Nawab VIP"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CasinoTopBar(
                title = screenTitle,
                balance = wallet.balance,
                vipLevel = wallet.level,
                language = wallet.language,
                soundEnabled = wallet.soundEnabled,
                showBackButton = currentScreen != CasinoScreen.LOBBY,
                onBackClick = { viewModel.navigateTo(CasinoScreen.LOBBY) },
                onWalletClick = { viewModel.navigateTo(CasinoScreen.WALLET) },
                onVipClick = { viewModel.navigateTo(CasinoScreen.VIP) },
                onLanguageToggle = { viewModel.toggleLanguage() },
                onSoundToggle = { viewModel.toggleSound() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CasinoEmeraldDark)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                CasinoScreen.LOBBY -> {
                    LobbyScreen(
                        wallet = wallet,
                        canClaimDaily = canClaimDaily,
                        onClaimDaily = { viewModel.claimDailyBonus() },
                        onNavigateToCrash = { viewModel.navigateTo(CasinoScreen.CRASH) },
                        onNavigateToTeenPatti = { viewModel.navigateTo(CasinoScreen.TEEN_PATTI) },
                        onNavigateToAndarBahar = { viewModel.navigateTo(CasinoScreen.ANDAR_BAHAR) },
                        onNavigateToSlots = { viewModel.navigateTo(CasinoScreen.SLOTS) },
                        onNavigateToWheel = { viewModel.navigateTo(CasinoScreen.WHEEL) },
                        onNavigateToWallet = { viewModel.navigateTo(CasinoScreen.WALLET) },
                        onNavigateToVip = { viewModel.navigateTo(CasinoScreen.VIP) }
                    )
                }
                CasinoScreen.CRASH -> {
                    CrashScreen(
                        state = crashState,
                        balance = wallet.balance,
                        language = lang,
                        onStartRound = { bet, autoCash -> viewModel.startCrashRound(bet, autoCash) },
                        onCashOut = { viewModel.cashOutCrash() },
                        onBack = { viewModel.navigateTo(CasinoScreen.LOBBY) }
                    )
                }
                CasinoScreen.TEEN_PATTI -> {
                    TeenPattiScreen(
                        state = teenPattiState,
                        balance = wallet.balance,
                        language = lang,
                        onStartGame = { boot -> viewModel.startTeenPatti(boot) },
                        onChaal = { viewModel.chaalTeenPatti() },
                        onPack = { viewModel.packTeenPatti() },
                        onShow = { viewModel.showTeenPatti() },
                        onSeeCards = { viewModel.seeCardsTeenPatti() },
                        onBack = { viewModel.navigateTo(CasinoScreen.LOBBY) }
                    )
                }
                CasinoScreen.ANDAR_BAHAR -> {
                    AndarBaharScreen(
                        state = andarBaharState,
                        balance = wallet.balance,
                        language = lang,
                        onPlaceBetAndar = { amt -> viewModel.betAndar(amt) },
                        onPlaceBetBahar = { amt -> viewModel.betBahar(amt) },
                        onDealRound = { viewModel.dealAndarBaharRound() },
                        onClearBets = { viewModel.clearAndarBaharBets() },
                        onBack = { viewModel.navigateTo(CasinoScreen.LOBBY) }
                    )
                }
                CasinoScreen.SLOTS -> {
                    SlotsScreen(
                        state = slotsState,
                        balance = wallet.balance,
                        language = lang,
                        onSpin = { bet -> viewModel.spinSlots(bet) },
                        onToggleAutoSpin = { viewModel.toggleAutoSpin() },
                        onBack = { viewModel.navigateTo(CasinoScreen.LOBBY) }
                    )
                }
                CasinoScreen.WHEEL -> {
                    LuckyWheelScreen(
                        balance = wallet.balance,
                        language = lang,
                        onSpinReward = { reward -> viewModel.claimSpinReward(reward) },
                        onBack = { viewModel.navigateTo(CasinoScreen.LOBBY) }
                    )
                }
                CasinoScreen.WALLET -> {
                    WalletScreen(
                        wallet = wallet,
                        transactions = transactions,
                        onAddSimulatedReload = { ch, base, bonus -> viewModel.addSimulatedReload(ch, base, bonus) },
                        onFaucetClaim = { viewModel.claimEmergencyFaucet() },
                        onBack = { viewModel.navigateTo(CasinoScreen.LOBBY) }
                    )
                }
                CasinoScreen.VIP -> {
                    VipScreen(
                        wallet = wallet,
                        onBack = { viewModel.navigateTo(CasinoScreen.LOBBY) }
                    )
                }
            }
        }
    }
}
