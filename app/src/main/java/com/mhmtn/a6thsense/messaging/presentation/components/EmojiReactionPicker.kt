package com.mhmtn.a6thsense.messaging.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val AVAILABLE_REACTIONS = listOf("❤️", "😂", "😮", "😢", "😡", "👍", "🔥", "✨")

@Composable
fun EmojiReactionPicker(
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}