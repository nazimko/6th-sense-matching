package com.mhmtn.a6thsense.soulsync.data

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.mhmtn.a6thsense.soulsync.domain.Player
import com.mhmtn.a6thsense.soulsync.domain.PlayerState
import com.mhmtn.a6thsense.soulsync.domain.SoulSyncRepository
import com.mhmtn.a6thsense.soulsync.domain.SoulSyncRoom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.mhmtn.a6thsense.R

@Singleton
class SoulSyncRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : SoulSyncRepository {

    private val database: DatabaseReference
    private val roomsRef: DatabaseReference

    init {
        val databaseUrl = "https://sixth-sense-9647e-default-rtdb.europe-west1.firebasedatabase.app"
        database = Firebase.database(databaseUrl).reference
        roomsRef = database.child("soul_sync_rooms")
    }

    override suspend fun createRoom(matchId: String, player1: Player, player2: Player): String {
        val roomId = roomsRef.push().key ?: return ""

        val room = mapOf(
            "matchId" to matchId,
            "players" to mapOf(
                player1.uid to mapOf(
                    "uid" to player1.uid,
                    "name" to player1.name,
                    "photoUrl" to player1.photoUrl,
                    "status" to "invited",
                    "score" to 0
                ),
                player2.uid to mapOf(
                    "uid" to player2.uid,
                    "name" to player2.name,
                    "photoUrl" to player2.photoUrl,
                    "status" to "invited",
                    "score" to 0
                )
            ),
            "gameState" to "waiting",
            "currentRound" to 1,
            "createdAt" to ServerValue.TIMESTAMP
        )

        roomsRef.child(roomId).setValue(room).await()
        return roomId
    }

    override suspend fun joinRoom(roomId: String) {
        val uid = auth.currentUser?.uid ?: return
        roomsRef.child(roomId).child("players").child(uid).child("status")
            .setValue("ready")
            .await()
    }

    override suspend fun leaveRoom(roomId: String) {
        val uid = auth.currentUser?.uid ?: return
        roomsRef.child(roomId).child("players").child(uid).child("status")
            .setValue("left")
            .await()
    }

    override fun observeRoom(roomId: String): Flow<SoulSyncRoom?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    trySend(null)
                    return
                }

                try {
                    val matchId = snapshot.child("matchId").getValue(String::class.java) ?: ""
                    val gameState = snapshot.child("gameState").getValue(String::class.java) ?: "waiting"
                    val currentRound = snapshot.child("currentRound").getValue(Int::class.java) ?: 1
                    val currentQuestionIndex = snapshot.child("currentQuestionIndex").getValue(Int::class.java) ?: 0
                    val countdownStartTime = snapshot.child("countdownStartTime").getValue(Long::class.java) ?: 0L
                    val createdAt = snapshot.child("createdAt").getValue(Long::class.java) ?: 0L

                    val players = mutableMapOf<String, PlayerState>()
                    snapshot.child("players").children.forEach { playerSnapshot ->
                        val uid = playerSnapshot.key ?: return@forEach
                        
                        val playerUid = playerSnapshot.child("uid").getValue(String::class.java) ?: uid
                        val name = playerSnapshot.child("name").getValue(String::class.java) ?: ""
                        val photoUrl = playerSnapshot.child("photoUrl").getValue(String::class.java) ?: ""
                        val status = playerSnapshot.child("status").getValue(String::class.java) ?: "invited"
                        val score = playerSnapshot.child("score").getValue(Int::class.java) ?: 0

                        val answers = mutableMapOf<String, String>()
                        playerSnapshot.child("answers").children.forEach { answerSnapshot ->
                            val roundKey = answerSnapshot.key ?: return@forEach
                            val answer = answerSnapshot.getValue(String::class.java) ?: ""
                            answers[roundKey] = answer
                        }

                        players[uid] = PlayerState(
                            uid = playerUid,
                            name = name,
                            photoUrl = photoUrl,
                            status = status,
                            score = score,
                            answers = answers
                        )
                    }

                    trySend(SoulSyncRoom(
                        matchId = matchId,
                        players = players,
                        gameState = gameState,
                        currentRound = currentRound,
                        currentQuestionIndex = currentQuestionIndex,
                        countdownStartTime = countdownStartTime,
                        createdAt = createdAt
                    ))
                } catch (e: Exception) {
                    trySend(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        roomsRef.child(roomId).addValueEventListener(listener)
        awaitClose { roomsRef.child(roomId).removeEventListener(listener) }
    }

    override suspend fun setGameState(roomId: String, gameState: String) {
        roomsRef.child(roomId).child("gameState").setValue(gameState).await()
    }

    override suspend fun setGameStateToPlaying(roomId: String) {
        roomsRef.child(roomId).child("gameState").setValue("playing").await()
    }

    override suspend fun startGame(roomId: String, context: Context) {
        val questions = context.resources.getStringArray(R.array.profile_questions_array)
        val randomIndex = questions.indices.random()
        
        roomsRef.child(roomId).updateChildren(
            mapOf(
                "gameState" to "countdown",
                "currentQuestionIndex" to randomIndex,
                "countdownStartTime" to ServerValue.TIMESTAMP
            )
        ).await()
    }

    override suspend fun submitAnswer(roomId: String, round: Int, answer: String) {
        val uid = auth.currentUser?.uid ?: return
        roomsRef.child(roomId)
            .child("players")
            .child(uid)
            .child("answers")
            .child("round$round")
            .setValue(answer.trim().lowercase())
            .await()
    }

    override suspend fun updateScores(
        roomId: String,
        player1Uid: String,
        player1Score: Int,
        player2Uid: String,
        player2Score: Int
    ) {
        roomsRef.child(roomId).child("players").updateChildren(
            mapOf(
                "$player1Uid/score" to player1Score,
                "$player2Uid/score" to player2Score
            )
        ).await()
    }

    override suspend fun nextRound(roomId: String, nextRound: Int, context: Context) {
        val questions = context.resources.getStringArray(R.array.profile_questions_array)
        val randomIndex = questions.indices.random()

        roomsRef.child(roomId).updateChildren(
            mapOf(
                "gameState" to "countdown",
                "currentRound" to nextRound,
                "currentQuestionIndex" to randomIndex,
                "countdownStartTime" to ServerValue.TIMESTAMP
            )
        ).await()
    }

    override suspend fun finishGame(roomId: String) {
        roomsRef.child(roomId).child("gameState").setValue("finished").await()
    }
}
