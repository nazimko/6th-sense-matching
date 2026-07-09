package com.mhmtn.a6thsense.friends.domain

import com.mhmtn.a6thsense.friends.domain.model.*
import kotlinx.coroutines.flow.Flow

interface FriendsRepository {
    // Friendships
    suspend fun sendFriendRequest(fromUid: String, toUid: String): Result<String>
    suspend fun acceptFriendRequest(friendshipId: String): Result<Unit>
    suspend fun rejectFriendRequest(friendshipId: String): Result<Unit>
    suspend fun removeFriend(friendshipId: String): Result<Unit>

    fun getFriends(uid: String): Flow<List<Friend>>
    fun getPendingRequests(uid: String): Flow<List<Friendship>>

    // Compatibility Test
    suspend fun runCompatibilityTest(myUid: String, friendUid: String): Result<CompatibilityTestResult>
    fun getCompatibilityHistory(uid: String): Flow<List<CompatibilityTestResult>>
    suspend fun deleteCompatibilityTest(testId: String): Result<Unit>

    suspend fun getOrCreateInviteCode(uid: String): String

    suspend fun acceptInviteCode(code: String, accepterUid: String): Result<String>

    // Soul Sync
    suspend fun startSoulSyncWithFriend(myUid: String, friendUid: String): Result<String>
}