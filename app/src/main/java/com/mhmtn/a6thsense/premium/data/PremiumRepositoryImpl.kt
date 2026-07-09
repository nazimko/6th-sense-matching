package com.mhmtn.a6thsense.premium.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import com.mhmtn.a6thsense.premium.domain.PremiumStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private val Context.premiumDataStore by preferencesDataStore("premium")

class PremiumRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) : PremiumRepository {// Source code removed.}