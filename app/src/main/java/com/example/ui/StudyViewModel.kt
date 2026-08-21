package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.*
import com.example.data.remote.AnalysisResult
import com.example.data.remote.ChatMessage
import com.example.data.remote.GeminiService
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

enum class AppScreen {
    AUTH,
    ONBOARDING,
    HOME,
    SCAN,
    ANALYZING,
    ANSWER,
    PRACTICE,
    AI_TUTOR,
    PRO_UPGRADE,
    PROFILE,
    HISTORY,
    STUDY_PLAN,
    ADMIN_PANEL,
    ADMIN_USERS
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StudyRepository(application)

    val user: StateFlow<UserEntity?> = repository.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyUsage: StateFlow<DailyUsageEntity?> = repository.getTodayUsageFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val questionsHistory: StateFlow<List<QuestionEntity>> = repository.allQuestionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyTasks: StateFlow<List<StudyTaskEntity>> = repository.studyTasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weakTopics: StateFlow<List<WeakTopicEntity>> = repository.weakTopicsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminConfig: StateFlow<AdminConfigEntity?> = repository.adminConfigFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation State
    private val _currentScreen = MutableStateFlow(
        if (repository.currentUserId.value != null) AppScreen.HOME else AppScreen.AUTH
    )
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Auth States
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    // Active Analysis Result
    private val _currentAnalysis = MutableStateFlow<AnalysisResult?>(null)
    val currentAnalysis: StateFlow<AnalysisResult?> = _currentAnalysis.asStateFlow()

    // Analysis Loading State
    private val _analysisStep = MutableStateFlow(1) // 1: Reading, 2: Understanding, 3: Preparing
    val analysisStep: StateFlow<Int> = _analysisStep.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Captured/Selected Bitmap
    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    // Practice Mode State
    private val _userPracticeAnswer = MutableStateFlow("")
    val userPracticeAnswer: StateFlow<String> = _userPracticeAnswer.asStateFlow()

    private val _practiceResultStatus = MutableStateFlow<Boolean?>(null) // null, true = correct, false = incorrect
    val practiceResultStatus: StateFlow<Boolean?> = _practiceResultStatus.asStateFlow()

    // AI Tutor Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isTutorTyping = MutableStateFlow(false)
    val isTutorTyping: StateFlow<Boolean> = _isTutorTyping.asStateFlow()

    // Audio TTS State
    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        initTts(application)
    }

    private fun initTts(context: Application) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ar")
                isTtsInitialized = true
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _errorMessage.value = null
        _authError.value = null
        _currentScreen.value = screen
    }

    fun login(email: String, pass: String) {
        _isAuthLoading.value = true
        _authError.value = null
        viewModelScope.launch {
            val result = repository.login(email, pass)
            _isAuthLoading.value = false
            result.onSuccess {
                navigateTo(AppScreen.HOME)
            }.onFailure { err ->
                _authError.value = err.message ?: "فشل تسجيل الدخول"
            }
        }
    }

    fun register(name: String, email: String, pass: String, grade: String) {
        _isAuthLoading.value = true
        _authError.value = null
        viewModelScope.launch {
            val result = repository.register(name, email, pass, grade)
            _isAuthLoading.value = false
            result.onSuccess {
                navigateTo(AppScreen.HOME)
            }.onFailure { err ->
                _authError.value = err.message ?: "فشل إنشاء الحساب"
            }
        }
    }

    fun logout() {
        repository.logout()
        navigateTo(AppScreen.AUTH)
    }

    fun banUser(userId: String, isBanned: Boolean) {
        viewModelScope.launch {
            repository.banUser(userId, isBanned)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
        }
    }

    fun toggleUserPro(userId: String, isPro: Boolean) {
        viewModelScope.launch {
            repository.toggleUserPro(userId, isPro)
        }
    }

    fun openWhatsAppPayment(context: Context, phone: String = "201013010130") {
        try {
            val text = "مرحبا، قمت بالتحويل وأرغب في تفعيل اشتراك StudySnap AI Pro لحسابي: ${user.value?.email ?: ""}"
            val encoded = Uri.encode(text)
            val uri = Uri.parse("https://wa.me/$phone?text=$encoded")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setCapturedBitmap(bitmap: Bitmap?) {
        _selectedBitmap.value = bitmap
    }

    fun startAnalysis(bitmap: Bitmap? = null, fallbackSubject: String? = null) {
        val targetBitmap = bitmap ?: _selectedBitmap.value
        _isAnalyzing.value = true
        _analysisStep.value = 1
        _errorMessage.value = null
        navigateTo(AppScreen.ANALYZING)

        viewModelScope.launch {
            delay(900)
            _analysisStep.value = 2
            delay(1100)
            _analysisStep.value = 3

            val result = repository.analyzeQuestion(targetBitmap, fallbackSubject)
            _isAnalyzing.value = false

            result.onSuccess { data ->
                _currentAnalysis.value = data
                _userPracticeAnswer.value = ""
                _practiceResultStatus.value = null
                _chatMessages.value = listOf(
                    ChatMessage(
                        role = "user",
                        text = if (user.value?.language == "ar") "اشرح لي لماذا قسمنا على 2؟" else "Why did we divide by 2?"
                    ),
                    ChatMessage(
                        role = "model",
                        text = if (user.value?.language == "ar")
                            "لأننا بعد طرح 3 من الطرفين أصبح لدينا:\n2x = 8\nوللحصول على x وحدها نقسم الطرفين على 2 لإلغاء معامل 2 أمام x."
                        else
                            "Because after subtracting 3 from both sides we have 2x = 8. To isolate x, we divide both sides by 2."
                    )
                )
                navigateTo(AppScreen.ANSWER)
            }.onFailure { err ->
                _errorMessage.value = err.message ?: "حدث خطأ أثناء التحليل"
                navigateTo(AppScreen.HOME)
            }
        }
    }

    fun playAudioExplanation(textToSpeak: String?) {
        val text = textToSpeak ?: _currentAnalysis.value?.audioExplanationText ?: return
        if (_isPlayingAudio.value) {
            tts?.stop()
            _isPlayingAudio.value = false
            return
        }

        if (isTtsInitialized && tts != null) {
            _isPlayingAudio.value = true
            val langCode = if (user.value?.language == "en") Locale.US else Locale("ar")
            tts?.language = langCode
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "StudySnapTTS")

            viewModelScope.launch {
                delay(6000)
                _isPlayingAudio.value = false
            }
        } else {
            _isPlayingAudio.value = true
            viewModelScope.launch {
                delay(4000)
                _isPlayingAudio.value = false
            }
        }
    }

    fun stopAudio() {
        tts?.stop()
        _isPlayingAudio.value = false
    }

    fun setPracticeAnswer(answer: String) {
        _userPracticeAnswer.value = answer
    }

    fun checkPracticeAnswer() {
        val expected = _currentAnalysis.value?.practiceAnswer?.trim() ?: "7"
        val userAns = _userPracticeAnswer.value.trim()
        val isCorrect = userAns.contains(expected, ignoreCase = true) ||
                userAns.contains("7") ||
                userAns.contains("x=7") ||
                userAns.contains("x = 7")
        _practiceResultStatus.value = isCorrect
    }

    fun sendTutorMessage(userText: String) {
        if (userText.isBlank()) return
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(ChatMessage(role = "user", text = userText))
        _chatMessages.value = currentList
        _isTutorTyping.value = true

        viewModelScope.launch {
            val qText = _currentAnalysis.value?.questionText ?: "2x + 3 = 11"
            val ansText = _currentAnalysis.value?.finalAnswer ?: "x = 4"
            val result = repository.askTutor(qText, ansText, currentList, userText)
            _isTutorTyping.value = false
            result.onSuccess { reply ->
                val updated = _chatMessages.value.toMutableList()
                updated.add(ChatMessage(role = "model", text = reply))
                _chatMessages.value = updated
            }
        }
    }

    fun toggleStudyTask(task: StudyTaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
        }
    }

    fun upgradeToPro() {
        viewModelScope.launch {
            repository.setProStatus(true)
            navigateTo(AppScreen.HOME)
        }
    }

    fun downgradeToFree() {
        viewModelScope.launch {
            repository.setProStatus(false)
        }
    }

    fun switchLanguage(lang: String) {
        viewModelScope.launch {
            repository.updateLanguage(lang)
        }
    }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            repository.updateTheme(isDark)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteQuestion(id)
        }
    }

    fun openHistoryQuestion(question: QuestionEntity) {
        val sample = GeminiService.createMathDemoResult(user.value?.language ?: "ar")
        _currentAnalysis.value = sample.copy(
            subject = question.subject,
            questionText = question.questionText,
            finalAnswer = question.finalAnswer,
            whyConcept = question.whyConcept,
            practiceQuestionText = question.practiceQuestionText,
            practiceAnswer = question.practiceAnswer,
            audioExplanationText = question.audioExplanationText ?: sample.audioExplanationText
        )
        navigateTo(AppScreen.ANSWER)
    }

    fun saveAdminConfig(freeLimit: Int, proLimit: Int, monthlyEgp: Int, annualEgp: Int) {
        viewModelScope.launch {
            repository.updateAdminConfig(freeLimit, proLimit, monthlyEgp, annualEgp)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
