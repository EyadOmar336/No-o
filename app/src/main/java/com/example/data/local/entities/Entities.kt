package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val password: String = "",
    val grade: String = "الثانوية العامة",
    val schoolLevel: String = "المرحلة الثانوية",
    val isPro: Boolean = false,
    val isAdmin: Boolean = false,
    val isBanned: Boolean = false,
    val proExpiresAt: Long = 0L,
    val streakDays: Int = 1,
    val registeredAt: Long = System.currentTimeMillis(),
    val language: String = "ar", // "ar" or "en"
    val isDarkMode: Boolean = true
)

@Entity(tableName = "daily_usage")
data class DailyUsageEntity(
    @PrimaryKey val dateKey: String, // e.g. "2026-08-21"
    val userId: String = "default_student",
    val questionsUsed: Int = 7,
    val questionsLimit: Int = 10,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val subject: String, // رياضيات, فيزياء, كيمياء, أحياء, إنجليزي, etc.
    val questionText: String,
    val stepsJson: String, // JSON array of steps
    val finalAnswer: String,
    val whyConcept: String,
    val practiceQuestionText: String,
    val practiceAnswer: String,
    val imageUri: String? = null,
    val difficulty: String = "متوسط", // سهل, متوسط, متقدم
    val audioExplanationText: String? = null
)

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val taskDate: String // "2026-08-21"
)

@Entity(tableName = "weak_topics")
data class WeakTopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val topicName: String,
    val statusText: String, // "يحتاج تحسين", "جيد", "ممتاز"
    val recommendation: String
)

@Entity(tableName = "admin_config")
data class AdminConfigEntity(
    @PrimaryKey val configKey: String = "global_config",
    val freeDailyLimit: Int = 10,
    val proDailyLimit: Int = 100,
    val monthlyPriceEgp: Int = 59,
    val annualPriceEgp: Int = 499
)
