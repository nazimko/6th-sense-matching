package com.mhmtn.a6thsense.settings.domain

data class UserSettings(
    val isDarkTheme: Boolean = true,
    val matchNotificationsEnabled: Boolean = true,
    val messageNotificationsEnabled: Boolean = true,
    val displayName: String = "",
    val photoUrl: String = ""
)
