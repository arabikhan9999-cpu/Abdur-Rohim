package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Card
import com.example.ui.theme.CasinoGold

@Composable
fun PlayingCardView(
    card: Card?,
    isFaceUp: Boolean = true,
    width: Dp = 56.dp,
    height: Dp = 82.dp,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFaceUp) 0f else 180f,
        label = "card_flip"
    )

    Box(
        modifier = modifier
            .size(width, height)
            .shadow(6.dp, RoundedCornerShape(8.dp))
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, if (isFaceUp) Color(0xFFD4AF37) else CasinoGold, RoundedCornerShape(8.dp))
    ) {
        if (rotation <= 90f && card != null && isFaceUp) {
            // Front face of card
            val textColor = if (card.suit.isRed) Color(0xFFD62828) else Color(0xFF1D2D44)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFCFCFC))
                    .padding(4.dp)
            ) {
                // Top-Left Index
                Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.rank.display,
                        color = textColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (height.value * 0.17f).sp,
                        lineHeight = (height.value * 0.17f).sp
                    )
                    Text(
                        text = card.suit.symbol,
                        color = textColor,
                        fontSize = (height.value * 0.18f).sp,
                        lineHeight = (height.value * 0.18f).sp
                    )
                }

                // Center Big Suit Symbol
                Text(
                    text = card.suit.symbol,
                    color = textColor.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    fontSize = (height.value * 0.38f).sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Bottom-Right Index (Rotated)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .graphicsLayer { rotationZ = 180f },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.rank.display,
                        color = textColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (height.value * 0.17f).sp,
                        lineHeight = (height.value * 0.17f).sp
                    )
                    Text(
                        text = card.suit.symbol,
                        color = textColor,
                        fontSize = (height.value * 0.18f).sp,
                        lineHeight = (height.value * 0.18f).sp
                    )
                }
            }
        } else {
            // Royal Card Back Pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF8B0000), Color(0xFF3A0000))
                        )
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, CasinoGold.copy(alpha = 0.6f), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "👑",
                            fontSize = (height.value * 0.22f).sp
                        )
                        Text(
                            text = "DHAKA",
                            color = CasinoGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (height.value * 0.11f).sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
