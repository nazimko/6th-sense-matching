package com.mhmtn.a6thsense.home.presentation

import android.content.Context
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.core.presentation.*
import com.mhmtn.a6thsense.home.components.CompactMatchCard
import com.mhmtn.a6thsense.home.components.ExpandableSessionSection
import com.mhmtn.a6thsense.messaging.presentation.PremiumEntryPoint
import com.mhmtn.a6thsense.home.components.UserStatsCard
import com.mhmtn.a6thsense.premium.domain.PremiumStatus
import com.mhmtn.a6thsense.activity.presentation.components.MatchThresholdPicker
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    isDark: Boolean,
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onShowSheet: () -> Unit,
    onAction: (HomeAction) -> Unit
) {
    if (state.error != null) {
        NetworkErrorView(
            message = state.error.asString(),
            onRetry = { onAction(HomeAction.Load) }
        )
        return
    }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val isTablet = configuration.screenWidthDp > 600
    val contentModifier = if (isTablet) Modifier.widthIn(max = 850.dp) else Modifier.fillMaxWidth()
    val horizontalPadding = if (isTablet) 32.dp else 24.dp

    val premiumStatus by remember {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            EntryPointAccessors
                .fromApplication(context.applicationContext, PremiumEntryPoint::class.java)
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
                    colors = if (isDark) listOf(
                        Color(0xFF0F0C29),
                        Color(0xFF1A1A2E),
                        Color(0xFF24243E)
                    )
                    else listOf(Color(0xFFF8F5FF), Color(0xFFF0EBFF), Color(0xFFE8DEFF))
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        HomeBackgroundBlobs()

        Column(
            modifier = Modifier
                .then(contentModifier)
                .fillMaxHeight()
                .verticalScroll(scrollState)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            HomeHeader(
                onSettingsClick = { onAction(HomeAction.OnSettingsClick) },
                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!state.isPremium) {
                PremiumCTA(
                    onUpgradeClick = { onAction(HomeAction.OnUpgradeClick) },
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding)
                        .revealFromBottom(delayMillis = 0)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            UserStatsCard(
                isPremium = state.isPremium,
                isDark = isDark,
                swipesUsed = premiumStatus.dailySwipesUsed,
                swipeLimit = premiumStatus.dailySwipeLimit,
                messagesUsed = premiumStatus.dailyMessagesUsed,
                messageLimit = premiumStatus.dailyMessageLimit,
                soulSyncUsed = premiumStatus.dailySoulSyncUsed,
                soulSyncLimit = premiumStatus.dailySoulSyncLimit,
                onUpgradeClick = { onAction(HomeAction.OnUpgradeClick) },
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
                    .revealFromBottom(delayMillis = 50)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DailyActivityStatCard(
                streak = state.currentStreak,
                completedToday = state.completedToday,
                isDark = isDark,
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
                    .revealFromBottom(delayMillis = 100)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ ACTIVE MATCHES (Frozen or Daily)
            if (state.todayMatches.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(horizontal = horizontalPadding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.todayMatches.size == 1) stringResource(R.string.today_match) else stringResource(
                                R.string.today_matches
                            ),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
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

                    if (state.todayMatches.size == 1) {
                        EnhancedMatchCard(
                            matchedUser = state.matchedUser!!,
                            similarity = state.similarity ?: 0,
                            isDark = isDark,
                            onClick = { onAction(HomeAction.OnMatchClick()) },
                            modifier = Modifier.revealFromBottom(delayMillis = 150)
                        )
                        Log.d("HomeScreen", "todayMatches size: ${state.similarity}")
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(state.todayMatches) { match ->
                                CompactMatchCard(
                                    match = match,
                                    modifier = Modifier.width(if (isTablet) 320.dp else 240.dp),
                                    onClick = { onAction(HomeAction.OnMatchClick(matchId = match.matchId)) }
                                )
                            }
                        }
                    }
                }
            } else {
                EmptyMatchState(
                    isDark = isDark,
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding)
                        .revealFromBottom(delayMillis = 150)
                )
            }
            val sheetState = rememberModalBottomSheetState()

            Spacer(modifier = Modifier.height(24.dp))

            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { onDismiss() },
                    sheetState = sheetState
                ) {
                    // ✅ Match Threshold Picker
                    MatchThresholdPicker(
                        currentValue = state.minSimilarity,
                        onValueChange = { onAction(HomeAction.OnThresholdChange(it)) },
                        modifier = Modifier.padding(horizontal = horizontalPadding).revealFromBottom(delayMillis = 180)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExpandableSessionSection(
                hasCompletedIntuition = state.hasCompletedIntuitionToday,
                hasCompletedPreference = state.hasCompletedPreferenceToday,
                onStartSession = { sessionType -> onAction(HomeAction.OnStartSession(sessionType)) },
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
                    .revealFromBottom(delayMillis = 200)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        FloatingActionButton(
            onClick = onShowSheet,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 24.dp)
                .revealFromBottom(delayMillis = 300)
        ) {
            Icon(
                painter = painterResource(R.drawable.speed_24px),
                contentDescription = "Ayarı Aç",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HomeBackgroundBlobs() {
    Box(
        modifier = Modifier
            .size(450.dp)
            .offset(x = (-150).dp, y = (-100).dp)
            .blur(100.dp)
            .background(Color(0xFF7B5EA7).copy(alpha = 0.12f), CircleShape)
    )
    Box(
        modifier = Modifier
            .size(400.dp)
            .offset(x = 300.dp, y = 200.dp)
            .blur(90.dp)
            .background(Color(0xFF4568DC).copy(alpha = 0.1f), CircleShape)
    )
}

@Composable
private fun HomeHeader(
    onSettingsClick: () -> Unit,
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = rememberDrawablePainter(
                        AppCompatResources.getDrawable(
                            context,
                            R.mipmap.ic_launcher
                        )
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .floating(offsetY = 8f, duration = 2500)
                )
                Column {
                    Text(
                        text = "Aurania",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Soul Connections",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
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
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFFFFD700).copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(colors = listOf(Color(0xFF2D1B69), Color(0xFF1A1A2E))))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFFFD700).copy(alpha = 0.4f),
                        Color(0xFFFFA500).copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .bounceClick(onClick = onUpgradeClick)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = shimmer * size.width }
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f),
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
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "👑", fontSize = 34.sp)
                Column {
                    Text(
                        text = stringResource(R.string.upgrade),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.use_limitless),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Outlined.Face,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun DailyActivityStatCard(
    streak: Int,
    completedToday: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isDark) Brush.linearGradient(
        listOf(
            Color(0xFF24243E).copy(alpha = 0.85f),
            Color(0xFF1A1A2E).copy(alpha = 0.95f)
        )
    )
    else Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = 0.95f),
            Color(0xFFF8F5FF).copy(alpha = 0.9f)
        )
    )

    val borderColor =
        if (isDark) Color(0xFF7B5EA7).copy(alpha = 0.3f) else Color(0xFF7B5EA7).copy(alpha = 0.1f)

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    8.dp,
                    RoundedCornerShape(20.dp),
                    ambientColor = Color(0xFFFF6B6B).copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(cardBackground)
                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "🔥", fontSize = 30.sp)
                Text(
                    text = "$streak",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (streak == 0) stringResource(R.string.no_streak) else pluralStringResource(
                        id = R.plurals.days_streak,
                        count = streak,
                        streak
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    8.dp,
                    RoundedCornerShape(20.dp),
                    ambientColor = if (completedToday) Color(0xFF43E97B).copy(alpha = 0.1f) else Color(
                        0xFF7B5EA7
                    ).copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(cardBackground)
                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (completedToday) "✅" else "⏳", fontSize = 30.sp)
                Text(
                    text = stringResource(R.string.today),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (completedToday) stringResource(R.string.completed) else stringResource(
                        R.string.waiting
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EnhancedMatchCard(
    matchedUser: AuthUser,
    similarity: Int,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isDark) Brush.linearGradient(
        listOf(
            Color(0xFF2D1B69),
            Color(0xFF1A1A2E),
            Color(0xFF0F0C29)
        )
    )
    else Brush.linearGradient(listOf(Color(0xFF7B5EA7), Color(0xFF4568DC)))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(
                24.dp,
                RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.4f),
                spotColor = Color(0xFF4568DC).copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(cardBackground)
            .border(
                1.5.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0xFF7B5EA7).copy(alpha = 0.5f),
                        Color(0xFF4568DC).copy(alpha = 0.3f)
                    )
                ),
                RoundedCornerShape(28.dp)
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
                    text = stringResource(R.string.today_match),
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF7B5EA7).copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ), CircleShape
                        )
                        .blur(20.dp)
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(3.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (matchedUser.photoUrl?.isNotBlank() == true) AsyncImage(
                        model = matchedUser.photoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    else Text(
                        text = matchedUser.name.firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = matchedUser.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            SimilarityIndicator(similarity = similarity)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.view_details),
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
        Text(
            text = stringResource(R.string.spiritual_harmony),
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
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
                                    similarity >= 80 -> listOf(
                                        Color(0xFF43E97B),
                                        Color(0xFF38F9D7)
                                    ); similarity >= 60 -> listOf(
                                        Color(0xFF610DE7),
                                        Color(0xFF4568DC)
                                    ); else -> listOf(Color(0xFFFFD700), Color(0xFFFF9A56))
                                }
                            )
                        )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$similarity%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = when {
                        similarity >= 80 -> Color(0xFF43E97B); similarity >= 60 -> Color(0xFFCEB8FF); else -> Color(
                            0xFFFFD700
                        )
                    }
                )
                Column {
                    Text(
                        text = when {
                            similarity >= 80 -> "${stringResource(R.string.perfect_harmony)} 🔥"; similarity >= 60 -> "${
                                stringResource(
                                    R.string.very_good_harmony
                                )
                            } ✨"; similarity >= 40 -> "${stringResource(R.string.good_harmony)} 💫"; similarity >= 20 -> "${
                                stringResource(
                                    R.string.interesting
                                )
                            } 🌟"; else -> "✨ ${stringResource(R.string.no_harmony)}"
                        }, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.connection),
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
    isDark: Boolean,
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

    val cardBackground = if (isDark) Brush.linearGradient(
        listOf(
            Color(0xFF24243E).copy(alpha = 0.85f),
            Color(0xFF1A1A2E).copy(alpha = 0.95f)
        )
    )
    else Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = 0.95f),
            Color(0xFFF8F5FF).copy(alpha = 0.9f)
        )
    )

    val borderColor =
        if (isDark) Color(0xFF7B5EA7).copy(alpha = 0.3f) else Color(0xFF7B5EA7).copy(alpha = 0.1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                8.dp,
                RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(cardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(28.dp))
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
                modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale })
            Text(
                text = stringResource(R.string.home_no_matches),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.home_subtitle),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
