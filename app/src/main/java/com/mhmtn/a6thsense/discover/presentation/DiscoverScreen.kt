package com.mhmtn.a6thsense.discover.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView
import com.mhmtn.a6thsense.core.presentation.PremiumLimitSnackbar
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.core.presentation.ShimmerBox
import com.mhmtn.a6thsense.discover.presentation.components.SwipeButtons
import com.mhmtn.a6thsense.discover.presentation.components.SwipeCard
import com.mhmtn.a6thsense.R

@Composable
fun DiscoverScreen(
    state: DiscoverContract.State,
    showPremiumSnackbar: Boolean, // 👈 Parametre
    onDismissSnackbar: () -> Unit,
    onUpgradeClick: () -> Unit,
    onAction: (DiscoverContract.Action) -> Unit
)  {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0C29),
                        Color(0xFF1A1A2E),
                        Color(0xFF24243E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            DiscoverHeader(
                activeCount = state.users.size,
                remaining = maxOf(0, state.users.size - state.currentIndex)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kart alanı
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading -> DiscoverLoadingSkeleton()

                    state.isEmpty || state.currentIndex >= state.users.size -> {
                        EmptyDiscoverView(
                            onReload = { onAction(DiscoverContract.Action.Reload) }
                        )
                    }

                    state.error != null -> {
                        NetworkErrorView(
                            message = state.error,
                            onRetry = { onAction(DiscoverContract.Action.Reload) }
                        )
                    }

                    else -> {
                        // Arka kart (stack efekti için)
                        val nextIndex = state.currentIndex + 1
                        if (nextIndex < state.users.size) {
                            SwipeCard(
                                user = state.users[nextIndex],
                                onSwipeLeft = {},
                                onSwipeRight = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        scaleX = 0.93f
                                        scaleY = 0.93f
                                        translationY = 20f
                                    }
                            )
                        }

                        // Üst kart (aktif)
                        SwipeCard(
                            user = state.users[state.currentIndex],
                            onSwipeLeft = { onAction(DiscoverContract.Action.SwipeLeft) },
                            onSwipeRight = { onAction(DiscoverContract.Action.SwipeRight) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Swipe butonları
            if (!state.isLoading &&
                !state.isEmpty &&
                state.currentIndex < state.users.size
            ) {
                SwipeButtons(
                    onSwipeLeft = { onAction(DiscoverContract.Action.SwipeLeft) },
                    onSwipeRight = { onAction(DiscoverContract.Action.SwipeRight) },
                    isLoading = state.isLoadingConversation,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        AnimatedVisibility(
            visible = showPremiumSnackbar,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PremiumLimitSnackbar(
                message = R.string.reached_daily_discover.toString(),
                onUpgradeClick = onUpgradeClick,
                onDismiss = onDismissSnackbar
            )
        }
    }
}

@Composable
private fun DiscoverHeader(
    activeCount: Int,
    remaining: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = R.string.discover.toString(),
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "$activeCount ${R.string.active_today.toString()}",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }

        // Kalan kart sayısı
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "$remaining ${R.string.card.toString()}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DiscoverLoadingSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
    ) {
        ShimmerBox(
            modifier = Modifier.fillMaxSize(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
        )
    }
}

@Composable
private fun EmptyDiscoverView(onReload: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(40.dp)
    ) {
        // Animasyonlu emoji
        val infiniteTransition = rememberInfiniteTransition(label = "empty")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Text(
            text = "🔭",
            fontSize = 72.sp,
            modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
        )

        Text(
            text = R.string.no_active_users.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = R.string.invite_your_friends.toString(),
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                    )
                )
                .bounceClick(onClick = onReload)
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(
                text = R.string.refresh.toString(),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}