package com.mhmtn.a6thsense.conversations.presentation.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

object SimilarityBadgeContract {

    data class SimilarityBadgeDimensions(
        val fontSize: TextUnit,
        val iconSize: TextUnit,
        val paddingHorizontal: Dp,
        val paddingVertical: Dp
    )

    enum class SimilarityBadgeSize {
        SMALL, MEDIUM, LARGE
    }
}