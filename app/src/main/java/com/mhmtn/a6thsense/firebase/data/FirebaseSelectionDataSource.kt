package com.mhmtn.a6thsense.firebase.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.core.domain.model.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseSelectionDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun saveSelections(
        uid: String?,
        selections: List<Option>
    ) {
        uid?.let {
            firestore.collection("users")
                .document(uid)
                .set(
                    mapOf(
                        "uid" to uid,
                        "selections" to selections.map { it.name },
                        "completedAt" to System.currentTimeMillis()
                    )
                )
                .await()
        }
    }

    suspend fun getOtherUsers(uid: String?,): List<UserProfile> {

        val snapshot = firestore
            .collection("users")
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { it.toObject(UserProfile::class.java) }
            .filter { it.uid != uid }
    }

    companion object {
        const val CHANNEL_ID = "match_channel"
        const val MATCH_NOTIFICATION_ID = 1001
    }
}