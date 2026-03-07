package com.mhmtn.a6thsense.home.presentation

import android.util.Log
import androidx.compose.animation.core.*
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract.SessionType
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.core.presentation.*
import com.mhmtn.a6thsense.home.components.CompactMatchCard
import com.mhmtn.a6thsense.home.components.CompletionBadge
import com.mhmtn.a6thsense.home.components.ExpandableSessionSection
import com.mhmtn.a6thsense.home.components.SessionCard
import com.mhmtn.a6thsense.messaging.presentation.PremiumEntryPoint
import com.mhmtn.a6thsense.home.components.UserStatsCard
import com.mhmtn.a6thsense.premium.domain.PremiumStatus
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit
) {
    if (state.error != null) {
        NetworkErrorView(
            message = state.error,
            onRetry = { onAction(HomeAction.Load) }
        )
        return
    }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val premiumStatus by remember {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            EntryPointAccessors
                .fromApplication(
                    context.applicationContext,
                    PremiumEntryPoint::class.java
                )
                .premiumRepository()
                .getPremiumStatus(uid)
        } else {
            flowOf(PremiumStatus())
        }
    }.collectAsStateWithLifecycle(initialValue = PremiumStatus())

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
        // Dekoratif bloblar
        HomeBackgroundBlobs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            HomeHeader(
                onSettingsClick = { onAction(HomeAction.OnSettingsClick) },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Premium CTA (eğer premium değilse)
            if (!state.isPremium) {
                PremiumCTA(
                    onUpgradeClick = { /* Navigate to paywall */ },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .revealFromBottom(delayMillis = 0)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            UserStatsCard(
                isPremium = state.isPremium,
                swipesUsed = premiumStatus.dailySwipesUsed,
                swipeLimit = premiumStatus.dailySwipeLimit,
                messagesUsed = premiumStatus.dailyMessagesUsed,
                messageLimit = premiumStatus.dailyMessageLimit,
                onUpgradeClick = { onAction(HomeAction.OnUpgradeClick) },
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .revealFromBottom(delayMillis = 50)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Günlük aktivite stat kartı
            DailyActivityStatCard(
                streak = state.currentStreak,
                completedToday = state.completedToday,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .revealFromBottom(delayMillis = 100)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // home/presentation/HomeScreen.kt içinde

// Match Section
            if (state.todayMatches.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.todayMatches.size == 1) R.string.today_match.toString() else R.string.today_matches.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        if (state.isPremium && state.todayMatches.size > 1) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "👑", fontSize = 16.sp)
                                Text(
                                    text = "${state.todayMatches.size}/3",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }
                    }

                    // Cards
                    if (state.todayMatches.size == 1) {
                        // Single match - büyük kart
                        EnhancedMatchCard(
                            matchedUser = state.matchedUser!!,
                            similarity = state.similarity ?: 0,
                            onClick = { onAction(HomeAction.OnMatchClick()) },
                            modifier = Modifier.revealFromBottom(delayMillis = 150)
                        )
                    } else {
                        // Multiple matches - horizontal carousel
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(state.todayMatches) { match ->
                                CompactMatchCard(
                                    match = match,
                                    onClick = {
                                        onAction(HomeAction.OnMatchClick(matchId = match.matchId))
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // Empty state
                EmptyMatchState(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .revealFromBottom(delayMillis = 150)
                )
            }

            /*
            // Match kartı veya boş durum
            if (state.matchedUser != null) {
                Log.d("HomeScreen", "state.matchedUser: ${state.matchedUser}")
                Log.d("HomeScreen", "state.similarity: ${state.similarity}")
                EnhancedMatchCard(
                    matchedUser = state.matchedUser,
                    similarity = state.similarity ?: 0,
                    onClick = { onAction(HomeAction.OnMatchClick) },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .revealFromBottom(delayMillis = 200)
                )
            } else {
                EmptyMatchState(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .revealFromBottom(delayMillis = 200)
                )
            }

             */

            Spacer(modifier = Modifier.height(24.dp))

            ExpandableSessionSection(
                hasCompletedIntuition = state.hasCompletedIntuitionToday,
                hasCompletedPreference = state.hasCompletedPreferenceToday,
                onStartSession = { sessionType ->
                    onAction(HomeAction.OnStartSession(sessionType))
                },
                modifier = Modifier.revealFromBottom(delayMillis = 200)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    /* Başlat butonu
    EnhancedStartButton(
        onClick = { onAction(HomeAction.OnStartDailyClick) },
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .revealFromBottom(delayMillis = 300)
    )

    Spacer(modifier = Modifier.height(40.dp))

     */
}


@Composable
private fun HomeBackgroundBlobs() {
    Box(
        modifier = Modifier
            .size(350.dp)
            .offset(x = (-100).dp, y = (-80).dp)
            .blur(80.dp)
            .background(
                Color(0xFF7B5EA7).copy(alpha = 0.15f),
                CircleShape
            )
    )
    Box(
        modifier = Modifier
            .size(280.dp)
            .offset(x = 250.dp, y = 150.dp)
            .blur(70.dp)
            .background(
                Color(0xFF4568DC).copy(alpha = 0.12f),
                CircleShape
            )
    )
}

@Composable
private fun HomeHeader(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo + Tagline
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔮",
                    fontSize = 32.sp,
                    modifier = Modifier.floating(offsetY = 8f, duration = 2500)
                )
                Column {
                    Text(
                        text = "VibeTribe",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Soul Connections",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Settings butonu
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PremiumCTA(
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "premium_shine")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFFFFD700).copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2D1B69),
                        Color(0xFF1A1A2E)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = 0.5f),
                        Color(0xFFFFA500).copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .bounceClick(onClick = onUpgradeClick)
            .padding(20.dp)
    ) {
        // Shimmer overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = shimmer * size.width
                }
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "👑", fontSize = 32.sp)
                Column {
                    Text(
                        text = R.string.upgrade.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = R.string.use_limitless.toString(),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.Face,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DailyActivityStatCard(
    streak: Int,
    completedToday: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Streak kartı
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0xFFFF6B6B).copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "🔥", fontSize = 28.sp)
                Text(
                    text = "$streak",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = pluralStringResource(
                        id = R.plurals.days_streak,
                        count = streak,
                        streak
                    ),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // Bugün tamamlandı kartı
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = if (completedToday)
                        Color(0xFF43E97B).copy(alpha = 0.2f)
                    else
                        Color(0xFF7B5EA7).copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (completedToday) "✅" else "⏳",
                    fontSize = 28.sp
                )
                Text(
                    text = R.string.today.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (completedToday) R.string.completed.toString() else R.string.waiting.toString(),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EnhancedMatchCard(
    matchedUser: AuthUser,
    similarity: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 3D tilt efekti
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            // .aspectRatio(1.5f)
            .wrapContentHeight()
            .graphicsLayer {
                rotationY = offsetX * 5f
                rotationX = -offsetY * 5f
                shadowElevation = 30f
            }
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.4f),
                spotColor = Color(0xFF4568DC).copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2D1B69),
                        Color(0xFF1A1A2E),
                        Color(0xFF0F0C29)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF7B5EA7).copy(alpha = 0.5f),
                        Color(0xFF4568DC).copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .bounceClick(onClick = onClick)
            .padding(24.dp)
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "✨", fontSize = 12.sp)
                Text(
                    text = R.string.today_match.toString(),
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Avatar + glow
            Box(contentAlignment = Alignment.Center) {
                // Glow efekti
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF7B5EA7).copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .blur(20.dp)
                )

                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                            )
                        )
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (matchedUser.photoUrl?.isNotBlank() == true) {
                        AsyncImage(
                            model = matchedUser.photoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = matchedUser.name.firstOrNull()?.uppercase() ?: "?",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // İsim
            Text(
                text = matchedUser.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Similarity badge
            SimilarityIndicator(similarity = similarity)

            Spacer(modifier = Modifier.height(16.dp))

            // CTA
            Text(
                text = R.string.view_details.toString(),
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun SimilarityIndicator(similarity: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Log.d("SimilarityIndicator", "similarity: $similarity")
        // Başlık
        Text(
            text = R.string.spiritual_harmony.toString(),
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )

        // Progress bar + skor
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Circular progress veya bar
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(similarity / 100f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.linearGradient(
                                colors = when {
                                    similarity >= 80 -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
                                    similarity >= 60 -> listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                                    else -> listOf(Color(0xFFFFD700), Color(0xFFFF9A56))
                                }
                            )
                        )
                )
            }

            // Yüzde + açıklama
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Büyük yüzde
                Text(
                    text = "$similarity%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = when {
                        similarity >= 80 -> Color(0xFF43E97B)
                        similarity >= 60 -> Color(0xFF7B5EA7)
                        else -> Color(0xFFFFD700)
                    }
                )

                // Emoji + açıklama
                Column {
                    Text(
                        text = when {
                            similarity >= 80 -> "${R.string.perfect_harmony.toString()} 🔥"
                            similarity >= 60 -> "${R.string.very_good_harmony.toString()} ✨"
                            similarity >= 40 -> "${R.string.good_harmony.toString()} 💫"
                            similarity >= 20 -> "${R.string.interesting.toString()} 🌟"
                            else -> "✨ ${R.string.no_harmony.toString()}"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = R.string.connection.toString(),
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMatchState(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🔮",
                fontSize = 64.sp,
                modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
            )
            Text(
                text = R.string.home_no_matches.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = R.string.home_subtitle.toString(),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}