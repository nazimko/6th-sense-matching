package com.mhmtn.a6thsense.activity.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.activity.data.MatchingRepositoryImpl
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract.SessionType
import com.mhmtn.a6thsense.core.domain.ActivityConfig.MAX_STEP
import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.activity.domain.MatchResult
import com.mhmtn.a6thsense.activity.domain.QuestionOption
import com.mhmtn.a6thsense.activity.domain.QuestionRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyActivityViewModel @Inject constructor(
    private val repository: MatchingRepositoryImpl,
    private val questionRepository: QuestionRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionType: SessionType = try {
        val typeString = savedStateHandle.get<String>("sessionType") ?: "INTUITION"
        SessionType.valueOf(typeString)
    } catch (e: Exception) {
        Log.e("DailyActivityVM", "Invalid sessionType, defaulting to INTUITION", e)
        SessionType.INTUITION
    }
    private val _state = MutableStateFlow(
        DailyActivityContract.State(
            type = sessionType
        )
    )
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DailyActivityContract.Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    init {
        Log.d("DailyActivityVM", "Initialized with sessionType: $sessionType")

        when (sessionType) {
            SessionType.INTUITION -> {
                // Intuition mode: Firebase questions yüklenmiyor
                _state.update { it.copy(isLoadingQuestions = false) }
            }

            SessionType.PREFERENCE -> {
                // Preference mode: Firebase'den dynamic questions yükle
                loadQuestions()
            }
        }
        analyticsHelper.logDailyActivityStarted()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingQuestions = true) }

            try {
                questionRepository.getQuestions().collect { questionSet ->
                    Log.d(
                        "DailyActivityVM",
                        "Questions loaded: version ${questionSet.version}, phases: ${questionSet.phases.size}"
                    )

                    _state.update {
                        it.copy(
                            questionSet = questionSet,
                            isLoadingQuestions = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("DailyActivityVM", "Error loading questions: ${e.message}", e)
                _state.update {
                    it.copy(isLoadingQuestions = false)
                }
            }
        }
    }

    fun onAction(action: DailyActivityContract.Action) {
        when (action) {

            DailyActivityContract.Action.Reset -> _state.update { DailyActivityContract.State() }

            // 👇 YENİ: Dynamic question option selection
            is DailyActivityContract.Action.OnOptionSelected -> {
                handleOptionSelected(action.questionId, action.optionId)
            }

            DailyActivityContract.Action.OnRefreshQuestions -> {
                refreshQuestions()
            }

            is DailyActivityContract.Action.SelectOption -> {
                val currentState = _state.value

                Log.d(
                    "DailyActivityVM",
                    "SelectOption: ${action.option.name}, Step: ${currentState.step}"
                )

                if (currentState.currentSelection != null) return

                // Seçimi hemen işaretle
                _state.update { it.copy(currentSelection = action.option) }

                viewModelScope.launch {
                    delay(350)

                    // 👇 Atomik güncelleme: selections'a Option.name ekle (String)
                    var finalStep = 0
                    var finalSelections =
                        emptyList<String>() // 👈 DEĞİŞTİ: List<Option> → List<String>

                    _state.update {
                        finalSelections =
                            it.selections + action.option.name // 👈 DEĞİŞTİ: Option objesi yerine name (String)
                        finalStep = it.step + 1
                        it.copy(
                            selections = finalSelections, // 👈 List<String>
                            step = finalStep,
                            currentSelection = null
                        )
                    }

                    Log.d("DailyActivityVM", "Intuition selection: ${action.option.name}")
                    Log.d("DailyActivityVM", "Total selections: ${finalSelections.size}")

                    // Oyun bitti mi kontrolü
                    if (finalStep >= MAX_STEP) {
                        handleStepEnd(finalStep, finalSelections)
                    }
                }
            }

            DailyActivityContract.Action.Enter -> {
                viewModelScope.launch {
                    repository.startDailySession()
                }
            }

            DailyActivityContract.Action.PhaseTransitionShown -> {
                _state.update { it.copy(isPhaseTransition = false) }
            }

            is DailyActivityContract.Action.TypeText -> {
                _state.update { it.copy(currentTextInput = action.text) }
            }

            DailyActivityContract.Action.SubmitTextAnswer -> {
                val currentState = _state.value
                val questionId = FreeTextQuestionProvider.getQuestionForStep(currentState.step).id

                if (currentState.currentTextInput.isNotBlank()) {
                    viewModelScope.launch {
                        delay(350)

                        var finalStep = 0
                        var finalAnswers = emptyMap<String, String>()

                        _state.update {
                            finalAnswers = it.freeTextAnswers + (questionId to it.currentTextInput)
                            finalStep = it.step + 1
                            it.copy(
                                freeTextAnswers = finalAnswers,
                                step = finalStep,
                                currentTextInput = ""
                            )
                        }

                        // Step bitti mi kontrolü
                        if (finalStep >= 4) { // 4 soru
                            handlePhase6End(finalAnswers)
                        }
                    }
                }
            }
        }
    }

    private fun handleOptionSelected(questionId: String, optionId: String) {
        if (_state.value.currentSelection != null) return

        // Question'ı bul
        val questionSet = _state.value.questionSet
        val phaseKey = "phase${_state.value.phase.ordinal + 1}"
        val question = questionSet?.phases?.get(phaseKey)?.questions?.find { it.id == questionId }
        val selectedOption = question?.options?.find { it.id == optionId }

        if (selectedOption == null) {
            Log.e("DailyActivityVM", "❌ Selected option not found: $questionId -> $optionId")
            return
        }

        // 👇 Türkçe'yi İngilizce'ye çevir
        val valuetoSave = selectedOption.id

        // selectedOptions map'ine ekle (UI tracking için)
        val newSelectedOptions = _state.value.selectedOptions.toMutableMap()
        newSelectedOptions[questionId] = optionId

        // selections listesine İngilizce değeri ekle (Firestore için)
        val newSelections = _state.value.selections.toMutableList()
        newSelections.add(valuetoSave)

        _state.update {
            it.copy(
                selectedOptions = newSelectedOptions,
                selections = newSelections, // 👈 List<String>
                currentSelection = Option.B // Dummy, animasyon için
            )
        }

        Log.d("DailyActivityVM", "✅ Option selected: '${selectedOption.text}' → '$valuetoSave'")
        Log.d("DailyActivityVM", "Total selections: ${newSelections.size}")
        Log.d("DailyActivityVM", "All selections: $newSelections")

        viewModelScope.launch {
            delay(350)

            // Step'i artır
            var finalStep = 0
            _state.update {
                finalStep = it.step + 1
                it.copy(
                    step = finalStep,
                    currentSelection = null
                )
            }

            // Phase bitişini kontrol et
            checkPhaseCompletion(finalStep)
        }
    }

    // 👇 YENİ: Phase completion checker
    private fun checkPhaseCompletion(currentStep: Int) {
        val currentPhase = _state.value.phase
        val questionSet = _state.value.questionSet ?: return

        // Mevcut phase'deki toplam soru sayısını hesapla
        val phaseKey = when (currentPhase) {
            DailyActivityContract.Phase.PHASE_1 -> "phase1"
            DailyActivityContract.Phase.PHASE_2 -> "phase2"
            DailyActivityContract.Phase.PHASE_3 -> "phase3"
            DailyActivityContract.Phase.PHASE_4 -> "phase4"
            DailyActivityContract.Phase.PHASE_5 -> "phase5"
            DailyActivityContract.Phase.PHASE_6 -> "phase6"
        }

        val totalQuestionsInPhase = questionSet.phases[phaseKey]?.questions?.size ?: 0

        Log.d(
            "DailyActivityVM",
            "Phase: $currentPhase, Step: $currentStep, Total: $totalQuestionsInPhase"
        )

        // Phase tamamlandı mı?
        if (currentStep >= totalQuestionsInPhase) {
            when (currentPhase) {
                DailyActivityContract.Phase.PHASE_1 -> {
                    goToDynamicPhase(DailyActivityContract.Phase.PHASE_2)
                    analyticsHelper.logPhaseCompleted(1)
                }

                DailyActivityContract.Phase.PHASE_2 -> {
                    goToDynamicPhase(DailyActivityContract.Phase.PHASE_3)
                    analyticsHelper.logPhaseCompleted(2)
                }

                DailyActivityContract.Phase.PHASE_3 -> {
                    goToDynamicPhase(DailyActivityContract.Phase.PHASE_4)
                    analyticsHelper.logPhaseCompleted(3)
                }

                DailyActivityContract.Phase.PHASE_4 -> {
                    goToDynamicPhase(DailyActivityContract.Phase.PHASE_5)
                    analyticsHelper.logPhaseCompleted(4)
                }

                DailyActivityContract.Phase.PHASE_5 -> {
                    goToDynamicPhase(DailyActivityContract.Phase.PHASE_6)
                    analyticsHelper.logPhaseCompleted(5)
                }

                DailyActivityContract.Phase.PHASE_6 -> {
                    // Phase 6 bitti, session'ı tamamla
                    completeDynamicSession()
                }
            }
        }
    }

    // 👇 YENİ: Dynamic session completion
    private fun completeDynamicSession() {
        viewModelScope.launch {
            try {
                val selections = _state.value.selections
                val freeTextAnswers = _state.value.freeTextAnswers


                selections.forEachIndexed { index, value ->
                    Log.d("DailyActivityVM", "Selection $index: $value")
                }

                if (selections.isEmpty()) {
                    Log.e("DailyActivityVM", "❌ ERROR: No selections!")
                    _effect.emit(DailyActivityContract.Effect.ShowNoMatch)
                    return@launch
                }

                val result = repository.completeSession(
                    uid = auth.currentUser!!.uid,
                    selections = selections, // Geçici: mevcut sistem için
                    freeTextAnswers = freeTextAnswers,
                    sessionType = _state.value.type
                )

                analyticsHelper.logDailyActivityCompleted()

                when (result) {
                    MatchResult.Matched -> {
                        analyticsHelper.logMatchFound(auth.currentUser?.uid ?: "")
                        _effect.emit(DailyActivityContract.Effect.NavigateToSimilarity)
                    }

                    MatchResult.NoMatch -> {
                        analyticsHelper.logNoMatchFound()
                        _effect.emit(DailyActivityContract.Effect.ShowNoMatch)
                    }

                    MatchResult.AlreadyCompleted -> {
                        _effect.emit(DailyActivityContract.Effect.ShowAlreadyCompleted)
                    }
                }
            } catch (e: Exception) {
                analyticsHelper.logError(e, "DailyActivity")
                e.printStackTrace()
            }
        }
    }

    // 👇 YENİ: Dynamic phase transition
    private fun goToDynamicPhase(nextPhase: DailyActivityContract.Phase) {
        _state.update {
            it.copy(
                phase = nextPhase,
                step = 0,
                isPhaseTransition = true
            )
        }
    }

    // 👇 YENİ: Refresh questions
    private fun refreshQuestions() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoadingQuestions = true) }
                questionRepository.refreshQuestions()
                _effect.emit(DailyActivityContract.Effect.ShowToast("Sorular güncellendi"))
            } catch (e: Exception) {
                Log.e("DailyActivityVM", "Error refreshing: ${e.message}", e)
                _effect.emit(DailyActivityContract.Effect.ShowToast("Güncelleme başarısız"))
            }
        }
    }

    // 👇 ESKİ metotlar (şimdilik ikisi de var, migrate ettikten sonra silinebilir)
    private fun handleStepEnd(
        finalStep: Int,
        finalSelections: List<String>
    ) {
        when (_state.value.phase) {
            DailyActivityContract.Phase.PHASE_1 -> {
                if (finalStep >= 4) goToPhase(DailyActivityContract.Phase.PHASE_2, finalSelections)
                analyticsHelper.logPhaseCompleted(1)
            }

            DailyActivityContract.Phase.PHASE_2 -> {
                if (finalStep >= MAX_STEP) goToPhase(
                    DailyActivityContract.Phase.PHASE_3,
                    finalSelections
                )
                analyticsHelper.logPhaseCompleted(2)
            }

            DailyActivityContract.Phase.PHASE_3 -> {
                if (finalStep >= MAX_STEP) goToPhase(
                    DailyActivityContract.Phase.PHASE_4,
                    finalSelections
                )
                analyticsHelper.logPhaseCompleted(3)
            }

            DailyActivityContract.Phase.PHASE_4 -> {
                if (finalStep >= MAX_STEP) goToPhase(
                    DailyActivityContract.Phase.PHASE_5,
                    finalSelections
                )
                analyticsHelper.logPhaseCompleted(4)
            }

            DailyActivityContract.Phase.PHASE_5 -> {
                if (finalStep >= MAX_STEP) {
                    goToPhase(DailyActivityContract.Phase.PHASE_6, finalSelections)
                    analyticsHelper.logPhaseCompleted(5)
                }
            }

            DailyActivityContract.Phase.PHASE_6 -> {
                // Bu phase'de handleStepEnd çağrılmaz
            }
        }
    }

    private fun handlePhase6End(answers: Map<String, String>) {
        viewModelScope.launch {
            try {
                val result = repository.completeSession(
                    uid = auth.currentUser!!.uid,
                    sessionType = sessionType,
                    selections = _state.value.selections,
                    freeTextAnswers = answers
                )
                analyticsHelper.logDailyActivityCompleted()
                when (result) {
                    MatchResult.Matched -> {
                        analyticsHelper.logMatchFound(auth.currentUser?.uid ?: "")
                        _effect.emit(DailyActivityContract.Effect.NavigateToSimilarity)
                    }

                    MatchResult.NoMatch -> {
                        analyticsHelper.logNoMatchFound()
                        _effect.emit(DailyActivityContract.Effect.ShowNoMatch)
                    }

                    MatchResult.AlreadyCompleted ->
                        _effect.emit(DailyActivityContract.Effect.ShowAlreadyCompleted)
                }
            } catch (e: Exception) {
                analyticsHelper.logError(e, "DailyActivity")
                e.printStackTrace()
            }
        }
    }

    private fun goToPhase(nextPhase: DailyActivityContract.Phase, selections: List<String>) {
        _state.update {
            it.copy(
                phase = nextPhase,
                step = 0,
                selections = selections,
                isPhaseTransition = true
            )
        }
    }
}