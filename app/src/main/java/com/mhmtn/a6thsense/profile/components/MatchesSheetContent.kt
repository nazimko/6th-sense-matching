package com.mhmtn.a6thsense.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.profile.domain.ProfileStats

@Composable
fun MatchesSheetContent(stats: ProfileStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "💫 ${stringResource(R.string.matches)}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Büyük sayı gösterimi
        AnimatedCounter(
            target = stats.totalMatches,
            label = stringResource(R.string.total_matches),
            gradient = listOf(Color(0xFFB06AB3), Color(0xFF4568DC))
        )

        // Milestone bar
        MilestoneProgress(
            current = stats.totalMatches,
            milestones = listOf(1, 5, 10, 25, 50),
            color = Color(0xFF7B5EA7)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}