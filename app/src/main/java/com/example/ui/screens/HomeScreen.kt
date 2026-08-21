package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.ProCrownBadge
import com.example.ui.components.UsageProgressBar
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val dailyUsage by viewModel.dailyUsage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isAr = user?.language != "en"
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                viewModel.setCapturedBitmap(bitmap)
                viewModel.startAnalysis(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = AppScreen.HOME,
                onNavigate = { viewModel.navigateTo(it) },
                isAr = isAr
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Top Bar: Pro Badge + Greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProCrownBadge(
                    isPro = user?.isPro == true,
                    onClick = { viewModel.navigateTo(AppScreen.PRO_UPGRADE) }
                )

                Column(
                    horizontalAlignment = if (isAr) Alignment.End else Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isAr) "مرحباً ${user?.name ?: "أحمد"} 👋" else "Hello, ${user?.name ?: "Ahmed"} 👋",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (isAr) "مستعد لحل أي سؤال اليوم؟" else "Ready to solve any question today?",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
            }

            // Error Notice Banner if any
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x33F43F5E)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error", tint = RoseError)
                        Text(text = errorMessage ?: "", color = RoseError, fontSize = 13.sp)
                    }
                }
            }

            // Daily Usage Card
            UsageProgressBar(
                used = dailyUsage?.questionsUsed ?: 7,
                limit = dailyUsage?.questionsLimit ?: 10,
                isPro = user?.isPro == true,
                isAr = isAr,
                onUpgradeClick = { viewModel.navigateTo(AppScreen.PRO_UPGRADE) }
            )

            // Primary Scan Button
            Button(
                onClick = { viewModel.navigateTo(AppScreen.SCAN) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = PurplePrimary)
                    .testTag("scan_question_primary_button"),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ScanButtonGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Camera",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (isAr) "صوّر سؤال" else "Scan Question",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Secondary Upload Image Button
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp))
                    .testTag("upload_image_secondary_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF131B2E)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = "Gallery",
                        tint = TextWhite,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (isAr) "رفع صورة" else "Upload Image",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Quick Demo / Subject Experiments Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isAr) "تجربة سريعة" else "Quick Test",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SubjectQuickCard(
                        title = if (isAr) "رياضيات" else "Math",
                        emoji = "➗",
                        color = SubjectMath,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.startAnalysis(fallbackSubject = "رياضيات") },
                        testTag = "demo_math"
                    )

                    SubjectQuickCard(
                        title = if (isAr) "كيمياء" else "Chemistry",
                        emoji = "🧪",
                        color = SubjectChemistry,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.startAnalysis(fallbackSubject = "كيمياء") },
                        testTag = "demo_chemistry"
                    )

                    SubjectQuickCard(
                        title = if (isAr) "أحياء" else "Biology",
                        emoji = "🔬",
                        color = SubjectBiology,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.startAnalysis(fallbackSubject = "أحياء") },
                        testTag = "demo_biology"
                    )

                    SubjectQuickCard(
                        title = if (isAr) "فيزياء" else "Physics",
                        emoji = "⚛️",
                        color = SubjectPhysics,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.startAnalysis(fallbackSubject = "فيزياء") },
                        testTag = "demo_physics"
                    )
                }
            }

            // Daily Streak & Study Plan Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.STUDY_PLAN) }
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (isAr) "🔥 7 أيام حماس ومذاكرة مستمرة" else "🔥 7-Day Study Streak",
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isAr) "اضغط لعرض خطة المذاكرة اليومية وتتبع مهامك" else "Tap to view today's study plan and tasks",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "View Plan",
                        tint = TextGray
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectQuickCard(
    title: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Text(
                text = title,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
