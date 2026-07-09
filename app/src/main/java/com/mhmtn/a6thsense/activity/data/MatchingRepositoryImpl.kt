package com.mhmtn.a6thsense.activity.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.activity.domain.MatchingRepository
import com.mhmtn.a6thsense.firebase.data.FirebaseSelectionDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MatchingRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val dataSource: FirebaseSelectionDataSource,
    @ApplicationContext private val context: Context
) : MatchingRepository {/* Source code removed.}