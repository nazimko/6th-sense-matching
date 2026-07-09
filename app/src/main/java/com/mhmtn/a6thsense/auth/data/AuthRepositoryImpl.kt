package com.mhmtn.a6thsense.auth.data

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import com.mhmtn.a6thsense.auth.domain.AuthUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.text.set

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val googleSignInClient: GoogleSignInClient
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): AuthUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user!!

        val fcmToken = try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            null
        }

        firestore.collection("users").document(user.uid).set(
            buildMap {
                put("name", user.displayName ?: "User")
                put("photoUrl", user.photoUrl?.toString() ?: "")
                put("showInDiscover", true)
                fcmToken?.let { put("fcmToken", it) }
            },
            SetOptions.merge()
        ).await()

        return AuthUser(
            uid = user.uid,
            name = user.displayName ?: "Unknown",
            photoUrl = user.photoUrl?.toString(),
            isPremium = false,
            showInDiscover = true,
            matchNotificationsEnabled = true,
            messageNotificationsEnabled = true
        )
    }

    override fun authStateChanges(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun currentUser(): AuthUser? {
        val user = auth.currentUser ?: return null
        return AuthUser(
            uid = user.uid,
            name = user.displayName ?: "Unknown",
            photoUrl = user.photoUrl?.toString()
        )
    }

    override suspend fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().await()
    }
}