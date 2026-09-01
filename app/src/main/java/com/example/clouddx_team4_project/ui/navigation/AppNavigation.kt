package com.example.clouddx_team4_project.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clouddx_team4_project.data.KakaoPlace
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.clouddx_team4_project.data.TokenManager

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.clouddx_team4_project.network.EmergencyTriggerRequest
import com.example.clouddx_team4_project.network.RetrofitClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch


@Composable
fun AppNavigation() {

    val context = LocalContext.current

    // 💡 토큰 매니저 초기화 및 유효성 검사
    val tokenManager = remember { TokenManager(context) }
    val startDest = "home"
    // val startDest = if (tokenManager.hasValidToken()) "home" else "login"

    // 💡 로그아웃 팝업 상태 변수 추가
    var showLogoutDialog by remember { mutableStateOf(false) }

    val navController =
        rememberNavController()

    val coroutineScope = rememberCoroutineScope()


    // ========================================
    // 안심경로에서 선택한 목적지
    // ========================================

    var selectedDestination by remember {
        mutableStateOf<KakaoPlace?>(null)
    }


    // ========================================
    // 선택 경로
    // ========================================

    var selectedRouteMode by remember {
        mutableStateOf("BROAD_FIRST")
    }


    // ========================================
    // 안심경로에서 경로 표시 여부
    // ========================================

    var showSelectedRoute by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 긴급구조 팝업
    // ========================================

    var showEmergencyDialog by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 기본 목적지 등록 시
    // 검색 화면에서 선택한 장소 임시 보관
    // ========================================

    var pendingDefaultPlace by remember {
        mutableStateOf<KakaoPlace?>(null)
    }


    // ========================================
    // 목적지 검색 용도
    //
    // ROUTE
    // → 일반 안심경로 목적지 검색
    //
    // DEFAULT_DESTINATION
    // → 기본 목적지 등록용 장소 검색
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

        composable(
            "login"
        ) {

            LoginScreen(

                onLoginClick = { _, _ ->

                    navController.navigate(
                        "home"
                    ) {

                        popUpTo(
                            "login"
                        ) {

                            inclusive =
                                true
                        }

                        launchSingleTop =
                            true
                    }
                },


                onSignUpClick = {

                    navController.navigate(
                        "signup"
                    )
                }
            )
        }


        // ========================================
        // 회원가입
        // ========================================

        composable(
            "signup"
        ) {

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

        composable(
            "home"
        ) {

            HomeScreen(

                onMenuClick = { menu ->

                    when (menu) {


                        "안심경로" -> {

                            showSelectedRoute =
                                false

                            navController.navigate(
                                "safe_route"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }


                        "안심친구" -> {

                            navController.navigate(
                                "friend"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }


                        "꽥꽥이" -> {

                            navController.navigate(
                                "quack"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }


                        "안심지도" -> {

                            navController.navigate(
                                "safe_map"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }


                        "사용 리포트" -> {

                            navController.navigate(
                                "report"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }


                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 안심경로
        // ========================================

        composable(
            "safe_route"
        ) {

            SafeRouteScreen(

                // 로그인 기능 완성 전
                // DB 테스트 사용자
                memberId =
                    3L,


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


                onStartSearchClick = {

                    // 현재 위치는 SafeRouteScreen에서
                    // GPS → 주소 변환 후 표시
                },


                // ========================================
                // 일반 목적지 검색
                // ========================================

                onDestinationSearchClick = {

                    showSelectedRoute =
                        false

                    destinationSearchMode =
                        "ROUTE"

                    navController.navigate(
                        "destination_search"
                    )
                },


                // ========================================
                // 경로 선택 화면 이동
                // ========================================

                onRouteSearchClick = {

                    if (
                        selectedDestination != null
                    ) {

                        navController.navigate(
                            "route_select"
                        )
                    }
                },


                // ========================================
                // 기본 목적지 선택
                //
                // 예:
                // 버튼 이름 = 집
                // 실제 장소 = 방배역
                //
                // 집 클릭
                // ↓
                // 방배역을 selectedDestination에 저장
                // ↓
                // 바로 경로 선택 화면으로 이동
                // ========================================

                onDefaultDestinationSelected = {
                        placeName,
                        address,
                        latitude,
                        longitude ->


                    selectedDestination =
                        KakaoPlace(

                            id =
                                "default_destination",

                            // 실제 검색된 장소명
                            // 예: 방배역
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


                    showSelectedRoute =
                        false


                    // ========================================
                    // 일반 검색과 동일하게
                    // 경로 선택 화면으로 바로 이동
                    // ========================================

                    navController.navigate(
                        "route_select"
                    ) {

                        launchSingleTop =
                            true
                    }
                },


                // ========================================
                // 지도에서 직접 목적지 지정
                // ========================================

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


                    showSelectedRoute =
                        false
                },


                // ========================================
                // 하단 메뉴
                // ========================================

                onTabSelected = { tab ->

                    when (tab) {


                        "홈" -> {

                            navController.navigate(
                                "home"
                            ) {

                                popUpTo(
                                    "home"
                                ) {

                                    inclusive =
                                        false
                                }

                                launchSingleTop =
                                    true
                            }
                        }


                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 목적지 검색
        // ========================================

        composable(
            "destination_search"
        ) {

            DestinationSearchScreen(

                onBackClick = {

                    navController.popBackStack()
                },


                onPlaceSelected = { place ->


                    // ========================================
                    // 일반 안심경로 검색
                    //
                    // 장소 선택
                    // ↓
                    // selectedDestination 저장
                    // ↓
                    // 경로 선택 화면
                    // ========================================

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

                                inclusive =
                                    true
                            }

                            launchSingleTop =
                                true
                        }


                    } else {


                        // ========================================
                        // 기본 목적지 등록용 검색
                        //
                        // 장소 선택
                        // ↓
                        // 기본 목적지 설정 화면 복귀
                        // ↓
                        // 사용자 이름 입력
                        // ========================================

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

        composable(
            "route_select"
        ) {

            RouteSelectScreen(

                startName =
                    "현재 위치",


                // ========================================
                // 기본 목적지든 일반 검색이든
                // 실제 장소명 표시
                //
                // ex)
                // 방배역
                // 서울교육대학교
                // 강남역
                // ========================================

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


                onBackClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 빠른길
                // ========================================

                onFastRouteClick = {

                    selectedRouteMode =
                        "SHORTEST"

                    navController.navigate(
                        "active_route"
                    ) {

                        launchSingleTop =
                            true
                    }
                },


                // ========================================
                // 밝은길 / AI 추천경로
                // ========================================

                onBrightRouteClick = {

                    // 아직 실제 AI 경로 미구현
                    // 추후 "AI_RECOMMENDED" 등으로 연결 예정
                },


                // ========================================
                // 대로변 우선
                // ========================================

                onBroadRouteClick = {

                    selectedRouteMode =
                        "BROAD_FIRST"

                    navController.navigate(
                        "active_route"
                    ) {

                        launchSingleTop =
                            true
                    }
                }
            )
        }


        // ========================================
        // 귀가 진행 중
        // ========================================

        composable(
            "active_route"
        ) {

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

                    showEmergencyDialog =
                        true
                },


                onQuackClick = {

                    navController.navigate(
                        "quack"
                    ) {

                        launchSingleTop =
                            true
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

                        popUpTo(
                            "home"
                        ) {

                            inclusive =
                                false
                        }

                        launchSingleTop =
                            true
                    }
                }
            )
        }


        // ========================================
        // 안심친구
        // ========================================

        composable(
            "friend"
        ) {

            FriendScreen(

                onBackClick = {

                    navController.popBackStack()
                },


                onAddFriendClick = {},


                onAddFriendSubmit = { _, _ ->

                },


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

                                popUpTo(
                                    "home"
                                ) {

                                    inclusive =
                                        false
                                }

                                launchSingleTop =
                                    true
                            }
                        }


                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 꽥꽥이
        // ========================================

        composable(
            "quack"
        ) {

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

                                popUpTo(
                                    "home"
                                ) {

                                    inclusive =
                                        false
                                }

                                launchSingleTop =
                                    true
                            }
                        }


                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 사용 리포트
        // ========================================

        composable(
            "report"
        ) {

            ReportScreen(

                onTabSelected = { tab ->

                    when (tab) {


                        "홈" -> {

                            navController.navigate(
                                "home"
                            ) {

                                popUpTo(
                                    "home"
                                ) {

                                    inclusive =
                                        false
                                }

                                launchSingleTop =
                                    true
                            }
                        }


                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 안심지도
        // ========================================

        composable(
            "safe_map"
        ) {

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

                                popUpTo(
                                    "home"
                                ) {

                                    inclusive =
                                        false
                                }

                                launchSingleTop =
                                    true
                            }
                        }


                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop =
                                    true
                            }
                        }
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 더보기
        // ========================================

        composable(
            "more"
        ) {

            MoreScreen(

                onMenuClick = { menu ->

                    when (menu) {
                        // 💡 로그아웃 메뉴 클릭 시 팝업 띄우기
                        "로그아웃" -> {
                            showLogoutDialog = true
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


                onSettingsClick = {

                    // 설정 화면 추후 연결
                },


                onTabSelected = { tab ->

                    when (tab) {


                        "홈" -> {

                            navController.navigate(
                                "home"
                            ) {

                                popUpTo(
                                    "home"
                                ) {

                                    inclusive =
                                        false
                                }

                                launchSingleTop =
                                    true
                            }
                        }


                        "더보기" -> {

                            // 현재 화면
                        }
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 기본 목적지 설정
        // ========================================

        composable(
            "default_destination"
        ) {

            DefaultDestinationScreen(

                memberId =
                    3L,


                onBackClick = {

                    pendingDefaultPlace =
                        null

                    navController.popBackStack()
                },


                // ========================================
                // 새 기본 목적지 등록
                // → 장소 검색부터 시작
                // ========================================

                onSearchPlaceClick = {

                    pendingDefaultPlace =
                        null

                    destinationSearchMode =
                        "DEFAULT_DESTINATION"

                    navController.navigate(
                        "destination_search"
                    )
                },


                // ========================================
                // 검색 후 선택한 실제 장소명
                // ========================================

                selectedPlaceName =
                    pendingDefaultPlace
                        ?.placeName,


                // ========================================
                // 도로명 주소 우선
                // ========================================

                selectedAddress =

                    pendingDefaultPlace
                        ?.roadAddressName
                        ?.takeIf {

                            it.isNotBlank()
                        }

                        ?: pendingDefaultPlace
                            ?.addressName,


                // ========================================
                // 위도
                // ========================================

                selectedLatitude =
                    pendingDefaultPlace
                        ?.latitude
                        ?.toDoubleOrNull(),


                // ========================================
                // 경도
                // ========================================

                selectedLongitude =
                    pendingDefaultPlace
                        ?.longitude
                        ?.toDoubleOrNull(),


                // ========================================
                // 저장 / 취소 후 초기화
                // ========================================

                onDestinationSaved = {

                    pendingDefaultPlace =
                        null
                }
            )
        }


        // ========================================
        // 공지사항
        // ========================================

        composable(
            "notice"
        ) {

            NoticeScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 문의하기
        // ========================================

        composable(
            "inquiry"
        ) {

            InquiryScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 도움말
        // ========================================

        composable(
            "help"
        ) {

            HelpScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 서비스 소개
        // ========================================

        composable(
            "service_intro"
        ) {

            ServiceIntroScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 개인정보처리방침
        // ========================================

        composable(
            "privacy_policy"
        ) {

            PrivacyPolicyScreen(

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 보호자 등록
        // ========================================

        composable(
            "guardian_register"
        ) {

            GuardianRegisterScreen(

                onBackClick = {

                    navController.popBackStack()
                },


                onRegisterClick = { _, _, _ ->

                    // GuardianRegisterScreen 내부에서 API 처리
                },


                onDeleteClick = { _ ->

                    // GuardianRegisterScreen 내부에서 API 처리
                }
            )
        }


        // ========================================
        // 프로필 설정
        // ========================================

        composable(
            "profile_setting"
        ) {

            ProfileSettingScreen(

                memberId =
                    3L,


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
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "로그아웃", fontWeight = FontWeight.Bold) },
            text = { Text(text = "정말 로그아웃 하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // 💡 토큰 삭제
                        tokenManager.clearToken()

                        // 💡 백스택(이전 화면 기록)을 모두 날리고 로그인 화면으로 이동
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("로그아웃", color = androidx.compose.ui.graphics.Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("취소", color = androidx.compose.ui.graphics.Color.Black)
                }
            }
        )
    }


    // ========================================
    // 공용 긴급구조 팝업
    // ========================================

    if (
        showEmergencyDialog
    ) {

        EmergencyDialog(

            onDismiss = {

                showEmergencyDialog =
                    false
            },


            onEmergencyConfirmed = {

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
                                            memberId = 1L,
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