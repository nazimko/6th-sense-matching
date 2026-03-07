package com.mhmtn.a6thsense.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mhmtn.a6thsense.profile.domain.ConfettiParticle
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ConfettiEffect(onComplete: () -> Unit) {
    val confettiColors = listOf(
        Color(0xFFFF6B6B), Color(0xFF7B5EA7), Color(0xFF4568DC),
        Color(0xFFFFD700), Color(0xFF43E97B), Color(0xFFFF9A56)
    )

    val particles = remember {
        List(60) {
            ConfettiParticle(
                color = confettiColors.random(),
                x = Random.nextFloat(),
                delay = Random.nextInt(0, 500)
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(3000)
        onComplete()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            ConfettiParticleView(particle = particle)
        }
    }
}