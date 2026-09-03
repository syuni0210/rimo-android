package com.example.clouddx_team4_project.ui.screens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.clouddx_team4_project.data.RouteSessionData
import com.example.clouddx_team4_project.data.RouteSessionPoint
import com.example.clouddx_team4_project.data.RouteSessionStore
import com.example.clouddx_team4_project.network.AiSafeRouteRequest
import com.example.clouddx_team4_project.network.AiSafeRouteResponse
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.network.AiRoutePoint
import com.example.clouddx_team4_project.network.RouteCandidateRequest
import com.example.clouddx_team4_project.BuildConfig
import com.example.clouddx_team4_project.data.KakaoReverseGeocodeClient
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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
// 일반 경로 정보
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

    onAiSafeRouteClick: () -> Unit = {},

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

    val coroutineScope = rememberCoroutineScope()

    // ========================================
    // 현재 위치
    // ========================================

    var currentLatitude by remember {
        mutableStateOf<Double?>(null)
    }

    var currentLongitude by remember {
        mutableStateOf<Double?>(null)
    }

    var resolvedStartName by remember {
        mutableStateOf(startName)
    }

    // ========================================
    // 빠른길 / 대로변
    // ========================================

    var fastRouteInfo by remember {
        mutableStateOf<RouteInfo?>(null)
    }

    var broadRouteInfo by remember {
        mutableStateOf<RouteInfo?>(null)
    }


    // ========================================
    // 실제 AI 안전경로 응답
    // ========================================

    var aiSafeRouteInfo by remember {
        mutableStateOf<AiSafeRouteResponse?>(null)
    }


    // ========================================
    // 상태
    //
    // 각 경로를 따로 로딩합니다.
    // AI가 오래 걸려도 빠른길/대로변은 먼저 선택할 수 있습니다.
    // ========================================

    var isFastLoading by remember {
        mutableStateOf(true)
    }

    var isBroadLoading by remember {
        mutableStateOf(true)
    }

    var isAiLoading by remember {
        mutableStateOf(true)
    }

    val isLoading =
        isFastLoading ||
                isBroadLoading ||
                isAiLoading

    var routeError by remember {
        mutableStateOf<String?>(null)
    }

    var showAiReasonDialog by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 위치 권한
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
    // 현재 GPS 한 번 조회
    // ========================================

    @SuppressLint("MissingPermission")
    fun loadCurrentLocation() {

        if (!hasLocationPermission) {

            routeError =
                "현재 위치 권한이 필요합니다."

            isFastLoading = false
            isBroadLoading = false
            isAiLoading = false

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
                    routeError = "현재 위치를 가져오지 못했습니다."
                    isFastLoading = false
                    isBroadLoading = false
                    isAiLoading = false
                    return@addOnSuccessListener
                }

                currentLatitude = location.latitude
                currentLongitude = location.longitude

                coroutineScope.launch {
                    try {
                        val response = KakaoReverseGeocodeClient.api.getAddressFromCoordinate(
                            "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
                            location.longitude,
                            location.latitude
                        )

                        val document = response.documents.firstOrNull()

                        resolvedStartName = document?.roadAddress?.addressName
                            ?: document?.address?.addressName
                                    ?: startName

                    } catch (e: Exception) {
                        resolvedStartName = startName
                    }
                }
            }
            .addOnFailureListener { error ->

                Log.e(
                    "ROUTE_SELECT",
                    "GPS 오류",
                    error
                )


                routeError =
                    "현재 위치를 가져오지 못했습니다."

                isFastLoading = false
                isBroadLoading = false
                isAiLoading = false
            }
    }


    LaunchedEffect(Unit) {

        loadCurrentLocation()
    }


    // ========================================
    // 빠른길 + 대로변 + AI 안전경로 조회
    //
    // 핵심 최적화
    // 1) 세 요청은 서로 독립적으로 동시에 실행
    // 2) 먼저 끝난 경로는 즉시 카드 활성화
    // 3) 전체 경로 좌표까지 RouteSessionStore에 저장
    // 4) 귀가 진행 화면에서는 API를 다시 호출하지 않고 재사용
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

            isFastLoading = false
            isBroadLoading = false
            isAiLoading = false

            return@LaunchedEffect
        }


        RouteSessionStore.prepareDestination(
            endLat,
            endLng
        )

        fastRouteInfo = null
        broadRouteInfo = null
        aiSafeRouteInfo = null

        isFastLoading = true
        isBroadLoading = true
        isAiLoading = true

        routeError = null


        supervisorScope {

            val shortestJob = launch {

                try {

                    val response =
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


                    val properties =
                        response
                            .route
                            ?.properties

                    val distance =
                        properties
                            ?.totalDistance

                    val time =
                        properties
                            ?.totalTime


                    if (
                        distance != null &&
                        time != null
                    ) {

                        val points =
                            mutableListOf<RouteSessionPoint>()


                        response
                            .route
                            ?.legs
                            ?.forEach { leg ->

                                leg.steps
                                    ?.forEach { step ->

                                        step.path
                                            ?.points
                                            ?.forEach { point ->

                                                if (
                                                    point.size >= 2
                                                ) {

                                                    points.add(
                                                        RouteSessionPoint(
                                                            latitude = point[1],
                                                            longitude = point[0]
                                                        )
                                                    )
                                                }
                                            }
                                    }
                            }


                        if (
                            points.size >= 2
                        ) {

                            RouteSessionStore.put(
                                RouteSessionData(

                                    routeMode =
                                        "SHORTEST",

                                    startLatitude =
                                        startLat,

                                    startLongitude =
                                        startLng,

                                    destinationLatitude =
                                        endLat,

                                    destinationLongitude =
                                        endLng,

                                    distanceMeter =
                                        distance,

                                    timeSecond =
                                        time,

                                    points =
                                        points,

                                    facilities =
                                        aiSafeRouteInfo
                                            ?.candidates
                                            ?.firstOrNull {
                                                it.routeMode == "SHORTEST"
                                            }
                                            ?.mapFacilities
                                            ?: emptyList(),

                                    facilitiesLoaded =
                                        aiSafeRouteInfo
                                            ?.candidates
                                            ?.any {
                                                it.routeMode == "SHORTEST"
                                            }
                                                == true,
                                )
                            )

                            // 캐시 저장이 끝난 뒤 카드를 활성화합니다.
                            fastRouteInfo =
                                RouteInfo(
                                    distanceMeter = distance,
                                    timeSecond = time
                                )
                        }
                    }


                } catch (
                    error: Exception
                ) {

                    Log.e(
                        "ROUTE_SELECT",
                        "빠른길 API 오류",
                        error
                    )

                } finally {

                    isFastLoading =
                        false
                }
            }


            val broadJob = launch {

                try {

                    val response =
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


                    val properties =
                        response
                            .route
                            ?.properties

                    val distance =
                        properties
                            ?.totalDistance

                    val time =
                        properties
                            ?.totalTime


                    if (
                        distance != null &&
                        time != null
                    ) {

                        val points =
                            mutableListOf<RouteSessionPoint>()


                        response
                            .route
                            ?.legs
                            ?.forEach { leg ->

                                leg.steps
                                    ?.forEach { step ->

                                        step.path
                                            ?.points
                                            ?.forEach { point ->

                                                if (
                                                    point.size >= 2
                                                ) {

                                                    points.add(
                                                        RouteSessionPoint(
                                                            latitude = point[1],
                                                            longitude = point[0]
                                                        )
                                                    )
                                                }
                                            }
                                    }
                            }


                        if (
                            points.size >= 2
                        ) {

                            RouteSessionStore.put(
                                RouteSessionData(

                                    routeMode =
                                        "BROAD_FIRST",

                                    startLatitude =
                                        startLat,

                                    startLongitude =
                                        startLng,

                                    destinationLatitude =
                                        endLat,

                                    destinationLongitude =
                                        endLng,

                                    distanceMeter =
                                        distance,

                                    timeSecond =
                                        time,

                                    points =
                                        points,

                                    facilities =
                                        aiSafeRouteInfo
                                            ?.candidates
                                            ?.firstOrNull {
                                                it.routeMode == "BROAD_FIRST"
                                            }
                                            ?.mapFacilities
                                            ?: emptyList(),

                                    facilitiesLoaded =
                                        aiSafeRouteInfo
                                            ?.candidates
                                            ?.any {
                                                it.routeMode == "BROAD_FIRST"
                                            }
                                                == true,
                                )
                            )

                            // 캐시 저장이 끝난 뒤 카드를 활성화합니다.
                            broadRouteInfo =
                                RouteInfo(
                                    distanceMeter = distance,
                                    timeSecond = time
                                )
                        }
                    }


                } catch (
                    error: Exception
                ) {

                    Log.e(
                        "ROUTE_SELECT",
                        "대로변 API 오류",
                        error
                    )

                } finally {

                    isBroadLoading =
                        false
                }
            }


            launch {

                // ========================================
                // Android의 SHORTEST / BROAD_FIRST 계산을
                // 먼저 모두 완료시킵니다.
                //
                // 각 카드의 활성화는 기존 coroutine 안에서
                // 개별 경로가 완료되는 즉시 이루어집니다.
                //
                // AI 요청만 두 후보가 모두 준비될 때까지 기다립니다.
                // ========================================

                shortestJob.join()
                broadJob.join()


                // ========================================
                // Android가 실제로 계산해서 캐시에 저장한
                // SHORTEST / BROAD_FIRST 경로를 가져옵니다.
                // ========================================

                val shortestRoute =
                    RouteSessionStore.get(
                        routeMode =
                            "SHORTEST",

                        destinationLatitude =
                            endLat,

                        destinationLongitude =
                            endLng
                    )


                val broadRoute =
                    RouteSessionStore.get(
                        routeMode =
                            "BROAD_FIRST",

                        destinationLatitude =
                            endLat,

                        destinationLongitude =
                            endLng
                    )


                // ========================================
                // Backend AI 요청용 후보 데이터로 변환
                //
                // Android에서 계산된 경로가 정상적으로 존재하면
                // Backend는 이 데이터를 그대로 사용하므로
                // Kakao API를 다시 호출하지 않습니다.
                // ========================================

                val shortestCandidateRequest =
                    shortestRoute
                        ?.takeIf {
                            it.points.size >= 2
                        }
                        ?.let { route ->

                            RouteCandidateRequest(

                                routeMode =
                                    "SHORTEST",

                                distanceMeter =
                                    route.distanceMeter,

                                timeSecond =
                                    route.timeSecond,

                                path =
                                    route.points.map { point ->

                                        AiRoutePoint(
                                            latitude =
                                                point.latitude,

                                            longitude =
                                                point.longitude
                                        )
                                    }
                            )
                        }


                val broadCandidateRequest =
                    broadRoute
                        ?.takeIf {
                            it.points.size >= 2
                        }
                        ?.let { route ->

                            RouteCandidateRequest(

                                routeMode =
                                    "BROAD_FIRST",

                                distanceMeter =
                                    route.distanceMeter,

                                timeSecond =
                                    route.timeSecond,

                                path =
                                    route.points.map { point ->

                                        AiRoutePoint(
                                            latitude =
                                                point.latitude,

                                            longitude =
                                                point.longitude
                                        )
                                    }
                            )
                        }


                try {

                    Log.d(
                        "ROUTE_SELECT",
                        "AI 요청 시작: " +
                                "shortestCandidate=${shortestCandidateRequest != null}, " +
                                "broadCandidate=${broadCandidateRequest != null}"
                    )


                    val aiResponse =
                        RetrofitClient
                            .aiSafeRouteApi
                            .getAiSafeRoute(

                                AiSafeRouteRequest(

                                    startLatitude =
                                        startLat,

                                    startLongitude =
                                        startLng,

                                    destinationLatitude =
                                        endLat,

                                    destinationLongitude =
                                        endLng,

                                    shortestCandidate =
                                        shortestCandidateRequest,

                                    broadCandidate =
                                        broadCandidateRequest
                                )
                            )


                    // ========================================
// AI가 최종 선택한 Kakao 경로 모드 확인
//
// 백엔드 routeMode가 SHORTEST 또는 BROAD_FIRST이면 그대로 사용하고,
// 없거나 예상하지 못한 값이면 후보 중 안전점수가 가장 높은 경로를 사용합니다.
// ========================================

                    val selectedKakaoMode =

                        aiResponse
                            .routeMode
                            .takeIf {
                                it == "SHORTEST" ||
                                        it == "BROAD_FIRST"
                            }
                            ?: aiResponse
                                .candidates
                                .maxByOrNull {
                                    it.safetyScore
                                }
                                ?.routeMode
                            ?: "SHORTEST"


// ========================================
// 각 후보 경로의 50m 이내 실제 시설 목록 분리
//
// SHORTEST:
// 빠른길 경로에서 50m 이내에 있는 실제 시설 좌표
//
// BROAD_FIRST:
// 대로변 경로에서 50m 이내에 있는 실제 시설 좌표
//
// 백엔드에서 이미 계산한 mapFacilities를 그대로 사용하므로
// Android에서 시설 DB/API를 추가로 조회하지 않습니다.
// ========================================

                    val shortestMapFacilities =
                        aiResponse
                            .candidates
                            .firstOrNull {
                                it.routeMode == "SHORTEST"
                            }
                            ?.mapFacilities
                            ?: emptyList()


                    val broadMapFacilities =
                        aiResponse
                            .candidates
                            .firstOrNull {
                                it.routeMode == "BROAD_FIRST"
                            }
                            ?.mapFacilities
                            ?: emptyList()


// ========================================
// AI가 선택한 최종 경로의 좌표를
// RouteSessionStore용 좌표 형식으로 변환합니다.
// ========================================

                    val points =
                        aiResponse
                            .path
                            .map { point ->

                                RouteSessionPoint(
                                    latitude =
                                        point.latitude,

                                    longitude =
                                        point.longitude
                                )
                            }


// 경로 좌표가 정상적으로 존재할 때만 캐시에 저장합니다.
                    if (
                        points.size >= 2
                    ) {

                        // ========================================
                        // AI 응답을 먼저 화면 상태에 저장
                        //
                        // 빠른길/대로변 API가 AI보다 늦게 끝나는 경우,
                        // 해당 coroutine에서 aiSafeRouteInfo를 읽어
                        // 자신의 mapFacilities를 바로 저장할 수 있게 합니다.
                        //
                        // 즉 API 완료 순서가 달라도 시설 데이터가 유지됩니다.
                        // ========================================

                        aiSafeRouteInfo =
                            aiResponse


                        // ========================================
                        // 이미 저장되어 있는 SHORTEST 캐시 보완
                        //
                        // 빠른길 API가 AI보다 먼저 완료된 경우에는
                        // 당시 aiSafeRouteInfo가 null이었기 때문에
                        // facilities가 emptyList()로 저장되어 있을 수 있습니다.
                        //
                        // 그런 경우 현재 AI 응답에서 받은
                        // SHORTEST의 50m 시설 목록으로 다시 갱신합니다.
                        // ========================================

                        RouteSessionStore.get(
                            routeMode =
                                "SHORTEST",

                            destinationLatitude =
                                endLat,

                            destinationLongitude =
                                endLng
                        )?.let { existingRoute ->

                            RouteSessionStore.put(
                                existingRoute.copy(
                                    facilities =
                                        shortestMapFacilities,

                                    facilitiesLoaded =
                                        true
                                )
                            )
                        }


                        // ========================================
                        // 이미 저장되어 있는 BROAD_FIRST 캐시 보완
                        //
                        // 대로변 API가 AI보다 먼저 완료되어
                        // facilities가 비어 있는 상태로 저장된 경우,
                        // AI 응답의 BROAD_FIRST 50m 시설 목록으로 갱신합니다.
                        // ========================================

                        RouteSessionStore.get(
                            routeMode =
                                "BROAD_FIRST",

                            destinationLatitude =
                                endLat,

                            destinationLongitude =
                                endLng
                        )?.let { existingRoute ->

                            RouteSessionStore.put(
                                existingRoute.copy(
                                    facilities =
                                        broadMapFacilities,

                                    facilitiesLoaded =
                                        true
                                )
                            )
                        }


                        // ========================================
                        // AI 안전경로 자체도 RouteSessionStore에 저장
                        //
                        // aiResponse.facilities는
                        // AI가 최종 선택한 경로의 50m 이내 시설 목록입니다.
                        // ========================================

                        RouteSessionStore.put(
                            RouteSessionData(

                                routeMode =
                                    "AI_SAFE",

                                startLatitude =
                                    startLat,

                                startLongitude =
                                    startLng,

                                destinationLatitude =
                                    endLat,

                                destinationLongitude =
                                    endLng,

                                distanceMeter =
                                    aiResponse.distanceMeter,

                                timeSecond =
                                    aiResponse.timeSecond,

                                points =
                                    points,

                                facilities =
                                    aiResponse.facilities,

                                aiSelectedKakaoRouteMode =
                                    selectedKakaoMode
                            )
                        )
                    }

                    Log.d(
                        "ROUTE_SELECT",
                        """
                        AI 안전경로 조회 성공
                        distance = ${aiResponse.distanceMeter}
                        time = ${aiResponse.timeSecond}
                        score = ${aiResponse.safetyScore}
                        pathSize = ${aiResponse.path.size}
                        facilityCount = ${aiResponse.facilities.size}
                        selectedMode = $selectedKakaoMode
                        """.trimIndent()
                    )


                } catch (
                    error: Exception
                ) {

                    Log.e(
                        "ROUTE_SELECT",
                        "AI 안전경로 API 오류",
                        error
                    )

                } finally {

                    isAiLoading =
                        false
                }
            }
        }


        if (
            fastRouteInfo == null &&
            broadRouteInfo == null &&
            aiSafeRouteInfo == null
        ) {

            routeError =
                "경로 정보를 불러오지 못했습니다."
        }
    }


    // ========================================
    // AI 추천 이유 Dialog
    // ========================================

    if (
        showAiReasonDialog &&
        aiSafeRouteInfo != null
    ) {

        val aiInfo =
            aiSafeRouteInfo!!


        AlertDialog(

            onDismissRequest = {

                showAiReasonDialog =
                    false
            },

            title = {

                Text(
                    text =
                        "AI 추천 이유",

                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Column {

                    Text(
                        text =
                            aiInfo.recommendationReason,

                        fontSize =
                            14.sp,

                        color =
                            RouteTextBlack
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                16.dp
                            )
                    )


                    Text(
                        text =
                            "안전점수 ${aiInfo.safetyScore}점",

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            RouteBlue
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    Text(
                        text =
                            "• 보안등 ${aiInfo.securityLightCount}개",

                        fontSize =
                            13.sp,

                        color =
                            RouteTextGray
                    )


                    Text(
                        text =
                            "• CCTV ${aiInfo.cctvCount}개",

                        fontSize =
                            13.sp,

                        color =
                            RouteTextGray
                    )


                    Text(
                        text =
                            "• 비상벨 ${aiInfo.emergencyBellCount}개",

                        fontSize =
                            13.sp,

                        color =
                            RouteTextGray
                    )


                    Text(
                        text =
                            "• 경찰시설 ${aiInfo.policeCount}개",

                        fontSize =
                            13.sp,

                        color =
                            RouteTextGray
                    )


                    Text(
                        text =
                            "• 안심지킴이집 ${aiInfo.safeHouseCount}개",

                        fontSize =
                            13.sp,

                        color =
                            RouteTextGray
                    )


                    Text(
                        text =
                            "• 스마트가로등 ${aiInfo.smartLightCount}개",

                        fontSize =
                            13.sp,

                        color =
                            RouteTextGray
                    )
                }
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        showAiReasonDialog =
                            false
                    }

                ) {

                    Text(
                        text =
                            "확인",

                        color =
                            RouteBlue
                    )
                }
            }
        )
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
                    resolvedStartName,

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
                            "도보 경로와 AI 안전경로를 계산하고 있습니다.",

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
                        MaterialTheme
                            .colorScheme
                            .error
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
                        ?: if (isFastLoading) {

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
                            !isFastLoading,

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
            // AI 안전경로
            // ========================================

            AiSafeRouteCard(

                routeInfo =
                    aiSafeRouteInfo,

                isLoading =
                    isAiLoading,

                onClick = {

                    if (
                        aiSafeRouteInfo != null
                    ) {

                        onAiSafeRouteClick()
                    }
                },

                onReasonClick = {

                    if (
                        aiSafeRouteInfo != null
                    ) {

                        showAiReasonDialog =
                            true
                    }
                }
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
                        ?: if (isBroadLoading) {

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
                            !isBroadLoading,

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
// 일반 경로 카드
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
// AI 안전경로 카드
// ========================================

@Composable
private fun AiSafeRouteCard(

    routeInfo: AiSafeRouteResponse?,

    isLoading: Boolean,

    onClick: () -> Unit,

    onReasonClick: () -> Unit

) {

    val enabled =
        routeInfo != null &&
                !isLoading


    Card(

        modifier =
            Modifier
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
                    1.5.dp,

                color =
                    if (enabled) {

                        RouteBlue

                    } else {

                        RouteBorderGray
                    }
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    )
        ) {

            Text(
                text =
                    "AI 안전경로 ✨",

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


            if (
                routeInfo == null
            ) {

                Text(
                    text =
                        if (isLoading) {

                            "AI 안전경로 계산 중"

                        } else {

                            "AI 안전경로를 불러오지 못했습니다."
                        },

                    fontSize =
                        13.sp,

                    color =
                        RouteTextGray
                )

                return@Column
            }


            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        routeDistanceText(
                            routeInfo.distanceMeter
                        ),

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        RouteTextBlack
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            12.dp
                        )
                )


                Text(
                    text =
                        routeTimeText(
                            routeInfo.timeSecond
                        ),

                    fontSize =
                        13.sp,

                    color =
                        RouteTextGray
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            12.dp
                        )
                )


                Text(
                    text =
                        "안전 ${routeInfo.safetyScore}점",

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        RouteBlue
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            Text(
                text =
                    "보안등 ${routeInfo.securityLightCount}개 · " +
                            "CCTV ${routeInfo.cctvCount}개 · " +
                            "비상벨 ${routeInfo.emergencyBellCount}개",

                fontSize =
                    12.sp,

                color =
                    RouteTextGray
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Text(
                text =
                    "실제 안전시설 데이터와 AI 분석을 반영한 추천 경로",

                fontSize =
                    12.sp,

                color =
                    RouteTextGray
            )


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            TextButton(

                onClick = {

                    onReasonClick()
                },

                contentPadding =
                    PaddingValues(
                        horizontal = 0.dp,
                        vertical = 0.dp
                    )

            ) {

                Text(
                    text =
                        "왜 이 경로를 추천했나요? >",

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        RouteBlue
                )
            }
        }
    }
}


// ========================================
// 거리
// ========================================

private fun routeDistanceText(
    meter: Int
): String {

    return if (
        meter < 1000
    ) {

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
// 시간
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
