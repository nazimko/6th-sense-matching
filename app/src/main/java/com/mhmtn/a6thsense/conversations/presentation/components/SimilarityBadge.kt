package com.mhmtn.a6thsense.conversations.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.mhmtn.a6thsense.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimilarityBadge(
    modifier: Modifier = Modifier,
    similarity: Int,
    size: SimilarityBadgeContract.SimilarityBadgeSize = SimilarityBadgeContract.SimilarityBadgeSize.MEDIUM
) {
    val dimensions = when (size) {
        SimilarityBadgeContract.SimilarityBadgeSize.SMALL ->
            SimilarityBadgeContract.SimilarityBadgeDimensions(10.sp, 8.sp, 6.dp, 3.dp)

        SimilarityBadgeContract.SimilarityBadgeSize.MEDIUM ->
            SimilarityBadgeContract.SimilarityBadgeDimensions(12.sp, 10.sp, 8.dp, 4.dp)

        SimilarityBadgeContract.SimilarityBadgeSize.LARGE ->
            SimilarityBadgeContract.SimilarityBadgeDimensions(14.sp, 12.sp, 10.dp, 5.dp)
    }

    val (fontSize, iconSize, paddingHorizontal, paddingVertical) = dimensions

    // Renk gradient'i benzerliğe göre
    val gradientColors = when {
        similarity >= 80 -> listOf(Color(0xFF43E97B), Color(0xFF38F9D7)) // Yeşil
        similarity >= 60 -> listOf(Color(0xFF7B5EA7), Color(0xFF4568DC)) // Mor
        else -> listOf(Color(0xFFFFD700), Color(0xFFFFA500)) // Altın
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(gradientColors.map { it.copy(alpha = 0.2f) })
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(gradientColors.map { it.copy(alpha = 0.5f) }),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Icon
            Text(
                text = when {
                    similarity >= 80 -> "🔥"
                    similarity >= 60 -> "✨"
                    else -> "💫"
                },
                fontSize = iconSize
            )

            Text(
                text = R.string.similarity_text.toString(),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )

            // Percentage
            Text(
                text = "%$similarity",
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = gradientColors[0]
            )
        }
    }
}
