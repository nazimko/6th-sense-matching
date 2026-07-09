package com.mhmtn.a6thsense.contact.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.contact.domain.ContactMessage
import com.mhmtn.a6thsense.contact.domain.ContactRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ContactRepository {// Source code removed.}