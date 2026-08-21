package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.StudyTaskEntity
import com.example.data.local.entities.WeakTopicEntity
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.theme.*

@Composable
fun StudyPlanScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.studyTasks.collectAsState()
    val weakTopics by viewModel.weakTopics.collectAsState()
    val user by viewModel.user.collectAsState()
    val isAr = user?.language != "en"

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size.coerceAtLeast(1)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier.testTag("study_plan_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (isAr) "خطة المذاكرة الذكية" else "Smart Study Plan",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = AppScreen.HOME,
                onNavigate = { viewModel.navigateTo(it) },
                isAr = isAr
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Streak & Completion Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isAr) "إنجاز مهام اليوم" else "Today's Task Progress",
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) "$completedCount من $totalCount مكتملة" else "$completedCount of $totalCount completed",
                                    color = TextGray,
                                    fontSize = 13.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x22F59E0B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🔥 ${user?.streakDays ?: 7}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                            }
                        }

                        // Progress Bar
                        val fraction = (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E293B))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction)
                                    .background(EmeraldSuccess)
                            )
                        }
                    }
                }
            }

            // Daily Tasks Section
            item {
                Text(
                    text = if (isAr) "مهام اليوم المقترحة" else "Today's Suggested Tasks",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(tasks) { task ->
                StudyTaskCard(
                    task = task,
                    onToggle = { viewModel.toggleStudyTask(task) }
                )
            }

            // Weak Topics Breakdown Section
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isAr) "نقاط التركيز والتحسين" else "Focus & Improvement Areas",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(weakTopics) { topic ->
                WeakTopicCard(topic = topic)
            }
        }
    }
}

@Composable
private fun StudyTaskCard(
    task: StudyTaskEntity,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(16.dp))
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (task.isCompleted) EmeraldSuccess else Color.Transparent)
                        .border(
                            2.dp,
                            if (task.isCompleted) EmeraldSuccess else Color(0xFF475569),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Completed",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) TextGray else TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${task.subject} • ${task.durationMinutes} دقيقة",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WeakTopicCard(topic: WeakTopicEntity) {
    val isNeedImp = topic.statusText.contains("تحسين") || topic.statusText.contains("Needs")
    val badgeColor = if (isNeedImp) RoseError else EmeraldSuccess

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${topic.subject}: ${topic.topicName}",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = topic.statusText,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = topic.recommendation,
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}
