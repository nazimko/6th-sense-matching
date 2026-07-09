package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import kotlinx.coroutines.delay
import com.mhmtn.a6thsense.R

@Composable
fun PhaseTransitionView(
    phase: DailyActivityContract.Phase,
    onTransitionComplete: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    val gradient = when (phase) {
        DailyActivityContract.Phase.PHASE_1 -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1A1A2E),
                Color(0xFF16213E)
            )
        )

        DailyActivityContract.Phase.PHASE_2 -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFF9A9E),
                Color(0xFFFAD0C4)
            )
        )

        DailyActivityContract.Phase.PHASE_3 -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2E3192),
                Color(0xFF1BFFFF)
            )
        )

        DailyActivityContract.Phase.PHASE_4 -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F2027),
                Color(0xFF203A43)
            )
        )

        DailyActivityContract.Phase.PHASE_5 -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F0C29),
                Color(0xFF302B63),
                Color(0xFF24243E)
            )
        )

        DailyActivityContract.Phase.PHASE_6 -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2E3192),
                Color(0xFF1BFFFF)
            )
        )
    }

    val (title, subtitle) = when (phase) {
        DailyActivityContract.Phase.PHASE_1 -> stringResource(R.string.intuition) to stringResource(R.string.phase1_subtitle)
        DailyActivityContract.Phase.PHASE_2 -> stringResource(R.string.colors) to stringResource(R.string.phase2_subtitle)
        DailyActivityContract.Phase.PHASE_3 -> stringResource(R.string.spirit_animals) to stringResource(R.string.phase3_subtitle)
        DailyActivityContract.Phase.PHASE_4 -> stringResource(R.string.elements) to stringResource(R.string.phase4_subtitle)
        DailyActivityContract.Phase.PHASE_5 -> stringResource(R.string.dimensions) to stringResource(R.string.phase5_subtitle)
        DailyActivityContract.Phase.PHASE_6 -> stringResource(R.string.free_spirit) to stringResource(R.string.phase6_subtitle)
    }

    LaunchedEffect(Unit) {
        visible = true
        delay(2500)
        visible = false
        delay(500)
        onTransitionComplete()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(animationSpec = tween(400)) + scaleOut(
            targetScale = 1.1f,
            animationSpec = tween(400)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PulsingText(
                    text = title,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Composable
private fun PulsingText(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = Color.White.copy(alpha = alpha),
        modifier = Modifier.scale(scale)
    )
}