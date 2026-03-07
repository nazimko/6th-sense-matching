package com.mhmtn.a6thsense.activity.domain

import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.home.domain.TodayMatch
import kotlinx.coroutines.flow.Flow

interface MatchingRepository {
    suspend fun saveUserSelections(selections: List<Option>)
    suspend fun completeSession(uid: String, sessionType: DailyActivityContract.SessionType, selections: List<String>, freeTextAnswers: Map<String, String> = emptyMap()) : MatchResult
    suspend fun startDailySession()
    suspend fun unmatch(matchId: String, conversationId: String, myUid: String): Result<Unit>
    fun getTodayMatches(uid: String, date: String): Flow<List<TodayMatch>>
}