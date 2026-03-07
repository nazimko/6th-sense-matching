package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import com.mhmtn.a6thsense.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PreferencePhaseTransitionView(
    phase: DailyActivityContract.Phase,
    onTransitionEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phaseTitle = when (phase) {
        DailyActivityContract.Phase.PHASE_1 -> R.string.elements.toString()
        DailyActivityContract.Phase.PHASE_2 -> R.string.colors.toString()
        DailyActivityContract.Phase.PHASE_3 -> R.string.nature.toString()
        DailyActivityContract.Phase.PHASE_4 -> R.string.moods.toString()
        DailyActivityContract.Phase.PHASE_5 -> R.string.preferenses.toString()
        DailyActivityContract.Phase.PHASE_6 -> R.string.last_questions.toString()
    }

    val phaseEmoji = when (phase) {
        DailyActivityContract.Phase.PHASE_1 -> "🔥"
        DailyActivityContract.Phase.PHASE_2 -> "🎨"
        DailyActivityContract.Phase.PHASE_3 -> "🌲"
        DailyActivityContract.Phase.PHASE_4 -> "😌"
        DailyActivityContract.Phase.PHASE_5 -> "💭"
        DailyActivityContract.Phase.PHASE_6 -> "✨"
    }

    val phaseColor = when (phase) {
        DailyActivityContract.Phase.PHASE_1 -> Color(0xFFf093fb)
        DailyActivityContract.Phase.PHASE_2 -> Color(0xFFa8edea)
        DailyActivityContract.Phase.PHASE_3 -> Color(0xFF43E97B)
        DailyActivityContract.Phase.PHASE_4 -> Color(0xFFFFD700)
        DailyActivityContract.Phase.PHASE_5 -> Color(0xFF667eea)
        DailyActivityContract.Phase.PHASE_6 -> Color(0xFFf5576c)
    }

    // Animations
    val scale = remember { Animatable(0.3f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val circleScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Entrance animations
        launch {
            scale.animateTo(
                1.1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            scale.animateTo(1f, animationSpec = tween(200))
        }

        launch { alpha.animateTo(1f, animationSpec = tween(400)) }
        launch { circleScale.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing)) }
        launch {
            rotation.animateTo(
                360f,
                animationSpec = tween(1500, easing = LinearEasing)
            )
        }

        // Hold
        delay(1500)

        // Exit animations
        launch { alpha.animateTo(0f, animationSpec = tween(300)) }
        launch { scale.animateTo(1.2f, animationSpec = tween(300)) }

        delay(300)
        onTransitionEnd()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        phaseColor.copy(alpha = 0.3f),
                        Color(0xFF1A1A2E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background circles
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size((200 + index * 80).dp)
                    .scale(circleScale.value)
                    .alpha(0.1f - index * 0.03f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                phaseColor.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            // Emoji with rotation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                phaseColor.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = phaseEmoji,
                    fontSize = 64.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Session type badge
            Box(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                phaseColor.copy(alpha = 0.3f),
                                phaseColor.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "✨ Vibe Check",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = phaseColor
                )
            }

            // Phase title
            Text(
                text = phaseTitle,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            // Progress indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == phase.ordinal) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == phase.ordinal)
                                    phaseColor
                                else
                                    Color.White.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}