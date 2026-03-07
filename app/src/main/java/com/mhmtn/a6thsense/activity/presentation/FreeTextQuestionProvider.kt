package com.mhmtn.a6thsense.activity.presentation

import com.mhmtn.a6thsense.activity.domain.FreeTextQuestion
import com.mhmtn.a6thsense.R

object FreeTextQuestionProvider {

    fun getQuestionForStep(step: Int): FreeTextQuestion {
        return when (step) {
            0 -> FreeTextQuestion(
                id = "planet",
                question = R.string.planet_question.toString(),
                placeholder = R.string.planet_placeholder.toString(),
                emoji = "🪐"
            )
            1 -> FreeTextQuestion(
                id = "team",
                question = R.string.team_question.toString(),
                placeholder = R.string.team_placeholder.toString(),
                emoji = "⚽"
            )
            2 -> FreeTextQuestion(
                id = "name",
                question = R.string.male_name_question.toString(),
                placeholder = R.string.male_name_placeholder.toString(),
                emoji = "👤"
            )
            3 -> FreeTextQuestion(
                id = "city",
                question = R.string.city_question.toString(),
                placeholder = R.string.city_placeholder.toString(),
                emoji = "🌆"
            )
            else -> FreeTextQuestion(
                id = "default",
                question = R.string.first_word.toString(),
                placeholder = "...",
                emoji = "💭"
            )
        }
    }

    // Daha fazla soru eklemek isterseniz:
    fun getAllQuestions(): List<FreeTextQuestion> =listOf(
        FreeTextQuestion(
            "planet",
            R.string.planet_question.toString(),
            R.string.planet_placeholder.toString(),
            "🪐"
        ),
        FreeTextQuestion(
            "team",
            R.string.team_question.toString(),
            R.string.team_placeholder.toString(),
            "⚽"
        ),
        FreeTextQuestion(
            "name",
            R.string.male_name_question.toString(),
            R.string.male_name_placeholder.toString(),
            "👤"
        ),
        FreeTextQuestion(
            "city",
            R.string.city_question.toString(),
            R.string.city_placeholder.toString(),
            "🌆"
        ),
        FreeTextQuestion(
            "food",
            R.string.food_question.toString(),
            R.string.food_placeholder.toString(),
            "🍕"
        ),
        FreeTextQuestion(
            "movie",
            R.string.movie_question.toString(),
            R.string.movie_placeholder.toString(),
            "🎬"
        ),
        FreeTextQuestion(
            "song",
            R.string.song_question.toString(),
            R.string.song_placeholder.toString(),
            "🎵"
        ),
        FreeTextQuestion(
            "book",
            R.string.book_question.toString(),
            R.string.book_placeholder.toString(),
            "📚"
        )
    )
}