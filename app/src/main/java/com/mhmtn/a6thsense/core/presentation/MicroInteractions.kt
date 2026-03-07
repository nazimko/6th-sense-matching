package com.mhmtn.a6thsense.core.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback

// ==================== PRESS SCALE ====================
// Basılınca küçülen, bırakınca spring ile geri dönen modifier

fun Modifier.pressScale(
    scaleDown: Float = 0.94f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "press_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
}

// ==================== BOUNCE CLICK ====================
// Tıklanınca bounce efekti veren modifier

fun Modifier.bounceClick(enabled: Boolean = true,onClick: () -> Unit): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bounce"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) { // 👈 enabled'a göre değişir
            if (enabled) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onClick()
                    }
                )
            }
        }
}

// ==================== SHAKE ====================
// Hata durumunda sallanan modifier

fun Modifier.shake(trigger: Boolean): Modifier = composed {
    val offsetX by animateFloatAsState(
        targetValue = if (trigger) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = 500
            0f at 0
            -16f at 80
            16f at 160
            -12f at 240
            12f at 320
            -8f at 400
            0f at 500
        },
        label = "shake"
    )

    this.graphicsLayer { translationX = offsetX }
}

// ==================== PULSE ====================
// Sürekli nabız gibi atan modifier

@Composable
fun Modifier.pulse(
    minScale: Float = 0.97f,
    maxScale: Float = 1.03f,
    duration: Int = 1500
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

// ==================== FLOAT ====================
// Yukarı aşağı yüzen modifier (hero elementler için)

@Composable
fun Modifier.floating(
    offsetY: Float = 8f,
    duration: Int = 2000
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = offsetY,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    return this.graphicsLayer { translationY = offset }
}

// ==================== REVEAL ====================
// Ekrana ilk girişte aşağıdan yukarı çıkan modifier

@Composable
fun Modifier.revealFromBottom(
    delayMillis: Int = 0
): Modifier {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        visible = true
    }

    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 60f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "reveal"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400),
        label = "reveal_alpha"
    )

    return this.graphicsLayer {
        translationY = offsetY
        this.alpha = alpha
    }
}