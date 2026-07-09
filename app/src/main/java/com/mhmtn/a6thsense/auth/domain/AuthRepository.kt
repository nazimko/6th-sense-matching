package com.mhmtn.a6thsense.auth.domain

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): AuthUser
    fun currentUser(): AuthUser?
    suspend fun signOut()
    fun authStateChanges() : Flow<FirebaseUser?>
}