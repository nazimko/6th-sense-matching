package com.mhmtn.a6thsense.soulsync.presentation.components

import android.util.Log
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.soulsync.domain.PlayerState

@Composable
fun WaitingForOpponent(
    currentPlayer: PlayerState?,
    otherPlayer: PlayerState?,
    isDark: Boolean,
    onJoinRoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (isDark) {
        listOf(Color(0xFF0F0C29), Color(0xFF1A1A2E), Color(0xFF24243E))
    } else {
        listOf(Color(0xFFF8F5FF), Color(0xFFF0EBFF), Color(0xFFE8DEFF))
    }
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    LaunchedEffect(currentPlayer) {
        Log.d("WaitingForOpponent", "Current UID: $currentUid")
        Log.d("WaitingForOpponent", "Current player: $currentPlayer")
        Log.d("WaitingForOpponent", "Current player status: ${currentPlayer?.status}")

        // Fallback: currentPlayer null ise de join yap
        if (currentPlayer == null) {
            Log.e("WaitingForOpponent", "Current player is NULL! Trying to join anyway...")
            onJoinRoom()
        } else if (currentPlayer.status == "invited") {
            Log.d("WaitingForOpponent", "Status is invited, joining room...")
            onJoinRoom()
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "waiting")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = gradientColors
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Animasyonlu logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
            ) {
                Text(text = "🔮", fontSize = 80.sp)
            }

            Text(
                text = "Soul Sync",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // İki oyuncu avatar'ları
            Row(
                horizontalArrangement = Arrangement.spacedBy(40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current player
                PlayerAvatar(
                    photoUrl = currentPlayer?.photoUrl ?: "",
                    name = currentPlayer?.name ?: "You",
                    isReady = currentPlayer?.status == "ready",
                    isCurrentPlayer = true
                )

                // VS text
                Text(
                    text = stringResource(R.string.and_text),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                // Other player
                PlayerAvatar(
                    photoUrl = otherPlayer?.photoUrl ?: "",
                    name = otherPlayer?.name ?: stringResource(R.string.your_match),
                    isReady = otherPlayer?.status == "ready",
                    isCurrentPlayer = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Loading text
            Text(
                text = when {
                    currentPlayer?.status == "invited" -> stringResource(R.string.game_status_joining)
                    otherPlayer?.status == "invited" -> stringResource(R.string.game_status_waiting_opponent)
                    else -> stringResource(R.string.game_status_starting)
                },
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            // Animated dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = alpha))
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerAvatar(
    photoUrl: String,
    name: String,
    isReady: Boolean,
    isCurrentPlayer: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Glow effect
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                if (isReady) Color(0xFF43E97B) else Color(0xFF7B5EA7),
                                Color.Transparent
                            ).map { it.copy(alpha = 0.3f) }
                        ),
                        CircleShape
                    )
            )

            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7B5EA7))
                    .border(
                        width = 3.dp,
                        color = if (isReady) Color(0xFF43E97B) else Color(0xFF7B5EA7),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = name.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Ready checkmark
            if (isReady) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF43E97B))
                        .border(2.dp, Color(0xFF0F0C29), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓", fontSize = 16.sp, color = Color.White)
                }
            }
        }

        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}