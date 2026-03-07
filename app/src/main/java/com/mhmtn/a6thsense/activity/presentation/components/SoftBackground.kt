package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.mhmtn.a6thsense.ui.theme.MeditationDeepPurple

@Composable
fun SoftBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C29))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MeditationDeepPurple,
                        MeditationDeepPurple.copy(alpha = 0.9f)
                    )
                )
            )
    ) {
        content()
    }
}