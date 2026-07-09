package com.mhmtn.a6thsense.activity.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.mhmtn.a6thsense.core.presentation.UiText
import java.time.LocalDate
import java.util.Locale

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : QuestionRepository {

    private val questionsRef = firestore.collection("daily_questions")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getQuestions(): Flow<QuestionSet> = callbackFlow {

        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value

        val listener = questionsRef
            .whereEqualTo("active", true)
            .whereEqualTo("dayOfWeek", dayOfWeek)
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

        val themeName = localizedString(data["themeName"])
        val themeEmoji = data["themeEmoji"] as? String ?: ""
        val themeDescription = localizedString(data["themeDescription"])


        val phases = phasesMap.mapValues { (phaseKey, phaseData) ->
            parsePhase(phaseData as Map<String, Any>)
        }

        return QuestionSet(
            version = version,
            active = active,
            phases = phases,
            themeName = themeName,
            themeEmoji = themeEmoji,
            themeDescription = themeDescription
        )
    }

    private fun parsePhase(data: Map<String, Any>): Phase {
        val title = UiText.DynamicString(localizedString(data["title"]))
        val description = UiText.DynamicString(localizedString(data["description"]))
        val questionsList = data["questions"] as? List<Map<String, Any>> ?: emptyList()
        val questions = questionsList.map { parseQuestion(it) }
        val emoji = data["emoji"] as? String ?: ""
        val color = data["color"] as? String ?: ""


        return Phase(
            title =title,
            description = description,
            emoji = emoji,
            color = color,
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
        val question = UiText.DynamicString(localizedString(data["question"]))
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
            text = UiText.DynamicString(localizedString(data["text"])),
            imageUrl = data["imageUrl"] as? String,
            color = data["color"] as? String,
            emoji = data["emoji"] as? String
        )
    }

    // Repo içinde
    private fun localizedString(data: Any?): String {
        val deviceLanguage = Locale.getDefault().language // "tr", "en" vs.
        return when (data) {
            is Map<*, *> -> {
                data[deviceLanguage] as? String  // önce cihaz dili
                    ?: data["en"] as? String     // yoksa English fallback
                    ?: ""
            }
            is String -> data                    // eski format String gelirse
            else -> ""
        }
    }

    // 👇 Fallback questions (Firestore'da soru yoksa)
    private fun getFallbackQuestions(): QuestionSet {
        return QuestionSet(
            version = 1,
            active = true,
            phases = mapOf(
                "phase1" to Phase(
                    title = UiText.StringResource(R.string.phase1_title),
                    description = UiText.StringResource(R.string.phase1_description),
                    questions = listOf(
                        Question(
                            id = "p1_q1",
                            type = QuestionType.TEXT_CHOICE,
                            question =  UiText.StringResource(R.string.phase1_question1),
                            options = listOf(
                                QuestionOption(id = "FREEDOM", text = UiText.StringResource(R.string.p1_q1_option1)),
                                QuestionOption(id = "BALANCE", text = UiText.StringResource(R.string.p1_q1_option2))
                            )
                        ),
                        Question(
                            id = "p1_q2",
                            type = QuestionType.EMOJI_CHOICE,
                            question = UiText.StringResource(R.string.phase1_question2),
                            options = listOf(
                                QuestionOption(id = "FIRE", text = UiText.StringResource(R.string.p1_q2_option1), emoji = "🔥"),
                                QuestionOption(id = "WATER", text = UiText.StringResource(R.string.p1_q2_option2), emoji = "🌊"))

                            )
                        )
                ),
                "phase2" to Phase(
                    title = UiText.StringResource(R.string.phase2_title),
                    description = UiText.StringResource(R.string.phase2_description),
                    questions = listOf(
                        Question(
                            id = "p2_q1",
                            type = QuestionType.COLOR_CHOICE,
                            question = UiText.StringResource(R.string.phase2_question1),
                            options = listOf(
                                QuestionOption(id = "WARM", text = UiText.StringResource(R.string.p2_q1_option1), color = "#FF6B6B"),
                                QuestionOption(id = "COLD", text = UiText.StringResource(R.string.p2_q1_option2), color = "#4ECDC4")
                            )
                        )
                    )
                ),
                // Diğer phase'ler de eklenebilir...
            )
        )
    }
}