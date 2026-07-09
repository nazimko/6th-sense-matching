package com.mhmtn.a6thsense.profile.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.mhmtn.a6thsense.core.presentation.NetworkErrorView
import com.mhmtn.a6thsense.core.presentation.ProfileScreenSkeleton
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.core.presentation.revealFromBottom
import com.mhmtn.a6thsense.profile.components.ConfettiEffect
import com.mhmtn.a6thsense.profile.components.FriendsCard
import com.mhmtn.a6thsense.profile.components.ProfileBottomSheet
import com.mhmtn.a6thsense.profile.domain.ProfileStats
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun ProfileScreen(
    state: ProfileContract.State,
    showConfetti: Boolean,
    isDark: Boolean,
    onUploadProfileImage: (Uri) -> Unit,
    onConfettiComplete: () -> Unit,
    onAction: (ProfileContract.Action) -> Unit
) {
    if (state.isLoading) {
        ProfileScreenSkeleton(isDark = isDark)
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
                .background(MaterialTheme.colorScheme.background)
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

                ProfileHeader(
                    modifier = Modifier.revealFromBottom(delayMillis = 0),
                    name = state.user?.name ?: "",
                    isPremium = state.isPremium,
                    photoUrl = state.user?.photoUrl ?: "",
                    onUploadProfileImage = onUploadProfileImage,
                    onAction = onAction
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
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ✅ Invite Button - Column içine (Column'un en altına) taşındı
                InviteButton(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .revealFromBottom(delayMillis = 300),
                    onInviteClick = {
                        onAction(ProfileContract.Action.onInviteClick)
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
        }
    }
}

@Composable
private fun InviteButton(
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                )
            )
            .bounceClick(onClick = onInviteClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🎁", fontSize = 18.sp)
            Text(
                text = stringResource(R.string.invite),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
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
    onUploadProfileImage: (Uri) -> Unit,
    onAction: (ProfileContract.Action) -> Unit,
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
    var showPhotoPreview by remember { mutableStateOf(false) }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {uri->
        if (uri != null) {
            onUploadProfileImage(uri)
        } else {
            onAction(ProfileContract.Action.Error(UiText.StringResource(R.string.no_image_selected)))
        }
    }

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
                    .size(120.dp)
                    .clickable{
                        if (photoUrl.isNotBlank()) showPhotoPreview = true
                    }
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
                            .background(MaterialTheme.colorScheme.surfaceVariant),
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

                IconButton(
                    onClick = {
                        pickMediaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 12.dp, y = 12.dp)
                        .background(Color(0xFF9900FF), CircleShape)
                        .padding(8.dp)
                        .size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_a_photo_24px),
                        contentDescription = "Change Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }


            }
            if (isPremium) {
                PremiumSparkles()
            }

            if (showPhotoPreview) {
                var isVisible by remember { mutableStateOf(false) }
                var scale2 by remember { mutableStateOf(1f) }
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }  // mevcut offsetY'yi bununla değiştir
                val isDraggingToDismiss = scale2 <= 1f
                val dismissThreshold = 200f

                // Açılış animasyonları
                val alpha by animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(300),
                    label = "alpha"
                )
                val scale by animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0.6f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "scale"
                )

                // Swipe'a göre arka plan kararmayı azalt
                val backgroundAlpha = (1f - (abs(offsetY) / (dismissThreshold * 2.5f)))
                    .coerceIn(0f, 1f)

                LaunchedEffect(Unit) { isVisible = true }

                fun dismiss() {
                    isVisible = false
                }

                var isPinching by remember { mutableStateOf(false) }

                Dialog(
                    onDismissRequest = { dismiss(); showPhotoPreview = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    // Animasyon bitince dialog'u kapat
                    LaunchedEffect(isVisible) {
                        if (!isVisible) {
                            delay(300)
                            showPhotoPreview = false
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.92f * backgroundAlpha))
                            .clickable { dismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .graphicsLayer {
                                    scaleX = scale * scale2
                                    scaleY = scale * scale2
                                    this.alpha = alpha
                                    translationY = offsetY
                                    translationX = offsetX
                                }
                                .clip(RoundedCornerShape(12.dp))
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        val firstDown = awaitFirstDown(requireUnconsumed = false)
                                        var secondDown = false

                                        // İkinci parmak geldi mi kontrol et
                                        withTimeoutOrNull(100) {
                                            awaitPointerEvent().changes.let { changes ->
                                                if (changes.size >= 2) secondDown = true
                                            }
                                        }

                                        if (secondDown) {
                                            // Pinch modu
                                            isPinching = true
                                            do {
                                                val event = awaitPointerEvent()
                                                val changes = event.changes

                                                if (changes.size >= 2) {
                                                    val c1 = changes[0]
                                                    val c2 = changes[1]

                                                    // Zoom hesapla
                                                    val prevDist = (c1.previousPosition - c2.previousPosition).getDistance()
                                                    val currDist = (c1.position - c2.position).getDistance()
                                                    if (prevDist > 0) {
                                                        val zoom = currDist / prevDist
                                                        scale2 = (scale2 * zoom).coerceIn(1f, 5f)
                                                    }

                                                    // Pan hesapla (zoom'dayken)
                                                    if (scale2 > 1f) {
                                                        val panX = changes.map { it.position.x - it.previousPosition.x }.average().toFloat()
                                                        val panY = changes.map { it.position.y - it.previousPosition.y }.average().toFloat()
                                                        offsetX += panX
                                                        offsetY += panY
                                                    }

                                                    changes.forEach { it.consume() }
                                                }
                                            } while (changes.any { it.pressed }.also { if (!it) isPinching = false })

                                            // Zoom'dan çıkınca sıfırla
                                            if (scale2 <= 1f) {
                                                offsetX = 0f
                                                offsetY = 0f
                                            }
                                        } else {
                                            // Tek parmak modu — swipe to dismiss veya çift tıklama
                                            var dragAmount = 0f
                                            var tapTime = System.currentTimeMillis()
                                            var isDragging = false

                                            do {
                                                val event = awaitPointerEvent()
                                                val change = event.changes.firstOrNull() ?: break
                                                val dy = change.position.y - change.previousPosition.y

                                                if (abs(dy) > 5f) isDragging = true

                                                if (isDragging && scale2 <= 1f) {
                                                    offsetY += dy
                                                    dragAmount += dy
                                                    change.consume()
                                                }
                                            } while (event.changes.any { it.pressed })

                                            when {
                                                // Çift tıklama
                                                !isDragging && System.currentTimeMillis() - tapTime < 300 -> {
                                                    scale2 = if (scale2 > 1f) 1f else 2f
                                                    offsetX = 0f
                                                    offsetY = 0f
                                                }
                                                // Swipe to dismiss
                                                abs(offsetY) > dismissThreshold && scale2 <= 1f -> dismiss()
                                                // Snap back
                                                else -> offsetY = 0f
                                            }
                                        }
                                    }
                                },
                            contentScale = ContentScale.Fit
                        )

                        IconButton(
                            onClick = { dismiss() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .graphicsLayer { this.alpha = alpha }
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // İsim
        Text(
            text = name,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
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
            text = stringResource(R.string.statistics),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
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
                label = if (stats.currentStreak == 0) {
                    stringResource(R.string.no_streak)
                } else {
                    pluralStringResource(
                        id = R.plurals.days_streak,
                        count = stats.currentStreak,
                        stats.currentStreak
                    )
                },
                gradient = listOf(Color(0xFFFF6B6B), Color(0xFFEE5A6F)),
                onClick = null // Streak için sheet yok
            )
            StatCard(
                modifier = Modifier.weight(1f),
                emoji = "✨",
                value = stats.totalActivities.toString(),
                label = stringResource(R.string.activities),
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
                label = stringResource(R.string.matches),
                gradient = listOf(Color(0xFFB06AB3), Color(0xFF4568DC)),
                onClick = onMatchesClick // 👈
            )
            StatCard(
                modifier = Modifier.weight(1f),
                emoji = "📅",
                value = "${stats.memberSinceDays}",
                label = stringResource(R.string.member_since),
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
            .background(MaterialTheme.colorScheme.surface)
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
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
