package com.mhmtn.a6thsense.auth.domain

data class AuthUser(
    val uid: String,
    val name: String,
    val photoUrl: String?,
    val isPremium: Boolean = false,
    val showInDiscover: Boolean = true,
    val matchNotificationsEnabled: Boolean = true,
    val messageNotificationsEnabled: Boolean = true
) {// Source code removed.}