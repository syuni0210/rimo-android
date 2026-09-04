package com.example.clouddx_team4_project.ui.screens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.example.clouddx_team4_project.data.KakaoDirectionsClient
import com.example.clouddx_team4_project.network.AiSafeRouteRequest
import com.example.clouddx_team4_project.network.AiRoutePoint
import com.example.clouddx_team4_project.network.RouteFacilitiesRequest
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.network.JourneySaveRequest
import com.example.clouddx_team4_project.network.FacilityMapDto
import com.google.android.gms.location.*
import com.kakao.vectormap.LatLng
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.*
import com.example.clouddx_team4_project.ui.components.SafetyCheckDialogHost
import com.example.clouddx_team4_project.ui.components.SafetyPopupState
import com.example.clouddx_team4_project.network.EmergencyTriggerRequest
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.data.TokenManager
import com.example.clouddx_team4_project.data.RouteSessionData
import com.example.clouddx_team4_project.data.RouteSessionPoint
import com.example.clouddx_team4_project.data.RouteSessionStore
import com.example.clouddx_team4_project.BuildConfig
import com.example.clouddx_team4_project.data.KakaoReverseGeocodeClient

// ========================================
// 색상
// ========================================

private val ActiveBlue =
    Color(0xFF6A92FE)

private val ActiveBackground =
    Color(0xFFF7F8FB)

private val ActiveTextBlack =
    Color(0xFF222222)

private val ActiveTextGray =
    Color(0xFF888888)

private val EmergencyRed =
    Color(0xFFFF3B3B)

private val QuackOrange =
    Color(0xFFFF8A34)


// 목적지 50m 이내 도착 처리
private const val ARRIVAL_DISTANCE_METER =
    50.0

// ========================================
// 실시간 주변 안전시설 갱신 조건
// ========================================

private const val FACILITY_REFRESH_DISTANCE_METER =
    5.0

private const val FACILITY_REFRESH_INTERVAL_MILLIS =
    5_000L

// ========================================
// 귀가 진행 중 화면
// ========================================

@Composable
fun ActiveRouteScreen(

    destinationName: String = "목적지",

    destinationLatitude: Double? = null,

    destinationLongitude: Double? = null,

    routeMode: String = "SHORTEST",

    onBackClick: () -> Unit = {},

    onEmergencyClick: () -> Unit = {},

    onQuackClick: () -> Unit = {},

    onFinishClick: () -> Unit = {}

) {

    val context =
        LocalContext.current

    val tokenManager = remember { TokenManager(context) }

    val fusedLocationClient =
        remember {

            LocationServices
                .getFusedLocationProviderClient(
                    context
                )
        }

    // ========================================
    // 경로 선택 화면에서 이미 계산한 경로 재사용
    //
    // 캐시가 있으면 귀가 진행 화면에 들어오자마자
    // 네트워크 호출 없이 경로/거리/시간을 즉시 사용합니다.
    // ========================================

    val cachedRoute =
        remember(
            routeMode,
            destinationLatitude,
            destinationLongitude
        ) {

            RouteSessionStore.get(
                routeMode =
                    routeMode,

                destinationLatitude =
                    destinationLatitude,

                destinationLongitude =
                    destinationLongitude
            )
        }

    // ========================================
    // 귀가 진행 중 현재 위치 주변 안전시설
    //
    // RouteSelectScreen에서 사용한 "경로 주변 시설"과 분리합니다.
    // ActiveRouteScreen에서는 현재 GPS 위치 기준
    // 반경 50m 시설만 표시합니다.
    //
    // 따라서 화면 진입 시 경로 전체 시설 캐시는
    // 지도에 사용하지 않고 빈 목록에서 시작합니다.
    // ========================================

    var routeFacilities by remember(
        routeMode,
        destinationLatitude,
        destinationLongitude
    ) {
        mutableStateOf(
            emptyList<FacilityMapDto>()
        )
    }


    // ========================================
    // 실시간 시설 갱신 기준 위치
    //
    // 마지막으로 시설 조회에 성공했던 위치입니다.
    // 여기서 5m 이상 이동했는지 판정합니다.
    // ========================================

    var lastFacilityLoadedLatitude by remember(
        routeMode,
        destinationLatitude,
        destinationLongitude
    ) {
        mutableStateOf<Double?>(
            null
        )
    }

    var lastFacilityLoadedLongitude by remember(
        routeMode,
        destinationLatitude,
        destinationLongitude
    ) {
        mutableStateOf<Double?>(
            null
        )
    }


    // ========================================
    // 마지막 시설 API 호출 시각
    //
    // API 성공/실패와 관계없이
    // 최소 5초 간격을 유지하기 위해 사용합니다.
    // ========================================

    var lastFacilityQueryTimeMillis by remember(
        routeMode,
        destinationLatitude,
        destinationLongitude
    ) {
        mutableStateOf(
            0L
        )
    }

    // ========================================
    // 안전 확인 팝업 상태
    // ========================================

    var safetyPopupState by remember {
        mutableStateOf(SafetyPopupState.NONE)
    }
    val coroutineScope = rememberCoroutineScope()


    // ========================================
    // 무움직임 / 경로이탈 감지용
    // ========================================

    var currentAccuracy by remember {
        mutableStateOf<Float?>(null)
    }

    var lastMovementTime by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    var lastMovementLat by remember {
        mutableStateOf<Double?>(null)
    }

    var lastMovementLng by remember {
        mutableStateOf<Double?>(null)
    }

    var deviationStartTime by remember {
        mutableStateOf<Long?>(null)
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
    // 현재 이동 방향
    //
    // Android Location.bearing 기준
    // 북쪽 0°, 동쪽 90°, 남쪽 180°, 서쪽 270°
    // ========================================

    var currentBearing by remember {
        mutableStateOf<Float?>(null)
    }

    // ========================================
// 현재 위치로 지도 다시 이동 요청
//
// 값이 증가할 때마다 KakaoMapView가
// 현재 GPS 위치로 카메라를 이동합니다.
// ========================================

    var recenterRequestKey by remember {
        mutableIntStateOf(0)
    }

    // ========================================
    // 경로 계산에 사용할 최초 출발 위치
    //
    // GPS는 계속 갱신하지만, 경로 계산 기준점은
    // 안내 시작 시 최초 위치로 고정합니다.
    // 따라서 안내 종료 전까지 선택한 경로가 유지됩니다.
    // ========================================
    var journeyStartTime by remember(
        destinationLatitude,
        destinationLongitude,
        routeMode
    ) {
        mutableStateOf<Long?>(
            System.currentTimeMillis()
        )
    }

    var routeStartLatitude by remember(
        destinationLatitude,
        destinationLongitude,
        routeMode
    ) {
        mutableStateOf(
            cachedRoute
                ?.startLatitude
        )
    }

    var routeStartLongitude by remember(
        destinationLatitude,
        destinationLongitude,
        routeMode
    ) {
        mutableStateOf(
            cachedRoute
                ?.startLongitude
        )
    }


    // ========================================
    // 최초 거리 / 시간
    // ========================================

    var initialDistance by remember(
        cachedRoute
    ) {
        mutableIntStateOf(
            cachedRoute
                ?.distanceMeter
                ?: 0
        )
    }

    var initialTime by remember(
        cachedRoute
    ) {
        mutableIntStateOf(
            cachedRoute
                ?.timeSecond
                ?: 0
        )
    }


    // ========================================
    // 남은 거리 / 시간
    // ========================================

    var remainingDistance by remember(
        cachedRoute
    ) {
        mutableIntStateOf(
            cachedRoute
                ?.distanceMeter
                ?: 0
        )
    }

    var remainingTime by remember(
        cachedRoute
    ) {
        mutableIntStateOf(
            cachedRoute
                ?.timeSecond
                ?: 0
        )
    }


    // ========================================
    // 실제 경로 좌표
    // ========================================

    var routePoints by remember(
        cachedRoute
    ) {
        mutableStateOf<List<LatLng>>(
            cachedRoute
                ?.points
                ?.map { point ->

                    LatLng.from(
                        point.latitude,
                        point.longitude
                    )
                }
                ?: emptyList()
        )
    }

    // ========================================
    // 귀가 진행 중 현재 위치 주변 50m 시설 갱신
    //
    // GPS 자체의 설정은 변경하지 않습니다.
    //
    // 최초 GPS 위치가 확보되면 즉시 한 번 조회하고,
    // 이후에는
    //
    // 1. 마지막 성공 조회 위치에서 5m 이상 이동
    // 2. 마지막 API 호출 후 5초 이상 경과
    //
    // 두 조건을 모두 만족할 때만 다시 조회합니다.
    //
    // API 요청은 이 루프 안에서 순차적으로 실행되므로
    // 같은 시설 조회 요청이 동시에 여러 개 겹치지 않습니다.
    // ========================================

    LaunchedEffect(
        routeMode,
        destinationLatitude,
        destinationLongitude
    ) {

        while (true) {

            val latitude =
                currentLatitude

            val longitude =
                currentLongitude


            if (
                latitude != null &&
                longitude != null
            ) {

                val now =
                    System.currentTimeMillis()


                // ========================================
                // 첫 조회 여부
                //
                // 아직 성공한 시설 조회 위치가 없다면
                // 5m 이동 조건 없이 바로 조회합니다.
                // ========================================

                val isFirstQuery =
                    lastFacilityLoadedLatitude == null ||
                            lastFacilityLoadedLongitude == null


                // ========================================
                // 마지막 성공 조회 위치에서 이동한 거리
                // ========================================

                val movedEnough =

                    if (
                        isFirstQuery
                    ) {

                        true

                    } else {

                        calculateDistanceMeter(
                            lastFacilityLoadedLatitude!!,
                            lastFacilityLoadedLongitude!!,
                            latitude,
                            longitude
                        ) >=
                                FACILITY_REFRESH_DISTANCE_METER
                    }


                // ========================================
                // 마지막 API 호출 후 5초 경과 여부
                // ========================================

                val timeEnough =
                    lastFacilityQueryTimeMillis == 0L ||
                            now - lastFacilityQueryTimeMillis >=
                            FACILITY_REFRESH_INTERVAL_MILLIS


                if (
                    movedEnough &&
                    timeEnough
                ) {

                    // 요청 시작 시각을 먼저 저장하여
                    // 실패하더라도 5초 이내 연속 재호출을 막습니다.
                    lastFacilityQueryTimeMillis =
                        now


                    try {

                        Log.d(
                            "ACTIVE_ROUTE",
                            "현재 위치 주변 시설 조회 시작: lat=$latitude, lng=$longitude"
                        )


                        val facilities =
                            RetrofitClient
                                .aiSafeRouteApi
                                .getFacilitiesNearLocation(
                                    latitude =
                                        latitude,

                                    longitude =
                                        longitude
                                )


                        // ========================================
                        // 기존 목록을 새 목록으로 완전히 교체
                        //
                        // 따라서 현재 위치에서 50m 밖으로 벗어난
                        // 시설은 지도에서 사라지고,
                        // 새로 50m 안으로 들어온 시설은 나타납니다.
                        // ========================================

                        routeFacilities =
                            facilities


                        // 조회가 성공한 위치를
                        // 다음 5m 이동 판정의 기준으로 저장합니다.
                        lastFacilityLoadedLatitude =
                            latitude

                        lastFacilityLoadedLongitude =
                            longitude


                        Log.d(
                            "ACTIVE_ROUTE",
                            "현재 위치 주변 시설 조회 완료: facilityCount=${facilities.size}"
                        )


                    } catch (
                        error: Exception
                    ) {

                        // 일시적인 네트워크 실패 시 기존 시설을 지우지 않습니다.
                        // 다음 조건 충족 시 다시 조회합니다.
                        Log.e(
                            "ACTIVE_ROUTE",
                            "현재 위치 주변 시설 조회 실패",
                            error
                        )
                    }
                }
            }


            // ========================================
            // 시설 갱신 조건 확인 주기
            //
            // GPS 요청 주기를 바꾸는 것이 아닙니다.
            // 이미 저장된 최신 GPS 좌표를 1초마다 확인할 뿐이며,
            // 실제 Backend 호출은 5m + 5초 조건을 만족할 때만 합니다.
            // ========================================

            delay(
                1_000L
            )
        }
    }

    // ========================================
    // AI가 최종 선택한 실제 Kakao 후보
    //
    // SHORTEST 또는 BROAD_FIRST
    //
    // KakaoMapView가 현재 routeMode를 이용해서
    // 다시 경로선을 그리므로 사용
    // ========================================

    var aiSelectedKakaoRouteMode by remember(
        cachedRoute
    ) {
        mutableStateOf<String?>(
            cachedRoute
                ?.aiSelectedKakaoRouteMode
        )
    }


    // ========================================
    // GPS가 움직일 때 전체 경로를 매번 처음부터 찾지 않도록
    // 직전에 가장 가까웠던 경로점 인덱스를 기억합니다.
    // ========================================

    var nearestRouteIndex by remember(
        routePoints
    ) {
        mutableIntStateOf(0)
    }


    // ========================================
    // 상태
    // ========================================

    var routeInitialized by remember(
        cachedRoute
    ) {
        mutableStateOf(
            cachedRoute != null
        )
    }

    var isLoading by remember(
        cachedRoute
    ) {
        mutableStateOf(
            cachedRoute == null
        )
    }

    var routeError by remember {
        mutableStateOf<String?>(null)
    }

    var hasArrived by remember {
        mutableStateOf(false)
    }


    var showArrivalDialog by remember {
        mutableStateOf(false)
    }


    var arrivalHandled by remember {
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
    // GPS 설정
    // ========================================

    val locationRequest =
        remember {

            LocationRequest
                .Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    3000L
                )
                .setMinUpdateIntervalMillis(
                    2000L
                )
                .setMinUpdateDistanceMeters(
                    3f
                )
                .build()
        }


    // ========================================
    // GPS Callback
    // ========================================

    val locationCallback =
        remember {

            object : LocationCallback() {

                override fun onLocationResult(
                    result: LocationResult
                ) {

                    val location =
                        result.lastLocation
                            ?: return


                    currentLatitude =
                        location.latitude

                    currentLongitude =
                        location.longitude

                    currentAccuracy =
                        location.accuracy

                    // ========================================
                    // 이동 중일 때만 방향 갱신
                    //
                    // 정지 상태에서는 GPS bearing이 튈 수 있으므로
                    // 마지막 정상 이동 방향을 그대로 유지합니다.
                    // ========================================

                    if (
                        location.hasBearing() &&
                        location.speed >= 0.4f
                    ) {
                        currentBearing =
                            location.bearing
                    }

                    Log.d(
                        "ACTIVE_ROUTE",
                        """
        GPS 갱신
        lat = ${location.latitude}
        lng = ${location.longitude}
        accuracy = ${location.accuracy}
        """.trimIndent()
                    )
                }
            }
        }


    // ========================================
    // GPS 시작
    // ========================================

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {

        if (!hasLocationPermission) {

            routeError =
                "위치 권한이 필요합니다."

            isLoading =
                false

            return
        }


        fusedLocationClient
            .requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
    }


    // ========================================
    // GPS 종료
    // ========================================

    fun stopLocationUpdates() {

        fusedLocationClient
            .removeLocationUpdates(
                locationCallback
            )
    }


    // ========================================
    // 귀가 여정 저장
    // 안내 종료 시점에 JRNY 테이블에 기록
    // ========================================

    fun saveJourneyRecord() {

        val memberId = tokenManager.getMemberId() ?: return

        val startLat = routeStartLatitude
        val startLng = routeStartLongitude
        val startTime = journeyStartTime

        if (startLat == null || startLng == null || startTime == null) {
            return
        }

        val endLat = currentLatitude ?: startLat
        val endLng = currentLongitude ?: startLng

        val startDateTime = java.time.Instant.ofEpochMilli(startTime)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime()
            .toString()

        val endDateTime = java.time.LocalDateTime.now().toString()

        coroutineScope.launch {
            try {

                // ========================================
                // 출발지 좌표 → 실제 주소로 변환
                // ========================================

                val startAddressText = try {

                    val response = KakaoReverseGeocodeClient.api.getAddressFromCoordinate(
                        "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
                        startLng,
                        startLat
                    )

                    val document = response.documents.firstOrNull()

                    document?.roadAddress?.addressName
                        ?: document?.address?.addressName
                        ?: "현재 위치"

                } catch (e: Exception) {
                    "현재 위치"
                }

                RetrofitClient.reportApi.saveJourney(
                    JourneySaveRequest(
                        memberId = memberId,
                        startAddress = startAddressText,
                        endAddress = destinationName,
                        startLatitude = startLat,
                        startLongitude = startLng,
                        endLatitude = endLat,
                        endLongitude = endLng,
                        pathTypeCode = routeMode,
                        statusCode = "COMPLETED",
                        startDateTime = startDateTime,
                        endDateTime = endDateTime
                    )
                )

                Log.d("JOURNEY_SAVE", "귀가 기록 저장 완료")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("JOURNEY_SAVE", "귀가 기록 저장 실패", e)
            }
        }
    }


    // ========================================
    // 화면 시작 / 종료
    // ========================================

    DisposableEffect(
        hasLocationPermission
    ) {

        if (hasLocationPermission) {

            startLocationUpdates()
        }


        onDispose {

            stopLocationUpdates()
        }
    }


    // ========================================
    // 최초 출발 위치 고정
    //
    // GPS는 길안내 동안 계속 갱신됩니다.
    // 다만 경로 자체는 최초 위치를 기준으로 딱 한 번 계산합니다.
    // ========================================

    LaunchedEffect(
        currentLatitude,
        currentLongitude,
        destinationLatitude,
        destinationLongitude,
        routeMode
    ) {

        if (
            !routeInitialized &&
            routeStartLatitude == null &&
            routeStartLongitude == null &&
            currentLatitude != null &&
            currentLongitude != null
        ) {
            journeyStartTime = System.currentTimeMillis()
            routeStartLatitude =
                currentLatitude

            routeStartLongitude =
                currentLongitude

            Log.d(
                "ACTIVE_ROUTE",
                "경로 출발 위치 고정: $routeStartLatitude, $routeStartLongitude"
            )
        }
    }


    // ========================================
    // 최초 경로 조회
    //
    // 중요:
    // currentLatitude/currentLongitude를 key로 사용하지 않습니다.
    // GPS가 갱신되어도 네트워크 경로 요청은 다시 실행되지 않습니다.
    // ========================================

    LaunchedEffect(
        routeStartLatitude,
        routeStartLongitude,
        destinationLatitude,
        destinationLongitude,
        routeMode,
        routeInitialized
    ) {

        if (
            routeInitialized
        ) {

            return@LaunchedEffect
        }


        val startLat =
            routeStartLatitude
                ?: return@LaunchedEffect

        val startLng =
            routeStartLongitude
                ?: return@LaunchedEffect

        val endLat =
            destinationLatitude
                ?: return@LaunchedEffect

        val endLng =
            destinationLongitude
                ?: return@LaunchedEffect


        try {

            isLoading =
                true

            routeError =
                null


            // ========================================
            // AI 안전경로
            // ========================================

            if (
                routeMode == "AI_SAFE"
            ) {

                val response =
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
                                    endLng
                            )
                        )


                initialDistance =
                    response.distanceMeter

                initialTime =
                    response.timeSecond

                remainingDistance =
                    initialDistance

                remainingTime =
                    initialTime


                // 서버가 최종 선택한 실제 경로를 그대로 고정 저장합니다.
                routePoints =
                    response.path
                        .map { point ->

                            LatLng.from(
                                point.latitude,
                                point.longitude
                            )
                        }


                aiSelectedKakaoRouteMode =
                    response
                        .candidates
                        .maxByOrNull {
                            it.safetyScore
                        }
                        ?.routeMode
                        ?: "SHORTEST"


                Log.d(
                    "ACTIVE_ROUTE",
                    """
                    AI 안전경로 조회 성공
                    distance = ${response.distanceMeter}
                    time = ${response.timeSecond}
                    safetyScore = ${response.safetyScore}
                    selectedMode = $aiSelectedKakaoRouteMode
                    pathSize = ${response.path.size}
                    reason = ${response.recommendationReason}
                    """.trimIndent()
                )


            } else {


                // ========================================
                // 빠른길 / 대로변
                // ========================================

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
                                "현재 위치",

                            endName =
                                destinationName,

                            routeMode =
                                routeMode
                        )


                val properties =
                    response
                        .route
                        ?.properties


                initialDistance =
                    properties
                        ?.totalDistance
                        ?: 0

                initialTime =
                    properties
                        ?.totalTime
                        ?: 0

                remainingDistance =
                    initialDistance

                remainingTime =
                    initialTime


                val points =
                    mutableListOf<LatLng>()


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

                                            val longitude =
                                                point[0]

                                            val latitude =
                                                point[1]


                                            points.add(
                                                LatLng.from(
                                                    latitude,
                                                    longitude
                                                )
                                            )
                                        }
                                    }
                            }
                    }


                routePoints =
                    points
            }


            val initializedRoutePoints = routePoints

            if (
                initializedRoutePoints.size >= 2
            ) {

                RouteSessionStore.put(
                    RouteSessionData(

                        routeMode =
                            routeMode,

                        startLatitude =
                            startLat,

                        startLongitude =
                            startLng,

                        destinationLatitude =
                            endLat,

                        destinationLongitude =
                            endLng,

                        distanceMeter =
                            initialDistance,

                        timeSecond =
                            initialTime,

                        points =
                            initializedRoutePoints.map { point ->

                                RouteSessionPoint(
                                    latitude =
                                        point.latitude,

                                    longitude =
                                        point.longitude
                                )
                            },

                        aiSelectedKakaoRouteMode =
                            aiSelectedKakaoRouteMode
                    )
                )

                routeInitialized =
                    true
            }


            Log.d(
                "ACTIVE_ROUTE",
                "고정 경로 좌표 수 = ${initializedRoutePoints.size}"
            )


        } catch (
            e: Exception
        ) {

            Log.e(
                "ACTIVE_ROUTE",
                "경로 API 실패",
                e
            )


            routeError =
                if (
                    routeMode == "AI_SAFE"
                ) {

                    "AI 안전경로를 불러오지 못했습니다."

                } else {

                    "경로 정보를 불러오지 못했습니다."
                }


        } finally {

            isLoading =
                false
        }
    }

    // ========================================
    // 움직임 감지 (5m 이상 이동 시 타이머 리셋)
    // GPS 정확도가 나쁘면(20m 초과) 판정에서 제외
    // ========================================

    LaunchedEffect(currentLatitude, currentLongitude, currentAccuracy) {

        val lat = currentLatitude ?: return@LaunchedEffect
        val lng = currentLongitude ?: return@LaunchedEffect
        val accuracy = currentAccuracy ?: return@LaunchedEffect

        if (accuracy > 20f) {
            return@LaunchedEffect
        }

        val prevLat = lastMovementLat
        val prevLng = lastMovementLng

        if (prevLat == null || prevLng == null) {
            lastMovementLat = lat
            lastMovementLng = lng
            lastMovementTime = System.currentTimeMillis()
            return@LaunchedEffect
        }

        val moved = calculateDistanceMeter(prevLat, prevLng, lat, lng)

        if (moved >= 5.0) {
            lastMovementLat = lat
            lastMovementLng = lng
            lastMovementTime = System.currentTimeMillis()
        }
    }


// ========================================
// 30분 감시 루프
// ========================================

    LaunchedEffect(Unit) {

        while (true) {

            //delay(30_000L)
            delay(5_000L)

            if (safetyPopupState == SafetyPopupState.NONE) {

                val now = System.currentTimeMillis()

                when {

                    //now - lastMovementTime >= 30 * 60 * 1000L -> {
                    now - lastMovementTime >= 30_000L -> {
                        safetyPopupState = SafetyPopupState.INACTIVITY_CHECK
                    }

                    deviationStartTime != null &&
                            //now - deviationStartTime!! >= 30 * 60 * 1000L -> {
                            now - deviationStartTime!! >= 20_000L -> {
                        safetyPopupState = SafetyPopupState.ROUTE_DEVIATION_CHECK
                    }
                }
            }
        }
    }


// ========================================
// 팝업 상태에 따라 알림 사운드/진동 제어
// ========================================

    LaunchedEffect(safetyPopupState) {

        when (safetyPopupState) {

            SafetyPopupState.INACTIVITY_CHECK,
            SafetyPopupState.ROUTE_DEVIATION_CHECK,
            SafetyPopupState.FINAL_CHECK -> {
                SafetyAlertPlayer.start(context)
            }

            else -> {
                SafetyAlertPlayer.stop()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            SafetyAlertPlayer.stop()
        }
    }


    // ========================================
    // 고정 경로의 누적 남은 거리 미리 계산
    //
    // routePoints가 바뀔 때 딱 한 번 계산합니다.
    // GPS 갱신 때마다 모든 남은 선분을 다시 더하지 않습니다.
    // ========================================

    val remainingDistanceFromIndex =
        remember(routePoints) {

            val fixedRoutePoints = routePoints

            DoubleArray(
                fixedRoutePoints.size
            ).also { distances ->

                if (
                    fixedRoutePoints.size >= 2
                ) {

                    for (
                    index in fixedRoutePoints.size - 2 downTo 0
                    ) {

                        val point1 =
                            fixedRoutePoints[index]

                        val point2 =
                            fixedRoutePoints[index + 1]

                        distances[index] =
                            distances[index + 1] +
                                    calculateDistanceMeter(
                                        point1.latitude,
                                        point1.longitude,
                                        point2.latitude,
                                        point2.longitude
                                    )
                    }
                }
            }
        }


    // ========================================
    // GPS 이동 시
    // 1. 도착 여부 확인
    // 2. 고정 경로에서 현재 위치와 가장 가까운 점 탐색
    // 3. 경로 이탈 여부 확인
    // 4. 남은 거리 / 시간 계산
    //
    // 전체 경로 탐색을 한 번만 수행합니다.
    // ========================================

    LaunchedEffect(
        currentLatitude,
        currentLongitude,
        currentAccuracy,
        routePoints,
        remainingDistanceFromIndex,
        destinationLatitude,
        destinationLongitude
    ) {

        val currentLat =
            currentLatitude
                ?: return@LaunchedEffect

        val currentLng =
            currentLongitude
                ?: return@LaunchedEffect

        val endLat =
            destinationLatitude
                ?: return@LaunchedEffect

        val endLng =
            destinationLongitude
                ?: return@LaunchedEffect

        val fixedRoutePoints = routePoints


        // 목적지까지 현재 직선거리
        val destinationDistance =
            calculateDistanceMeter(
                currentLat,
                currentLng,
                endLat,
                endLng
            )


        Log.d(
            "ACTIVE_ROUTE",
            "목적지까지 직선 거리 = ${destinationDistance.roundToInt()}m"
        )


        if (
            destinationDistance <=
            ARRIVAL_DISTANCE_METER &&
            !arrivalHandled
        ) {

            arrivalHandled =
                true

            hasArrived =
                true

            remainingDistance =
                0

            remainingTime =
                0

            stopLocationUpdates()

            showArrivalDialog =
                true


            Log.d(
                "ACTIVE_ROUTE",
                "목적지 부근 도착 감지"
            )

            return@LaunchedEffect
        }


        if (
            arrivalHandled ||
            fixedRoutePoints.isEmpty()
        ) {

            return@LaunchedEffect
        }


        // ========================================
        // 고정 경로에서 현재 위치와 가장 가까운 점
        //
        // 매 GPS 갱신마다 경로 전체를 스캔하지 않고
        // 직전 위치 주변 구간을 먼저 찾습니다.
        // 주변에서 경로를 찾지 못한 경우에만 전체 스캔합니다.
        // ========================================

        var nearestIndex =
            nearestRouteIndex
                .coerceIn(
                    0,
                    fixedRoutePoints.lastIndex
                )

        var nearestDistance =
            Double.MAX_VALUE


        val searchStart =
            (nearestIndex - 40)
                .coerceAtLeast(
                    0
                )

        val searchEnd =
            (nearestIndex + 120)
                .coerceAtMost(
                    fixedRoutePoints.lastIndex
                )


        for (
        index in searchStart..searchEnd
        ) {

            val point =
                fixedRoutePoints[index]

            val distance =
                calculateDistanceMeter(
                    currentLat,
                    currentLng,
                    point.latitude,
                    point.longitude
                )


            if (
                distance < nearestDistance
            ) {

                nearestDistance =
                    distance

                nearestIndex =
                    index
            }
        }


        // GPS가 크게 튀었거나 사용자가 멀리 이동한 경우
        // 부분 탐색만으로 잘못 판정하지 않도록 전체 경로를 한 번 확인합니다.
        if (
            nearestDistance > 60.0 &&
            (
                    searchStart > 0 ||
                            searchEnd < fixedRoutePoints.lastIndex
                    )
        ) {

            fixedRoutePoints
                .forEachIndexed {
                        index,
                        point ->

                    val distance =
                        calculateDistanceMeter(
                            currentLat,
                            currentLng,
                            point.latitude,
                            point.longitude
                        )


                    if (
                        distance < nearestDistance
                    ) {

                        nearestDistance =
                            distance

                        nearestIndex =
                            index
                    }
                }
        }


        nearestRouteIndex =
            nearestIndex


        // ========================================
        // 경로 이탈 감지
        // GPS 정확도가 나쁜 경우에는 이탈 판정을 하지 않습니다.
        // ========================================

        val accuracy =
            currentAccuracy

        if (
            accuracy != null &&
            accuracy <= 20f
        ) {

            if (
                nearestDistance > 30.0
            ) {

                if (
                    deviationStartTime == null
                ) {

                    deviationStartTime =
                        System.currentTimeMillis()
                }

            } else {

                deviationStartTime =
                    null
            }
        }


        // ========================================
        // 남은 거리
        // 미리 계산한 누적 거리 배열에서 즉시 가져옵니다.
        // ========================================

        remainingDistance =
            remainingDistanceFromIndex
                .getOrNull(
                    nearestIndex
                )
                ?.roundToInt()
                ?.coerceAtLeast(
                    0
                )
                ?: 0


        // ========================================
        // 남은 시간 추정
        // ========================================

        remainingTime =
            if (
                initialDistance > 0 &&
                initialTime > 0
            ) {

                (
                        initialTime *
                                (
                                        remainingDistance.toDouble() /
                                                initialDistance.toDouble()
                                        )
                        )
                    .roundToInt()
                    .coerceAtLeast(
                        0
                    )

            } else {

                0
            }
    }


    // ========================================
    // 진행률
    // ========================================

    val progress =

        if (hasArrived) {

            1f

        } else if (
            initialDistance > 0
        ) {

            (
                    1f -
                            remainingDistance.toFloat() /
                            initialDistance.toFloat()
                    )
                .coerceIn(
                    0f,
                    1f
                )

        } else {

            0f
        }


    // ========================================
    // 남은 시간
    // ========================================

    val remainingMinutes =

        if (
            remainingTime > 0
        ) {

            ceil(
                remainingTime / 60.0
            )
                .toInt()
                .coerceAtLeast(
                    1
                )

        } else {

            0
        }


    // ========================================
    // 도착 예상시간
    // ========================================

    val arrivalTime =

        if (
            remainingMinutes > 0
        ) {

            val calendar =
                Calendar.getInstance()


            calendar.add(
                Calendar.MINUTE,
                remainingMinutes
            )


            SimpleDateFormat(
                "HH:mm",
                Locale.KOREA
            )
                .format(
                    calendar.time
                )

        } else {

            "--:--"
        }


    // ========================================
    // 거리 표시
    // ========================================

    val distanceText =

        when {

            remainingDistance <= 0 -> {

                "0m"
            }


            remainingDistance < 1000 -> {

                "${remainingDistance}m"
            }


            else -> {

                String.format(
                    Locale.KOREA,
                    "%.1fkm",
                    remainingDistance / 1000.0
                )
            }
        }


    // ========================================
    // 경로 표시 이름
    // ========================================

    val routeName =

        when (
            routeMode
        ) {

            "SHORTEST" ->
                "빠른길"

            "BROAD_FIRST" ->
                "대로변"

            "AI_SAFE" ->
                "AI 안전경로"

            else ->
                "안심경로"
        }


    // ========================================
    // 화면
    // ========================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ActiveBackground
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize()
        ) {


            // ========================================
            // 헤더
            // ========================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
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
                        ActiveTextBlack,

                    modifier = Modifier
                        .align(
                            Alignment.CenterStart
                        )
                        .size(
                            20.dp
                        )
                        .clickable {

                            onBackClick()
                        }
                )


                Text(
                    text =

                        if (hasArrived) {

                            "목적지 도착"

                        } else {

                            "귀가 진행 중"
                        },

                    fontSize =
                        20.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        ActiveTextBlack,

                    modifier =
                        Modifier.align(
                            Alignment.Center
                        )
                )
            }


            // ========================================
            // 정보 카드
            // ========================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp
                    ),

                shape =
                    RoundedCornerShape(
                        16.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 16.dp
                        ),

                    horizontalArrangement =
                        Arrangement.SpaceEvenly,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    ActiveRouteStat(

                        title =
                            "예상 도착 시간",

                        value =
                            when {

                                isLoading ->
                                    "계산 중"

                                hasArrived ->
                                    "도착"

                                else ->
                                    arrivalTime
                            }
                    )


                    VerticalDivider(
                        modifier =
                            Modifier.height(
                                38.dp
                            )
                    )


                    ActiveRouteStat(

                        title =
                            "남은 거리",

                        value =
                            if (isLoading) {

                                "-"

                            } else {

                                distanceText
                            }
                    )


                    VerticalDivider(
                        modifier =
                            Modifier.height(
                                38.dp
                            )
                    )


                    ActiveRouteStat(

                        title =
                            "남은 시간",

                        value =
                            when {

                                isLoading ->
                                    "-"

                                hasArrived ->
                                    "0분"

                                else ->
                                    "${remainingMinutes}분"
                            }
                    )
                }


                HorizontalDivider(
                    color =
                        Color(
                            0xFFF0F0F0
                        )
                )


                // ========================================
                // 위치공유
                // 기존 UI 유지
                // ========================================

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 13.dp
                        ),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "위치 공유",

                            fontSize =
                                12.sp,

                            color =
                                ActiveTextGray
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    8.dp
                                )
                        )


                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        14.dp
                                    )
                                )
                                .background(
                                    ActiveBlue
                                )
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                        ) {

                            Text(
                                text =
                                    "ON",

                                fontSize =
                                    11.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }
                    }


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Filled.Person,

                            contentDescription =
                                null,

                            tint =
                                ActiveBlue,

                            modifier =
                                Modifier.size(
                                    17.dp
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
                                "공유 대상 2명",

                            fontSize =
                                12.sp,

                            color =
                                ActiveTextBlack
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            // ========================================
            // 지도
            // ========================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(
                        1f
                    )
            ) {


                // ========================================
                // 지도에는 최초에 확정한 routePoints를 그대로 전달합니다.
                // GPS가 움직여도 경로선은 다시 계산하지 않습니다.
                // 현재 위치 마커만 계속 갱신됩니다.
                // ========================================

                KakaoMapView(

                    modifier =
                        Modifier.fillMaxSize(),

                    // 안내 시작 당시 고정된 출발 위치
                    startLatitude =
                        routeStartLatitude,

                    startLongitude =
                        routeStartLongitude,

                    // ========================================
                    // 귀가 경로에서는 특정 시설 하나가 아니라
                    // 경로 주변 50m 이내의 모든 안전시설을 표시합니다.
                    //
                    // KakaoMapView는 selectedFacility가 null이면
                    // 시설 마커 표시를 중단하기 때문에,
                    // 경로 시설 표시용 값을 전달합니다.
                    // ========================================
                    selectedFacility =
                        "ROUTE_FACILITIES",

                    destinationName =
                        destinationName,

                    destinationLatitude =
                        destinationLatitude,

                    destinationLongitude =
                        destinationLongitude,

                    routeMode =
                        routeMode,

                    showRoute =
                        routePoints.size >= 2,

                    fixedRoutePoints =
                        routePoints,

                    currentLatitude =
                        currentLatitude,

                    currentLongitude =
                        currentLongitude,

                    currentBearing =
                        currentBearing,

                    recenterRequestKey =
                        recenterRequestKey,

                    // ========================================
                    // 현재 선택한 경로에서 50m 이내에 있는
                    // 안전시설만 지도 마커 데이터로 전달합니다.
                    // ========================================
                    facilities =
                        routeFacilities
                )


                // ========================================
// 오른쪽 버튼
// ========================================

                Column(
                    modifier = Modifier
                        .align(
                            Alignment.CenterEnd
                        )
                        .offset(
                            y = 44.dp
                        )
                        .padding(
                            end = 18.dp
                        ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    // ========================================
                    // 긴급신고
                    // ========================================

                    FloatingActionButton(

                        onClick =
                            onEmergencyClick,

                        modifier =
                            Modifier.size(
                                64.dp
                            ),

                        shape =
                            CircleShape,

                        containerColor =
                            EmergencyRed
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Icon(
                                painter = painterResource(
                                    R.drawable.ic_emergency_siren
                                ),

                                contentDescription =
                                    "긴급신고",

                                tint =
                                    Color.White,

                                modifier =
                                    Modifier.size(
                                        26.dp
                                    )
                            )

                            Text(
                                text =
                                    "긴급신고",

                                fontSize =
                                    9.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    // ========================================
                    // 꽥꽥이
                    // ========================================

                    FloatingActionButton(

                        onClick =
                            onQuackClick,

                        modifier =
                            Modifier.size(
                                60.dp
                            ),

                        shape =
                            CircleShape,

                        containerColor =
                            Color.White
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Filled.Campaign,

                                contentDescription =
                                    "꽥꽥이",

                                tint =
                                    QuackOrange,

                                modifier =
                                    Modifier.size(
                                        27.dp
                                    )
                            )

                            Text(
                                text =
                                    "꽥꽥이",

                                fontSize =
                                    9.sp,

                                color =
                                    ActiveTextBlack
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    // ========================================
                    // 현재 위치로 돌아가기
                    // ========================================

                    FloatingActionButton(

                        onClick = {
                            recenterRequestKey++
                        },

                        modifier =
                            Modifier.size(
                                48.dp
                            ),

                        shape =
                            CircleShape,

                        containerColor =
                            Color.White
                    ) {

                        Icon(
                            imageVector =
                                Icons.Filled.MyLocation,

                            contentDescription =
                                "현재 위치로 이동",

                            tint =
                                Color.Black,

                            modifier =
                                Modifier.size(
                                    24.dp
                                )
                        )
                    }
                }
            }


            // ========================================
            // 하단 진행 상황
            // ========================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 14.dp
                    )
            ) {

                Text(
                    text =
                        "현재 경로 · $routeName",

                    fontSize =
                        12.sp,

                    color =
                        ActiveTextGray
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )


                LinearProgressIndicator(

                    progress = {
                        progress
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            6.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                3.dp
                            )
                        ),

                    color =
                        ActiveBlue,

                    trackColor =
                        Color(
                            0xFFE5E8EF
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    Text(
                        text =
                            "출발",

                        fontSize =
                            10.sp,

                        color =
                            ActiveTextGray
                    )


                    Text(
                        text =

                            if (hasArrived) {

                                "도착"

                            } else {

                                "${(progress * 100).roundToInt()}%"
                            },

                        fontSize =
                            10.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            ActiveBlue
                    )


                    Text(
                        text =
                            "도착",

                        fontSize =
                            10.sp,

                        color =
                            ActiveTextGray
                    )
                }


                if (
                    routeError != null
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                10.dp
                            )
                    )


                    Text(
                        text =
                            routeError!!,

                        fontSize =
                            12.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            15.dp
                        )
                )


                OutlinedButton(

                    onClick = {

                        stopLocationUpdates()
                        saveJourneyRecord()
                        RouteSessionStore.clear()
                        onFinishClick()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            50.dp
                        ),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )

                ) {

                    Text(
                        text =

                            if (hasArrived) {

                                "귀가 완료"

                            } else {

                                "안내 종료"
                            },

                        fontSize =
                            15.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            ActiveBlue
                    )
                }
            }
        }


        // ========================================
        // 목적지 도착 팝업
        // ========================================

        if (
            showArrivalDialog
        ) {
            AlertDialog(
                onDismissRequest = {
                    showArrivalDialog =
                        false
                },
                title = {
                    Text(
                        text =
                            "목적지 도착",
                        fontWeight =
                            FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text =
                            "목적지 부근에 도착했습니다.\n안전경로 안내를 종료합니다."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showArrivalDialog =
                                false
                            stopLocationUpdates()
                            saveJourneyRecord()
                            RouteSessionStore.clear()
                            onFinishClick()
                        },
                        colors =
                            ButtonDefaults
                                .buttonColors(
                                    containerColor =
                                        ActiveBlue
                                )
                    ) {
                        Text(
                            text =
                                "확인",
                            color =
                                Color.White
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showArrivalDialog =
                                false
                        }
                    ) {
                        Text(
                            text =
                                "계속 이동",
                            color =
                                Color.Gray
                        )
                    }
                }
            )
        }
    }

    // ========================================
    // 안전 확인 팝업
    // ========================================

    SafetyCheckDialogHost(

        state = safetyPopupState,

        onSafeClick = {
            safetyPopupState = SafetyPopupState.NONE
            lastMovementTime = System.currentTimeMillis()
            deviationStartTime = null
        },

        onNeedHelpClick = {
            safetyPopupState = SafetyPopupState.FINAL_CHECK
        },

        onEmergencyClick = {
            safetyPopupState = SafetyPopupState.NONE
            onEmergencyClick()
        },

        onFinalTimeout = {

            val currentMemberId = tokenManager.getMemberId()

            if (currentMemberId != null) {

                coroutineScope.launch {

                    try {

                        RetrofitClient.trackingApi.triggerEmergency(
                            EmergencyTriggerRequest(
                                memberId = currentMemberId,
                                lat = currentLatitude ?: 0.0,
                                lng = currentLongitude ?: 0.0
                            )
                        )

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            safetyPopupState = SafetyPopupState.GUARDIAN_ALERT_SENT
        },

        onQuackClick = {
            safetyPopupState = SafetyPopupState.NONE
            onQuackClick()
        },

        onConfirmClick = {
            safetyPopupState = SafetyPopupState.NONE
        }

    )
}


// ========================================
// 정보 표시
// ========================================

@Composable
private fun ActiveRouteStat(
    title: String,
    value: String
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text =
                title,

            fontSize =
                10.sp,

            color =
                ActiveTextGray
        )


        Spacer(
            modifier =
                Modifier.height(
                    5.dp
                )
        )


        Text(
            text =
                value,

            fontSize =
                15.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                ActiveTextBlack
        )
    }
}


// ========================================
// GPS 거리 계산
// ========================================

private fun calculateDistanceMeter(

    lat1: Double,

    lng1: Double,

    lat2: Double,

    lng2: Double

): Double {

    val earthRadius =
        6371000.0


    val latitudeDistance =
        Math.toRadians(
            lat2 - lat1
        )


    val longitudeDistance =
        Math.toRadians(
            lng2 - lng1
        )


    val a =

        sin(
            latitudeDistance / 2
        ).pow(
            2
        ) +

                cos(
                    Math.toRadians(
                        lat1
                    )
                ) *

                cos(
                    Math.toRadians(
                        lat2
                    )
                ) *

                sin(
                    longitudeDistance / 2
                ).pow(
                    2
                )


    val c =
        2 * atan2(
            sqrt(a),
            sqrt(
                1 - a
            )
        )


    return earthRadius * c
}


// ========================================
// 긴급 알림 사운드 + 진동
// 폰 설정(무음/진동/소리)과 무관하게 강제 재생
// ========================================

private object SafetyAlertPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    fun start(context: android.content.Context) {

        stop()

        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.siren).apply {
                isLooping = true
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            vibrator = getVibrator(context)

            val pattern = longArrayOf(0, 500, 300, 500, 300)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                vibrator?.vibrate(
                    VibrationEffect.createWaveform(pattern, 0)
                )

            } else {

                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {

        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null
    }

    private fun getVibrator(context: android.content.Context): Vibrator {

        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

            val vibratorManager = context.getSystemService(
                android.content.Context.VIBRATOR_MANAGER_SERVICE
            ) as android.os.VibratorManager

            vibratorManager.defaultVibrator

        } else {

            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
}


// ========================================
// Preview
// ========================================

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ActiveRouteScreenPreview() {

    ActiveRouteScreen(

        destinationName =
            "강남역",

        destinationLatitude =
            37.4979,

        destinationLongitude =
            127.0276,

        routeMode =
            "SHORTEST"
    )
}
