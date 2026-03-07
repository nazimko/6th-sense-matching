package com.mhmtn.a6thsense.profile.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.profile.domain.ConfettiParticle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ConfettiParticleView(particle: ConfettiParticle) {
    val yOffset = remember { Animatable(-50f) }
    val alpha = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(particle.delay.toLong())
        kotlinx.coroutines.coroutineScope {
            launch {
                yOffset.animateTo(
                    targetValue = 1200f,
                    animationSpec = tween(
                        durationMillis = 2500,
                        easing = LinearEasing
                    )
                )
            }
            launch {
                rotation.animateTo(
                    targetValue = 720f,
                    animationSpec = tween(2500, easing = LinearEasing)
                )
            }
            launch {
                delay(1500)
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(1000)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = particle.x * size.width
                translationY = yOffset.value
                rotationZ = rotation.value
                this.alpha = alpha.value
            }
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(
                    if (Random.nextBoolean()) CircleShape
                    else RoundedCornerShape(2.dp)
                )
                .background(particle.color)
        )
    }
}