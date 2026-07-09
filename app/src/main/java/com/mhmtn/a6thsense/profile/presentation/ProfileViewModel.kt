package com.mhmtn.a6thsense.profile.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.auth.domain.AuthUser
import com.mhmtn.a6thsense.core.presentation.UiText
import com.mhmtn.a6thsense.profile.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel(){// Source code removed.}