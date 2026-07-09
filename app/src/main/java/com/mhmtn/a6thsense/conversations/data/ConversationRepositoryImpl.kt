package com.mhmtn.a6thsense.conversations.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.conversations.domain.ConversationRepository
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ConversationRepository {// Source code removed.}