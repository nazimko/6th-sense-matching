package com.mhmtn.a6thsense.home.domain

import com.mhmtn.a6thsense.activity.domain.DailyActivityContract

data class TodayMatch(
    val matchId: String,
    val userId: String,
    val userName: String,
    val userPhoto: String,
    val similarity: Int,
    val sessionType: DailyActivityContract.SessionType,
    val timestamp: Long
)
