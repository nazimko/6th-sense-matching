package com.mhmtn.a6thsense.home.domain

import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.auth.domain.AuthUser

interface HomeRepository {
    suspend fun getMatchedUser(): AuthUser?
    suspend fun getSimilarity(): Int?
    suspend fun getCurrentStreak(uid: String): Int // 👈
    suspend fun isCompletedToday(uid: String): Boolean // 👈
    suspend fun isPremium(uid: String): Boolean // 👈
    suspend fun getTodaysSessions(uid: String, date: String): List<DailyActivityContract.State> // 👈 YENİ

}
