package com.mhmtn.a6thsense.soulsync.domain

data class PlayerState(
    val name: String = "",
    val photoUrl: String = "",
    val uid: String = "",
    val status: String = "invited", // invited, ready
    val score: Int = 0,
    val answers: Map<String, String> = emptyMap()
)
