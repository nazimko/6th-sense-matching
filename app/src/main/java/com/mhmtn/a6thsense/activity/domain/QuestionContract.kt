package com.mhmtn.a6thsense.activity.domain

import com.mhmtn.a6thsense.core.presentation.UiText

data class QuestionSet(
    val version: Int = 1,
    val active: Boolean = true,
    val phases: Map<String, Phase> = emptyMap(),
    val themeName: String = "",
    val themeEmoji: String = "",
    val themeDescription: String = "",
) {// Source code removed.}

data class Phase(
    val title: UiText,
    val description: UiText,
    val emoji: String = "",
    val color: String = "",
    val questions: List<Question> = emptyList()
) {// Source code removed.}

data class Question(
    val id: String = "",
    val type: QuestionType = QuestionType.TEXT_CHOICE,
    val question: UiText,
    val imageUrl: String? = null,
    val options: List<QuestionOption> = emptyList()
) {// Source code removed.}

enum class QuestionType {// Source code removed.}

data class QuestionOption(
    val id: String = "",
    val text: UiText,
    val imageUrl: String? = null,
    val color: String? = null,
    val emoji: String? = null
) {// Source code removed.}