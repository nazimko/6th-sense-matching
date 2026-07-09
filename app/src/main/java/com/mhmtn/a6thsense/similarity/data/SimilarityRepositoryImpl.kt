package com.mhmtn.a6thsense.similarity.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.similarity.domain.SimilarityRepository
import com.mhmtn.a6thsense.similarity.domain.SimilarityResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SimilarityRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SimilarityRepository {// Source code removed.}
