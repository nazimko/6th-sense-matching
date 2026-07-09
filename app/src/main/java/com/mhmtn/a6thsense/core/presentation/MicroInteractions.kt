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
): Modifier = composed {// Source code removed.}

// ==================== BOUNCE CLICK ====================
// Tıklanınca bounce efekti veren modifier

fun Modifier.bounceClick(enabled: Boolean = true,onClick: () -> Unit): Modifier = composed {// Source code removed.}

// ==================== SHAKE ====================
// Hata durumunda sallanan modifier

fun Modifier.shake(trigger: Boolean): Modifier = composed {// Source code removed.}

// ==================== PULSE ====================
// Sürekli nabız gibi atan modifier

@Composable
fun Modifier.pulse(
    minScale: Float = 0.97f,
    maxScale: Float = 1.03f,
    duration: Int = 1500
): Modifier {// Source code removed.}

// ==================== FLOAT ====================
// Yukarı aşağı yüzen modifier (hero elementler için)

@Composable
fun Modifier.floating(
    offsetY: Float = 8f,
    duration: Int = 2000
): Modifier {// Source code removed.}

// ==================== REVEAL ====================
// Ekrana ilk girişte aşağıdan yukarı çıkan modifier

@Composable
fun Modifier.revealFromBottom(
    delayMillis: Int = 0
): Modifier {// Source code removed.}