package com.mhmtn.a6thsense.discover.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.core.presentation.bounceClick

@Composable
fun SwipeButtons(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Geç butonu
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFFFF6B6B).copy(alpha = 0.3f)
                )
                .clip(CircleShape)
                .background(Color.White)
                .bounceClick(onClick = onSwipeLeft),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "👋", fontSize = 28.sp)
        }

        // Mesajlaş butonu
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFF43E97B).copy(alpha = 0.4f)
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF43E97B), Color(0xFF38F9D7))
                    )
                )
                .bounceClick(onClick = onSwipeRight),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Text(text = "💬", fontSize = 32.sp)
            }
        }

        // Süper like / tekrar bak butonu
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFF7B5EA7).copy(alpha = 0.3f)
                )
                .clip(CircleShape)
                .background(Color.White)
                .bounceClick(onClick = {}), // İleride eklenebilir
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⭐", fontSize = 28.sp)
        }
    }
}