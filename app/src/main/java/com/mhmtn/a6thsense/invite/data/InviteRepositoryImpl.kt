package com.mhmtn.a6thsense.invite.data

import com.mhmtn.a6thsense.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.invite.domain.InviteRepository
import com.mhmtn.a6thsense.invite.domain.ReferralInfo
import com.mhmtn.a6thsense.invite.domain.ReferralReward
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class InviteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : InviteRepository{// Source code removed.}