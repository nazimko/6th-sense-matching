package com.mhmtn.a6thsense.core.domain.model

data class UserProfile(
    val uid: String = "",
    val selections: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)