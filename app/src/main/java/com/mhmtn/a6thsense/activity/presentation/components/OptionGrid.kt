package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme

@Composable
fun OptionGrid(
    options: List<Option>,
    currentSelection: Option?,
    onOptionSelected: (Option) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Birinci satır (ilk 2 öğe)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            options.take(2).forEach { option ->
                OptionCard(
                    option = option,
                    isSelected = currentSelection == option,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOptionSelected(option)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        // İkinci satır (sonraki 2 öğe)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            options.drop(2).take(2).forEach { option ->
                OptionCard(
                    option = option,
                    isSelected = currentSelection == option,
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOptionSelected(option) 
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}