package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
fun OnboardingScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = viewModel.user.value?.language != "en"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "StudySnap AI",
                    color = TextWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (isAr) "صوّر أي سؤال\nونفهمه معك خطوة بخطوة" else "Snap any question\nUnderstand it step by step",
                    color = Color(0xFFC084FC),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
            }

            // 3D Student Hero Art
            Box(
                modifier = Modifier
                    .size(290.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF131B2E)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_onboarding_student),
                    contentDescription = "Student studying with AI",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Bottom CTA Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = PurplePrimary)
                        .testTag("get_started_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleDark
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isAr) "ابدأ الآن" else "Get Started Now",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = if (isAr) "لديك حساب؟ تسجيل الدخول" else "Already have an account? Log in",
                    color = TextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.HOME) }
                        .testTag("login_link_button")
                )
            }
        }
    }
}
