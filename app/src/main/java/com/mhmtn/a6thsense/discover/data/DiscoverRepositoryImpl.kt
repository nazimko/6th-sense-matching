package com.mhmtn.a6thsense.discover.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mhmtn.a6thsense.discover.domain.DiscoverRepository
import com.mhmtn.a6thsense.discover.domain.DiscoverUser
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DiscoverRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DiscoverRepository {// Source code removed.}