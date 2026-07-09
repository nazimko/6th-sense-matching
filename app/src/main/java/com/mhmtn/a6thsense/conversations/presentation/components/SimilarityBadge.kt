package com.mhmtn.a6thsense.conversations.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R

@Composable
fun SimilarityBadge(
    modifier: Modifier = Modifier,
    similarity: Int,
    size: SimilarityBadgeContract.SimilarityBadgeSize = SimilarityBadgeContract.SimilarityBadgeSize.MEDIUM,
    isDark: Boolean
) {
    val dimensions = when (size) {
        SimilarityBadgeContract.SimilarityBadgeSize.SMALL ->
            SimilarityBadgeContract.SimilarityBadgeDimensions(10.sp, 9.sp, 8.dp, 3.dp)
        SimilarityBadgeContract.SimilarityBadgeSize.MEDIUM ->
            SimilarityBadgeContract.SimilarityBadgeDimensions(12.sp, 11.sp, 10.dp, 4.dp)
        SimilarityBadgeContract.SimilarityBadgeSize.LARGE ->
            SimilarityBadgeContract.SimilarityBadgeDimensions(14.sp, 13.sp, 12.dp, 6.dp)
    }

    val (fontSize, iconSize, paddingHorizontal, paddingVertical) = dimensions

    // Renk skalası - Benzerliğe göre
    val baseColor = when {
        similarity >= 80 -> Color(0xFF43E97B) // Yeşil
        similarity >= 60 -> Color(0xFF7B5EA7) // Mor
        else -> Color(0xFFFFD700) // Altın
    }

    // Metin ve ikon rengi (Light mode'da daha koyu, Dark mode'da orijinal renk)
    val contentColor = when {
        similarity >= 80 -> if (isDark) Color(0xFF43E97B) else Color(0xFF1B8E3E)
        similarity >= 60 -> if (isDark) Color(0xFFA58FD1) else Color(0xFF5A418A)
        else -> if (isDark) Color(0xFFFFD700) else Color(0xFFB8860B)
    }

    val backgroundColor = contentColor.copy(alpha = if (isDark) 0.15f else 0.1f)
    val borderColor = contentColor.copy(alpha = if (isDark) 0.4f else 0.25f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Emoji İkonu
            Text(
                text = when {
                    similarity >= 80 -> "🔥"
                    similarity >= 60 -> "✨"
                    else -> "💫"
                },
                fontSize = iconSize
            )

            // "Benzerlik" metni
            Text(
                text = stringResource(R.string.similarity_text),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontSize = fontSize,
                fontWeight = FontWeight.Medium
            )

            // Yüzde Değeri
            Text(
                text = "%$similarity",
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor
            )
        }
    }
}

@Preview(showBackground = true, name = "Similarity Badges Light")
@Composable
fun SimilarityBadgePreview_Light() {
    MaterialTheme {
        Surface(color = Color.White) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SimilarityBadge(similarity = 95, isDark = false)
                SimilarityBadge(similarity = 75, isDark = false)
                SimilarityBadge(similarity = 45, isDark = false)
                SimilarityBadge(
                    similarity = 85, 
                    size = SimilarityBadgeContract.SimilarityBadgeSize.SMALL, 
                    isDark = false
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Similarity Badges Dark")
@Composable
fun SimilarityBadgePreview_Dark() {
    MaterialTheme {
        Surface(color = Color(0xFF1A1A2E)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SimilarityBadge(similarity = 95, isDark = true)
                SimilarityBadge(similarity = 75, isDark = true)
                SimilarityBadge(similarity = 45, isDark = true)
                SimilarityBadge(
                    similarity = 85, 
                    size = SimilarityBadgeContract.SimilarityBadgeSize.SMALL, 
                    isDark = true
                )
            }
        }
    }
}
