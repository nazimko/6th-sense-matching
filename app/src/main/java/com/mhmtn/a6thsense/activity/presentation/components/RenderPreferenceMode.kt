package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.domain.QuestionType
import com.mhmtn.a6thsense.R

@Composable
fun RenderPreferenceMode(
    state: DailyActivityContract.State,
    step: Int,
    onAction: (DailyActivityContract.Action) -> Unit,
    playSound: () -> Unit
) {
    val questionSet = state.questionSet
    val phaseKey = "phase${state.phase.ordinal + 1}"
    val phaseInfo = questionSet?.phases?.get(phaseKey)
    val question = phaseInfo?.questions?.getOrNull(step)

    if (question != null) {
        when (question.type) {
            QuestionType.IMAGE_CHOICE -> {
                ImageChoiceQuestion(
                    question = question,
                    selectedOption = state.selectedOptions[question.id],
                    onOptionSelected = { optionId ->
                        playSound()
                        onAction(
                            DailyActivityContract.Action.OnOptionSelected(
                                question.id,
                                optionId
                            )
                        )
                    }
                )
            }
            QuestionType.COLOR_CHOICE -> {
                ColorChoiceQuestion(
                    question = question,
                    selectedOption = state.selectedOptions[question.id],
                    onOptionSelected = { optionId ->
                        playSound()
                        onAction(
                            DailyActivityContract.Action.OnOptionSelected(
                                question.id,
                                optionId
                            )
                        )
                    }
                )
            }
            QuestionType.EMOJI_CHOICE -> {
                EmojiChoiceQuestion(
                    question = question,
                    selectedOption = state.selectedOptions[question.id],
                    onOptionSelected = { optionId ->
                        playSound()
                        onAction(
                            DailyActivityContract.Action.OnOptionSelected(
                                question.id,
                                optionId
                            )
                        )
                    }
                )
            }
            QuestionType.TEXT_CHOICE -> {
                TextChoiceQuestion(
                    question = question,
                    selectedOption = state.selectedOptions[question.id],
                    onOptionSelected = { optionId ->
                        playSound()
                        onAction(
                            DailyActivityContract.Action.OnOptionSelected(
                                question.id,
                                optionId
                            )
                        )
                    }
                )
            }
            QuestionType.IMAGE_QUESTION_TEXT_OPTIONS -> {
                ImageQuestionTextOptionsScreen(
                    question = question,
                    selectedOption = state.selectedOptions[question.id],
                    onOptionSelected = { optionId ->
                        playSound()
                        onAction(
                            DailyActivityContract.Action.OnOptionSelected(
                                question.id,
                                optionId
                            )
                        )
                    }
                )
            }
        }
    } else {
        // Fallback
        Text(
            text = stringResource( R.string.questions_loading),
            color = Color.White,
            fontSize = 18.sp
        )
    }
}