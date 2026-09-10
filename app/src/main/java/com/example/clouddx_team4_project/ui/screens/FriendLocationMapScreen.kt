package com.example.clouddx_team4_project.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.BuildConfig
import com.example.clouddx_team4_project.data.KakaoReverseGeocodeClient
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.network.SharingFriendResponse
import kotlinx.coroutines.delay

private val AnOnBlue = Color(0xFF6A92FE)
private val ScreenBackground = Color(0xFFF7F8FC)
private val TextBlack = Color(0xFF222222)
private val TextGray = Color(0xFF888888)

@Composable
fun FriendLocationMapScreen(
    friendId: Long?,
    friendName: String,
    friendLat: Double,
    friendLng: Double,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val memberId = remember { RetrofitClient.tokenManager?.getMemberId() ?: 3L }

    var friendAddressDisplay by remember { mutableStateOf("친구 위치 확인 중...") }
    var recenterRequestKey by remember { mutableIntStateOf(0) }

    // ========================================
    // 안심경로와 동일한 기기 방향(나침반) 센서 설정
    // ========================================
    var currentBearing by remember { mutableStateOf<Float?>(null) }
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    DisposableEffect(sensorManager) {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)

                    var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    if (azimuth < 0) azimuth += 360f

                    currentBearing = azimuth
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        rotationSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    // ========================================
    // ⭐️ 안심경로와 100% 동일한 getSharingFriendsLocations 폴링 로직
    // ========================================
    var sharingFriends by remember {
        mutableStateOf(
            listOf(
                SharingFriendResponse(
                    friendId = friendId ?: 0L,
                    friendName = friendName,
                    lat = friendLat,
                    lng = friendLng
                )
            )
        )
    }

    LaunchedEffect(memberId) {
        while (true) {
            try {
                val response = RetrofitClient.trackingApi.getSharingFriendsLocations(
                    requesterId = memberId
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        sharingFriends = body
                    }
                }
            } catch (e: Exception) {
                Log.e("FRIEND_MAP", "친구 위치 폴링 중 예외 발생", e)
            }
            delay(1000)
        }
    }

    // 현재 대상 친구의 좌표 추출
    val currentFriend = sharingFriends.find { it.friendId == friendId }
    val activeLat = currentFriend?.lat ?: friendLat
    val activeLng = currentFriend?.lng ?: friendLng

    // ============================================================
    // 친구 좌표 → 주소 변환
    // ============================================================
    LaunchedEffect(activeLat, activeLng) {
        try {
            val response = KakaoReverseGeocodeClient.api.getAddressFromCoordinate(
                authorization = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
                longitude = activeLng,
                latitude = activeLat
            )

            val document = response.documents.firstOrNull()
            val buildingName = document?.roadAddress?.buildingName?.takeIf { it.isNotBlank() }
            val roadAddress = document?.roadAddress?.addressName?.takeIf { it.isNotBlank() }
            val jibunAddress = document?.address?.addressName?.takeIf { it.isNotBlank() }

            friendAddressDisplay = buildingName ?: roadAddress ?: jibunAddress ?: "주소를 찾을 수 없습니다"

        } catch (e: Exception) {
            Log.e("FRIEND_MAP", "주소 변환 실패", e)
            friendAddressDisplay = "주소 확인 실패"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(62.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "뒤로가기",
                    tint = TextBlack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(21.dp)
                        .clickable { onBackClick() }
                )

                Text(
                    text = "${friendName}님의 위치",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 친구 주소 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "위치 아이콘",
                        tint = AnOnBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "현재 위치",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextBlack
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = friendAddressDisplay,
                        fontSize = 14.sp,
                        color = TextGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ========================================
            // ⭐️ 안심경로와 100% 동일한 KakaoMapView 렌더링 호출
            // ========================================
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                KakaoMapView(
                    modifier = Modifier.fillMaxSize(),
                    destinationName = "",
                    destinationLatitude = null,
                    destinationLongitude = null,
                    sharingFriends = sharingFriends, // 안심경로가 쓰던 리스트 그대로 주입
                    showRoute = false,
                    routeMode = "BROAD_FIRST",
                    recenterRequestKey = recenterRequestKey,
                    currentBearing = currentBearing
                )

                // 현재 위치로 이동 버튼
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 40.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                        .clickable {
                            recenterRequestKey++
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = "내 위치로 이동",
                        tint = AnOnBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}