package com.mhmtn.a6thsense.activity.domain

data class QuestionSet(
    val version: Int = 1,
    val active: Boolean = true,
    val phases: Map<String, Phase> = emptyMap()
)

data class Phase(
    val title: String = "",
    val description: String = "",
    val questions: List<Question> = emptyList()
)

data class Question(
    val id: String = "",
    val type: QuestionType = QuestionType.TEXT_CHOICE,
    val question: String = "",
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
    val text: String = "",
    val imageUrl: String? = null,
    val color: String? = null,
    val emoji: String? = null
)