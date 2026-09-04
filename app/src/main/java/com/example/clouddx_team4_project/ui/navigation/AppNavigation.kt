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
import com.example.clouddx_team4_project.network.EmergencyPopupResponse
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
import com.example.clouddx_team4_project.network.LocationUpdateRequest
import kotlinx.coroutines.delay

@Composable
fun AppNavigation() {

    val context = LocalContext.current

    val tokenManager = remember {
        TokenManager(context)
    }

    val startDest = if (tokenManager.hasValidToken()) "home" else "login"

    // 로그인한 사용자 memberId
    var currentMemberId by remember {
        mutableStateOf(tokenManager.getMemberId())
    }

    SideEffect {
        RetrofitClient.tokenManager = tokenManager
    }

    val navController =
        rememberNavController()

    val coroutineScope = rememberCoroutineScope()

    // ========================================
    // 친구에게서 받은 긴급신고 팝업
    // ========================================
    var pendingEmergencyPopup by remember {
        mutableStateOf<EmergencyPopupResponse?>(null)
    }

    var isEmergencyPopupAcking by remember {
        mutableStateOf(false)
    }

    // ========================================
    // 로그인 상태 동안 3초마다 GPS를 서버로 전송
    // (위치공유 기능용)
    // ========================================

    LaunchedEffect(currentMemberId) {

        val memberId = currentMemberId ?: return@LaunchedEffect

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        while (true) {

            delay(3000L)

            val finePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            val coarsePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            if (
                finePermission != PackageManager.PERMISSION_GRANTED &&
                coarsePermission != PackageManager.PERMISSION_GRANTED
            ) {
                continue
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->

                if (location != null) {

                    coroutineScope.launch {

                        try {

                            RetrofitClient.trackingApi.updateLocation(
                                LocationUpdateRequest(
                                    memberId = memberId,
                                    lat = location.latitude,
                                    lng = location.longitude
                                )
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    // ========================================
    // 로그인 상태 동안 3초마다
    // 나에게 온 긴급신고 팝업 확인
    // ========================================
    LaunchedEffect(currentMemberId) {

        val memberId = currentMemberId ?: return@LaunchedEffect

        while (true) {

            try {

                // 이미 팝업을 보여주고 있다면
                // 새 조회 결과로 덮어쓰지 않음
                if (pendingEmergencyPopup == null) {

                    val response =
                        RetrofitClient.trackingApi.getPendingEmergencyPopup(
                            memberId
                        )

                    if (response.isSuccessful) {

                        val popup = response.body()

                        if (
                            popup?.hasEmergency == true &&
                            popup.emergencyId != null
                        ) {

                            pendingEmergencyPopup = popup
                        }
                    }
                }

            } catch (e: Exception) {

                android.util.Log.e(
                    "EMERGENCY_POPUP",
                    "긴급신고 팝업 조회 실패",
                    e
                )
            }

            delay(3000L)
        }
    }

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
        // 안심경로 (동적 변수 friendId, friendName 수신 가능하도록 변경)
        // ========================================
        composable(
            route = "safe_route?friendId={friendId}&friendName={friendName}",
            arguments = listOf(
                androidx.navigation.navArgument("friendId") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                androidx.navigation.navArgument("friendName") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val memberId = currentMemberId ?: return@composable

            // 라우터에서 전달받은 진짜(동적) 데이터를 꺼냅니다.
            val passedFriendId = backStackEntry.arguments?.getString("friendId")?.toLongOrNull()
            val passedFriendName = backStackEntry.arguments?.getString("friendName")

            SafeRouteScreen(
                memberId = memberId,
                friendId = passedFriendId,     // 고정값이 아닌 전달받은 진짜 ID 적용
                friendName = passedFriendName, // 고정값이 아닌 전달받은 진짜 이름 적용

                destinationName = selectedDestination?.placeName ?: "",
                destinationLatitude = selectedDestination?.latitude?.toDoubleOrNull(),
                destinationLongitude = selectedDestination?.longitude?.toDoubleOrNull(),
                showSelectedRoute = showSelectedRoute,
                selectedRouteMode = selectedRouteMode,

                onBackClick = { navController.popBackStack() },
                onStartSearchClick = {},
                onDestinationSearchClick = {
                    showSelectedRoute = false
                    destinationSearchMode = "ROUTE"
                    navController.navigate("destination_search")
                },
                onRouteSearchClick = {
                    if (selectedDestination != null) {
                        navController.navigate("route_select")
                    }
                },
                onDefaultDestinationSelected = { placeName, address, latitude, longitude ->
                    selectedDestination = KakaoPlace(
                        id = "default_destination",
                        placeName = placeName,
                        addressName = address,
                        roadAddressName = address,
                        longitude = longitude.toString(),
                        latitude = latitude.toString()
                    )
                    showSelectedRoute = false
                    navController.navigate("route_select") {
                        launchSingleTop = true
                    }
                },
                onTabSelected = { tab ->
                    when (tab) {
                        "홈" -> {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                        "더보기" -> {
                            navController.navigate("more") { launchSingleTop = true }
                        }
                    }
                },
                onEmergencyClick = { showEmergencyDialog = true }
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
            val friendViewModel: com.example.clouddx_team4_project.ui.screens.FriendViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            val navigateData = friendViewModel.navigateToMapData
            LaunchedEffect(navigateData) {
                navigateData?.let { (name, lat, lng) ->
                    navController.navigate("friend_map/$name/$lat/$lng")
                    friendViewModel.clearNavigation()
                }
            }

            FriendScreen(

                onBackClick = {

                    navController.popBackStack()
                },

                onAddFriendClick = {},

                onAddFriendSubmit = { _, _ -> },

                onAcceptRequest = {},

                onRejectRequest = {},

                onDeleteFriend = {},

                onLocationClick = { friendName ->
                    val targetFriend = friendViewModel.friends.find { it.memberName == friendName }
                    targetFriend?.let {
                        // AppNavigation 상단에 정의된 '내 로그인 ID'를 안전하게 가져옵니다.
                        val myId = currentMemberId ?: return@let

                        friendViewModel.fetchFriendLocation(
                            requesterId = myId,           // 1. 내 회원 ID (토큰 기반)
                            friendMemberId = it.mmbrId,   // 2. 누른 친구의 회원 ID
                            friendName = it.memberName    // 3. 지도에 띄울 친구 이름 (파라미터 누락 해결)
                        )
                    }
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
        // 안심친구 위치 지도 화면 (이 블록을 새로 추가해 주세요)
        // ========================================

        composable(
            route = "friend_map/{name}/{lat}/{lng}",
            arguments = listOf(
                androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("lat") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("lng") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val lat = backStackEntry.arguments?.getString("lat")?.toDouble() ?: 0.0
            val lng = backStackEntry.arguments?.getString("lng")?.toDouble() ?: 0.0

            com.example.clouddx_team4_project.ui.screens.FriendLocationMapScreen(
                friendName = name,
                friendLat = lat,
                friendLng = lng,
                friendId = null,
                onBackClick = { navController.popBackStack() }
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
    // 공용 긴급신고 팝업 (팀원 추가 위치 권한 연동 완료)
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
                                            "긴급신고 전송 완료, 보호자 ${response.notifiedGuardianCount}명"
                                        )

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        android.util.Log.e(
                                            "EMERGENCY_API",
                                            "긴급신고 전송 실패",
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
                        "로그인 정보(memberId)가 없어 긴급신고를 전송할 수 없습니다."
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
    // ========================================
    // 친구에게서 받은 긴급신고 전역 팝업
    // ========================================
    val receivedEmergency = pendingEmergencyPopup

    if (
        receivedEmergency != null &&
        !showEmergencyDialog &&
        !showLogoutDialog
    ) {

        AlertDialog(

            // 바깥 영역이나 뒤로가기로 닫히지 않도록 함
            // 반드시 확인 버튼으로 처리
            onDismissRequest = {},

            title = {
                Text(
                    text = "긴급신고",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text =
                        "${receivedEmergency.senderName ?: "친구"}님이 긴급신고를 보냈습니다."
                )
            },

            confirmButton = {

                TextButton(

                    enabled = !isEmergencyPopupAcking,

                    onClick = {

                        val memberId =
                            currentMemberId

                        val emergencyId =
                            receivedEmergency.emergencyId

                        if (
                            memberId != null &&
                            emergencyId != null &&
                            !isEmergencyPopupAcking
                        ) {

                            isEmergencyPopupAcking = true

                            coroutineScope.launch {

                                try {

                                    val response =
                                        RetrofitClient.trackingApi
                                            .acknowledgeEmergencyPopup(
                                                emergencyId = emergencyId,
                                                memberId = memberId
                                            )

                                    if (response.isSuccessful) {

                                        pendingEmergencyPopup = null

                                    } else {

                                        android.util.Log.e(
                                            "EMERGENCY_POPUP",
                                            "긴급신고 확인 처리 실패: ${response.code()}"
                                        )
                                    }

                                } catch (e: Exception) {

                                    android.util.Log.e(
                                        "EMERGENCY_POPUP",
                                        "긴급신고 확인 처리 중 오류",
                                        e
                                    )

                                } finally {

                                    isEmergencyPopupAcking = false
                                }
                            }
                        }
                    }
                ) {

                    Text(
                        text = "확인"
                    )
                }
            }
        )
    }
}