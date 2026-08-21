package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.theme.*

@Composable
fun ProCrownBadge(
    isPro: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("pro_badge_button"),
        color = if (isPro) Color(0xFFF59E0B) else Color(0x33F59E0B),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "👑",
                fontSize = 14.sp
            )
            Text(
                text = if (isPro) "PRO ACTIVE" else "Pro",
                color = if (isPro) Color.Black else Color(0xFFFBBF24),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun UsageProgressBar(
    used: Int,
    limit: Int,
    isPro: Boolean,
    isAr: Boolean,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fraction = if (isPro) 0.1f else (used.toFloat() / limit.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val remaining = (limit - used).coerceAtLeast(0)
    val isLimitReached = !isPro && used >= limit

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(24.dp))
            .testTag("usage_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "استخدامك اليوم" else "Today's Usage",
                    color = TextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isPro) {
                    Surface(
                        color = Color(0x2210B981),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isAr) "غير محدود ✨" else "Unlimited ✨",
                            color = EmeraldSuccess,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$used",
                    color = EmeraldSuccess,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "/ $limit",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = if (isAr) "أسئلة مستخدمة" else "questions used",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                )
            }

            // Animated Gradient Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1E293B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF8B5CF6), Color(0xFF6366F1), Color(0xFF06B6D4))
                            )
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLimitReached) {
                        if (isAr) "تم الوصول للحد اليومي" else "Daily limit reached"
                    } else {
                        if (isAr) "$remaining أسئلة متبقية اليوم" else "$remaining questions remaining today"
                    },
                    color = if (isLimitReached) RoseError else TextGray,
                    fontSize = 13.sp,
                    fontWeight = if (isLimitReached) FontWeight.Bold else FontWeight.Normal
                )

                if (isLimitReached || !isPro) {
                    Text(
                        text = if (isAr) "ترقية إلى Pro" else "Upgrade to Pro",
                        color = Color(0xFFFBBF24),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(onClick = onUpgradeClick)
                            .testTag("upgrade_link_text")
                    )
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    isAr: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1322))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Filled.Home,
                label = if (isAr) "الرئيسية" else "Home",
                selected = currentScreen == AppScreen.HOME,
                onClick = { onNavigate(AppScreen.HOME) },
                testTag = "nav_home"
            )

            BottomNavItem(
                icon = Icons.Outlined.History,
                label = if (isAr) "السجل" else "History",
                selected = currentScreen == AppScreen.HISTORY,
                onClick = { onNavigate(AppScreen.HISTORY) },
                testTag = "nav_history"
            )

            // Prominent Scan Button in Center
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(y = (-12).dp)
                    .size(62.dp)
                    .shadow(16.dp, CircleShape, spotColor = Color(0xFF7C3AED))
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))
                        )
                    )
                    .clickable { onNavigate(AppScreen.SCAN) }
                    .testTag("nav_scan_center")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Scan",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            BottomNavItem(
                icon = Icons.Outlined.Psychology,
                label = if (isAr) "تدريب" else "Practice",
                selected = currentScreen == AppScreen.PRACTICE,
                onClick = { onNavigate(AppScreen.PRACTICE) },
                testTag = "nav_practice"
            )

            BottomNavItem(
                icon = Icons.Outlined.Person,
                label = if (isAr) "الملف" else "Profile",
                selected = currentScreen == AppScreen.PROFILE,
                onClick = { onNavigate(AppScreen.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) PurplePrimary else TextMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (selected) PurplePrimary else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AudioWaveformPlayer(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    isAr: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2A3859), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162036))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5CF6))
                    .clickable(onClick = onTogglePlay)
                    .testTag("audio_play_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Audio Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Animated waveform bars
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val barHeights = listOf(14, 22, 28, 16, 26, 32, 18, 24, 30, 20, 15, 28, 22, 16, 24)
                barHeights.forEachIndexed { index, h ->
                    val anim = rememberInfiniteTransition(label = "wave")
                    val animatedH by anim.animateFloat(
                        initialValue = (h * 0.4f),
                        targetValue = if (isPlaying) h.toFloat() else (h * 0.5f),
                        animationSpec = infiniteRepeatable(
                            animation = tween(400 + (index * 60), easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "waveH"
                    )
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(animatedH.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isPlaying) Color(0xFF8B5CF6) else Color(0xFF475569))
                    )
                }
            }

            Text(
                text = "0:15",
                color = TextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
