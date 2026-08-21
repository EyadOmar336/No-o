package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.theme.*

@Composable
fun PracticeScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val analysis by viewModel.currentAnalysis.collectAsState()
    val practiceAnswer by viewModel.userPracticeAnswer.collectAsState()
    val practiceStatus by viewModel.practiceResultStatus.collectAsState()
    val isAr = viewModel.user.collectAsState().value?.language != "en"

    val practiceText = analysis?.practiceQuestionText ?: "حل المعادلة التالية:\n3x - 5 = 16\nأوجد قيمة x"

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
                    modifier = Modifier.testTag("practice_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (isAr) "سؤال مشابه" else "Practice Question",
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
                currentScreen = AppScreen.PRACTICE,
                onNavigate = { viewModel.navigateTo(it) },
                isAr = isAr
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Timer / Progress bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = "Timer",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1E293B))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.7f)
                                .background(EmeraldSuccess)
                        )
                    }
                }

                // Problem Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isAr) "اختبر فهمك للمفهوم" else "Test your understanding",
                            color = TextGray,
                            fontSize = 14.sp
                        )

                        Text(
                            text = practiceText,
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp
                        )
                    }
                }

                // User Input Field for Variable
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "x =",
                            color = PurplePrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = practiceAnswer,
                            onValueChange = { viewModel.setPracticeAnswer(it) },
                            placeholder = {
                                Text(
                                    text = if (isAr) "اكتب إجابتك هنا" else "Type answer here",
                                    color = TextMuted,
                                    fontSize = 15.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("practice_answer_input")
                        )
                    }
                }

                // Result Feedback Card
                if (practiceStatus != null) {
                    val isCorrect = practiceStatus == true
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) Color(0xFF0F2D24) else Color(0xFF33141E)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                contentDescription = "Result",
                                tint = if (isCorrect) EmeraldSuccess else RoseError,
                                modifier = Modifier.size(28.dp)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = if (isCorrect)
                                        (if (isAr) "إجابة صحيحة! أحسنت 🌟" else "Correct Answer! Well done 🌟")
                                    else
                                        (if (isAr) "إجابة غير صحيحة، حاول مجدداً" else "Incorrect, try again!"),
                                    color = if (isCorrect) EmeraldSuccess else RoseError,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (isCorrect)
                                        (if (isAr) "تم إضافة نقطة قوة إلى مهارات الجبر." else "Point added to Algebra mastery.")
                                    else
                                        (if (isAr) "3x - 5 = 16 => 3x = 21 => x = 7" else "3x - 5 = 16 => 3x = 21 => x = 7"),
                                    color = TextGray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = { viewModel.checkPracticeAnswer() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = PurplePrimary)
                        .testTag("check_practice_button"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Text(
                        text = if (isAr) "تحقق من الإجابة" else "Check Answer",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (isAr) "تخطي هذا السؤال" else "Skip this question",
                    color = TextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.HOME) }
                        .testTag("skip_practice_button")
                )
            }
        }
    }
}
