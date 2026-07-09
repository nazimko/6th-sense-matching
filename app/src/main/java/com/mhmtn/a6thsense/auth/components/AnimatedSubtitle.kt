package com.mhmtn.a6thsense.auth.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.ui.theme.MeditationSoftLavender
import kotlinx.coroutines.delay

@Composable
fun AnimatedSubtitle() {
    val subtitles = listOf(
        stringResource(R.string.auth_subtitle_1),
        stringResource(R.string.auth_subtitle_2),
        stringResource(R.string.auth_subtitle_3),
    )

    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentIndex = (currentIndex + 1) % subtitles.size
        }
    }

    AnimatedContent(
        targetState = subtitles[currentIndex],
        transitionSpec = {
            (fadeIn(animationSpec = tween(800)) +
                    slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(800)
                    )).togetherWith(
                fadeOut(animationSpec = tween(400))
            )
        },
        label = "subtitle"
    ) { text ->
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Light,
                letterSpacing = 1.5.sp
            ),
            color = MeditationSoftLavender.copy(alpha = 0.9f)
        )
    }
}