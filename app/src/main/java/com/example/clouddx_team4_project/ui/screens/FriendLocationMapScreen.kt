package com.example.clouddx_team4_project.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.clouddx_team4_project.BuildConfig
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.data.KakaoReverseGeocodeClient
import com.example.clouddx_team4_project.network.RetrofitClient
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import kotlinx.coroutines.launch

// ========================================
// 색상
// ========================================
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
    var friendAddressDisplay by remember { mutableStateOf("친구 위치 확인 중...") }
    var kakaoMapInstance by remember { mutableStateOf<KakaoMap?>(null) }

    // 현재 위치 복귀 트리거
    var recenterRequestKey by remember { mutableIntStateOf(0) }

    var currentLat by remember { mutableStateOf(friendLat) }
    var currentLng by remember { mutableStateOf(friendLng) }

    // ========================================
    // 3초 주기 실시간 위치 폴링 (서버에서 최신 위치 갱신)
    // ========================================
    LaunchedEffect(key1 = friendId) {
        val targetFriendId = friendId ?: return@LaunchedEffect

        while (true) {
            kotlinx.coroutines.delay(3000)
            try {
                val myId = RetrofitClient.tokenManager?.getMemberId() ?: continue

                val response = RetrofitClient.trackingApi.getFriendLocation(
                    friendId = targetFriendId,
                    requesterId = myId
                )

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    if (result.success) {
                        currentLat = result.lat
                        currentLng = result.lng

                        val newPosition = LatLng.from(currentLat, currentLng)
                        val layer = kakaoMapInstance?.labelManager?.layer
                        val existingMarker = layer?.getLabel("friend_marker_id")

                        if (existingMarker == null) {
                            // PNG + 텍스트를 합성한 비트맵 마커 적용
                            val markerBitmap = createCustomFriendMarkerBitmap(context, friendName)
                            layer?.addLabel(
                                LabelOptions.from("friend_marker_id", newPosition)
                                    .setStyles(
                                        LabelStyle.from(markerBitmap)
                                            .setAnchorPoint(0.5f, 1.0f) // 마커의 맨 아래쪽이 정확한 좌표를 가리키도록 닻(Anchor) 설정
                                    )
                            )
                        } else {
                            existingMarker.moveTo(newPosition)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("FRIEND_MAP", "실시간 위치 갱신 실패", e)
            }
        }
    }

    // ============================================================
    // 친구 좌표 → 주소 변환
    // ============================================================
    LaunchedEffect(currentLat, currentLng) {
        try {
            val response = KakaoReverseGeocodeClient.api.getAddressFromCoordinate(
                authorization = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
                longitude = currentLng,
                latitude = currentLat
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
            val newPosition = LatLng.from(currentLat, currentLng)
            kakaoMapInstance?.moveCamera(
                CameraUpdateFactory.newCenterPosition(newPosition, 16)
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
                                object : MapLifeCycleCallback() {
                                    override fun onMapDestroy() {}
                                    override fun onMapError(error: Exception?) {
                                        Log.e("KAKAO_MAP", "맵 로드 에러: ${error?.message}")
                                    }
                                },
                                object : KakaoMapReadyCallback() {
                                    override fun onMapReady(kakaoMap: KakaoMap) {
                                        kakaoMapInstance = kakaoMap
                                        val initialPosition = LatLng.from(currentLat, currentLng)

                                        kakaoMap.moveCamera(
                                            CameraUpdateFactory.newCenterPosition(initialPosition, 16)
                                        )

                                        // PNG + 텍스트를 합성한 비트맵 마커 적용
                                        val layer = kakaoMap.labelManager?.layer
                                        val markerBitmap = createCustomFriendMarkerBitmap(ctx, friendName)
                                        layer?.addLabel(
                                            LabelOptions.from("friend_marker_id", initialPosition)
                                                .setStyles(
                                                    LabelStyle.from(markerBitmap)
                                                        .setAnchorPoint(0.5f, 1.0f)
                                                )
                                        )
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // ========================================
                // 친구 위치로 다시 이동 버튼 (우측 하단)
                // ========================================
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
                        contentDescription = "친구 위치로 이동",
                        tint = AnOnBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// ============================================================
// 1. marker_current_location.png 로드
// 2. 15% 크기 축소 및 노란색 틴트 적용
// 3. 마커 '위쪽'에 친구 이름 합성
// 4. 틴트로 인해 덮인 가운데 하얀색 원 직접 복구
// ============================================================
private fun createCustomFriendMarkerBitmap(context: Context, friendName: String): Bitmap {
    // 1. 프로젝트 내 PNG 마커 이미지 불러오기
    val drawable = ContextCompat.getDrawable(context, R.drawable.marker_current_location)?.mutate()

    // 2. 마커에 예쁜 노란색(#FFC107) 틴트 입히기
    // (이때 하얀색 구멍까지 싹 다 노란색으로 덮입니다)
    if (drawable != null) {
        DrawableCompat.setTint(drawable, android.graphics.Color.parseColor("#FFC107"))
    }

    // 3. 기존 크기에서 15% 축소 (0.85배)
    val originalW = drawable?.intrinsicWidth ?: 100
    val originalH = drawable?.intrinsicHeight ?: 100
    val markerW = (originalW * 0.85f).toInt()
    val markerH = (originalH * 0.85f).toInt()

    // 4. 이름 텍스트 및 테두리 설정
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        textSize = 42f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = 42f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    // 5. 전체 도화지 크기 계산
    val textMargin = 12
    val textHeight = (textPaint.descent() - textPaint.ascent()).toInt()
    val totalWidth = Math.max(markerW, textPaint.measureText(friendName).toInt() + 20)
    val totalHeight = textHeight + textMargin + markerH

    val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 6. 이름 텍스트 렌더링
    val textX = totalWidth / 2f
    val textY = -textPaint.ascent()
    canvas.drawText(friendName, textX, textY, strokePaint)
    canvas.drawText(friendName, textX, textY, textPaint)

    // 7. 노란색으로 덮인 PNG 마커 렌더링
    val markerLeft = (totalWidth - markerW) / 2
    val markerTop = textHeight + textMargin
    drawable?.setBounds(markerLeft, markerTop, markerLeft + markerW, markerTop + markerH)
    drawable?.draw(canvas)

    // 8. ⭐️ 여기서 가운데 하얀색 포인트를 다시 복구합니다! ⭐️
    val whiteCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
    }
    // 마커 머리 부분의 중심점 (대략 위에서부터 마커 너비의 절반만큼 내려온 위치)
    val circleCenterX = markerLeft + (markerW / 2f)
    val circleCenterY = markerTop + (markerW / 2f)
    // 하얀 원의 크기 (마커 너비의 22% 정도로 설정)
    val circleRadius = markerW * 0.22f

    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, whiteCirclePaint)

    return bitmap
}