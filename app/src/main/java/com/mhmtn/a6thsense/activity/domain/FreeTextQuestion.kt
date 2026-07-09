package com.mhmtn.a6thsense.activity.domain

import com.mhmtn.a6thsense.core.presentation.UiText

data class FreeTextQuestion(
    val id: String,
    val question: UiText,
    val placeholder: UiText,
    val emoji: String
)