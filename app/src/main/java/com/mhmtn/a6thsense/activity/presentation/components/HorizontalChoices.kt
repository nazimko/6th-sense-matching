package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.ui.theme.MeditationSoftLavender

@Composable
fun HorizontalChoices(
    modifier: Modifier,
    state: DailyActivityContract.State,
    playSound: () -> Unit,
    onAction: (DailyActivityContract.Action) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = R.string.choose_feel.toString(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Light,
                letterSpacing = 1.5.sp
            ),
            color = MeditationSoftLavender,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChoiceCard(
                modifier = Modifier.weight(1f),
                isSelected = state.currentSelection == Option.A,
                isDimmed = state.currentSelection != null && state.currentSelection != Option.A,
                onClick = {
                    playSound()
                    onAction(DailyActivityContract.Action.SelectOption(Option.A))
                }
            )

            ChoiceCard(
                modifier = Modifier.weight(1f),
                isSelected = state.currentSelection == Option.B,
                isDimmed = state.currentSelection != null && state.currentSelection != Option.B,
                onClick = {
                    playSound()
                    onAction(DailyActivityContract.Action.SelectOption(Option.B))
                }
            )
        }
    }
}

