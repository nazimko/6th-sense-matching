package com.mhmtn.a6thsense.auth.data

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val googleSignInClient: GoogleSignInClient
) : AuthRepository {// Source code removed.}