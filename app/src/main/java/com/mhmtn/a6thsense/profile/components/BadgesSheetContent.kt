package com.mhmtn.a6thsense.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.profile.domain.Badge

@Composable
fun BadgesSheetContent(badges: List<Badge>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "👑 ${R.string.badges_text.toString()}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D1B69)
            )

            val unlockedCount = badges.count { it.isUnlocked }
            Text(
                text = "$unlockedCount/${badges.size}",
                fontSize = 14.sp,
                color = Color(0xFF7B5EA7),
                fontWeight = FontWeight.SemiBold
            )
        }

        badges.forEach { badge ->
            BadgeItem(badge = badge)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}