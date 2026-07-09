package com.mhmtn.a6thsense.profile.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.mhmtn.a6thsense.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.profile.domain.Badge
import com.mhmtn.a6thsense.profile.domain.ProfileRepository
import com.mhmtn.a6thsense.profile.domain.ProfileStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    ) : ProfileRepository {// Source code removed.}