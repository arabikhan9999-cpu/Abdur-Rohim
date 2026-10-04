package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CasinoCrimson
import com.example.ui.theme.CasinoEmeraldBorder
import com.example.ui.theme.CasinoEmeraldCard
import com.example.ui.theme.CasinoGold
import com.example.ui.theme.CasinoGoldLight
import com.example.ui.theme.CasinoNeonCyan
import com.example.ui.theme.TextPrimary

data class CasinoChip(
    val amount: Long,
    val label: String,
    val primaryColor: Color,
    val secondaryColor: Color
)

val STANDARD_CHIPS = listOf(
    CasinoChip(100L, "100", Color(0xFF00B4D8), Color(0xFF0077B6)),
    CasinoChip(500L, "500", Color(0xFF9D4EDD), Color(0xFF5A189A)),
    CasinoChip(1000L, "1K", Color(0xFFE63946), Color(0xFF9B111E)),
    CasinoChip(5000L, "5K", Color(0xFFFFB703), Color(0xFFD48B00)),
    CasinoChip(10000L, "10K", Color(0xFF0077B6), Color(0xFF023E8A)),
    CasinoChip(25000L, "25K", Color(0xFF2EC4B6), Color(0xFF0E7C7B)),
    CasinoChip(50000L, "50K", Color(0xFF212529), Color(0xFFFFD700))
)

@Composable
fun ChipSelector(
    selectedAmount: Long,
    onSelectAmount: (Long) -> Unit,
    userBalance: Long,
    onDouble: () -> Unit = {},
    onHalf: () -> Unit = {},
    onMax: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CasinoEmeraldCard)
            .border(1.dp, CasinoEmeraldBorder, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Quick multiplier buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "বাজির চিপ (Bet Chips)",
                color = CasinoGoldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                QuickActionPill("½x", onClick = onHalf, testTag = "chip_half_btn")
                QuickActionPill("2x", onClick = onDouble, testTag = "chip_double_btn")
                QuickActionPill("MAX", onClick = onMax, testTag = "chip_max_btn", isGold = true)
            }
        }

        // Chip row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            STANDARD_CHIPS.forEach { chip ->
                val isSelected = selectedAmount == chip.amount
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1.0f,
                    label = "chip_scale"
                )

                CasinoChipItem(
                    chip = chip,
                    isSelected = isSelected,
                    scale = scale,
                    onClick = { onSelectAmount(chip.amount) }
                )
            }
        }
    }
}

@Composable
fun CasinoChipItem(
    chip: CasinoChip,
    isSelected: Boolean,
    scale: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag("chip_${chip.amount}")
            .scale(scale)
            .size(52.dp)
            .shadow(if (isSelected) 10.dp else 4.dp, CircleShape, spotColor = CasinoGold)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(chip.primaryColor, chip.secondaryColor)
                )
            )
            .border(
                width = if (isSelected) 3.dp else 1.5.dp,
                color = if (isSelected) CasinoGold else Color.White.copy(alpha = 0.6f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Dashed inner edge ring
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "৳",
                    color = if (isSelected) CasinoGold else Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chip.label,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun QuickActionPill(
    label: String,
    onClick: () -> Unit,
    testTag: String,
    isGold: Boolean = false
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isGold) CasinoGold else Color(0xFF1D3B2B))
            .border(
                1.dp,
                if (isGold) CasinoGoldLight else Color(0xFF2C5E45),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isGold) Color.Black else TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
