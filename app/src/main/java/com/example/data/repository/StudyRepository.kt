package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.remote.AnalysisResult
import com.example.data.remote.ChatMessage
import com.example.data.remote.GeminiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class StudyRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val usageDao = db.usageDao()
    private val questionDao = db.questionDao()
    private val studyTaskDao = db.studyTaskDao()
    private val weakTopicDao = db.weakTopicDao()
    private val adminConfigDao = db.adminConfigDao()

    private val prefs = context.getSharedPreferences("studysnap_prefs", Context.MODE_PRIVATE)

    // Current Logged In User ID State
    private val _currentUserId = MutableStateFlow<String?>(prefs.getString("current_user_id", null))
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialConfigIfNeeded()
        }
    }

    private fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    val userFlow: Flow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id != null) userDao.getUserFlow(id) else flowOf(null)
    }

    val allUsersFlow: Flow<List<UserEntity>> = userDao.getAllUsersFlow()
    val allQuestionsFlow: Flow<List<QuestionEntity>> = questionDao.getAllQuestionsFlow()
    val studyTasksFlow: Flow<List<StudyTaskEntity>> = studyTaskDao.getAllTasksFlow()
    val weakTopicsFlow: Flow<List<WeakTopicEntity>> = weakTopicDao.getAllWeakTopicsFlow()
    val adminConfigFlow: Flow<AdminConfigEntity?> = adminConfigDao.getConfigFlow()

    private fun isSuperAdminCredentials(email: String, name: String, password: String): Boolean {
        val cleanEmail = email.trim().lowercase()
        val cleanName = name.trim()
        val cleanPass = password.trim()

        val emailMatch = cleanEmail == "www.eyadomar@gmail.com" || cleanEmail == "eyadomar@gmail.com"
        val nameMatch = cleanName.contains("اياد عمر") || cleanName.contains("إياد عمر")
        val passMatch = cleanPass == "EyadOmar01013"

        return emailMatch || (nameMatch && passMatch) || (emailMatch && passMatch)
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        grade: String = "الثانوية العامة"
    ): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        val cleanName = name.trim()
        val cleanPass = password.trim()

        if (cleanEmail.isBlank() || cleanName.isBlank() || cleanPass.isBlank()) {
            return Result.failure(Exception("يرجى ملء جميع الحقول المطلوبة"))
        }

        val existingUser = userDao.getUserByEmail(cleanEmail)
        if (existingUser != null) {
            return Result.failure(Exception("هذا البريد الإلكتروني مسجل بالفعل. يرجى تسجيل الدخول."))
        }

        val isAdmin = isSuperAdminCredentials(cleanEmail, cleanName, cleanPass)
        val isPro = isAdmin // Admin gets Pro by default

        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            name = cleanName,
            email = cleanEmail,
            password = cleanPass,
            grade = grade,
            schoolLevel = if (grade.contains("إعدادي")) "المرحلة الإعدادية" else "المرحلة الثانوية",
            isPro = isPro,
            isAdmin = isAdmin,
            isBanned = false,
            proExpiresAt = if (isPro) Long.MAX_VALUE else 0L,
            streakDays = 1,
            registeredAt = System.currentTimeMillis(),
            language = "ar",
            isDarkMode = true
        )

        userDao.insertOrUpdateUser(newUser)
        setCurrentUserId(newUser.id)
        return Result.success(newUser)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        val cleanPass = password.trim()

        if (cleanEmail.isBlank() || cleanPass.isBlank()) {
            return Result.failure(Exception("يرجى إدخال البريد الإلكتروني وكلمة المرور"))
        }

        var user = userDao.getUserByEmail(cleanEmail)

        // If it's the requested Super Admin credentials and not created yet, auto-create
        if (user == null && isSuperAdminCredentials(cleanEmail, "اياد عمر محمد عمر", cleanPass)) {
            val adminUser = UserEntity(
                id = UUID.randomUUID().toString(),
                name = "اياد عمر محمد عمر",
                email = "www.eyadomar@gmail.com",
                password = "EyadOmar01013",
                grade = "المسؤول العام",
                schoolLevel = "لوحة الإدارة",
                isPro = true,
                isAdmin = true,
                isBanned = false,
                proExpiresAt = Long.MAX_VALUE,
                streakDays = 7,
                registeredAt = System.currentTimeMillis(),
                language = "ar",
                isDarkMode = true
            )
            userDao.insertOrUpdateUser(adminUser)
            user = adminUser
        }

        if (user == null) {
            return Result.failure(Exception("لا يوجد حساب مسجل بهذا البريد. يرجى إنشاء حساب جديد."))
        }

        if (user.isBanned) {
            return Result.failure(Exception("تم حظر هذا الحساب من قبل إدارة التطبيق. للتواصل: 01013010130 عبر واتساب"))
        }

        if (user.password.isNotBlank() && user.password != cleanPass) {
            return Result.failure(Exception("كلمة المرور غير صحيحة. يرجى التأكد والمحاولة مرة أخرى."))
        }

        // Elevate admin if matches
        if (isSuperAdminCredentials(user.email, user.name, cleanPass) && (!user.isAdmin || !user.isPro)) {
            user = user.copy(isAdmin = true, isPro = true)
            userDao.insertOrUpdateUser(user)
        }

        setCurrentUserId(user.id)
        return Result.success(user)
    }

    fun logout() {
        prefs.edit().remove("current_user_id").apply()
        _currentUserId.value = null
    }

    private fun setCurrentUserId(id: String) {
        prefs.edit().putString("current_user_id", id).apply()
        _currentUserId.value = id
    }

    suspend fun banUser(userId: String, isBanned: Boolean) {
        userDao.setUserBanned(userId, isBanned)
    }

    suspend fun deleteUser(userId: String) {
        userDao.deleteUserById(userId)
        if (_currentUserId.value == userId) {
            logout()
        }
    }

    suspend fun toggleUserPro(userId: String, isPro: Boolean) {
        userDao.setUserPro(userId, isPro)
    }

    fun getTodayUsageFlow(): Flow<DailyUsageEntity?> {
        return usageDao.getUsageFlow(getTodayDateKey())
    }

    suspend fun getTodayUsage(): DailyUsageEntity {
        val today = getTodayDateKey()
        var usage = usageDao.getUsage(today)
        if (usage == null) {
            val currentId = _currentUserId.value
            val user = if (currentId != null) userDao.getUser(currentId) else null
            val config = adminConfigDao.getConfig() ?: AdminConfigEntity()
            val limit = if (user?.isPro == true) config.proDailyLimit else config.freeDailyLimit
            usage = DailyUsageEntity(
                dateKey = today,
                questionsUsed = 0, // Real fresh start for genuine students
                questionsLimit = limit
            )
            usageDao.insertOrUpdateUsage(usage)
        }
        return usage
    }

    suspend fun canAnalyzeQuestion(): Pair<Boolean, String> {
        val currentId = _currentUserId.value
        val user = if (currentId != null) userDao.getUser(currentId) else null

        if (user?.isBanned == true) {
            return Pair(false, "تم حظر هذا الحساب من قبل إدارة التطبيق.")
        }

        val usage = getTodayUsage()
        if (user?.isPro == true || user?.isAdmin == true) {
            return Pair(true, "")
        }
        if (usage.questionsUsed >= usage.questionsLimit) {
            val isAr = user?.language != "en"
            val msg = if (isAr) {
                "لقد استهلكت جميع الأسئلة المجانية الـ 10 لهذا اليوم. ترقية إلى Pro للحصول على أسئلة غير محدودة!"
            } else {
                "You have reached today's 10 free questions limit. Upgrade to Pro for unlimited questions!"
            }
            return Pair(false, msg)
        }
        return Pair(true, "")
    }

    suspend fun analyzeQuestion(
        bitmap: Bitmap?,
        fallbackText: String? = null
    ): Result<AnalysisResult> {
        val (canAnalyze, errorMsg) = canAnalyzeQuestion()
        if (!canAnalyze) {
            return Result.failure(Exception(errorMsg))
        }

        val currentId = _currentUserId.value
        val user = if (currentId != null) userDao.getUser(currentId) else null
        val lang = user?.language ?: "ar"

        // Execute AI Call via Gemini
        val result = GeminiService.analyzeImageQuestion(bitmap, fallbackText, lang)
        if (result.isSuccess) {
            val data = result.getOrThrow()
            // Increment usage atomically
            val currentUsage = getTodayUsage()
            val updatedUsage = currentUsage.copy(
                questionsUsed = currentUsage.questionsUsed + 1,
                updatedAt = System.currentTimeMillis()
            )
            usageDao.insertOrUpdateUsage(updatedUsage)

            // Save question to history
            val stepsJsonArr = JSONArray()
            data.steps.forEach { step ->
                val obj = JSONObject()
                obj.put("stepNumber", step.stepNumber)
                obj.put("title", step.title)
                obj.put("description", step.description)
                if (step.mathExpression != null) obj.put("mathExpression", step.mathExpression)
                stepsJsonArr.put(obj)
            }

            val questionEntity = QuestionEntity(
                subject = data.subject,
                questionText = data.questionText,
                stepsJson = stepsJsonArr.toString(),
                finalAnswer = data.finalAnswer,
                whyConcept = data.whyConcept,
                practiceQuestionText = data.practiceQuestionText,
                practiceAnswer = data.practiceAnswer,
                difficulty = data.difficulty,
                audioExplanationText = data.audioExplanationText
            )
            questionDao.insertQuestion(questionEntity)
        }
        return result
    }

    suspend fun askTutor(
        contextQuestion: String,
        contextAnswer: String,
        history: List<ChatMessage>,
        userMessage: String
    ): Result<String> {
        val currentId = _currentUserId.value
        val user = if (currentId != null) userDao.getUser(currentId) else null
        val lang = user?.language ?: "ar"
        return GeminiService.askTutor(contextQuestion, contextAnswer, history, userMessage, lang)
    }

    suspend fun toggleTaskCompleted(task: StudyTaskEntity) {
        studyTaskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    suspend fun setProStatus(isPro: Boolean) {
        val currentId = _currentUserId.value ?: return
        val user = userDao.getUser(currentId) ?: return
        val config = adminConfigDao.getConfig() ?: AdminConfigEntity()
        val updated = user.copy(
            isPro = isPro,
            proExpiresAt = if (isPro) System.currentTimeMillis() + 30L * 24 * 3600 * 1000 else 0L
        )
        userDao.insertOrUpdateUser(updated)

        // Update daily limit
        val todayUsage = getTodayUsage()
        val newLimit = if (isPro) config.proDailyLimit else config.freeDailyLimit
        usageDao.insertOrUpdateUsage(todayUsage.copy(questionsLimit = newLimit))
    }

    suspend fun updateLanguage(lang: String) {
        val currentId = _currentUserId.value ?: return
        val user = userDao.getUser(currentId) ?: return
        userDao.insertOrUpdateUser(user.copy(language = lang))
    }

    suspend fun updateTheme(isDark: Boolean) {
        val currentId = _currentUserId.value ?: return
        val user = userDao.getUser(currentId) ?: return
        userDao.insertOrUpdateUser(user.copy(isDarkMode = isDark))
    }

    suspend fun deleteQuestion(id: Long) {
        questionDao.deleteQuestionById(id)
    }

    suspend fun updateAdminConfig(freeLimit: Int, proLimit: Int, monthlyEgp: Int, annualEgp: Int) {
        val config = AdminConfigEntity(
            freeDailyLimit = freeLimit,
            proDailyLimit = proLimit,
            monthlyPriceEgp = monthlyEgp,
            annualPriceEgp = annualEgp
        )
        adminConfigDao.insertOrUpdateConfig(config)
        val currentId = _currentUserId.value
        val user = if (currentId != null) userDao.getUser(currentId) else null
        val usage = getTodayUsage()
        val newLimit = if (user?.isPro == true) proLimit else freeLimit
        usageDao.insertOrUpdateUsage(usage.copy(questionsLimit = newLimit))
    }

    private suspend fun seedInitialConfigIfNeeded() {
        if (adminConfigDao.getConfig() == null) {
            adminConfigDao.insertOrUpdateConfig(
                AdminConfigEntity(
                    freeDailyLimit = 10,
                    proDailyLimit = 100,
                    monthlyPriceEgp = 59,
                    annualPriceEgp = 499
                )
            )
        }

        // Seed Super Admin account if not existing yet
        val adminUser = userDao.getUserByEmail("www.eyadomar@gmail.com")
        if (adminUser == null) {
            userDao.insertOrUpdateUser(
                UserEntity(
                    id = "super_admin_eyad",
                    name = "اياد عمر محمد عمر",
                    email = "www.eyadomar@gmail.com",
                    password = "EyadOmar01013",
                    grade = "المسؤول العام",
                    schoolLevel = "لوحة الإدارة",
                    isPro = true,
                    isAdmin = true,
                    isBanned = false,
                    proExpiresAt = Long.MAX_VALUE,
                    streakDays = 7,
                    registeredAt = System.currentTimeMillis(),
                    language = "ar",
                    isDarkMode = true
                )
            )
        }

        val today = getTodayDateKey()
        val tasks = studyTaskDao.getAllTasksFlow().firstOrNull()
        if (tasks.isNullOrEmpty()) {
            studyTaskDao.insertTasks(
                listOf(
                    StudyTaskEntity(title = "مراجعة الجبر والمعادلات", subject = "رياضيات", durationMinutes = 30, isCompleted = false, taskDate = today),
                    StudyTaskEntity(title = "حل مسائل نيوتن", subject = "فيزياء", durationMinutes = 45, isCompleted = false, taskDate = today),
                    StudyTaskEntity(title = "مفاهيم الكيمياء العضوية", subject = "كيمياء", durationMinutes = 30, isCompleted = false, taskDate = today)
                )
            )
        }

        val weakTopics = weakTopicDao.getAllWeakTopicsFlow().firstOrNull()
        if (weakTopics.isNullOrEmpty()) {
            weakTopicDao.insertTopics(
                listOf(
                    WeakTopicEntity(subject = "رياضيات", topicName = "الجبر والمعادلات الخطية", statusText = "يحتاج تحسين", recommendation = "تدرب على 5 مسائل إضافية لعزل المتغيرات."),
                    WeakTopicEntity(subject = "فيزياء", topicName = "قوانين نيوتن وحساب المتجهات", statusText = "جيد", recommendation = "راجع رسم مخطط الجسم الحر.")
                )
            )
        }
    }
}

