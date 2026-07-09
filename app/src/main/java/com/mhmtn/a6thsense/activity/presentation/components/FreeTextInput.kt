package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mhmtn.a6thsense.activity.domain.FreeTextQuestion

@Composable
fun FreeTextInput(
    question: FreeTextQuestion,
    currentInput: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {// Source code removed.}