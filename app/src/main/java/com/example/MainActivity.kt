package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.screens.*
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.StudySnapTheme
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    private val viewModel: StudyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Notifications
        NotificationHelper.createNotificationChannel(this)
        NotificationHelper.scheduleRecurringStudyReminders(this)

        setContent {
            val user by viewModel.user.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val context = LocalContext.current

            // Notification Permission Request for Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    NotificationHelper.sendNotification(
                        context,
                        "✨ مرحباً بك في StudySnap AI!",
                        "الإشعارات مفعلة الآن بنجاح لتذكيرك بالمذاكرة وحل الأسئلة."
                    )
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            val isDark = user?.isDarkMode ?: true
            val isRtl = (user?.language ?: "ar") != "en"

            StudySnapTheme(darkTheme = isDark, isRtl = isRtl) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(200))
                        },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.AUTH -> AuthScreen(viewModel = viewModel)
                            AppScreen.ONBOARDING -> OnboardingScreen(viewModel = viewModel)
                            AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                            AppScreen.SCAN -> ScanScreen(viewModel = viewModel)
                            AppScreen.ANALYZING -> AnalyzingScreen(viewModel = viewModel)
                            AppScreen.ANSWER -> AnswerScreen(viewModel = viewModel)
                            AppScreen.PRACTICE -> PracticeScreen(viewModel = viewModel)
                            AppScreen.AI_TUTOR -> AiTutorScreen(viewModel = viewModel)
                            AppScreen.PRO_UPGRADE -> ProUpgradeScreen(viewModel = viewModel)
                            AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                            AppScreen.HISTORY -> HistoryScreen(viewModel = viewModel)
                            AppScreen.STUDY_PLAN -> StudyPlanScreen(viewModel = viewModel)
                            AppScreen.ADMIN_PANEL -> ProfileScreen(viewModel = viewModel)
                            AppScreen.ADMIN_USERS -> AdminUsersScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
