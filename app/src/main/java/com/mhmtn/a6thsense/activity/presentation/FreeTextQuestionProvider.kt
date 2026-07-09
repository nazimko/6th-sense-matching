package com.mhmtn.a6thsense.activity.presentation

import com.mhmtn.a6thsense.activity.domain.FreeTextQuestion
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.UiText

object FreeTextQuestionProvider {

    fun getQuestionForStep(step: Int): FreeTextQuestion {
        return when (step) {
            0 -> FreeTextQuestion(
                id = "planet",
                question = UiText.StringResource(R.string.planet_question),
                placeholder = UiText.StringResource(R.string.planet_placeholder),
                emoji = "🪐"
            )
            1 -> FreeTextQuestion(
                id = "team",
                question = UiText.StringResource(R.string.team_question),
                placeholder = UiText.StringResource(R.string.team_placeholder),
                emoji = "⚽"
            )
            2 -> FreeTextQuestion(
                id = "name",
                question = UiText.StringResource(R.string.male_name_question),
                placeholder = UiText.StringResource(R.string.male_name_placeholder),
                emoji = "👤"
            )
            3 -> FreeTextQuestion(
                id = "city",
                question = UiText.StringResource(R.string.city_question),
                placeholder = UiText.StringResource(R.string.city_placeholder),
                emoji = "🌆"
            )
            4 -> FreeTextQuestion(
                "food",
                UiText.StringResource(R.string.food_question),
                UiText.StringResource(R.string.food_placeholder),
                "🍕"
            )
            5 -> FreeTextQuestion(
                "movie",
                UiText.StringResource(R.string.movie_question),
                UiText.StringResource(R.string.movie_placeholder),
                "🎬"
            )

            else -> FreeTextQuestion(
                id = "default",
                question = UiText.StringResource(R.string.first_word),
                placeholder = UiText.DynamicString("..."),
                emoji = "💭"
            )
        }
    }

    // Daha fazla soru eklemek isterseniz:
    fun getAllQuestions(): List<FreeTextQuestion> =listOf(
        FreeTextQuestion(
            "planet",
            UiText.StringResource(R.string.planet_question),
            UiText.StringResource(R.string.planet_placeholder),
            "🪐"
        ),
        FreeTextQuestion(
            "team",
            UiText.StringResource(R.string.team_question),
                UiText.StringResource(R.string.team_placeholder),
            "⚽"
        ),
        FreeTextQuestion(
            "name",
            UiText.StringResource(R.string.male_name_question),
                UiText.StringResource(R.string.male_name_placeholder),
            "👤"
        ),
        FreeTextQuestion(
            "city",
            UiText.StringResource(R.string.city_question),
                UiText.StringResource(R.string.city_placeholder),
            "🌆"
        ),
        FreeTextQuestion(
            "food",
            UiText.StringResource(R.string.food_question),
                UiText.StringResource(R.string.food_placeholder),
            "🍕"
        ),
        FreeTextQuestion(
            "movie",
            UiText.StringResource(R.string.movie_question),
                UiText.StringResource(R.string.movie_placeholder),
            "🎬"
        ),
        FreeTextQuestion(
            "song",
            UiText.StringResource(R.string.song_question),
                UiText.StringResource(R.string.song_placeholder),
            "🎵"
        )
    )
}