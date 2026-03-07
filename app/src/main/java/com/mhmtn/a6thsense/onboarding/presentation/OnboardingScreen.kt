package com.mhmtn.a6thsense.onboarding.presentation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import com.mhmtn.a6thsense.R
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.mhmtn.a6thsense.core.presentation.bounceClick
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onAction: (OnboardingContract.Action) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        onAction(OnboardingContract.Action.Complete)
    }

    val currentPage = onboardingPages[pagerState.currentPage]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = currentPage.gradientColors.map { Color(it) }
                )
            )
    ) {
        // Dekoratif parçacıklar
        OnboardingParticles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip butonu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    Text(
                        text = R.string.skip.toString(),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 15.sp,
                        modifier = Modifier
                            .bounceClick {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        onboardingPages.size - 1
                                    )
                                }
                            }
                            .padding(8.dp)
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageView(
                    page = onboardingPages[page],
                    pageOffset = (pagerState.currentPage - page) +
                            pagerState.currentPageOffsetFraction
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Dot indikatörler
            PagerIndicator(
                currentPage = pagerState.currentPage,
                totalPages = onboardingPages.size
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Buton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                        )
                    )
                    .bounceClick {
                        if (pagerState.currentPage < onboardingPages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1
                                )
                            }
                        } else {
                            // Son sayfada bildirim izni iste
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            } else {
                                onAction(OnboardingContract.Action.Complete)
                            }
                        }
                    }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (pagerState.currentPage < onboardingPages.size - 1)
                        R.string.continue_text.toString()
                    else
                        "${R.string.begin.toString()} 🔮",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun OnboardingPageView(
    page: OnboardingPage,
    pageOffset: Float
) {
    val scale = 1f - 0.1f * kotlin.math.abs(pageOffset)
    val alpha = 1f - 0.3f * kotlin.math.abs(pageOffset)

    // Emoji pulse animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "emoji_pulse")
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Emoji + Glow
        Box(contentAlignment = Alignment.Center) {
            // Glow efekti
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF7B5EA7).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )
            Text(
                text = page.emoji,
                fontSize = 88.sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = emojiScale
                    scaleY = emojiScale
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun PagerIndicator(
    currentPage: Int,
    totalPages: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isSelected) 32.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "dot_width"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White
                        else Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
private fun OnboardingParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    // Yüzen parçacıklar
    listOf(
        Triple(0.1f, 0.2f, 2200),
        Triple(0.8f, 0.15f, 1800),
        Triple(0.3f, 0.7f, 2500),
        Triple(0.7f, 0.6f, 2000),
        Triple(0.5f, 0.4f, 1600),
        Triple(0.15f, 0.5f, 2100),
        Triple(0.85f, 0.8f, 1900)
    ).forEach { (x, startY, duration) ->
        val offsetY by infiniteTransition.animateFloat(
            initialValue = startY,
            targetValue = startY - 0.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_$duration"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha_$duration"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .graphicsLayer {
                        translationX = x * size.width
                        translationY = offsetY * size.height
                        this.alpha = alpha
                    }
                    .background(Color.White, CircleShape)
            )
        }
    }
}