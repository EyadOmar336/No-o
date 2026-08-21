package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.remote.GeminiService
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.components.AudioWaveformPlayer
import com.example.ui.theme.*

@Composable
fun AnswerScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val analysis by viewModel.currentAnalysis.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val isAr = viewModel.user.collectAsState().value?.language != "en"

    val data = analysis ?: GeminiService.createMathDemoResult(if (isAr) "ar" else "en")

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
                    modifier = Modifier.testTag("answer_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (isAr) "الشرح خطوة بخطوة" else "Step-by-Step Explanation",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.AI_TUTOR) },
                    modifier = Modifier.testTag("answer_tutor_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SmartToy,
                        contentDescription = "AI Tutor",
                        tint = PurplePrimary
                    )
                }
            }
        },
        bottomBar = {
            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.PRACTICE) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = PurplePrimary)
                            .testTag("similar_question_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                    ) {
                        Text(
                            text = if (isAr) "سؤال مشابه" else "Practice Similar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.playAudioExplanation(data.audioExplanationText) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .border(1.dp, Color(0xFF8B5CF6), RoundedCornerShape(18.dp))
                            .testTag("voice_explain_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlayingAudio) Icons.Filled.Pause else Icons.Filled.VolumeUp,
                                contentDescription = "Voice",
                                tint = PurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (isPlayingAudio)
                                    (if (isAr) "إيقاف الصوت" else "Pause Voice")
                                else
                                    (if (isAr) "اشرحها بصوت" else "Explain with Voice"),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subject Pill Tag
            Surface(
                color = Color(0x2210B981),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SubjectMath.copy(alpha = 0.4f))
            ) {
                Text(
                    text = data.subject,
                    color = EmeraldSuccess,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Question Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isAr) "السؤال المكتشف:" else "Detected Problem:",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                    Text(
                        text = data.questionText,
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                }
            }

            // Audio Player Banner if Active
            if (isPlayingAudio) {
                AudioWaveformPlayer(
                    isPlaying = true,
                    onTogglePlay = { viewModel.playAudioExplanation(data.audioExplanationText) },
                    isAr = isAr
                )
            }

            // Step-by-Step Card ("الشرح")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "الشرح بالتفصيل" else "Detailed Steps",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Expand",
                            tint = TextGray
                        )
                    }

                    Divider(color = Color(0xFF1E293B))

                    data.steps.forEach { step ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isAr) "الخطوة ${step.stepNumber}: ${step.title}" else "Step ${step.stepNumber}: ${step.title}",
                                color = PurplePrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = step.description,
                                color = TextWhite,
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            )
                            if (step.mathExpression != null) {
                                Surface(
                                    color = Color(0xFF0B0F19),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = step.mathExpression,
                                        color = CyanAccent,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Final Answer Card ("الإجابة النهائية")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2624))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (isAr) "الإجابة النهائية" else "Final Answer",
                            color = EmeraldSuccess,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = data.finalAnswer,
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Correct",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Why Concept Card ("لماذا؟")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "💡", fontSize = 18.sp)
                        Text(
                            text = if (isAr) "لماذا قمنا بهذه الخطوات؟" else "Why did this work?",
                            color = Color(0xFFFBBF24),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = data.whyConcept,
                        color = TextGray,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            // AI Tutor Invitation Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { viewModel.navigateTo(AppScreen.AI_TUTOR) }
                    .border(1.dp, PurplePrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .testTag("ask_ai_tutor_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1438))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = "Tutor",
                            tint = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAr) "هل لديك سؤال حول هذا الحل؟" else "Have questions about this solution?",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isAr) "تحدث مع مساعد AI ليشرح لك أي خطوة" else "Chat with AI Tutor for simpler explanation",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Chat",
                        tint = PurplePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
