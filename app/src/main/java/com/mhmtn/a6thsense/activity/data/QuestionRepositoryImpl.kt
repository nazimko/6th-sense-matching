package com.mhmtn.a6thsense.activity.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.activity.domain.QuestionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : QuestionRepository {// Source code removed.}