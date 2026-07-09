package com.mhmtn.a6thsense.messaging.data


import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mhmtn.a6thsense.messaging.domain.MessagingRepository
import com.mhmtn.a6thsense.messaging.domain.model.Conversation
import com.mhmtn.a6thsense.messaging.domain.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessagingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessagingRepository{// Source code removed.}