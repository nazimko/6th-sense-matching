package com.mhmtn.a6thsense.discover.domain

data class DiscoverUser(
    val uid: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val similarityScore: Int = 0,
    val isMatched: Boolean = false,
    val lastActiveDate: String = "",
    val isPremium: Boolean = false
)
