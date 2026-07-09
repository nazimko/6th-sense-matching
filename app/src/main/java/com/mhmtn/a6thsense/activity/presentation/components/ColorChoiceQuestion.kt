package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import com.mhmtn.a6thsense.activity.domain.Question
import com.mhmtn.a6thsense.activity.domain.QuestionOption

@Composable
fun ColorChoiceQuestion(
    question: Question,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {// Source code removed.}

@Composable
fun ColorOptionCard(
    option: QuestionOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {// Source code removed.}