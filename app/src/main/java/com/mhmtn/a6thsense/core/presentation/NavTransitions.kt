package com.mhmtn.a6thsense.core.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*

// Sağdan sola (ileri gitme)
fun slideInFromRight() = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(350, easing = EaseOutCubic)
) + fadeIn(animationSpec = tween(350))

fun slideOutToLeft() = slideOutHorizontally(
    targetOffsetX = { -it / 3 },
    animationSpec = tween(350, easing = EaseOutCubic)
) + fadeOut(animationSpec = tween(200))

// Soldan sağa (geri gitme)
fun slideInFromLeft() = slideInHorizontally(
    initialOffsetX = { -it / 3 },
    animationSpec = tween(350, easing = EaseOutCubic)
) + fadeIn(animationSpec = tween(350))

fun slideOutToRight() = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(350, easing = EaseOutCubic)
) + fadeOut(animationSpec = tween(200))

// Aşağıdan yukarı (modal/sheet tarzı ekranlar)
fun slideInFromBottom() = slideInVertically(
    initialOffsetY = { it },
    animationSpec = tween(400, easing = EaseOutCubic)
) + fadeIn(animationSpec = tween(300))

fun slideOutToBottom() = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = tween(350, easing = EaseInCubic)
) + fadeOut(animationSpec = tween(200))

// Fade (tab geçişleri için)
fun fadeInTransition() = fadeIn(
    animationSpec = tween(300, easing = EaseOutCubic)
)

fun fadeOutTransition() = fadeOut(
    animationSpec = tween(200, easing = EaseInCubic)
)

// Scale + Fade (splash/auth için wow etkisi)
fun scaleInTransition() = scaleIn(
    initialScale = 0.92f,
    animationSpec = tween(400, easing = EaseOutCubic)
) + fadeIn(animationSpec = tween(400))

fun scaleOutTransition() = scaleOut(
    targetScale = 0.92f,
    animationSpec = tween(300, easing = EaseInCubic)
) + fadeOut(animationSpec = tween(300))