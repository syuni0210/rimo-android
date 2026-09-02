package com.example.clouddx_team4_project.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clouddx_team4_project.data.KakaoPlace
import com.example.clouddx_team4_project.ui.components.EmergencyDialog
import com.example.clouddx_team4_project.ui.screens.ActiveRouteScreen
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
import com.example.clouddx_team4_project.ui.screens.DefaultDestinationScreen

@Composable
fun AppNavigation() {

    val context = LocalContext.current

    // 💡 토큰 매니저 초기화 및 유효성 검사
    val tokenManager = remember { TokenManager(context) }
    val startDest = if (tokenManager.hasValidToken()) "home" else "login"

    // 💡 로그아웃 팝업 상태 변수 추가
    var showLogoutDialog by remember { mutableStateOf(false) }

    val navController =
        rememberNavController()


    // ========================================
    // 목적지
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

                onLoginClick = {
                        _,
                        _ ->

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
                    // 현재 위치 고정
                },

                onDestinationSearchClick = {

                    showSelectedRoute =
                        false
                    navController.navigate(
                        "destination_search"
                    )
                },

                onRouteSearchClick = {

                    if (
                        selectedDestination != null
                    ) {

                        navController.navigate(
                            "route_select"
                        )
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

                onBrightRouteClick = {

                    // 현재 비활성화
                },

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


                onAddFriendSubmit = {
                        _,
                        _ -> },

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
                onBackClick = {
                    navController.popBackStack()
                },
                onSearchPlaceClick = {
                    // 장소 검색 화면으로 이동
                    navController.navigate("destination_search")
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
        composable("guardian_register") {
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

                // 나중에 실제 긴급신고 API 연결
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