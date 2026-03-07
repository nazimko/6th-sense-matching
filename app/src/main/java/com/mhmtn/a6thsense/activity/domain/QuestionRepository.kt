package com.mhmtn.a6thsense.activity.domain

import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getQuestions(): Flow<QuestionSet>
    suspend fun refreshQuestions()
    suspend fun getQuestionVersion(): Int
}