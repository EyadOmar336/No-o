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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@Composable
fun ProUpgradeScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val isAr = user?.language != "en"
    var isAnnual by remember { mutableStateOf(true) }

    val monthlyPrice = adminConfig?.monthlyPriceEgp ?: 59
    val annualPrice = adminConfig?.annualPriceEgp ?: 499

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
                    modifier = Modifier.testTag("pro_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = TextWhite
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "StudySnap AI Pro",
                    color = Color(0xFFFBBF24),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }
        },
        bottomBar = {
            val context = androidx.compose.ui.platform.LocalContext.current
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.openWhatsAppPayment(context, "201013010130") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = Color(0xFF10B981))
                        .testTag("pro_whatsapp_contact_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF059669), Color(0xFF10B981), Color(0xFF34D399))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Chat,
                                contentDescription = "WhatsApp",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = if (isAr) "الرجاء الاتصال على وتساب على الرقم الاعلى للاشتراك" else "Contact on WhatsApp to Subscribe",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Text(
                    text = if (isAr) "يتم تفعيل الاشتراك فور تأكيد التحويل عبر واتساب • دعم على مدار الساعة" else "Account activated instantly upon WhatsApp verification • 24/7 Support",
                    color = TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Crown Badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0x33F59E0B)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👑", fontSize = 42.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isAr) "أطلق العنان لقدراتك الدراسية" else "Supercharge Your Grades",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isAr) "حلول وشروحات لا نهائية مع أقوى نماذج الذكاء الاصطناعي" else "Unlimited solutions & explanations powered by Gemini",
                    color = TextGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Billing Toggle (Monthly vs Annual)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PlanToggleButton(
                        title = if (isAr) "شهري" else "Monthly",
                        priceSubtitle = "$monthlyPrice ${if (isAr) "ج.م / شهر" else "EGP/mo"}",
                        isSelected = !isAnnual,
                        badge = null,
                        modifier = Modifier.weight(1f),
                        onClick = { isAnnual = false },
                        testTag = "plan_monthly_toggle"
                    )

                    PlanToggleButton(
                        title = if (isAr) "سنوي (الأفضل)" else "Annual (Best)",
                        priceSubtitle = "$annualPrice ${if (isAr) "ج.م / سنة" else "EGP/yr"}",
                        isSelected = isAnnual,
                        badge = if (isAr) "وفر 30%" else "Save 30%",
                        modifier = Modifier.weight(1f),
                        onClick = { isAnnual = true },
                        testTag = "plan_annual_toggle"
                    )
                }
            }

            // Vodafone Cash / InstaPay & WhatsApp Confirmation Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D231E))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payments,
                            contentDescription = "Payment",
                            tint = Color(0xFF10B981)
                        )
                        Text(
                            text = if (isAr) "طريقة الدفع (فودافون كاش / إنستاباي)" else "Payment Method (Vodafone Cash / InstaPay)",
                            color = Color(0xFF6EE7B7),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Phone number box
                    Surface(
                        color = Color(0xFF064E3B),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "01013010130",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    // Exact requested text
                    Text(
                        text = "الرجاء التحدث على وتساب على هذا الرقم لضمان اشتراكك",
                        color = Color(0xFFFDE047),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = { viewModel.openWhatsAppPayment(context, "201013010130") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("whatsapp_contact_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Chat,
                                contentDescription = "WhatsApp",
                                tint = Color.Black
                            )
                            Text(
                                text = if (isAr) "تحدث عبر واتساب لتفعيل الاشتراك فوراً" else "Chat on WhatsApp to activate",
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Pro Features List Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProFeatureRow(
                        icon = Icons.Filled.Bolt,
                        title = if (isAr) "أسئلة وشروحات غير محدودة" else "Unlimited daily questions & scans",
                        description = if (isAr) "لا مزيد من قيود الـ 10 أسئلة يومياً" else "No daily quota limits"
                    )

                    ProFeatureRow(
                        icon = Icons.Filled.AutoAwesome,
                        title = if (isAr) "محرك Gemini 3.1 Pro المتقدم" else "Gemini 3.1 Pro Engine",
                        description = if (isAr) "أعلى دقة في فهم المسائل المعقدة والمعادلات" else "Deepest multimodal mathematical understanding"
                    )

                    ProFeatureRow(
                        icon = Icons.Filled.VolumeUp,
                        title = if (isAr) "شروحات صوتية ذكية تفاعلية" else "Interactive Voice Explanations",
                        description = if (isAr) "استمع إلى شرح مفصل لأي مسألة بصوت طبيعي" else "Listen to natural step-by-step audio"
                    )

                    ProFeatureRow(
                        icon = Icons.Filled.SmartToy,
                        title = if (isAr) "معلم خصوصي AI على مدار الساعة" else "24/7 AI Private Tutor",
                        description = if (isAr) "اسأله وناقشه في أي خطوة حتى تفهمها تماماً" else "Multi-turn tutoring with instant feedback"
                    )

                    ProFeatureRow(
                        icon = Icons.Filled.Block,
                        title = if (isAr) "تجربة نظيفة بدون أي إعلانات" else "100% Ad-Free Experience",
                        description = if (isAr) "تركيز كامل في المذاكرة دون تشتيت" else "Zero distractions while studying"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PlanToggleButton(
    title: String,
    priceSubtitle: String,
    isSelected: Boolean,
    badge: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = if (isSelected) Color(0xFF24304A) else Color.Transparent,
        shape = RoundedCornerShape(14.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)) else null,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (badge != null) {
                Surface(
                    color = Color(0xFF10B981),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = title,
                color = if (isSelected) Color.White else TextGray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = priceSubtitle,
                color = if (isSelected) Color(0xFFFBBF24) else TextMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ProFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0x22F59E0B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(20.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = TextGray,
                fontSize = 12.sp
            )
        }
    }
}
