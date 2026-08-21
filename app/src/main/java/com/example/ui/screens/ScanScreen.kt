package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.theme.*
import java.util.concurrent.Executors

@Composable
fun ScanScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val isAr = viewModel.user.collectAsState().value?.language != "en"
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isFlashOn by remember { mutableStateOf(false) }
    var useFrontCamera by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCameraAvailable by remember { mutableStateOf(true) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

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

    fun captureRealPhoto() {
        val capture = imageCapture
        if (capture != null && hasCameraPermission && isCameraAvailable) {
            isCapturing = true
            val executor = ContextCompat.getMainExecutor(context)
            capture.takePicture(
                executor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                        try {
                            val buffer = imageProxy.planes[0].buffer
                            val bytes = ByteArray(buffer.remaining())
                            buffer.get(bytes)
                            val original = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            val matrix = Matrix().apply {
                                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                            }
                            val rotated = Bitmap.createBitmap(
                                original,
                                0,
                                0,
                                original.width,
                                original.height,
                                matrix,
                                true
                            )
                            imageProxy.close()
                            isCapturing = false
                            viewModel.setCapturedBitmap(rotated)
                            viewModel.startAnalysis(rotated)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            imageProxy.close()
                            isCapturing = false
                            // Fallback
                            val sample = createSampleMathBitmap()
                            viewModel.setCapturedBitmap(sample)
                            viewModel.startAnalysis(sample)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("ScanScreen", "Camera capture error", exception)
                        isCapturing = false
                        // Fallback sample for testing
                        val sample = createSampleMathBitmap()
                        viewModel.setCapturedBitmap(sample)
                        viewModel.startAnalysis(sample)
                    }
                }
            )
        } else {
            // Fallback for emulator / non-camera env
            val sample = createSampleMathBitmap()
            viewModel.setCapturedBitmap(sample)
            viewModel.startAnalysis(sample)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
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
                    onClick = {
                        isFlashOn = !isFlashOn
                        cameraControl?.enableTorch(isFlashOn)
                    },
                    modifier = Modifier.testTag("flash_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) Color(0xFFFBBF24) else TextWhite
                    )
                }

                Text(
                    text = if (isAr) "كاميرا تصوير الأسئلة" else "Scan Question",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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

            // Central Camera Viewfinder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, PurplePrimary, RoundedCornerShape(24.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }

                                    val capture = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                        .build()
                                    imageCapture = capture

                                    val cameraSelector = if (useFrontCamera) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }

                                    cameraProvider.unbindAll()
                                    val cam = cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        capture
                                    )
                                    cameraControl = cam.cameraControl
                                    isCameraAvailable = true
                                } catch (e: Exception) {
                                    Log.e("ScanScreen", "Use case binding failed", e)
                                    isCameraAvailable = false
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Permission not granted UI
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Camera Permission",
                            tint = PurplePrimary,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = if (isAr) "مطلوب إذن الكاميرا الحقيقية" else "Camera Permission Required",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAr) "يرجى منح إذن استخدام الكاميرا لتصوير الأسئلة مباشرة من هاتفك." else "Please grant camera permission to scan questions directly from your device.",
                            color = TextGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("منح إذن الكاميرا", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Focus Overlay corners & Laser scan line animation
                val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
                val scanProgress by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2400, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "scan_offset"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.7f)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                )

                // Laser scan line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.TopCenter)
                        .padding(top = (scanProgress * 280).dp)
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

                // Loading overlay when taking shot
                if (isCapturing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PurplePrimary)
                    }
                }
            }

            // Helper Tip
            Text(
                text = if (isAr) "ضع السؤال بالكامل داخل الإطار واضغط زر التصوير" else "Align the question inside the frame and tap to capture",
                color = TextGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
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

                // Primary Real Hardware Shutter Button
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(CircleShape)
                        .border(4.dp, PurplePrimary, CircleShape)
                        .background(Color.White)
                        .clickable(enabled = !isCapturing) {
                            captureRealPhoto()
                        }
                        .testTag("scan_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Capture",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Flip Camera (Front / Back)
                IconButton(
                    onClick = {
                        useFrontCamera = !useFrontCamera
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

private fun createSampleMathBitmap(): Bitmap {
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
