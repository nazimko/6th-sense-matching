package com.mhmtn.a6thsense.soulsync.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R

@Composable
fun RevealAnswersScreen(
    question: String,
    myAnswer: String,
    theirAnswer: String,
    isMatch: Boolean,
    showConfetti: Boolean,
    pointsEarned: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "reveal")

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
                .padding(32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Soru
            Text(
                text = question,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cevap kartları
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // My answer
                AnswerCard(
                    answer = myAnswer,
                    label = R.string.your_answer.toString(),
                    isMatch = isMatch,
                    modifier = Modifier.weight(1f)
                )

                // Their answer
                AnswerCard(
                    answer = theirAnswer,
                    label = R.string.your_matches_answer.toString(),
                    isMatch = isMatch,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Result badge
            ResultBadge(
                isMatch = isMatch,
                pointsEarned = pointsEarned
            )
        }

        // Konfeti
        if (showConfetti && isMatch) {
            ConfettiAnimation(
                isVisible = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun AnswerCard(
    answer: String,
    label: String,
    isMatch: Boolean,
    modifier: Modifier = Modifier
) {
    // Flip animation
    val rotation by animateFloatAsState(
        targetValue = 180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_flip"
    )

    Box(
        modifier = modifier
            .aspectRatio(0.75f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        // Front (back side)
        if (rotation <= 90f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.5f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2D1B69),
                                Color(0xFF1A1A2E)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = Color(0xFF7B5EA7).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔮",
                    fontSize = 48.sp,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            }
        }
        // Back (answer)
        else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = if (isMatch)
                            Color(0xFF43E97B).copy(alpha = 0.5f)
                        else
                            Color(0xFF7B5EA7).copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = if (isMatch) listOf(
                                Color(0xFF43E97B).copy(alpha = 0.3f),
                                Color(0xFF38F9D7).copy(alpha = 0.2f)
                            ) else listOf(
                                Color(0xFF2D1B69),
                                Color(0xFF1A1A2E)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = if (isMatch) listOf(
                                Color(0xFF43E97B),
                                Color(0xFF38F9D7)
                            ) else listOf(
                                Color(0xFF7B5EA7).copy(alpha = 0.5f),
                                Color(0xFF4568DC).copy(alpha = 0.5f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = answer,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ResultBadge(
    isMatch: Boolean,
    pointsEarned: Int
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "badge_scale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = if (isMatch)
                    Color(0xFF43E97B).copy(alpha = 0.5f)
                else
                    Color(0xFFFFD700).copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = if (isMatch) listOf(
                        Color(0xFF43E97B),
                        Color(0xFF38F9D7)
                    ) else if (pointsEarned > 0) listOf(
                        Color(0xFFFFD700),
                        Color(0xFFFFA500)
                    ) else listOf(
                        Color(0xFF7B5EA7).copy(alpha = 0.5f),
                        Color(0xFF4568DC).copy(alpha = 0.5f)
                    )
                )
            )
            .padding(horizontal = 32.dp, vertical = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Emoji
            Text(
                text = when {
                    isMatch -> "🎉"
                    pointsEarned > 0 -> "✨"
                    else -> "💭"
                },
                fontSize = 48.sp
            )

            // Text
            Text(
                text = when {
                    isMatch -> R.string.match_perfect.toString()
                    pointsEarned > 0 -> R.string.match_near.toString()
                    else -> R.string.match_different.toString()
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            // Points
            Text(
                text = "+$pointsEarned ${R.string.points.toString()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}