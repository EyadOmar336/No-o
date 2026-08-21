package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val authError by viewModel.authError.collectAsState()
    val isLoading by viewModel.isAuthLoading.collectAsState()

    var isSignUp by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf("الثانوية العامة") }
    var passwordVisible by remember { mutableStateOf(false) }

    val gradeOptions = listOf(
        "الصف الأول الثانوي",
        "الصف الثاني الثانوي",
        "الثانوية العامة (الصف الثالث)",
        "المرحلة الإعدادية",
        "المرحلة الجامعية"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo & Header
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(PurplePrimary, Color(0xFF4F46E5))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoStories,
                    contentDescription = "StudySnap Logo",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "StudySnap AI",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "منصة المساعد التعليمي الذكي للطلاب",
                color = TextGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Selector: Login vs Sign Up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF131B2E))
                    .padding(4.dp)
            ) {
                TabButton(
                    title = "تسجيل الدخول",
                    isSelected = !isSignUp,
                    modifier = Modifier.weight(1f),
                    onClick = { isSignUp = false }
                )
                TabButton(
                    title = "حساب جديد",
                    isSelected = isSignUp,
                    modifier = Modifier.weight(1f),
                    onClick = { isSignUp = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error Message Alert
            AnimatedVisibility(visible = authError != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF451A1A))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = "Error",
                            tint = Color(0xFFF87171)
                        )
                        Text(
                            text = authError ?: "",
                            color = Color(0xFFFECACA),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Input Fields Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Sign Up Specific: Name & Grade
                    if (isSignUp) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("الاسم الكامل") },
                            placeholder = { Text("مثال: إياد عمر") },
                            leadingIcon = {
                                Icon(Icons.Filled.Person, contentDescription = "Name", tint = PurplePrimary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = Color(0xFF2A3859),
                                focusedLabelColor = PurplePrimary,
                                unfocusedLabelColor = TextGray
                            )
                        )

                        // Grade Selection
                        Text(
                            text = "المرحلة الدراسية:",
                            color = TextGray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            gradeOptions.forEach { grade ->
                                val isSelected = selectedGrade == grade
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PurplePrimary.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable { selectedGrade = grade }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedGrade = grade },
                                        colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                                    )
                                    Text(
                                        text = grade,
                                        color = if (isSelected) Color.White else TextGray,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("البريد الإلكتروني") },
                        placeholder = { Text("example@gmail.com") },
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = "Email", tint = PurplePrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = Color(0xFF2A3859),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextGray
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة المرور") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = "Password", tint = PurplePrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password",
                                    tint = TextGray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = Color(0xFF2A3859),
                            focusedLabelColor = PurplePrimary,
                            unfocusedLabelColor = TextGray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Submit Button
            Button(
                onClick = {
                    if (isSignUp) {
                        viewModel.register(name, email, password, selectedGrade)
                    } else {
                        viewModel.login(email, password)
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = PurplePrimary)
                    .testTag("auth_submit_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleDark)
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
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isSignUp) "إنشاء الحساب وبدء المذاكرة" else "تسجيل الدخول",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick demo helper note
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔒 حسابك محمي بكلمة مرور مشفرة محلياً على جهازك",
                        color = TextGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PurplePrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextGray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
