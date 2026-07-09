package com.mhmtn.a6thsense.friends.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Filter
import com.mhmtn.a6thsense.friends.domain.FriendsRepository
import com.mhmtn.a6thsense.friends.domain.model.*
import kotlinx.coroutines.channels.awaitClose
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.domain.model.UiTextException
import com.mhmtn.a6thsense.core.presentation.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FriendsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FriendsRepository {// Source code removed.}