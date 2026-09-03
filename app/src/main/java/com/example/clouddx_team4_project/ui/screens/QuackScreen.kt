package com.example.clouddx_team4_project.ui.screens

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.media.AudioAttributes
import android.media.AudioManager

// ========================================
// 색상
// ========================================

private val AnOnBlue = Color(0xFF6A92FE)
private val LightCardBlue = Color(0xFFF7F9FF)
private val StatusBlue = Color(0xFF4D7FFF)
private val SuccessGreen = Color(0xFF42B96B)
private val EmergencyRed = Color(0xFFE23F3F)
private val Orange = Color(0xFFFF963A)


// ========================================
// 꽥꽥이 화면
// ========================================

@Composable
fun QuackScreen(
    onBackClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onTabSelected: (String) -> Unit = {},
    onEmergencyClick: () -> Unit = {}
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ========================================
    // 카메라 / 플래시 준비
    // ========================================

    val cameraManager = remember {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    val cameraId = remember {

        try {

            cameraManager.cameraIdList.firstOrNull { id ->

                val characteristics =
                    cameraManager.getCameraCharacteristics(id)

                val flashAvailable =
                    characteristics.get(
                        CameraCharacteristics.FLASH_INFO_AVAILABLE
                    ) == true

                val lensFacing =
                    characteristics.get(
                        CameraCharacteristics.LENS_FACING
                    )

                flashAvailable &&
                        lensFacing ==
                        CameraCharacteristics.LENS_FACING_BACK
            }

        } catch (e: Exception) {

            null
        }
    }


    // 플래시 반복 작업
    var flashJob by remember {
        mutableStateOf<Job?>(null)
    }


    // 사이렌 플레이어
    var mediaPlayer by remember {
        mutableStateOf<MediaPlayer?>(null)
    }

    // 원래 볼륨 저장용
    var originalAlarmVolume by remember {
        mutableStateOf<Int?>(null)
    }

    // 작동 상태
    var isRunning by remember {
        mutableStateOf(true)
    }


    // ========================================
    // 사이렌 시작
    // ========================================

    fun startSiren() {

        if (mediaPlayer != null) {
            return
        }

        try {

            // ========================================
            // 기기 알람 볼륨을 최대로 강제 설정
            // (원래 볼륨은 저장해두고, 종료 시 복원)
            // ========================================

            val audioManager =
                context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            originalAlarmVolume =
                audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

            val maxVolume =
                audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)

            audioManager.setStreamVolume(
                AudioManager.STREAM_ALARM,
                maxVolume,
                0
            )

            mediaPlayer =
                MediaPlayer.create(
                    context,
                    R.raw.siren,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build(),
                    audioManager.generateAudioSessionId()
                )

            mediaPlayer?.apply {

                isLooping = true

                setVolume(1.0f, 1.0f)

                start()
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }


    // ========================================
    // 사이렌 중지
    // ========================================

    fun stopSiren() {

        try {

            mediaPlayer?.let {

                if (it.isPlaying) {
                    it.stop()
                }

                it.release()
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        mediaPlayer = null

        // ========================================
        // 원래 알람 볼륨으로 복원
        // ========================================

        try {

            originalAlarmVolume?.let { originalVolume ->

                val audioManager =
                    context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

                audioManager.setStreamVolume(
                    AudioManager.STREAM_ALARM,
                    originalVolume,
                    0
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        originalAlarmVolume = null
    }


    // ========================================
    // 플래시 시작
    // ========================================

    fun startFlash() {

        // 플래시가 없는 기기면 실행 안 함
        if (cameraId == null) {
            return
        }

        // 이미 실행 중이면 중복 방지
        if (flashJob?.isActive == true) {
            return
        }

        flashJob =
            coroutineScope.launch {

                try {

                    while (isActive) {

                        // 플래시 ON
                        cameraManager.setTorchMode(
                            cameraId,
                            true
                        )

                        delay(350)

                        // 플래시 OFF
                        cameraManager.setTorchMode(
                            cameraId,
                            false
                        )

                        delay(350)
                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }
    }


    // ========================================
    // 플래시 중지
    // ========================================

    fun stopFlash() {

        flashJob?.cancel()
        flashJob = null

        if (cameraId != null) {

            try {

                cameraManager.setTorchMode(
                    cameraId,
                    false
                )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }


    // ========================================
    // 꽥꽥이 전체 시작
    // ========================================

    fun startQuack() {

        isRunning = true

        startSiren()
        startFlash()
    }


    // ========================================
    // 꽥꽥이 전체 중지
    // ========================================

    fun stopQuack() {

        isRunning = false

        stopSiren()
        stopFlash()
    }


    // 화면 진입 시 자동 시작
    LaunchedEffect(Unit) {

        startQuack()
    }


    // 화면을 벗어나면 반드시 종료
    DisposableEffect(Unit) {

        onDispose {

            stopQuack()
        }
    }


    // ========================================
    // 화면 전체
    // ========================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnOnBlue)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                // 하단 네비게이션 자리 확보
                .padding(bottom = 92.dp)
        ) {

            // ========================================
            // 1. 상단바
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "뒤로가기",
                    tint = Color.White,
                    modifier = Modifier
                        .size(23.dp)
                        .clickable {

                            stopQuack()
                            onBackClick()
                        }
                )


                Spacer(
                    modifier = Modifier.width(14.dp)
                )


                Text(
                    text = "꽥꽥이",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            // ========================================
            // 2. 메인 콘텐츠
            // ========================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // 상단바 → 큰 아이콘
                Spacer(
                    modifier = Modifier.height(24.dp)
                )


                // ========================================
                // 2-1. 큰 중앙 아이콘
                // ========================================

                Box(
                    modifier = Modifier.size(164.dp),
                    contentAlignment = Alignment.Center
                ) {

                    // 바깥쪽 연한 원
                    Box(
                        modifier = Modifier
                            .size(164.dp)
                            .background(
                                color = Color.White.copy(
                                    alpha = 0.12f
                                ),
                                shape = CircleShape
                            )
                    )


                    // 가운데 원
                    Box(
                        modifier = Modifier
                            .size(152.dp)
                            .background(
                                color = AnOnBlue,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {


                        // 안쪽 원
                        Box(
                            modifier = Modifier
                                .size(144.dp)
                                .background(
                                    color = Color.White.copy(
                                        alpha = 0.08f
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {


                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = "경고음",
                                    tint = Color.White,
                                    modifier = Modifier.size(76.dp)
                                )


                                Spacer(
                                    modifier = Modifier.width(3.dp)
                                )


                                // 주황색 포인트
                                Box(
                                    modifier = Modifier
                                        .size(13.dp)
                                        .background(
                                            color = Orange,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }


                // 큰 아이콘 → 제목
                Spacer(
                    modifier = Modifier.height(26.dp)
                )


                // ========================================
                // 2-2. 작동 상태
                // ========================================

                Text(
                    text = "꽥꽥이 작동 중",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // 설명 1
                Text(
                    text = "경고음과 플래시가 자동으로 작동됩니다.",
                    color = Color.White.copy(
                        alpha = 0.95f
                    ),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                // 설명 2
                Text(
                    text = "사용자가 등록한 보호자에게\n문자가 전송되지 않습니다.",
                    color = Color.White.copy(
                        alpha = 0.88f
                    ),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )


                // 설명 → 상태 카드
                Spacer(
                    modifier = Modifier.height(26.dp)
                )


                // ========================================
                // 2-3. 경고음 / 플래시 상태 카드
                // ========================================

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {


                    QuackStatusCard(
                        title = "경고음",
                        subTitle = "작동 중",
                        iconType = "sound",
                        modifier = Modifier.weight(1f)
                    )


                    QuackStatusCard(
                        title = "플래시",
                        subTitle = "작동 중",
                        iconType = "flash",
                        modifier = Modifier.weight(1f)
                    )
                }


                // 상태 카드 → 중지 버튼
                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                // ========================================
                // 2-4. 중지하기 버튼
                // ========================================

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(74.dp)
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                        .background(Color.White)
                        .clickable {

                            stopQuack()

                            onStopClick()
                        }
                        .padding(
                            horizontal = 22.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {


                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "중지",
                        tint = EmergencyRed,
                        modifier = Modifier.size(30.dp)
                    )


                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )


                    Column {

                        Text(
                            text = "중지하기",
                            color = Color(0xFF222222),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )


                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )


                        Text(
                            text = "꽥꽥이 즉시 중지",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }


        // ========================================
        // 3. 하단 네비게이션
        // ========================================

        AnOnBottomBar(
            selectedTab = "",

            onTabSelected = { tab ->

                // 다른 화면으로 이동하기 전에 종료
                stopQuack()

                onTabSelected(tab)
            },

            onEmergencyClick = {

                // 긴급구조 화면 이동 전에도 종료
                stopQuack()

                onEmergencyClick()
            },

            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }
}


// ========================================
// 경고음 / 플래시 상태 카드
// ========================================

@Composable
private fun QuackStatusCard(
    title: String,
    subTitle: String,
    iconType: String,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .height(80.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(14.dp)
            )
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(
                LightCardBlue
            )
            .padding(
                horizontal = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {


        // ========================================
        // 왼쪽 아이콘
        // ========================================

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = AnOnBlue,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector =
                    if (iconType == "flash") {
                        Icons.Filled.FlashOn
                    } else {
                        Icons.Filled.VolumeUp
                    },

                contentDescription = title,

                tint = Color.White,

                modifier = Modifier.size(25.dp)
            )
        }


        Spacer(
            modifier = Modifier.width(10.dp)
        )


        // ========================================
        // 텍스트
        // ========================================

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = Color(0xFF222222),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )


            Spacer(
                modifier = Modifier.height(3.dp)
            )


            Text(
                text = subTitle,
                color = StatusBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }


        // ========================================
        // 작동 중 체크
        // ========================================

        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "작동 중",
            tint = SuccessGreen,
            modifier = Modifier.size(18.dp)
        )
    }
}


// ========================================
// Preview
// ========================================

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true
)
@Composable
fun QuackScreenPreview() {

    QuackScreen()
}