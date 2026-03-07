package com.mhmtn.a6thsense.activity.domain

sealed interface MatchResult {
    object Matched : MatchResult
    object NoMatch : MatchResult
    object AlreadyCompleted : MatchResult
}


