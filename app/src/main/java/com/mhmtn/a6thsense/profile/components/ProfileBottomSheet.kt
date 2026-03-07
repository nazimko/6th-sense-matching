package com.mhmtn.a6thsense.profile.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.profile.domain.Badge
import com.mhmtn.a6thsense.profile.domain.ProfileStats
import com.mhmtn.a6thsense.profile.presentation.ProfileContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    type: ProfileContract.BottomSheetType,
    stats: ProfileStats,
    badges: List<Badge>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF8F5FF),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        when (type) {
            ProfileContract.BottomSheetType.MATCHES -> MatchesSheetContent(stats)
            ProfileContract.BottomSheetType.ACTIVITY_STATS -> ActivityStatsSheetContent(stats)
            ProfileContract.BottomSheetType.BADGES -> BadgesSheetContent(badges)
        }
    }
}