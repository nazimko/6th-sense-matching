package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.activity.domain.Question
import com.mhmtn.a6thsense.activity.domain.QuestionOption

@Composable
fun EmojiChoiceQuestion(
    question: Question,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}

@Composable
fun EmojiOptionCard(
    option: QuestionOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {// Source code removed.}