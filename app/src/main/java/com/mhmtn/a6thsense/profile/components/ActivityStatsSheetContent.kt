package com.mhmtn.a6thsense.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.profile.domain.ProfileStats

@Composable
fun ActivityStatsSheetContent(stats: ProfileStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "✨ ${R.string.activities_details_text.toString()}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D1B69)
        )

        AnimatedCounter(
            target = stats.totalActivities,
            label = R.string.total_activities.toString(),
            gradient = listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
        )

        // Son 7 gün aktivite grafiği
        Text(
            text = R.string.last_7_days.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D1B69).copy(alpha = 0.7f)
        )

        WeeklyActivityChart(weeklyActivity = stats.weeklyActivity)

        MilestoneProgress(
            current = stats.totalActivities,
            milestones = listOf(1, 10, 25, 50, 100),
            color = Color(0xFF4568DC)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}