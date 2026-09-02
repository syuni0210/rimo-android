package com.example.clouddx_team4_project.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clouddx_team4_project.BuildConfig
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.data.KakaoReverseGeocodeClient
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.MapLifeCycleCallback
import kotlinx.coroutines.launch

// ========================================
// 색상 (기존 화면들과 동일하게 통일)
// ========================================
private val AnOnBlue = Color(0xFF6A92FE)
private val ScreenBackground = Color(0xFFF7F8FC)
private val TextBlack = Color(0xFF222222)
private val TextGray = Color(0xFF888888)

@Composable
fun FriendLocationMapScreen(
    friendName: String,
    friendLat: Double,
    friendLng: Double,
    onBackClick: () -> Unit
) {
    // ========================================
    // 상태 관리
    // ========================================
    var friendAddressDisplay by remember { mutableStateOf("친구 위치 확인 중...") }
    var kakaoMapInstance by remember { mutableStateOf<KakaoMap?>(null) }
    var myTrackingLabel by remember { mutableStateOf<com.kakao.vectormap.label.Label?>(null) }

    // 현재 위치 복귀 트리거
    var recenterRequestKey by remember { mutableIntStateOf(0) }

    // ============================================================
    // 친구 좌표 → 주소 변환
    // ============================================================
    LaunchedEffect(friendLat, friendLng) {
        try {
            val response = KakaoReverseGeocodeClient.api.getAddressFromCoordinate(
                authorization = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
                longitude = friendLng,
                latitude = friendLat
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

    // ============================================================
    // 친구 위치로 카메라 재이동 트리거
    // ============================================================
    LaunchedEffect(recenterRequestKey) {
        if (recenterRequestKey > 0) {
            kakaoMapInstance?.moveCamera(
                CameraUpdateFactory.newCenterPosition(LatLng.from(friendLat, friendLng))
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ========================================
            // 상단 헤더
            // ========================================
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

            // ========================================
            // 친구 주소 카드
            // ========================================
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
            // 지도 영역
            // ========================================
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            start(
                                // 1. 필수: 맵 라이프사이클 콜백 추가
                                object : MapLifeCycleCallback() {
                                    override fun onMapDestroy() {
                                        // 맵이 파괴될 때 실행할 로직 (비워둬도 무방)
                                    }

                                    override fun onMapError(error: Exception?) {
                                        Log.e("KAKAO_MAP", "맵 로드 에러: ${error?.message}")
                                    }
                                },
                                // 2. 맵 레디 콜백
                                object : KakaoMapReadyCallback() {
                                    override fun onMapReady(kakaoMap: KakaoMap) {
                                        kakaoMapInstance = kakaoMap
                                        val friendPosition = LatLng.from(friendLat, friendLng)

                                        // 처음 지도가 뜰 때 카메라 중심을 '친구 위치'로 설정
                                        kakaoMap.moveCamera(
                                            CameraUpdateFactory.newCenterPosition(
                                                friendPosition,
                                                16
                                            )
                                        )

                                        // 친구 위치 마커 (오류 방지를 위해 Rimo 앱 아이콘으로 임시 설정)
                                        val layer = kakaoMap.labelManager?.layer
                                        layer?.addLabel(
                                            LabelOptions.from(friendPosition)
                                                .setStyles(LabelStyle.from(R.mipmap.rimo))
                                        )
                                        val trackingLabel = layer?.addLabel(
                                            LabelOptions.from(friendPosition)
                                                .setStyles(LabelStyle.from(android.R.drawable.ic_menu_mylocation))
                                        )
                                        myTrackingLabel = trackingLabel

                                        trackingLabel?.let { label ->
                                            kakaoMap.trackingManager?.startTracking(label)
                                        }
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // ========================================
                // 내 위치로 돌아가기 버튼 (우측 하단)
                // ========================================
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 40.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE0E0E0), CircleShape) // 버튼 테두리 추가
                        .clickable {
                            // 버튼 클릭 시 내 위치로 부드럽게 이동
                            myTrackingLabel?.let { label ->
                                kakaoMapInstance?.trackingManager?.startTracking(label)
                            }
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