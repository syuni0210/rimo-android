package com.example.clouddx_team4_project.ui.screens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.clouddx_team4_project.data.KakaoDirectionsClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.async
import java.util.Locale


private val RouteBlue =
    Color(0xFF6A92FE)

private val RouteBackground =
    Color(0xFFF8F9FC)

private val RouteTextBlack =
    Color(0xFF222222)

private val RouteTextGray =
    Color(0xFF8B8B8B)

private val RouteBorderGray =
    Color(0xFFE7E9EE)


// ========================================
// API 경로 정보
// ========================================

private data class RouteInfo(
    val distanceMeter: Int,
    val timeSecond: Int
)


// ========================================
// 경로 선택 화면
// ========================================

@Composable
fun RouteSelectScreen(

    startName: String = "현재 위치",

    destinationName: String = "목적지",

    destinationLatitude: Double? = null,

    destinationLongitude: Double? = null,


    onBackClick: () -> Unit = {},

    onFastRouteClick: () -> Unit = {},

    onBrightRouteClick: () -> Unit = {},

    onBroadRouteClick: () -> Unit = {}
) {

    val context =
        LocalContext.current


    val fusedLocationClient =
        remember {

            LocationServices
                .getFusedLocationProviderClient(
                    context
                )
        }


    // ========================================
    // 현재 위치
    // ========================================

    var currentLatitude by remember {
        mutableStateOf<Double?>(null)
    }

    var currentLongitude by remember {
        mutableStateOf<Double?>(null)
    }


    // ========================================
    // 실제 경로 결과
    // ========================================

    var fastRouteInfo by remember {
        mutableStateOf<RouteInfo?>(null)
    }

    var broadRouteInfo by remember {
        mutableStateOf<RouteInfo?>(null)
    }


    // ========================================
    // 상태
    // ========================================

    var isLoading by remember {
        mutableStateOf(true)
    }

    var routeError by remember {
        mutableStateOf<String?>(null)
    }


    // ========================================
    // 권한
    // ========================================

    val hasLocationPermission =

        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED


    // ========================================
    // 현재 GPS
    // ========================================

    @SuppressLint("MissingPermission")
    fun loadCurrentLocation() {

        if (!hasLocationPermission) {

            routeError =
                "현재 위치 권한이 필요합니다."

            isLoading =
                false

            return
        }


        val token =
            CancellationTokenSource()


        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                token.token
            )
            .addOnSuccessListener { location ->

                if (location == null) {

                    routeError =
                        "현재 위치를 가져오지 못했습니다."

                    isLoading =
                        false

                    return@addOnSuccessListener
                }


                currentLatitude =
                    location.latitude

                currentLongitude =
                    location.longitude
            }
            .addOnFailureListener { error ->

                Log.e(
                    "ROUTE_SELECT",
                    "GPS 오류",
                    error
                )


                routeError =
                    "현재 위치를 가져오지 못했습니다."

                isLoading =
                    false
            }
    }


    LaunchedEffect(Unit) {

        loadCurrentLocation()
    }


    // ========================================
    // 실제 경로 두 개 요청
    // ========================================

    LaunchedEffect(
        currentLatitude,
        currentLongitude,
        destinationLatitude,
        destinationLongitude
    ) {

        val startLat =
            currentLatitude
                ?: return@LaunchedEffect


        val startLng =
            currentLongitude
                ?: return@LaunchedEffect


        val endLat =
            destinationLatitude


        val endLng =
            destinationLongitude


        if (
            endLat == null ||
            endLng == null
        ) {

            routeError =
                "목적지 위치 정보가 없습니다."

            isLoading =
                false

            return@LaunchedEffect
        }


        try {

            isLoading =
                true

            routeError =
                null


            // ========================================
            // 빠른길
            // ========================================

            val fastDeferred =
                async {

                    KakaoDirectionsClient
                        .api
                        .getWalkingRoute(

                            authorization =
                                KakaoDirectionsClient
                                    .authorization,

                            startX =
                                startLng.toString(),

                            startY =
                                startLat.toString(),

                            endX =
                                endLng.toString(),

                            endY =
                                endLat.toString(),

                            startName =
                                startName,

                            endName =
                                destinationName,

                            routeMode =
                                "SHORTEST"
                        )
                }


            // ========================================
            // 대로변
            // ========================================

            val broadDeferred =
                async {

                    KakaoDirectionsClient
                        .api
                        .getWalkingRoute(

                            authorization =
                                KakaoDirectionsClient
                                    .authorization,

                            startX =
                                startLng.toString(),

                            startY =
                                startLat.toString(),

                            endX =
                                endLng.toString(),

                            endY =
                                endLat.toString(),

                            startName =
                                startName,

                            endName =
                                destinationName,

                            routeMode =
                                "BROAD_FIRST"
                        )
                }


            val fastResponse =
                fastDeferred.await()


            val broadResponse =
                broadDeferred.await()


            // ========================================
            // 빠른길 데이터
            // ========================================

            fastResponse
                .route
                ?.properties
                ?.let {

                    val distance =
                        it.totalDistance

                    val time =
                        it.totalTime


                    if (
                        distance != null &&
                        time != null
                    ) {

                        fastRouteInfo =
                            RouteInfo(
                                distanceMeter = distance,
                                timeSecond = time
                            )
                    }
                }


            // ========================================
            // 대로변 데이터
            // ========================================

            broadResponse
                .route
                ?.properties
                ?.let {

                    val distance =
                        it.totalDistance

                    val time =
                        it.totalTime


                    if (
                        distance != null &&
                        time != null
                    ) {

                        broadRouteInfo =
                            RouteInfo(
                                distanceMeter = distance,
                                timeSecond = time
                            )
                    }
                }


            if (
                fastRouteInfo == null &&
                broadRouteInfo == null
            ) {

                routeError =
                    "도보 경로를 찾지 못했습니다."
            }


        } catch (
            error: Exception
        ) {

            Log.e(
                "ROUTE_SELECT",
                "경로 API 오류",
                error
            )


            routeError =
                "경로 정보를 불러오지 못했습니다."


        } finally {

            isLoading =
                false
        }
    }


    // ========================================
    // 화면
    // ========================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                RouteBackground
            )
            .statusBarsPadding()
    ) {


        // ========================================
        // 헤더
        // ========================================

        RouteHeader(
            onBackClick =
                onBackClick
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 18.dp
                )
        ) {

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            // ========================================
            // 출발 / 도착
            // ========================================

            RouteLocationCard(
                startName =
                    startName,

                destinationName =
                    destinationName
            )


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            // ========================================
            // 로딩
            // ========================================

            if (isLoading) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 10.dp
                        ),

                    horizontalArrangement =
                        Arrangement.Center,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(
                                21.dp
                            ),

                        strokeWidth =
                            2.dp,

                        color =
                            RouteBlue
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                9.dp
                            )
                    )


                    Text(
                        text =
                            "도보 경로를 계산하고 있습니다.",

                        fontSize =
                            13.sp,

                        color =
                            RouteTextGray
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            5.dp
                        )
                )
            }


            // ========================================
            // 오류
            // ========================================

            if (routeError != null) {

                Text(
                    text =
                        routeError!!,

                    fontSize =
                        13.sp,

                    color =
                        MaterialTheme.colorScheme.error
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )
            }


            // ========================================
            // 빠른길
            // ========================================

            RouteChoiceCard(

                title =
                    "빠른길",

                distance =
                    fastRouteInfo
                        ?.let {
                            routeDistanceText(
                                it.distanceMeter
                            )
                        }
                        ?: if (isLoading) {
                            "계산 중"
                        } else {
                            "-"
                        },

                time =
                    fastRouteInfo
                        ?.let {
                            routeTimeText(
                                it.timeSecond
                            )
                        }
                        ?: "",

                description =
                    "가장 짧은 거리의 도보 경로",

                enabled =
                    fastRouteInfo != null &&
                            !isLoading,

                onClick =
                    onFastRouteClick
            )


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // ========================================
            // 밝은길
            // ========================================

            RouteChoiceCard(

                title =
                    "밝은길",

                distance =
                    "준비 중",

                time =
                    "",

                description =
                    "가로등·CCTV 등 안전시설 기반 경로",

                enabled =
                    false,

                onClick =
                    onBrightRouteClick
            )


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // ========================================
            // 대로변
            // ========================================

            RouteChoiceCard(

                title =
                    "대로변",

                distance =
                    broadRouteInfo
                        ?.let {
                            routeDistanceText(
                                it.distanceMeter
                            )
                        }
                        ?: if (isLoading) {
                            "계산 중"
                        } else {
                            "-"
                        },

                time =
                    broadRouteInfo
                        ?.let {
                            routeTimeText(
                                it.timeSecond
                            )
                        }
                        ?: "",

                description =
                    "넓은 길을 우선하는 도보 경로",

                enabled =
                    broadRouteInfo != null &&
                            !isLoading,

                onClick =
                    onBroadRouteClick
            )


            Spacer(
                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            Text(
                text =
                    "경로를 선택하면 안내가 시작됩니다.",

                fontSize =
                    12.sp,

                color =
                    RouteTextGray,

                modifier =
                    Modifier.align(
                        Alignment.CenterHorizontally
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        25.dp
                    )
            )
        }
    }
}


// ========================================
// 헤더
// ========================================

@Composable
private fun RouteHeader(
    onBackClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                60.dp
            )
            .padding(
                horizontal = 18.dp
            )
    ) {

        Icon(
            imageVector =
                Icons.Filled.ArrowBackIosNew,

            contentDescription =
                "뒤로가기",

            tint =
                RouteTextBlack,

            modifier = Modifier
                .size(
                    20.dp
                )
                .align(
                    Alignment.CenterStart
                )
                .clickable {

                    onBackClick()
                }
        )


        Text(
            text =
                "경로 선택",

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                RouteTextBlack,

            modifier =
                Modifier.align(
                    Alignment.Center
                )
        )


        Icon(
            imageVector =
                Icons.Filled.Info,

            contentDescription =
                null,

            tint =
                Color(
                    0xFF555555
                ),

            modifier = Modifier
                .size(
                    21.dp
                )
                .align(
                    Alignment.CenterEnd
                )
        )
    }
}


// ========================================
// 위치 카드
// ========================================

@Composable
private fun RouteLocationCard(
    startName: String,
    destinationName: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                14.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    16.dp
                )
        ) {

            RouteLocationRow(
                title =
                    "출발지",

                value =
                    startName
            )


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            HorizontalDivider(
                color =
                    RouteBorderGray
            )


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            RouteLocationRow(
                title =
                    "도착지",

                value =
                    destinationName
            )
        }
    }
}


@Composable
private fun RouteLocationRow(
    title: String,
    value: String
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                title,

            fontSize =
                13.sp,

            fontWeight =
                FontWeight.SemiBold,

            color =
                RouteBlue,

            modifier =
                Modifier.width(
                    58.dp
                )
        )


        Text(
            text =
                value,

            fontSize =
                14.sp,

            fontWeight =
                FontWeight.Medium,

            color =
                RouteTextBlack,

            maxLines =
                1,

            overflow =
                TextOverflow.Ellipsis,

            modifier =
                Modifier.weight(
                    1f
                )
        )
    }
}


// ========================================
// 경로 카드
// ========================================

@Composable
private fun RouteChoiceCard(

    title: String,

    distance: String,

    time: String,

    description: String,

    enabled: Boolean,

    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled =
                    enabled
            ) {

                onClick()
            },

        shape =
            RoundedCornerShape(
                14.dp
            ),

        border =
            BorderStroke(
                width =
                    1.dp,

                color =
                    RouteBorderGray
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    if (enabled) {

                        Color.White

                    } else {

                        Color(
                            0xFFF3F4F6
                        )
                    }
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    16.dp
                )
        ) {

            Text(
                text =
                    title,

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    if (enabled) {

                        RouteTextBlack

                    } else {

                        RouteTextGray
                    }
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        distance,

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        if (enabled) {

                            RouteTextBlack

                        } else {

                            RouteTextGray
                        }
                )


                if (time.isNotBlank()) {

                    Spacer(
                        modifier =
                            Modifier.width(
                                12.dp
                            )
                    )


                    Text(
                        text =
                            time,

                        fontSize =
                            13.sp,

                        color =
                            RouteTextGray
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.LocationOn,

                    contentDescription =
                        null,

                    tint =
                        if (enabled) {

                            RouteBlue

                        } else {

                            RouteTextGray
                        },

                    modifier =
                        Modifier.size(
                            16.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            5.dp
                        )
                )


                Text(
                    text =
                        description,

                    fontSize =
                        12.sp,

                    color =
                        RouteTextGray
                )
            }
        }
    }
}


// ========================================
// 거리 포맷
// ========================================

private fun routeDistanceText(
    meter: Int
): String {

    return if (meter < 1000) {

        "${meter}m"

    } else {

        String.format(
            Locale.KOREA,
            "%.1fkm",
            meter / 1000.0
        )
    }
}


// ========================================
// 시간 포맷
// ========================================

private fun routeTimeText(
    second: Int
): String {

    val minute =
        (second / 60)
            .coerceAtLeast(
                1
            )


    return "예상 ${minute}분"
}


// ========================================
// Preview
// ========================================

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun RouteSelectScreenPreview() {

    RouteSelectScreen(
        destinationName =
            "강남역",

        destinationLatitude =
            37.4979,

        destinationLongitude =
            127.0276
    )
}