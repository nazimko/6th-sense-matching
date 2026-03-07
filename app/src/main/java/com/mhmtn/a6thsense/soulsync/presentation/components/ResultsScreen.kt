package com.mhmtn.a6thsense.soulsync.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.bounceClick
import kotlinx.coroutines.delay
@Composable
fun ResultsScreen(
    myScore: Int,
    theirScore: Int,
    compatibility: Int,
    showConfetti: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "results")

    val shimmer by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween (2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )


    LaunchedEffect(Unit) {
        delay(500)
    }

    Box(
        modifier = modifier
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Trophy icon
            val trophyScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "trophy"
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = trophyScale
                        scaleY = trophyScale
                    }
            ) {
                Text(text = "🏆", fontSize = 100.sp)
            }

            // Title
            Text(
                text = R.string.test_over.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Compatibility circle
            CompatibilityCircle(
                compatibility = compatibility,
                shimmer = shimmer
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Scores
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ScoreCard(
                    score = myScore,
                    label = R.string.you.toString(),
                    modifier = Modifier.weight(1f)
                )

                ScoreCard(
                    score = theirScore,
                    label = R.string.your_match.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Result message
            ResultMessage(compatibility = compatibility)

            Spacer(modifier = Modifier.height(32.dp))

            // Continue button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                        )
                    )
                    .bounceClick(onClick = onContinue)
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = R.string.continue_messaging_text.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Konfeti
        if (showConfetti) {
            ConfettiAnimation(
                isVisible = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun CompatibilityCircle(
    compatibility: Int,
    shimmer: Float
) {
    Box(
        modifier = Modifier
            .size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow rings
        repeat(3) { index ->
            val size = 240.dp - (index * 40.dp)
            Box(
                modifier = Modifier
                    .size(size)
                    .background(
                        when {
                            compatibility >= 80 -> Color(0xFF43E97B)
                            compatibility >= 60 -> Color(0xFF7B5EA7)
                            else -> Color(0xFFFFD700)
                        }.copy(alpha = 0.2f - (index * 0.05f)),
                        CircleShape
                    )
            )
        }

        // Main circle
        Box(
            modifier = Modifier
                .size(200.dp)
                .shadow(
                    elevation = 30.dp,
                    shape = CircleShape,
                    ambientColor = when {
                        compatibility >= 80 -> Color(0xFF43E97B)
                        compatibility >= 60 -> Color(0xFF7B5EA7)
                        else -> Color(0xFFFFD700)
                    }.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = when {
                            compatibility >= 80 -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
                            compatibility >= 60 -> listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                            else -> listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                        }
                    )
                )
                .border(
                    width = 4.dp,
                    color = Color.White.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Shimmer overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = shimmer * size.width * 2
                    }
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "%$compatibility",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            blurRadius = 8f
                        )
                    )
                )

                Text(
                    text = R.string.similarity_text.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun ScoreCard(
    score: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2D1B69).copy(alpha = 0.8f),
                        Color(0xFF1A1A2E).copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF7B5EA7).copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "$score",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = R.string.score.toString(),
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ResultMessage(compatibility: Int) {
    val (emoji, title, message) = when {
        compatibility >= 90 -> Triple(
            "🔥",
            R.string.comp_legendary_title.toString(),
            R.string.comp_legendary_msg.toString()
        )
        compatibility >= 80 -> Triple(
            "✨",
            R.string.comp_perfect_title.toString(),
            R.string.comp_perfect_msg.toString()
        )
        compatibility >= 60 -> Triple(
            "💫",
            R.string.comp_strong_title.toString(),
            R.string.comp_strong_msg.toString()
        )
        compatibility >= 40 -> Triple(
            "🌟",
            R.string.comp_good_title.toString(),
            R.string.comp_good_msg.toString()
        )
        else -> Triple(
            "💭",
            R.string.comp_different_title.toString(),
            R.string.comp_different_msg.toString()
        )
    }

    // UI kodların buraya gelecek (Column, Text vs.)


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = emoji, fontSize = 40.sp)

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}