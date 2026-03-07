package com.mhmtn.a6thsense.activity.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.mhmtn.a6thsense.activity.domain.Phase
import com.mhmtn.a6thsense.activity.domain.Question
import com.mhmtn.a6thsense.activity.domain.QuestionOption
import com.mhmtn.a6thsense.activity.domain.QuestionRepository
import com.mhmtn.a6thsense.activity.domain.QuestionSet
import com.mhmtn.a6thsense.activity.domain.QuestionType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.mhmtn.a6thsense.R

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : QuestionRepository {

    private val questionsRef = firestore.collection("daily_questions")

    override fun getQuestions(): Flow<QuestionSet> = callbackFlow {
        Log.d("QuestionRepo", "Starting to observe questions")

        val listener = questionsRef
            .whereEqualTo("active", true)
            .orderBy("version", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("QuestionRepo", "Error observing questions: ${error.message}", error)
                    close(error)
                    return@addSnapshotListener
                }

                val doc = snapshot?.documents?.firstOrNull()
                if (doc != null) {
                    Log.d("QuestionRepo", "Question document found: ${doc.id}")
                    try {
                        val questionSet = parseQuestionSet(doc.data ?: emptyMap())
                        Log.d("QuestionRepo", "Parsed question set - version: ${questionSet.version}, phases: ${questionSet.phases.size}")
                        trySend(questionSet)
                    } catch (e: Exception) {
                        Log.e("QuestionRepo", "Error parsing questions: ${e.message}", e)
                        // Fallback to hardcoded questions
                        trySend(getFallbackQuestions())
                    }
                } else {
                    Log.w("QuestionRepo", "No active question set found, using fallback")
                    trySend(getFallbackQuestions())
                }
            }

        awaitClose {
            Log.d("QuestionRepo", "Removing question listener")
            listener.remove()
        }
    }

    override suspend fun refreshQuestions() {
        try {
            Log.d("QuestionRepo", "Forcing refresh from server")
            questionsRef
                .whereEqualTo("active", true)
                .get(Source.SERVER)
                .await()
        } catch (e: Exception) {
            Log.e("QuestionRepo", "Error refreshing questions: ${e.message}", e)
        }
    }

    override suspend fun getQuestionVersion(): Int {
        return try {
            val snapshot = questionsRef
                .whereEqualTo("active", true)
                .orderBy("version", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val version = snapshot.documents.firstOrNull()?.getLong("version")?.toInt() ?: 1
            Log.d("QuestionRepo", "Current question version: $version")
            version
        } catch (e: Exception) {
            Log.e("QuestionRepo", "Error getting version: ${e.message}", e)
            1
        }
    }

    private fun parseQuestionSet(data: Map<String, Any>): QuestionSet {
        val version = (data["version"] as? Long)?.toInt() ?: 1
        val active = data["active"] as? Boolean ?: true
        val phasesMap = data["phases"] as? Map<String, Any> ?: emptyMap()

        val phases = phasesMap.mapValues { (phaseKey, phaseData) ->
            parsePhase(phaseData as Map<String, Any>)
        }

        return QuestionSet(
            version = version,
            active = active,
            phases = phases
        )
    }

    private fun parsePhase(data: Map<String, Any>): Phase {
        val title = data["title"] as? String ?: ""
        val description = data["description"] as? String ?: ""
        val questionsList = data["questions"] as? List<Map<String, Any>> ?: emptyList()

        val questions = questionsList.map { parseQuestion(it) }

        return Phase(
            title = title,
            description = description,
            questions = questions
        )
    }

    private fun parseQuestion(data: Map<String, Any>): Question {
        val id = data["id"] as? String ?: ""
        val typeStr = data["type"] as? String ?: "TEXT_CHOICE"
        val type = try {
            QuestionType.valueOf(typeStr)
        } catch (e: Exception) {
            QuestionType.TEXT_CHOICE
        }
        val question = data["question"] as? String ?: ""
        val imageUrl = data["imageUrl"] as? String
        val optionsList = data["options"] as? List<Map<String, Any>> ?: emptyList()

        val options = optionsList.map { parseOption(it) }

        return Question(
            id = id,
            type = type,
            question = question,
            imageUrl = imageUrl,
            options = options
        )
    }

    private fun parseOption(data: Map<String, Any>): QuestionOption {
        return QuestionOption(
            id = data["id"] as? String ?: "",
            text = data["text"] as? String ?: "",
            imageUrl = data["imageUrl"] as? String,
            color = data["color"] as? String,
            emoji = data["emoji"] as? String
        )
    }

    // 👇 Fallback questions (Firestore'da soru yoksa)
    private fun getFallbackQuestions(): QuestionSet {
        return QuestionSet(
            version = 1,
            active = true,
            phases = mapOf(
                "phase1" to Phase(
                    title = R.string.phase1_title.toString(),
                    description = R.string.phase1_description.toString(),
                    questions = listOf(
                        Question(
                            id = "p1_q1",
                            type = QuestionType.TEXT_CHOICE,
                            question = R.string.phase1_question1.toString(),
                            options = listOf(
                                QuestionOption(id = "FREEDOM", text = R.string.p1_q1_option1.toString()),
                                QuestionOption(id = "BALANCE", text = R.string.p1_q1_option2.toString())
                            )
                        ),
                        Question(
                            id = "p1_q2",
                            type = QuestionType.EMOJI_CHOICE,
                            question = R.string.phase1_question2.toString(),
                            options = listOf(
                                QuestionOption(id = "FIRE", text = R.string.p1_q2_option1.toString(), emoji = "🔥"),
                                QuestionOption(id = "WATER", text = R.string.p1_q2_option2.toString(), emoji = "🌊"))

                            )
                        )
                ),
                "phase2" to Phase(
                    title = R.string.phase2_title.toString(),
                    description = R.string.phase2_description.toString(),
                    questions = listOf(
                        Question(
                            id = "p2_q1",
                            type = QuestionType.COLOR_CHOICE,
                            question = R.string.phase2_question1.toString(),
                            options = listOf(
                                QuestionOption(id = "WARM", text = R.string.p2_q1_option1.toString(), color = "#FF6B6B"),
                                QuestionOption(id = "COLD", text = R.string.p2_q1_option2.toString(), color = "#4ECDC4")
                            )
                        )
                    )
                ),
                // Diğer phase'ler de eklenebilir...
            )
        )
    }
}