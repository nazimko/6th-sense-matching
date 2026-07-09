package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme
import kotlin.math.roundToInt

@Composable
fun MatchThresholdPicker(
    modifier: Modifier = Modifier,
    currentValue: Int,
    onValueChange: (Int) -> Unit
) {
    val description = when {
        currentValue < 30 -> stringResource(R.string.threshold_desc_open)
        currentValue < 60 -> stringResource(R.string.threshold_desc_balanced)
        currentValue < 85 -> stringResource(R.string.threshold_desc_selective)
        else -> stringResource(R.string.threshold_desc_soulmate)
    }

    val color = when {
        currentValue < 30 -> Color(0xFF4CAF50) // Yeşil
        currentValue < 60 -> Color(0xFF2196F3) // Mavi
        currentValue < 85 -> Color(0xFF9C27B0) // Mor
        else -> Color(0xFFFF5722) // Turuncu/Kırmızı
    }

    Card(
        modifier = modifier.fillMaxWidth().padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.match_sensitivity),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "%$currentValue",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Slider(
                value = currentValue.toFloat(),
                onValueChange = { onValueChange(it.roundToInt()) },
                valueRange = 0f..100f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = color,
                    inactiveTrackColor = color.copy(alpha = 0.2f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Mutual Threshold Information Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text =  stringResource(R.string.mutual_threshold_info),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Preview
@Composable
fun MatchThresholdPickerPreview(modifier: Modifier = Modifier) {
    _6thSenseTheme(darkTheme = true) {
        MatchThresholdPicker(
            modifier = modifier,
            currentValue = 90,
            onValueChange = {}
        )
    }
}
