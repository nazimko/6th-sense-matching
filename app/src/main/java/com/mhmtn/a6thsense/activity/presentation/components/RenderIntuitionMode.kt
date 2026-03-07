package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.presentation.DailyOptionProvider
import com.mhmtn.a6thsense.activity.presentation.FreeTextQuestionProvider

@Composable
fun RenderIntuitionMode(
    state: DailyActivityContract.State,
    step: Int,
    onAction: (DailyActivityContract.Action) -> Unit,
    modifier: Modifier,
    playSound: () -> Unit
) {
    when (state.phase) {
        DailyActivityContract.Phase.PHASE_1 -> {
            if (step % 2 == 0) {
                VerticalChoices(
                    modifier = Modifier,
                    state = state,
                    playSound = playSound,
                    onAction = onAction
                )
            } else {
                HorizontalChoices(
                    modifier = Modifier,
                    state = state,
                    playSound = playSound,
                    onAction = onAction
                )
            }
        }
        DailyActivityContract.Phase.PHASE_2,
        DailyActivityContract.Phase.PHASE_3,
        DailyActivityContract.Phase.PHASE_4,
        DailyActivityContract.Phase.PHASE_5 -> {
            val options = DailyOptionProvider.optionsForPhaseAndStep(
                phase = state.phase,
                step = step
            )
            OptionGrid(
                options = options,
                currentSelection = state.currentSelection,
                onOptionSelected = { option ->
                    playSound()
                    onAction(DailyActivityContract.Action.SelectOption(option))
                }
            )
        }
        DailyActivityContract.Phase.PHASE_6 -> {
            val question = FreeTextQuestionProvider.getQuestionForStep(step)
            FreeTextInput(
                question = question,
                currentInput = state.currentTextInput,
                onTextChange = { text ->
                    onAction(DailyActivityContract.Action.TypeText(text))
                },
                onSubmit = {
                    playSound()
                    onAction(DailyActivityContract.Action.SubmitTextAnswer)
                }
            )
        }
    }
}