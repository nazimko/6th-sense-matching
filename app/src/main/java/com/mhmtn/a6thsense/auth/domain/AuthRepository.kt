package com.mhmtn.a6thsense.auth.domain

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): AuthUser
    fun currentUser(): AuthUser?
    suspend fun signOut()
}