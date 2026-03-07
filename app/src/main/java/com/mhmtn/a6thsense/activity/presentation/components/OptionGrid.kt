package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.core.domain.Option
import kotlinx.coroutines.delay

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

        // İkinci satır (2 öğe)
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
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}
@Composable
private fun AnimatedOptionCard(
    option: Option,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    delay: Long = 0L
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) +
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
    ) {
        OptionCard(
            option = option,
            isSelected = isSelected,
            onClick = onClick,
            modifier = modifier
        )
    }
}