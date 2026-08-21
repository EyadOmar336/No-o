package com.example.data.local.daos

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserFlow(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUser(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY registeredAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :id")
    suspend fun setUserBanned(id: String, isBanned: Boolean)

    @Query("UPDATE users SET isPro = :isPro WHERE id = :id")
    suspend fun setUserPro(id: String, isPro: Boolean)
}

@Dao
interface UsageDao {
    @Query("SELECT * FROM daily_usage WHERE dateKey = :dateKey LIMIT 1")
    fun getUsageFlow(dateKey: String): Flow<DailyUsageEntity?>

    @Query("SELECT * FROM daily_usage WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getUsage(dateKey: String): DailyUsageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUsage(usage: DailyUsageEntity)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY timestamp DESC")
    fun getAllQuestionsFlow(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE subject = :subject ORDER BY timestamp DESC")
    fun getQuestionsBySubjectFlow(subject: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: Long): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestionById(id: Long)

    @Query("DELETE FROM questions")
    suspend fun clearAll()
}

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks ORDER BY id ASC")
    fun getAllTasksFlow(): Flow<List<StudyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<StudyTaskEntity>)

    @Update
    suspend fun updateTask(task: StudyTaskEntity)
}

@Dao
interface WeakTopicDao {
    @Query("SELECT * FROM weak_topics ORDER BY id ASC")
    fun getAllWeakTopicsFlow(): Flow<List<WeakTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<WeakTopicEntity>)
}

@Dao
interface AdminConfigDao {
    @Query("SELECT * FROM admin_config WHERE configKey = :key LIMIT 1")
    fun getConfigFlow(key: String = "global_config"): Flow<AdminConfigEntity?>

    @Query("SELECT * FROM admin_config WHERE configKey = :key LIMIT 1")
    suspend fun getConfig(key: String = "global_config"): AdminConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: AdminConfigEntity)
}
