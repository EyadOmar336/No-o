package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@Composable
fun ScanScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = viewModel.user.collectAsState().value?.language != "en"
    val context = LocalContext.current
    var isFlashOn by remember { mutableStateOf(false) }

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

    // Generate a clean dummy math note bitmap for simulation if camera capture is triggered
    fun createSampleMathBitmap(): Bitmap {
        val bmp = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(android.graphics.Color.WHITE)
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 48f
            isAntiAlias = true
        }
        canvas.drawText("حل المعادلة التالية:", 100f, 250f, paint)
        paint.textSize = 64f
        canvas.drawText("2x + 3 = 11", 100f, 400f, paint)
        paint.textSize = 48f
        canvas.drawText("أوجد قيمة x", 100f, 550f, paint)
        return bmp
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Flash, Title, Grid, Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isFlashOn = !isFlashOn },
                    modifier = Modifier.testTag("flash_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) Color(0xFFFBBF24) else TextWhite
                    )
                }

                Text(
                    text = if (isAr) "صوّر السؤال" else "Scan Question",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { /* Grid toggle */ },
                        modifier = Modifier.testTag("grid_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Grid4x4,
                            contentDescription = "Grid",
                            tint = TextWhite
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier.testTag("scan_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = TextWhite
                        )
                    }
                }
            }

            // Central Viewfinder Frame with Handwritten Note Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFF8B5CF6), RoundedCornerShape(24.dp))
                    .background(Color(0xFFF4EBD9)), // Paper note background
                contentAlignment = Alignment.Center
            ) {
                // Math Notebook Content
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "حل المعادلة التالية:",
                        color = Color(0xFF1E293B),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "2x + 3 = 11",
                        color = Color(0xFF0F172A),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "أوجد قيمة x",
                        color = Color(0xFF334155),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                // Laser scan line animation
                val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
                val scanOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "scan_offset"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (scanOffset - 180).dp)
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0xFF8B5CF6),
                                    Color(0xFF06B6D4),
                                    Color(0xFF8B5CF6),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Helper Tip
            Text(
                text = if (isAr) "تأكد من وضوح السؤال في الصورة" else "Ensure the question is clearly visible in frame",
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            // Shutter Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery button
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .testTag("scan_gallery_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = TextWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Primary Shutter Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color(0xFF8B5CF6), CircleShape)
                        .background(Color.White)
                        .clickable {
                            val sampleBmp = createSampleMathBitmap()
                            viewModel.setCapturedBitmap(sampleBmp)
                            viewModel.startAnalysis(sampleBmp)
                        }
                        .testTag("scan_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                // Flip/Crop button
                IconButton(
                    onClick = {
                        val sampleBmp = createSampleMathBitmap()
                        viewModel.setCapturedBitmap(sampleBmp)
                        viewModel.startAnalysis(sampleBmp)
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .testTag("scan_flip_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.FlipCameraAndroid,
                        contentDescription = "Flip Camera",
                        tint = TextWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
