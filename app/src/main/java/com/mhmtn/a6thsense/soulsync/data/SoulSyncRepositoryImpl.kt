package com.mhmtn.a6thsense.soulsync.data

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.mhmtn.a6thsense.soulsync.domain.Player
import com.mhmtn.a6thsense.soulsync.domain.PlayerState
import com.mhmtn.a6thsense.soulsync.domain.SoulSyncRepository
import com.mhmtn.a6thsense.soulsync.domain.SoulSyncRoom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.mhmtn.a6thsense.R

@Singleton
class SoulSyncRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : SoulSyncRepository {// Source code removed.}