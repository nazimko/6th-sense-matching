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
) : ContactRepository {

    override suspend fun sendMessage(message: ContactMessage): Result<Unit> {
        return try {
            val messageData = hashMapOf(
                "uid" to message.uid,
                "userName" to message.userName,
                "userEmail" to message.userEmail,
                "subject" to message.subject.name,
                "message" to message.message,
                "timestamp" to FieldValue.serverTimestamp(),
                "status" to message.status.name,
                "platform" to message.platform,
                "deviceInfo" to message.deviceInfo
            )

            firestore.collection("messages")
                .add(messageData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}