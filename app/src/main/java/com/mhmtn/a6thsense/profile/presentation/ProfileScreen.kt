package com.mhmtn.a6thsense.profile.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView
import com.mhmtn.a6thsense.core.presentation.ProfileScreenSkeleton
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.core.presentation.revealFromBottom
import com.mhmtn.a6thsense.profile.components.ConfettiEffect
import com.mhmtn.a6thsense.profile.components.FriendsCard
import com.mhmtn.a6thsense.profile.components.ProfileBottomSheet
import com.mhmtn.a6thsense.profile.domain.ProfileStats

@Composable
fun ProfileScreen(
    state: ProfileContract.State,
    showConfetti: Boolean,
    onConfettiComplete: () -> Unit,
    onAction: (ProfileContract.Action) -> Unit
) {
    if (state.isLoading) {
        ProfileScreenSkeleton()
    } else if (state.error != null) {
        NetworkErrorView(
            message = state.error,
            onRetry = { onAction(ProfileContract.Action.Load) }
        )
        return
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F5FF)) // 👈 Soft lavender beyaz
        ) {
            val scrollState = rememberScrollState()

            ProfileBackground()

            if (showConfetti) {
                ConfettiEffect(onComplete = onConfettiComplete)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Avatar ve isim bölümü
                ProfileHeader(
                    modifier = Modifier.revealFromBottom(delayMillis = 0),
                    name = state.user?.name ?: "",
                    isPremium = state.isPremium,
                    photoUrl = state.user?.photoUrl ?: ""
                )

                Spacer(modifier = Modifier.height(40.dp))

                FriendsCard(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp)
                        .revealFromBottom(delayMillis = 100),
                    onClick = { onAction(ProfileContract.Action.NavigateToFriends) }
                )

                Spacer(modifier = Modifier.height(40.dp))

                ProfileStatsSection(
                    modifier = Modifier.revealFromBottom(delayMillis = 200),
                    stats = state.stats,
                    onMatchesClick = {
                        onAction(ProfileContract.Action.NavigateToMatchHistory)
                    },
                    onActivityClick = {
                        onAction(
                            ProfileContract.Action.OpenSheet(
                                ProfileContract.BottomSheetType.ACTIVITY_STATS
                            )
                        )
                    },
                    onBadgesClick = {
                        onAction(
                            ProfileContract.Action.OpenSheet(
                                ProfileContract.BottomSheetType.BADGES
                            )
                        )
                        // 👇 Rozet açılınca konfeti de tetikle
                        onAction(ProfileContract.Action.TriggerConfetti)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            state.activeSheet?.let { sheetType ->
                ProfileBottomSheet(
                    type = sheetType,
                    stats = state.stats,
                    badges = state.badges,
                    onDismiss = { onAction(ProfileContract.Action.CloseSheet) }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                        )
                    )
                    .bounceClick(onClick = {
                        onAction(ProfileContract.Action.onInviteClick)
                    })
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🎁", fontSize = 20.sp)
                    Text(
                        text = R.string.invite.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Sol üst blob
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(
                    Color(0xFF7B5EA7).copy(alpha = 0.08f),
                    CircleShape
                )
                .blur(60.dp)
        )

        // Sağ alt blob
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .background(
                    Color(0xFF4568DC).copy(alpha = 0.06f),
                    CircleShape
                )
                .blur(60.dp)
        )

        // Orta blob
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .offset(y = 100.dp)
                .background(
                    Color(0xFFB06AB3).copy(alpha = 0.05f),
                    CircleShape
                )
                .blur(80.dp)
        )
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    photoUrl: String,
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_glow")
    val glowRadius by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    val premiumGradient = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
    val normalGradient = listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar glow + foto
        Box(contentAlignment = Alignment.Center) {
            if (isPremium) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = glowRadius * 0.8f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .blur(25.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Brush.radialGradient(
                            colors = if (isPremium) {
                                listOf(
                                    Color(0xFFFFD700).copy(alpha = glowRadius * 0.5f),
                                    Color.Transparent
                                )
                            } else {
                                listOf(
                                    Color(0xFF7B5EA7).copy(alpha = glowRadius),
                                    Color.Transparent
                                )
                            }
                        ),
                        CircleShape
                    )
                    .blur(20.dp)
            )

            // Avatar border
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        Brush.linearGradient(
                            colors = if (isPremium) premiumGradient else normalGradient
                        ),
                        CircleShape
                    )
                    .padding(if (isPremium) 4.dp else 3.dp)
            ) {
                if (photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFEDE7F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPremium) Color(0xFFFFD700) else Color(0xFF7B5EA7)
                        )
                    }
                }
            }
            if (isPremium) {
                PremiumSparkles()
            }
        }

        // İsim
        Text(
            text = name,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        if (isPremium) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(premiumGradient)
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "👑", fontSize = 14.sp)
                    Text(
                        text = "Premium",
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF7B5EA7).copy(alpha = 0.15f),
                                Color(0xFF4568DC).copy(alpha = 0.15f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🔮 Aurania",
                    fontSize = 13.sp,
                    color = Color(0xFF7B5EA7),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsSection(
    modifier: Modifier = Modifier,
    stats: ProfileStats,
    onMatchesClick: () -> Unit,
    onActivityClick: () -> Unit,
    onBadgesClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = R.string.statistics.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69),
            modifier = Modifier.padding(start = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                emoji = "🔥",
                value = stats.currentStreak.toString(),
                label = pluralStringResource(
                    id = R.plurals.days_streak,
                    count = stats.currentStreak,
                    stats.currentStreak
                ),
                gradient = listOf(Color(0xFFFF6B6B), Color(0xFFEE5A6F)),
                onClick = null // Streak için sheet yok
            )
            StatCard(
                modifier = Modifier.weight(1f),
                emoji = "✨",
                value = stats.totalActivities.toString(),
                label = R.string.activities.toString(),
                gradient = listOf(Color(0xFF7B5EA7), Color(0xFF4568DC)),
                onClick = onActivityClick // 👈
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                emoji = "💫",
                value = stats.totalMatches.toString(),
                label = R.string.matches.toString(),
                gradient = listOf(Color(0xFFB06AB3), Color(0xFF4568DC)),
                onClick = onMatchesClick // 👈
            )
            StatCard(
                modifier = Modifier.weight(1f),
                emoji = "📅",
                value = "${stats.memberSinceDays}",
                label = R.string.member_since.toString(),
                gradient = listOf(Color(0xFF43E97B), Color(0xFF38F9D7)),
                onClick = onBadgesClick // 👈
            )
        }
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    gradient: List<Color>,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onClick()
        }
    } else Modifier

    // Tıklanabilir kartlarda küçük "tap" hint animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "hint")
    val hintScale by if (onClick != null) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.97f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "hint"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Box(
        modifier = modifier
            .scale(if (onClick != null) hintScale else 1f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = gradient.first().copy(alpha = 0.3f),
                spotColor = gradient.first().copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .then(clickableModifier)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(colors = gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 22.sp)
                }

                // Tıklanabilirse ok ikonu göster
                if (onClick != null) {
                    Text(
                        text = "›",
                        fontSize = 22.sp,
                        color = gradient.first().copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2D1B69)
            )

            Text(
                text = label,
                fontSize = 13.sp,
                color = Color(0xFF9E9E9E),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
@Composable
private fun BoxScope.PremiumSparkles() {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkles")

    // 4 küçük yıldız
    listOf(
        Triple(0.15f, 0.2f, 0),
        Triple(0.85f, 0.25f, 300),
        Triple(0.2f, 0.8f, 600),
        Triple(0.8f, 0.75f, 900)
    ).forEach { (x, y, delay) ->
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing, delayMillis = delay),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sparkle_alpha_$delay"
        )

        val scale by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing, delayMillis = delay),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sparkle_scale_$delay"
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (x * 100 - 50).dp, y = (y * 100 - 50).dp)
                .graphicsLayer {
                    this.alpha = alpha
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            Text(
                text = "✨",
                fontSize = 12.sp,
                modifier = Modifier.graphicsLayer { rotationZ = 45f }
            )
        }
    }
}