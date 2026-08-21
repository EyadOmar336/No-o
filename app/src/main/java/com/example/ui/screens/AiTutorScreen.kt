package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@Composable
fun AiTutorScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isTutorTyping by viewModel.isTutorTyping.collectAsState()
    val isAr = viewModel.user.collectAsState().value?.language != "en"
    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.ANSWER) },
                    modifier = Modifier.testTag("tutor_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SmartToy,
                        contentDescription = "Tutor Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAr) "مساعد StudySnap AI" else "StudySnap AI Tutor",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldSuccess)
                        )
                        Text(
                            text = if (isAr) "متصل الآن ومستعد للشرح" else "Online & Ready",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Suggestion Prompt Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickChip(
                        text = if (isAr) "💡 بسّط الشرح أكثر" else "💡 Simplify more",
                        onClick = { viewModel.sendTutorMessage(if (isAr) "اشرح لي الحل بطريقة أبسط وتشبيه من الحياة اليومية" else "Explain this simpler with a real-world analogy") },
                        testTag = "chip_simplify"
                    )

                    QuickChip(
                        text = if (isAr) "❓ اختبرني بسؤال" else "❓ Test me",
                        onClick = { viewModel.sendTutorMessage(if (isAr) "اختبرني بسؤال مشابه وتأكد من فهمي" else "Give me a practice problem to test my understanding") },
                        testTag = "chip_test"
                    )

                    QuickChip(
                        text = if (isAr) "📝 مثال آخر" else "📝 Another example",
                        onClick = { viewModel.sendTutorMessage(if (isAr) "أعطني مثالاً إضافياً مع حله" else "Give me another worked example") },
                        testTag = "chip_example"
                    )
                }

                // Chat Input Field
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(26.dp))
                        .background(Color(0xFF131B2E), RoundedCornerShape(26.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = {
                            Text(
                                text = if (isAr) "اسأل المعلم الذكي أي سؤال..." else "Ask AI Tutor anything...",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tutor_message_input")
                    )

                    IconButton(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                viewModel.sendTutorMessage(inputMessage)
                                inputMessage = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary)
                            .testTag("tutor_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.role == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (isUser) PurplePrimary else Color(0xFF131B2E),
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isUser) 20.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 20.dp
                        ),
                        border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3859)) else null,
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = TextWhite,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            if (isTutorTyping) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = Color(0xFF131B2E),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3859))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = PurplePrimary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = if (isAr) "يكتب الشرح الآن..." else "Typing explanation...",
                                    color = TextGray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickChip(
    text: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Text(
            text = text,
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
