package com.example.clouddx_team4_project.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import com.example.clouddx_team4_project.data.KakaoPlace
import com.example.clouddx_team4_project.data.TokenManager
import com.example.clouddx_team4_project.network.EmergencyTriggerRequest
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.ui.components.EmergencyDialog
import com.example.clouddx_team4_project.ui.screens.ActiveRouteScreen
import com.example.clouddx_team4_project.ui.screens.DefaultDestinationScreen
import com.example.clouddx_team4_project.ui.screens.DestinationSearchScreen
import com.example.clouddx_team4_project.ui.screens.FriendScreen
import com.example.clouddx_team4_project.ui.screens.GuardianRegisterScreen
import com.example.clouddx_team4_project.ui.screens.HomeScreen
import com.example.clouddx_team4_project.ui.screens.LoginScreen
import com.example.clouddx_team4_project.ui.screens.MoreScreen
import com.example.clouddx_team4_project.ui.screens.ProfileSettingScreen
import com.example.clouddx_team4_project.ui.screens.QuackScreen
import com.example.clouddx_team4_project.ui.screens.ReportScreen
import com.example.clouddx_team4_project.ui.screens.RouteSelectScreen
import com.example.clouddx_team4_project.ui.screens.SafeMapScreen
import com.example.clouddx_team4_project.ui.screens.SafeRouteScreen
import com.example.clouddx_team4_project.ui.screens.SignUpScreen
import com.example.clouddx_team4_project.ui.screens.more.HelpScreen
import com.example.clouddx_team4_project.ui.screens.more.InquiryScreen
import com.example.clouddx_team4_project.ui.screens.more.NoticeScreen
import com.example.clouddx_team4_project.ui.screens.more.PrivacyPolicyScreen
import com.example.clouddx_team4_project.ui.screens.more.ServiceIntroScreen

@Composable
fun AppNavigation() {

    val context = LocalContext.current

    // ========================================
    // 로그인 사용자 정보
    // ========================================

    val tokenManager = remember {
        TokenManager(context)
    }

    val startDest = if (tokenManager.hasValidToken()) "home" else "login"

    // 로그인한 사용자 memberId L
    var currentMemberId by remember {
        mutableStateOf(tokenManager.getMemberId())
    }

    // 앱을 다시 실행해서 이미 로그인된 상태여도
    // Retrofit의 JWT 인터셉터가 TokenManager를 사용할 수 있도록 연결
    SideEffect {
        RetrofitClient.tokenManager = tokenManager
    }


    // ========================================
    // Navigation
    // ========================================

    val navController =
        rememberNavController()

    val coroutineScope = rememberCoroutineScope()


    // ========================================
    // 로그아웃 팝업
    // ========================================

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 안심경로에서 선택한 목적지
    // ========================================

    var selectedDestination by remember {
        mutableStateOf<KakaoPlace?>(null)
    }


    // ========================================
    // 선택한 경로 모드
    // ========================================

    var selectedRouteMode by remember {
        mutableStateOf("BROAD_FIRST")
    }


    // ========================================
    // 안심경로 화면에서 선택 경로 표시 여부
    // ========================================

    var showSelectedRoute by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 긴급 구조 팝업
    // ========================================

    var showEmergencyDialog by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 기본 목적지 등록 중
    // 검색 화면에서 선택한 장소 임시 저장
    // ========================================

    var pendingDefaultPlace by remember {
        mutableStateOf<KakaoPlace?>(null)
    }


    // ========================================
    // 목적지 검색 용도
    // ========================================

    var destinationSearchMode by remember {
        mutableStateOf("ROUTE")
    }


    // ========================================
    // 전체 Navigation
    // ========================================

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {


        // ========================================
        // 로그인
        // ========================================

        composable("login") {

            LoginScreen(

                onLoginClick = { _, _ ->

                    val loggedInMemberId =
                        tokenManager.getMemberId()

                    if (loggedInMemberId != null) {

                        currentMemberId =
                            loggedInMemberId

                        RetrofitClient.tokenManager =
                            tokenManager

                        navController.navigate("home") {

                            popUpTo("login") {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                },

                onSignUpClick = {

                    navController.navigate("signup")
                }
            )
        }


        // ========================================
        // 회원가입
        // ========================================

        composable("signup") {

            SignUpScreen(

                onBackClick = {

                    navController.popBackStack()
                },

                onSignUpComplete = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 홈
        // ========================================

        composable("home") {

            HomeScreen(

                onMenuClick = { menu ->

                    when (menu) {

                        "안심경로" -> {
                            showSelectedRoute = false
                            selectedDestination = null
                            navController.navigate("safe_route") {
                                launchSingleTop = true
                            }
                        }

                        "안심친구" -> {

                            navController.navigate("friend") {
                                launchSingleTop = true
                            }
                        }

                        "꽥꽥이" -> {

                            navController.navigate("quack") {
                                launchSingleTop = true
                            }
                        }

                        "안심지도" -> {

                            navController.navigate("safe_map") {
                                launchSingleTop = true
                            }
                        }

                        "사용 리포트" -> {

                            navController.navigate("report") {
                                launchSingleTop = true
                            }
                        }

                        "더보기" -> {

                            navController.navigate("more") {
                                launchSingleTop = true
                            }
                        }
                    }
                },

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 안심경로
        // ========================================

        composable("safe_route") {

            val memberId =
                currentMemberId ?: return@composable

            SafeRouteScreen(

                memberId = memberId,

                destinationName =
                    selectedDestination
                        ?.placeName
                        ?: "",

                destinationLatitude =
                    selectedDestination
                        ?.latitude
                        ?.toDoubleOrNull(),

                destinationLongitude =
                    selectedDestination
                        ?.longitude
                        ?.toDoubleOrNull(),

                showSelectedRoute =
                    showSelectedRoute,

                selectedRouteMode =
                    selectedRouteMode,

                onBackClick = {

                    navController.popBackStack()
                },

                onStartSearchClick = {},

                onDestinationSearchClick = {

                    showSelectedRoute = false

                    destinationSearchMode =
                        "ROUTE"

                    navController.navigate(
                        "destination_search"
                    )
                },

                onRouteSearchClick = {

                    if (selectedDestination != null) {

                        navController.navigate(
                            "route_select"
                        )
                    }
                },

                onDefaultDestinationSelected = {
                        placeName,
                        address,
                        latitude,
                        longitude ->

                    selectedDestination =
                        KakaoPlace(

                            id =
                                "default_destination",

                            placeName =
                                placeName,

                            addressName =
                                address,

                            roadAddressName =
                                address,

                            longitude =
                                longitude.toString(),

                            latitude =
                                latitude.toString()
                        )

                    showSelectedRoute = false

                    navController.navigate(
                        "route_select"
                    ) {

                        launchSingleTop = true
                    }
                },

                onMapDestinationSelected = {
                        latitude,
                        longitude ->

                    selectedDestination =
                        KakaoPlace(

                            id =
                                "manual_location",

                            placeName =
                                "선택한 위치",

                            addressName =
                                "",

                            roadAddressName =
                                "",

                            longitude =
                                longitude.toString(),

                            latitude =
                                latitude.toString()
                        )

                    showSelectedRoute = false
                },

                onTabSelected = { tab ->

                    when (tab) {

                        "홈" -> {

                            navController.navigate("home") {

                                popUpTo("home") {
                                    inclusive = false
                                }

                                launchSingleTop = true
                            }
                        }

                        "더보기" -> {

                            navController.navigate("more") {
                                launchSingleTop = true
                            }
                        }
                    }
                },

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 목적지 검색
        // ========================================

        composable("destination_search") {

            DestinationSearchScreen(

                onBackClick = {

                    navController.popBackStack()
                },

                onPlaceSelected = { place ->

                    if (
                        destinationSearchMode ==
                        "ROUTE"
                    ) {

                        selectedDestination =
                            place

                        showSelectedRoute =
                            false

                        navController.navigate(
                            "route_select"
                        ) {

                            popUpTo(
                                "destination_search"
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }

                    } else {

                        pendingDefaultPlace =
                            place

                        navController.popBackStack()
                    }
                }
            )
        }


        // ========================================
        // 경로 선택
        // ========================================

        composable("route_select") {

            RouteSelectScreen(

                startName =
                    "현재 위치",

                destinationName =
                    selectedDestination
                        ?.placeName
                        ?: "목적지를 검색하세요",

                destinationLatitude =
                    selectedDestination
                        ?.latitude
                        ?.toDoubleOrNull(),

                destinationLongitude =
                    selectedDestination
                        ?.longitude
                        ?.toDoubleOrNull(),

                onBackClick = {
                    selectedDestination = null
                    navController.popBackStack()
                },

                onFastRouteClick = {

                    selectedRouteMode =
                        "SHORTEST"

                    navController.navigate(
                        "active_route"
                    ) {

                        launchSingleTop = true
                    }
                },

                onAiSafeRouteClick = {

                    selectedRouteMode =
                        "AI_SAFE"

                    navController.navigate(
                        "active_route"
                    ) {

                        launchSingleTop = true
                    }
                },

                onBroadRouteClick = {

                    selectedRouteMode =
                        "BROAD_FIRST"

                    navController.navigate(
                        "active_route"
                    ) {

                        launchSingleTop = true
                    }
                }
            )
        }


        // ========================================
        // 귀가 진행 중
        // ========================================

        composable("active_route") {

            ActiveRouteScreen(

                destinationName =
                    selectedDestination
                        ?.placeName
                        ?: "목적지",

                destinationLatitude =
                    selectedDestination
                        ?.latitude
                        ?.toDoubleOrNull(),

                destinationLongitude =
                    selectedDestination
                        ?.longitude
                        ?.toDoubleOrNull(),

                routeMode =
                    selectedRouteMode,

                onBackClick = {

                    navController.popBackStack()
                },

                onEmergencyClick = {

                    showEmergencyDialog = true
                },

                onQuackClick = {

                    navController.navigate(
                        "quack"
                    ) {

                        launchSingleTop = true
                    }
                },

                onFinishClick = {

                    showSelectedRoute =
                        false

                    selectedDestination =
                        null

                    navController.navigate(
                        "home"
                    ) {

                        popUpTo("home") {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                }
            )
        }


        // ========================================
        // 안심친구
        // ========================================

        composable("friend") {

            FriendScreen(

                onBackClick = {

                    navController.popBackStack()
                },

                onAddFriendClick = {},

                onAddFriendSubmit = { _, _ -> },

                onAcceptRequest = {},

                onRejectRequest = {},

                onDeleteFriend = {},

                onLocationClick = {},

                onTabSelected = { tab ->

                    when (tab) {

                        "홈" -> {

                            navController.navigate(
                                "home"
                            ) {

                                popUpTo("home") {
                                    inclusive = false
                                }

                                launchSingleTop = true
                            }
                        }

                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop = true
                            }
                        }
                    }
                },

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 꽥꽥이
        // ========================================

        composable("quack") {

            QuackScreen(

                onBackClick = {

                    navController.popBackStack()
                },

                onStopClick = {

                    navController.popBackStack()
                },

                onTabSelected = { tab ->

                    when (tab) {

                        "홈" -> {

                            navController.navigate(
                                "home"
                            ) {

                                popUpTo("home") {
                                    inclusive = false
                                }

                                launchSingleTop = true
                            }
                        }

                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop = true
                            }
                        }
                    }
                },

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 사용 리포트
        // ========================================

        composable("report") {

            ReportScreen(

                onTabSelected = { tab ->

                    when (tab) {

                        "홈" -> {

                            navController.navigate(
                                "home"
                            ) {

                                popUpTo("home") {
                                    inclusive = false
                                }

                                launchSingleTop = true
                            }
                        }

                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop = true
                            }
                        }
                    }
                },

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 안심지도
        // ========================================

        composable("safe_map") {

            SafeMapScreen(

                currentLocationText =
                    "현재 위치 확인 중",

                onBackClick = {

                    navController.popBackStack()
                },

                onTabSelected = { tab ->

                    when (tab) {

                        "홈" -> {

                            navController.navigate(
                                "home"
                            ) {

                                popUpTo("home") {
                                    inclusive = false
                                }

                                launchSingleTop = true
                            }
                        }

                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop = true
                            }
                        }
                    }
                },

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 더보기
        // ========================================

        composable("more") {

            MoreScreen(

                onMenuClick = { menu ->

                    when (menu) {

                        "로그아웃" -> {

                            showLogoutDialog =
                                true
                        }

                        "프로필 설정" -> {

                            navController.navigate(
                                "profile_setting"
                            )
                        }

                        "기본 목적지 설정" -> {

                            navController.navigate(
                                "default_destination"
                            )
                        }

                        "보호자 등록" -> {

                            navController.navigate(
                                "guardian_register"
                            )
                        }

                        "공지사항" -> {

                            navController.navigate(
                                "notice"
                            )
                        }

                        "문의하기" -> {

                            navController.navigate(
                                "inquiry"
                            )
                        }

                        "도움말" -> {

                            navController.navigate(
                                "help"
                            )
                        }

                        "서비스 소개" -> {

                            navController.navigate(
                                "service_intro"
                            )
                        }

                        "개인정보처리방침" -> {

                            navController.navigate(
                                "privacy_policy"
                            )
                        }
                    }
                },

                onSettingsClick = {},

                onTabSelected = { tab ->

                    when (tab) {

                        "홈" -> {

                            navController.navigate(
                                "home"
                            ) {

                                popUpTo("home") {
                                    inclusive = false
                                }

                                launchSingleTop = true
                            }
                        }

                        "더보기" -> {}
                    }
                },

                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 기본 목적지 설정 (통합 완료)
        // ========================================

        composable(
            "default_destination"
        ) {

            val memberId =
                currentMemberId ?: return@composable

            DefaultDestinationScreen(

                memberId =
                    memberId,

                onBackClick = {

                    pendingDefaultPlace =
                        null

                    navController.popBackStack()
                },

                onSearchPlaceClick = {

                    pendingDefaultPlace =
                        null

                    destinationSearchMode =
                        "DEFAULT_DESTINATION"

                    navController.navigate(
                        "destination_search"
                    )
                },

                selectedPlaceName =
                    pendingDefaultPlace
                        ?.placeName,

                selectedAddress =

                    pendingDefaultPlace
                        ?.roadAddressName
                        ?.takeIf {
                            it.isNotBlank()
                        }

                        ?: pendingDefaultPlace
                            ?.addressName,

                selectedLatitude =
                    pendingDefaultPlace
                        ?.latitude
                        ?.toDoubleOrNull(),

                selectedLongitude =
                    pendingDefaultPlace
                        ?.longitude
                        ?.toDoubleOrNull(),

                onDestinationSaved = {

                    pendingDefaultPlace =
                        null
                }
            )
        }


        // ========================================
        // 공지사항
        // ========================================

        composable("notice") {

            NoticeScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 문의하기
        // ========================================

        composable("inquiry") {

            InquiryScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 도움말
        // ========================================

        composable("help") {

            HelpScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 서비스 소개
        // ========================================

        composable("service_intro") {

            ServiceIntroScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 개인정보처리방침
        // ========================================

        composable("privacy_policy") {

            PrivacyPolicyScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 보호자 등록
        // ========================================

        composable("guardian_register") {

            GuardianRegisterScreen(

                onBackClick = {

                    navController.popBackStack()
                },

                onRegisterClick = { _, _, _ -> },

                onDeleteClick = { _ -> }
            )
        }


        // ========================================
        // 프로필 설정
        // ========================================

        composable("profile_setting") {

            val memberId =
                currentMemberId ?: return@composable

            ProfileSettingScreen(

                memberId =
                    memberId,

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }
    }


    // ========================================
    // 로그아웃 확인 팝업
    // ========================================

    if (showLogoutDialog) {

        AlertDialog(

            onDismissRequest = {

                showLogoutDialog =
                    false
            },

            title = {

                Text(
                    text = "로그아웃",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {

                Text(
                    text = "정말 로그아웃 하시겠습니까?"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        showLogoutDialog =
                            false

                        tokenManager.clearToken()

                        currentMemberId = null

                        navController.navigate(
                            "login"
                        ) {

                            popUpTo(0) {
                                inclusive = true
                            }

                            launchSingleTop =
                                true
                        }
                    }
                ) {

                    Text(
                        text = "로그아웃",
                        color =
                            androidx.compose.ui.graphics.Color.Red
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showLogoutDialog =
                            false
                    }
                ) {

                    Text(
                        text = "취소",
                        color =
                            androidx.compose.ui.graphics.Color.Black
                    )
                }
            }
        )
    }


    // ========================================
    // 공용 긴급구조 팝업 (팀원 추가 위치 권한 연동 완료)
    // ========================================

    if (showEmergencyDialog) {

        EmergencyDialog(

            onDismiss = {

                showEmergencyDialog =
                    false
            },

            onEmergencyConfirmed = {

                val currentMemberId = tokenManager.getMemberId()

                if (currentMemberId != null) {

                    val finePermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    val coarsePermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                    if (
                        finePermission == PackageManager.PERMISSION_GRANTED ||
                        coarsePermission == PackageManager.PERMISSION_GRANTED
                    ) {

                        val fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(context)

                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->

                            if (location != null) {

                                coroutineScope.launch {

                                    try {

                                        val response = RetrofitClient.trackingApi.triggerEmergency(
                                            EmergencyTriggerRequest(
                                                memberId = currentMemberId,
                                                lat = location.latitude,
                                                lng = location.longitude
                                            )
                                        )

                                        android.util.Log.d(
                                            "EMERGENCY_API",
                                            "긴급구조 전송 완료, 보호자 ${response.notifiedGuardianCount}명"
                                        )

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        android.util.Log.e(
                                            "EMERGENCY_API",
                                            "긴급구조 전송 실패",
                                            e
                                        )
                                    }
                                }
                            }
                        }
                    }

                } else {

                    android.util.Log.e(
                        "EMERGENCY_API",
                        "로그인 정보(memberId)가 없어 긴급구조를 전송할 수 없습니다."
                    )
                }
            },

            onQuackClick = {

                showEmergencyDialog =
                    false

                navController.navigate(
                    "quack"
                ) {

                    launchSingleTop =
                        true
                }
            }
        )
    }
}