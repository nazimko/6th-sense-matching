package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.activity.domain.Question

@Composable
fun ImageQuestionTextOptionsScreen(
    question: Question,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}