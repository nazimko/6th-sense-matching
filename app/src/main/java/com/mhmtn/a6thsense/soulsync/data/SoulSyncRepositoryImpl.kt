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
        // 👇 Explicit database URL
        val databaseUrl = "https://sixth-sense-9647e-default-rtdb.europe-west1.firebasedatabase.app" // 👈 Kendi URL'ini koy

        database = Firebase.database(databaseUrl).reference
        roomsRef = database.child("soul_sync_rooms")

        Log.d("SoulSyncRepo", "Database initialized: $databaseUrl")

        // Test read
        roomsRef.get().addOnSuccessListener { snapshot ->
            Log.d("SoulSyncRepo", "Test read successful: ${snapshot.exists()}")
            Log.d("SoulSyncRepo", "Children count: ${snapshot.childrenCount}")
        }.addOnFailureListener { e ->
            Log.e("SoulSyncRepo", "Test read failed: ${e.message}", e)
        }
    }

    override suspend fun createRoom(matchId: String, player1: Player, player2: Player): String {
        val roomId = roomsRef.push().key ?: return ""

        val room = mapOf(
            "matchId" to matchId,
            "players" to mapOf(
                player1.uid to mapOf(
                    "uid" to player1.uid, // 👈 Eklendi
                    "name" to player1.name,
                    "photoUrl" to player1.photoUrl,
                    "status" to "invited",
                    "score" to 0
                ),
                player2.uid to mapOf(
                    "uid" to player2.uid, // 👈 Eklendi
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

    override fun observeRoom(roomId: String): Flow<SoulSyncRoom?> = callbackFlow {
        Log.d("SoulSyncRepo", "Starting to observe room: $roomId")

        // 👇 Önce direct get ile test et
        roomsRef.child(roomId).get().addOnSuccessListener { snapshot ->
            Log.d("SoulSyncRepo", "Direct get successful")
            Log.d("SoulSyncRepo", "Snapshot exists: ${snapshot.exists()}")
            Log.d("SoulSyncRepo", "Snapshot value: ${snapshot.value}")

            if (snapshot.exists()) {
                val players = snapshot.child("players")
                Log.d("SoulSyncRepo", "Players node exists: ${players.exists()}")
                Log.d("SoulSyncRepo", "Players children: ${players.childrenCount}")

                players.children.forEach { child ->
                    Log.d("SoulSyncRepo", "Player key: ${child.key}")
                    Log.d("SoulSyncRepo", "Player value: ${child.value}")
                }
            }
        }.addOnFailureListener { e ->
            Log.e("SoulSyncRepo", "Direct get failed: ${e.message}", e)
        }

        // 👇 Sonra listener
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("SoulSyncRepo", "⭐ onDataChange CALLED ⭐")
                Log.d("SoulSyncRepo", "Snapshot exists: ${snapshot.exists()}")
                Log.d("SoulSyncRepo", "Snapshot value: ${snapshot.value}")

                if (!snapshot.exists()) {
                    Log.e("SoulSyncRepo", "Room does not exist!")
                    trySend(null)
                    return
                }

                try {
                    // Manuel parse
                    val matchId = snapshot.child("matchId").getValue(String::class.java) ?: ""
                    val gameState = snapshot.child("gameState").getValue(String::class.java) ?: "waiting"
                    val currentRound = snapshot.child("currentRound").getValue(Int::class.java) ?: 1
                    val currentQuestion = snapshot.child("currentQuestion").getValue(String::class.java) ?: ""
                    val countdownStartTime = snapshot.child("countdownStartTime").getValue(Long::class.java) ?: 0L
                    val createdAt = snapshot.child("createdAt").getValue(Long::class.java) ?: 0L

                    Log.d("SoulSyncRepo", "Parsed basic fields: gameState=$gameState")

                    // Players
                    val players = mutableMapOf<String, PlayerState>()
                    val playersSnapshot = snapshot.child("players")

                    Log.d("SoulSyncRepo", "Players snapshot exists: ${playersSnapshot.exists()}")
                    Log.d("SoulSyncRepo", "Players children count: ${playersSnapshot.childrenCount}")

                    playersSnapshot.children.forEach { playerSnapshot ->
                        val uid = playerSnapshot.key
                        Log.d("SoulSyncRepo", "Processing player with key: $uid")

                        if (uid == null) {
                            Log.e("SoulSyncRepo", "Player UID is null, skipping")
                            return@forEach
                        }

                        val playerUid = playerSnapshot.child("uid").getValue(String::class.java) ?: uid
                        val name = playerSnapshot.child("name").getValue(String::class.java) ?: ""
                        val photoUrl = playerSnapshot.child("photoUrl").getValue(String::class.java) ?: ""
                        val status = playerSnapshot.child("status").getValue(String::class.java) ?: "invited"
                        val score = playerSnapshot.child("score").getValue(Int::class.java) ?: 0

                        Log.d("SoulSyncRepo", "Player data: uid=$playerUid, name=$name, status=$status")

                        // Answers
                        val answers = mutableMapOf<String, String>()
                        playerSnapshot.child("answers").children.forEach { answerSnapshot ->
                            val roundKey = answerSnapshot.key ?: return@forEach
                            val answer = answerSnapshot.getValue(String::class.java) ?: ""
                            answers[roundKey] = answer
                        }

                        val playerState = PlayerState(
                            uid = playerUid,
                            name = name,
                            photoUrl = photoUrl,
                            status = status,
                            score = score,
                            answers = answers
                        )

                        players[uid] = playerState
                        Log.d("SoulSyncRepo", "Added player to map: $uid")
                    }

                    Log.d("SoulSyncRepo", "Total players parsed: ${players.size}")

                    val room = SoulSyncRoom(
                        matchId = matchId,
                        players = players,
                        gameState = gameState,
                        currentRound = currentRound,
                        currentQuestion = currentQuestion,
                        countdownStartTime = countdownStartTime,
                        createdAt = createdAt
                    )

                    Log.d("SoulSyncRepo", "Sending room with ${room.players.size} players")
                    trySend(room)

                } catch (e: Exception) {
                    Log.e("SoulSyncRepo", "Error parsing room: ${e.message}", e)
                    e.printStackTrace()
                    trySend(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SoulSyncRepo", "Room observation cancelled: ${error.message}")
                Log.e("SoulSyncRepo", "Error code: ${error.code}")
                Log.e("SoulSyncRepo", "Error details: ${error.details}")
                close(error.toException())
            }
        }

        roomsRef.child(roomId).addValueEventListener(listener)
        Log.d("SoulSyncRepo", "Listener attached to room: $roomId")

        awaitClose {
            Log.d("SoulSyncRepo", "Removing listener from room: $roomId")
            roomsRef.child(roomId).removeEventListener(listener)
        }
    }

    override suspend fun setGameState(roomId: String, gameState: String) {
        roomsRef.child(roomId).child("gameState")
            .setValue(gameState)
            .await()
    }

    override suspend fun setGameStateToPlaying(roomId: String) {
        roomsRef.child(roomId).child("gameState")
            .setValue("playing")
            .await()
        Log.d("SoulSyncRepo", "Game state set to 'playing'")
    }
    override suspend fun startGame(roomId: String, context: Context) {
        val question = getRandomQuestion(context)

        roomsRef.child(roomId).updateChildren(
            mapOf(
                "gameState" to "countdown",
                "currentQuestion" to question,
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
        val question = getRandomQuestion(context)

        roomsRef.child(roomId).updateChildren(
            mapOf(
                "gameState" to "countdown",
                "currentRound" to nextRound,
                "currentQuestion" to question,
                "countdownStartTime" to ServerValue.TIMESTAMP
            )
        ).await()
    }

    override suspend fun finishGame(roomId: String) {
        roomsRef.child(roomId).updateChildren(
            mapOf("gameState" to "finished")
        ).await()
    }

    private fun getRandomQuestion(context: Context): String {
        val questions = context.resources.getStringArray(R.array.profile_questions_array).toList()
        return questions.random()
    }
}