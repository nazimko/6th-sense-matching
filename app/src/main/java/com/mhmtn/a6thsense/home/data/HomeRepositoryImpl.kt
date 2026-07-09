package com.mhmtn.a6thsense.home.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.home.domain.HomeRepository
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : HomeRepository {// Source code removed.}