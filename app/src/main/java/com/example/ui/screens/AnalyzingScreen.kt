package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@Composable
fun AnalyzingScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val analysisStep by viewModel.analysisStep.collectAsState()
    val isAr = viewModel.user.collectAsState().value?.language != "en"

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier.testTag("analyzing_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (isAr) "جاري التحليل..." else "Analyzing...",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            // 3D AI Robot Studying Art
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF131B2E)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_ai_robot_studying),
                    contentDescription = "AI Robot Studying",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Analysis Progress Checklist Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnalysisCheckItem(
                        title = if (isAr) "قراءة السؤال..." else "Reading the question...",
                        isDone = analysisStep >= 2,
                        isInProgress = analysisStep == 1,
                        spinAngle = spinAngle
                    )

                    AnalysisCheckItem(
                        title = if (isAr) "فهم المشكلة..." else "Understanding the problem...",
                        isDone = analysisStep >= 3,
                        isInProgress = analysisStep == 2,
                        spinAngle = spinAngle
                    )

                    AnalysisCheckItem(
                        title = if (isAr) "تجهيز الشرح..." else "Preparing the explanation...",
                        isDone = false,
                        isInProgress = analysisStep >= 3,
                        spinAngle = spinAngle
                    )
                }
            }

            // Tip Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Text(
                    text = if (isAr)
                        "نصيحة: حاول تصوير السؤال بوضوح للحصول على أفضل نتيجة"
                    else
                        "Tip: Keep your phone steady and ensure clear lighting for best results",
                    color = TextGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                )
            }
        }
    }
}

@Composable
private fun AnalysisCheckItem(
    title: String,
    isDone: Boolean,
    isInProgress: Boolean,
    spinAngle: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = if (isDone || isInProgress) TextWhite else TextMuted,
            fontSize = 15.sp,
            fontWeight = if (isInProgress) FontWeight.Bold else FontWeight.Normal
        )

        if (isDone) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Completed",
                tint = EmeraldSuccess,
                modifier = Modifier.size(22.dp)
            )
        } else if (isInProgress) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = PurplePrimary,
                strokeWidth = 2.5.dp
            )
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF334155), CircleShape)
            )
        }
    }
}
