package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.entities.QuestionEntity
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.theme.*

@Composable
fun HistoryScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val questions by viewModel.questionsHistory.collectAsState()
    val isAr = viewModel.user.collectAsState().value?.language != "en"
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("الكل") }

    val subjects = listOf("الكل", "رياضيات", "فيزياء", "كيمياء", "أحياء", "إنجليزي")

    val filteredQuestions = questions.filter { q ->
        (selectedSubject == "الكل" || q.subject == selectedSubject) &&
                (searchQuery.isBlank() || q.questionText.contains(searchQuery, ignoreCase = true) || q.subject.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "سجل الأسئلة المحلولة" else "Solved Questions History",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredQuestions.size} ${if (isAr) "سؤال" else "items"}",
                        color = PurplePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isAr) "ابحث في الأسئلة أو المواد..." else "Search questions or subjects...",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextGray)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF2A3859),
                        focusedContainerColor = Color(0xFF131B2E),
                        unfocusedContainerColor = Color(0xFF131B2E),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_search_input")
                )

                // Subject Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subjects) { subj ->
                        val isSelected = selectedSubject == subj
                        Surface(
                            color = if (isSelected) PurplePrimary else Color(0xFF131B2E),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) PurplePrimary else Color(0xFF2A3859)
                            ),
                            modifier = Modifier
                                .clickable { selectedSubject = subj }
                                .testTag("history_filter_$subj")
                        ) {
                            Text(
                                text = subj,
                                color = if (isSelected) Color.White else TextGray,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = AppScreen.HISTORY,
                onNavigate = { viewModel.navigateTo(it) },
                isAr = isAr
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        if (filteredQuestions.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "📚", fontSize = 48.sp)
                    Text(
                        text = if (isAr) "لا توجد أسئلة مسجلة" else "No saved questions yet",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr) "صوّر سؤالك الأول لتظهر خطة الشرح هنا" else "Snap your first question to see solutions here",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredQuestions, key = { it.id }) { item ->
                    HistoryQuestionCard(
                        question = item,
                        isAr = isAr,
                        onClick = { viewModel.openHistoryQuestion(item) },
                        onDelete = { viewModel.deleteHistoryItem(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryQuestionCard(
    question: QuestionEntity,
    isAr: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp))
            .testTag("history_item_${question.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0x2210B981),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = question.subject,
                        color = EmeraldSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = question.questionText,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isAr) "الحل: " else "Ans: "} ${question.finalAnswer}",
                    color = PurplePrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = if (isAr) "عرض الشرح ←" else "View Steps →",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
