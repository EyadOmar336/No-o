package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.data.local.entities.AdminConfigEntity
import com.example.data.local.entities.UserEntity
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminUsersScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val currentUser by viewModel.user.collectAsState()
    val isAr = currentUser?.language != "en"

    var searchQuery by remember { mutableStateOf("") }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }
    var showConfigDialog by remember { mutableStateOf(false) }

    val filteredUsers = remember(allUsers, searchQuery) {
        if (searchQuery.isBlank()) {
            allUsers
        } else {
            allUsers.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.email.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val totalUsersCount = allUsers.size
    val proUsersCount = allUsers.count { it.isPro }
    val bannedUsersCount = allUsers.count { it.isBanned }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                            modifier = Modifier.testTag("admin_back_button")
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                        }

                        Column {
                            Text(
                                text = if (isAr) "لوحة إدارة المستخدمين" else "User Management",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "المسؤول: ${currentUser?.name ?: "إياد عمر"}",
                                color = Color(0xFFFBBF24),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = { showConfigDialog = true },
                        modifier = Modifier.testTag("admin_open_pricing_config")
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Pricing & Quotas", tint = PurplePrimary)
                    }
                }

                // Stats overview chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBadge(title = "المستخدمين", count = totalUsersCount.toString(), color = PurplePrimary, modifier = Modifier.weight(1f))
                    StatBadge(title = "مشتركو Pro", count = proUsersCount.toString(), color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
                    StatBadge(title = "محظورين", count = bannedUsersCount.toString(), color = Color(0xFFEF4444), modifier = Modifier.weight(1f))
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث بالاسم أو البريد الإلكتروني...", color = TextGray, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = PurplePrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextGray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color(0xFF2A3859),
                        focusedContainerColor = Color(0xFF131B2E),
                        unfocusedContainerColor = Color(0xFF131B2E)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("admin_user_search_input")
                )
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (filteredUsers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) "لا يوجد مستخدمين مسجلين بعد" else "لا توجد نتائج مطابقة للبحث",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            items(filteredUsers, key = { it.id }) { userItem ->
                AdminUserCard(
                    user = userItem,
                    isCurrentSession = userItem.id == currentUser?.id,
                    onToggleBan = { viewModel.banUser(userItem.id, !userItem.isBanned) },
                    onTogglePro = { viewModel.toggleUserPro(userItem.id, !userItem.isPro) },
                    onDelete = { userToDelete = userItem }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Delete confirmation dialog
    if (userToDelete != null) {
        val target = userToDelete!!
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            containerColor = Color(0xFF131B2E),
            title = {
                Text(
                    text = "حذف المستخدم نهائياً",
                    color = Color(0xFFF87171),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "هل أنت متأكد من حذف الحساب:\n${target.name} (${target.email})؟\nلا يمكن التراجع عن هذا الإجراء وسيتم مسح بياناته بالكامل.",
                    color = TextWhite,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(target.id)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("نعم، حذف نهائياً", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("إلغاء", color = TextGray)
                }
            }
        )
    }

    // Pricing & Quota Config Dialog
    if (showConfigDialog) {
        AdminPricingConfigDialog(
            initialConfig = adminConfig,
            onDismiss = { showConfigDialog = false },
            onSave = { freeLim, proLim, mPrice, aPrice ->
                viewModel.saveAdminConfig(freeLim, proLim, mPrice, aPrice)
                showConfigDialog = false
            }
        )
    }
}

@Composable
private fun AdminUserCard(
    user: UserEntity,
    isCurrentSession: Boolean,
    onToggleBan: () -> Unit,
    onTogglePro: () -> Unit,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val regDate = remember(user.registeredAt) { sdf.format(Date(user.registeredAt)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (user.isBanned) Color(0xFFEF4444).copy(alpha = 0.6f) else Color(0xFF2A3859),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isBanned) Color(0xFF221318) else Color(0xFF131B2E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Name, Email & Status Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (user.isAdmin) Color(0xFFF59E0B) else PurplePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = user.name,
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (user.isAdmin) {
                            Surface(
                                color = Color(0xFFF59E0B),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "مسؤول",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = user.email,
                        color = Color(0xFF93C5FD),
                        fontSize = 13.sp
                    )

                    Text(
                        text = "${user.grade} • مسجل: $regDate",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pro Badge
                Surface(
                    color = if (user.isPro) Color(0xFF065F46) else Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (user.isPro) "👑 باقة PRO نشطة" else "مجاني (10 يومياً)",
                        color = if (user.isPro) Color(0xFF6EE7B7) else TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Ban status badge
                Surface(
                    color = if (user.isBanned) Color(0xFF7F1D1D) else Color(0xFF064E3B),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (user.isBanned) "🚫 محظور من التطبيق" else "🟢 الحساب نشط",
                        color = if (user.isBanned) Color(0xFFFCA5A5) else Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = Color(0xFF1E293B))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ban / Unban Button
                OutlinedButton(
                    onClick = onToggleBan,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (user.isBanned) Color(0xFF064E3B) else Color(0xFF3F1D28)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (user.isBanned) Icons.Filled.CheckCircle else Icons.Filled.Block,
                            contentDescription = null,
                            tint = if (user.isBanned) Color(0xFF6EE7B7) else Color(0xFFF87171),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (user.isBanned) "فك الحظر" else "حظر",
                            color = if (user.isBanned) Color(0xFF6EE7B7) else Color(0xFFF87171),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Toggle Pro Button
                OutlinedButton(
                    onClick = onTogglePro,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (user.isPro) Color(0xFF1E293B) else Color(0xFF2E2010)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (user.isPro) "إلغاء Pro" else "تفعيل Pro",
                            color = Color(0xFFFBBF24),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Delete User Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF3B1219), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete User",
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBadge(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, color = color, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = title, color = TextGray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AdminPricingConfigDialog(
    initialConfig: AdminConfigEntity?,
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
                text = "⚙️ ضبط الأسعار والحدود اليومية",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = freeLimit,
                    onValueChange = { freeLimit = it },
                    label = { Text("الحد اليومي المجاني للأسئلة") },
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
                    label = { Text("سعر اشتراك Pro الشهري (ج.م)") },
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
                    label = { Text("سعر اشتراك Pro السنوي (ج.م)") },
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
