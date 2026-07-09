package com.mhmtn.a6thsense.matchhistory.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Filter
import com.mhmtn.a6thsense.friends.domain.model.FriendshipStatus
import com.mhmtn.a6thsense.matchhistory.domain.MatchHistoryItem
import com.mhmtn.a6thsense.matchhistory.domain.MatchHistoryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MatchHistoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MatchHistoryRepository {// Source code removed.}