package com.mhmtn.a6thsense.activity.domain

import com.mhmtn.a6thsense.core.presentation.UiText

data class QuestionSet(
    val version: Int = 1,
    val active: Boolean = true,
    val phases: Map<String, Phase> = emptyMap(),
    val themeName: String = "",
    val themeEmoji: String = "",
    val themeDescription: String = "",
)

data class Phase(
    val title: UiText,
    val description: UiText,
    val emoji: String = "",
    val color: String = "",
    val questions: List<Question> = emptyList()
)

data class Question(
    val id: String = "",
    val type: QuestionType = QuestionType.TEXT_CHOICE,
    val question: UiText,
    val imageUrl: String? = null,
    val options: List<QuestionOption> = emptyList()
)

enum class QuestionType {
    IMAGE_CHOICE,      // Görsel seçenekler (2x2 grid)
    TEXT_CHOICE,       // Metin seçenekler (liste)
    COLOR_CHOICE,      // Renk seçenekler (renkli kartlar)
    EMOJI_CHOICE,      // Emoji seçenekler (büyük emojiler)
    IMAGE_QUESTION_TEXT_OPTIONS
}

data class QuestionOption(
    val id: String = "",
    val text: UiText,
    val imageUrl: String? = null,
    val color: String? = null,
    val emoji: String? = null
)