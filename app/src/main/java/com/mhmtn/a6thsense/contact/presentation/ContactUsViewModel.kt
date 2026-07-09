package com.mhmtn.a6thsense.contact.presentation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.contact.domain.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val auth: FirebaseAuth
) : ViewModel() {// Source code removed.}