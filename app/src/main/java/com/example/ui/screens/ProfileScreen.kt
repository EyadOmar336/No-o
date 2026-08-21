package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.ProCrownBadge
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val isAr = user?.language != "en"
    var showAdminDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "الملف الشخصي" else "Student Profile",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { showAdminDialog = true },
                    modifier = Modifier.testTag("admin_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AdminPanelSettings,
                        contentDescription = "Admin Config",
                        tint = PurplePrimary
                    )
                }
            }
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = AppScreen.PROFILE,
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Student Card with Avatar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(2.dp, PurplePrimary, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_student_avatar),
                            contentDescription = "Student Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = user?.name ?: "طالب جديد",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (user?.isAdmin == true) {
                                Surface(
                                    color = Color(0xFFF59E0B),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "المسؤول",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = user?.email ?: "",
                            color = Color(0xFF93C5FD),
                            fontSize = 12.sp
                        )

                        Text(
                            text = user?.grade ?: "الثانوية العامة",
                            color = TextGray,
                            fontSize = 13.sp
                        )
                        ProCrownBadge(
                            isPro = user?.isPro == true,
                            onClick = { viewModel.navigateTo(AppScreen.PRO_UPGRADE) }
                        )
                    }
                }
            }

            // Subscription Management Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isAr) "خطة الاشتراك" else "Subscription Plan",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (user?.isPro == true)
                                (if (isAr) "خطة Pro غير المحدودة (59 ج.م)" else "Pro Unlimited Plan (59 EGP)")
                            else
                                (if (isAr) "الخطة المجانية (10 أسئلة يومياً)" else "Free Plan (10 questions/day)"),
                            color = if (user?.isPro == true) Color(0xFFFBBF24) else TextGray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        TextButton(
                            onClick = {
                                if (user?.isPro == true) {
                                    viewModel.downgradeToFree()
                                } else {
                                    viewModel.navigateTo(AppScreen.PRO_UPGRADE)
                                }
                            },
                            modifier = Modifier.testTag("toggle_plan_button")
                        ) {
                            Text(
                                text = if (user?.isPro == true)
                                    (if (isAr) "تغيير للخطة المجانية" else "Downgrade")
                                else
                                    (if (isAr) "ترقية إلى Pro" else "Upgrade"),
                                color = PurplePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // App Settings Section (Language & Theme)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isAr) "إعدادات التطبيق" else "App Settings",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Language Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Language, contentDescription = "Lang", tint = PurplePrimary)
                            Text(
                                text = if (isAr) "لغة التطبيق والشرح" else "Language",
                                color = TextWhite,
                                fontSize = 14.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            LanguageChip(
                                label = "العربية",
                                selected = isAr,
                                onClick = { viewModel.switchLanguage("ar") },
                                testTag = "lang_ar_button"
                            )
                            LanguageChip(
                                label = "English",
                                selected = !isAr,
                                onClick = { viewModel.switchLanguage("en") },
                                testTag = "lang_en_button"
                            )
                        }
                    }

                    Divider(color = Color(0xFF1E293B))

                    // Notifications Settings
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.NotificationsActive, contentDescription = "Notifications", tint = Color(0xFF10B981))
                            Column {
                                Text(
                                    text = if (isAr) "إشعارات المذاكرة والتحفيز" else "Study Notifications",
                                    color = TextWhite,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isAr) "تذكيرات يومية لمراجعة الأسئلة" else "Daily revision alerts",
                                    color = TextGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                com.example.util.NotificationHelper.sendNotification(
                                    context,
                                    "🎯 تذكير المذاكرة من StudySnap AI",
                                    "أحسنت! حافظ على نشاطك اليومي وحل سؤالاً جديداً الآن لتقوية مستواك 🚀"
                                )
                            },
                            modifier = Modifier.testTag("send_test_notification_btn")
                        ) {
                            Text(
                                text = if (isAr) "تجربة إشعار" else "Test Alert",
                                color = Color(0xFF10B981),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Divider(color = Color(0xFF1E293B))

                    // Dark Theme Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.DarkMode, contentDescription = "Theme", tint = PurplePrimary)
                            Text(
                                text = if (isAr) "الوضع الليلي الداكن" else "Dark Mode",
                                color = TextWhite,
                                fontSize = 14.sp
                            )
                        }

                        Switch(
                            checked = user?.isDarkMode ?: true,
                            onCheckedChange = { viewModel.toggleTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PurplePrimary
                            ),
                            modifier = Modifier.testTag("theme_switch")
                        )
                    }
                }
            }

            // Admin Dashboard Access (For Admins)
            if (user?.isAdmin == true) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF221A0F))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Security, contentDescription = "Admin", tint = Color(0xFFF59E0B))
                            Text(
                                text = "لوحة تحكم المسؤول العام (إياد عمر)",
                                color = Color(0xFFFDE047),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "عرض جميع المسجلين، حظر الحسابات، حذف المستخدمين، وإدارة أسعار وباقات التطبيق.",
                            color = TextGray,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ADMIN_USERS) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("open_admin_users_screen_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.People, contentDescription = null, tint = Color.Black)
                                Text("إدارة المستخدمين والمحظورين", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Logout Button
            OutlinedButton(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .testTag("logout_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF241419))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = Color(0xFFF87171))
                    Text(
                        text = if (isAr) "تسجيل الخروج من الحساب" else "Log Out",
                        color = Color(0xFFFCA5A5),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showAdminDialog) {
        AdminConfigDialog(
            initialConfig = adminConfig,
            onDismiss = { showAdminDialog = false },
            onSave = { freeLim, proLim, mPrice, aPrice ->
                viewModel.saveAdminConfig(freeLim, proLim, mPrice, aPrice)
                showAdminDialog = false
            }
        )
    }
}

@Composable
private fun LanguageChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = if (selected) PurplePrimary else Color(0xFF1E293B),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) PurplePrimary else Color(0xFF334155)),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else TextGray,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun AdminConfigDialog(
    initialConfig: com.example.data.local.entities.AdminConfigEntity?,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, Int) -> Unit
) {
    var freeLimit by remember { mutableStateOf((initialConfig?.freeDailyLimit ?: 10).toString()) }
    var proLimit by remember { mutableStateOf((initialConfig?.proDailyLimit ?: 100).toString()) }
    var monthlyPrice by remember { mutableStateOf((initialConfig?.monthlyPriceEgp ?: 59).toString()) }
    var annualPrice by remember { mutableStateOf((initialConfig?.annualPriceEgp ?: 499).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF131B2E),
        title = {
            Text(
                text = "⚙️ لوحة تحكم المسؤول (Admin)",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = freeLimit,
                    onValueChange = { freeLimit = it },
                    label = { Text("الحد اليومي المجاني (افتراضي 10)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF2A3859)
                    )
                )

                OutlinedTextField(
                    value = monthlyPrice,
                    onValueChange = { monthlyPrice = it },
                    label = { Text("سعر باقة Pro الشهرية (ج.م)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF2A3859)
                    )
                )

                OutlinedTextField(
                    value = annualPrice,
                    onValueChange = { annualPrice = it },
                    label = { Text("سعر باقة Pro السنوية (ج.م)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF2A3859)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fL = freeLimit.toIntOrNull() ?: 10
                    val pL = proLimit.toIntOrNull() ?: 100
                    val mP = monthlyPrice.toIntOrNull() ?: 59
                    val aP = annualPrice.toIntOrNull() ?: 499
                    onSave(fL, pL, mP, aP)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("حفظ التغييرات", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextGray)
            }
        }
    )
}
