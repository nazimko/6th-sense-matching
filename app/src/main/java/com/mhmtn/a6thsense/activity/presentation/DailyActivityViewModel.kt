package com.mhmtn.a6thsense.activity.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.activity.data.MatchingRepositoryImpl
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract.SessionType
import com.mhmtn.a6thsense.core.domain.ActivityConfig.MAX_STEP
import com.mhmtn.a6thsense.core.domain.Option
import com.mhmtn.a6thsense.activity.domain.MatchResult
import com.mhmtn.a6thsense.activity.domain.QuestionOption
import com.mhmtn.a6thsense.activity.domain.QuestionRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.core.presentation.UiText
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
                _state.update { it.copy(isLoadingQuestions = false) }
            }

            SessionType.PREFERENCE -> {
                loadQuestions()
            }
        }

        // ✅ DÜZELTME: "threshold" yerine "minSimilarity" kullanıldı
        val minSimilarityThreshold = savedStateHandle.get<Int>("minSimilarity") ?: 40
        Log.d("DailyActivityVM", "Initialized with minSimilarity: $minSimilarityThreshold")
        _state.update { it.copy(minSimilarity = minSimilarityThreshold)}

        analyticsHelper.logDailyActivityStarted()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingQuestions = true) }

            try {
                questionRepository.getQuestions().collect { questionSet ->
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

            is DailyActivityContract.Action.OnOptionSelected -> {
                handleOptionSelected(action.questionId, action.optionId)
            }

            DailyActivityContract.Action.OnRefreshQuestions -> {
                refreshQuestions()
            }

            is DailyActivityContract.Action.SelectOption -> {
                val currentState = _state.value

                if (currentState.currentSelection != null) return

                _state.update { it.copy(currentSelection = action.option) }

                viewModelScope.launch {
                    delay(350)

                    var finalStep = 0
                    var finalSelections = emptyList<String>()

                    val isLastStep = currentState.step >= MAX_STEP - 1

                    _state.update {
                        finalSelections = it.selections + action.option.name
                        finalStep = it.step + 1
                        
                        it.copy(
                            selections = finalSelections,
                            step = if (isLastStep) it.step else finalStep, // ✅ DÜZELTME
                            currentSelection = null
                        )
                    }

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
                val totalQuestionsCount = FreeTextQuestionProvider.getAllQuestions().size
                
                if (currentState.step >= totalQuestionsCount) return

                val currentQuestion = FreeTextQuestionProvider.getQuestionForStep(currentState.step)
                val questionId = currentQuestion.id

                if (currentState.currentTextInput.isNotBlank()) {
                    viewModelScope.launch {
                        delay(350)

                        val isLastQuestion = currentState.step == totalQuestionsCount - 1
                        val newAnswers = currentState.freeTextAnswers + (questionId to currentState.currentTextInput)

                        if (isLastQuestion) {
                            _state.update {
                                it.copy(
                                    freeTextAnswers = newAnswers,
                                    currentTextInput = ""
                                )
                            }
                            handlePhase6End(newAnswers)
                        } else {
                            _state.update {
                                it.copy(
                                    freeTextAnswers = newAnswers,
                                    step = it.step + 1,
                                    currentTextInput = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleOptionSelected(questionId: String, optionId: String) {
        if (_state.value.currentSelection != null) return

        val currentState = _state.value
        val questionSet = currentState.questionSet
        val phaseKey = "phase${currentState.phase.ordinal + 1}"
        val question = questionSet?.phases?.get(phaseKey)?.questions?.find { it.id == questionId }
        val selectedOption = question?.options?.find { it.id == optionId }

        if (selectedOption == null) return

        val newSelectedOptions = currentState.selectedOptions.toMutableMap()
        newSelectedOptions[questionId] = optionId

        val newSelections = currentState.selections.toMutableList()
        newSelections.add(optionId)

        _state.update {
            it.copy(
                selectedOptions = newSelectedOptions,
                selections = newSelections,
                currentSelection = Option.B // Animation dummy
            )
        }

        viewModelScope.launch {
            delay(350)

            val totalQuestionsInPhase = questionSet?.phases?.get(phaseKey)?.questions?.size ?: 0
            val nextStep = currentState.step + 1
            val isLastStepOfPhase = nextStep >= totalQuestionsInPhase

            if (isLastStepOfPhase) {
                // ✅ DÜZELTME: Son soruda step'i artırma, direkt geçişi tetikle
                _state.update { it.copy(currentSelection = null) }
                checkPhaseCompletion(nextStep)
            } else {
                _state.update {
                    it.copy(
                        step = nextStep,
                        currentSelection = null
                    )
                }
                checkPhaseCompletion(nextStep)
            }
        }
    }

    private fun checkPhaseCompletion(currentStep: Int) {
        val currentPhase = _state.value.phase
        val questionSet = _state.value.questionSet ?: return

        val phaseKey = when (currentPhase) {
            DailyActivityContract.Phase.PHASE_1 -> "phase1"
            DailyActivityContract.Phase.PHASE_2 -> "phase2"
            DailyActivityContract.Phase.PHASE_3 -> "phase3"
            DailyActivityContract.Phase.PHASE_4 -> "phase4"
            DailyActivityContract.Phase.PHASE_5 -> "phase5"
            DailyActivityContract.Phase.PHASE_6 -> "phase6"
        }

        val totalQuestionsInPhase = questionSet.phases[phaseKey]?.questions?.size ?: 0

        if (currentStep >= totalQuestionsInPhase) {
            when (currentPhase) {
                DailyActivityContract.Phase.PHASE_1 -> goToDynamicPhase(DailyActivityContract.Phase.PHASE_2)
                DailyActivityContract.Phase.PHASE_2 -> goToDynamicPhase(DailyActivityContract.Phase.PHASE_3)
                DailyActivityContract.Phase.PHASE_3 -> goToDynamicPhase(DailyActivityContract.Phase.PHASE_4)
                DailyActivityContract.Phase.PHASE_4 -> goToDynamicPhase(DailyActivityContract.Phase.PHASE_5)
                DailyActivityContract.Phase.PHASE_5 -> goToDynamicPhase(DailyActivityContract.Phase.PHASE_6)
                DailyActivityContract.Phase.PHASE_6 -> completeDynamicSession()
            }
        }
    }

    private fun completeDynamicSession() {
        viewModelScope.launch {
            try {
                val selections = _state.value.selections
                val freeTextAnswers = _state.value.freeTextAnswers

                if (selections.isEmpty()) {
                    _effect.emit(DailyActivityContract.Effect.ShowNoMatch)
                    return@launch
                }

                val result = repository.completeSession(
                    uid = auth.currentUser!!.uid,
                    selections = selections,
                    freeTextAnswers = freeTextAnswers,
                    sessionType = _state.value.type,
                    minSimilarity = _state.value.minSimilarity
                )

                analyticsHelper.logDailyActivityCompleted()

                when (result) {
                    MatchResult.Matched -> _effect.emit(DailyActivityContract.Effect.NavigateToSimilarity)
                    MatchResult.NoMatch -> _effect.emit(DailyActivityContract.Effect.ShowNoMatch)
                    MatchResult.AlreadyCompleted -> _effect.emit(DailyActivityContract.Effect.ShowAlreadyCompleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun goToDynamicPhase(nextPhase: DailyActivityContract.Phase) {
        _state.update {
            it.copy(
                phase = nextPhase,
                step = 0,
                isPhaseTransition = true
            )
        }
    }

    private fun refreshQuestions() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoadingQuestions = true) }
                questionRepository.refreshQuestions()
                _effect.emit(DailyActivityContract.Effect.ShowToast(UiText.StringResource(R.string.questions_updated)))
            } catch (e: Exception) {
                _effect.emit(DailyActivityContract.Effect.ShowToast(UiText.StringResource(R.string.questions_update_failed)))
            }
        }
    }

    private fun handleStepEnd(finalStep: Int, finalSelections: List<String>) {
        when (_state.value.phase) {
            DailyActivityContract.Phase.PHASE_1 -> {
                if (finalStep >= 4) goToPhase(DailyActivityContract.Phase.PHASE_2, finalSelections)
            }
            DailyActivityContract.Phase.PHASE_2 -> {
                if (finalStep >= MAX_STEP) goToPhase(DailyActivityContract.Phase.PHASE_3, finalSelections)
            }
            DailyActivityContract.Phase.PHASE_3 -> {
                if (finalStep >= MAX_STEP) goToPhase(DailyActivityContract.Phase.PHASE_4, finalSelections)
            }
            DailyActivityContract.Phase.PHASE_4 -> {
                if (finalStep >= MAX_STEP) goToPhase(DailyActivityContract.Phase.PHASE_5, finalSelections)
            }
            DailyActivityContract.Phase.PHASE_5 -> {
                if (finalStep >= MAX_STEP) goToPhase(DailyActivityContract.Phase.PHASE_6, finalSelections)
            }
            DailyActivityContract.Phase.PHASE_6 -> {}
        }
    }

    private fun handlePhase6End(answers: Map<String, String>) {
        viewModelScope.launch {
            try {
                val result = repository.completeSession(
                    uid = auth.currentUser!!.uid,
                    sessionType = sessionType,
                    selections = _state.value.selections,
                    minSimilarity = _state.value.minSimilarity,
                    freeTextAnswers = answers
                )
                analyticsHelper.logDailyActivityCompleted()
                when (result) {
                    MatchResult.Matched -> _effect.emit(DailyActivityContract.Effect.NavigateToSimilarity)
                    MatchResult.NoMatch -> _effect.emit(DailyActivityContract.Effect.ShowNoMatch)
                    MatchResult.AlreadyCompleted -> _effect.emit(DailyActivityContract.Effect.ShowAlreadyCompleted)
                }
            } catch (e: Exception) {
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
