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


@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    var selectedDestination by remember { mutableStateOf<KakaoPlace?>(null) }
    var selectedRouteMode by remember { mutableStateOf("BROAD_FIRST") }
    var showSelectedRoute by remember { mutableStateOf(false) }
    var showEmergencyDialog by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginClick = { _, _ ->
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpComplete = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                onMenuClick = { menu ->
                    when (menu) {
                        "안심경로" -> {
                            showSelectedRoute = false
                            navController.navigate("safe_route") { launchSingleTop = true }
                        }
                        "안심친구" -> {
                            navController.navigate("friend") { launchSingleTop = true }
                        }
                        "꽥꽥이" -> {
                            navController.navigate("quack") { launchSingleTop = true }
                        }
                        "안심지도" -> {
                            navController.navigate("safe_map") { launchSingleTop = true }
                        }
                        "사용 리포트" -> {
                            navController.navigate("report") { launchSingleTop = true }
                        }
                        "더보기" -> {
                            navController.navigate("more") { launchSingleTop = true }
                        }
                    }
                },
                onEmergencyClick = { showEmergencyDialog = true }
            )
        }

        composable("safe_route") {
            SafeRouteScreen(
                destinationName = selectedDestination?.placeName ?: "",
                destinationLatitude = selectedDestination?.latitude?.toDoubleOrNull(),
                destinationLongitude = selectedDestination?.longitude?.toDoubleOrNull(),
                showSelectedRoute = showSelectedRoute,
                selectedRouteMode = selectedRouteMode,
                onBackClick = { navController.popBackStack() },
                onStartSearchClick = { },
                onDestinationSearchClick = {
                    showSelectedRoute = false
                    navController.navigate("destination_search")
                },
                onRouteSearchClick = {
                    if (selectedDestination != null) {
                        navController.navigate("route_select")
                    }
                },
                onMapDestinationSelected = { latitude, longitude ->
                    selectedDestination = KakaoPlace(
                        id = "manual_location",
                        placeName = "선택한 위치",
                        addressName = "",
                        roadAddressName = "",
                        longitude = longitude.toString(),
                        latitude = latitude.toString()
                    )
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

        composable("destination_search") {
            DestinationSearchScreen(
                onBackClick = { navController.popBackStack() },
                onPlaceSelected = { place ->
                    selectedDestination = place
                    showSelectedRoute = false
                    navController.navigate("route_select") {
                        popUpTo("destination_search") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("route_select") {
            RouteSelectScreen(
                startName = "현재 위치",
                destinationName = selectedDestination?.placeName ?: "목적지",
                destinationLatitude = selectedDestination?.latitude?.toDoubleOrNull(),
                destinationLongitude = selectedDestination?.longitude?.toDoubleOrNull(),
                onBackClick = { navController.popBackStack() },
                onFastRouteClick = {
                    selectedRouteMode = "SHORTEST"
                    navController.navigate("active_route") { launchSingleTop = true }
                },
                onBrightRouteClick = { },
                onBroadRouteClick = {
                    selectedRouteMode = "BROAD_FIRST"
                    navController.navigate("active_route") { launchSingleTop = true }
                }
            )
        }

        composable("active_route") {
            ActiveRouteScreen(
                destinationName = selectedDestination?.placeName ?: "목적지",
                destinationLatitude = selectedDestination?.latitude?.toDoubleOrNull(),
                destinationLongitude = selectedDestination?.longitude?.toDoubleOrNull(),
                routeMode = selectedRouteMode,
                onBackClick = { navController.popBackStack() },
                onEmergencyClick = { showEmergencyDialog = true },
                onQuackClick = {
                    navController.navigate("quack") { launchSingleTop = true }
                },
                onFinishClick = {
                    showSelectedRoute = false
                    selectedDestination = null
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("friend") {
            FriendScreen(
                onBackClick = { navController.popBackStack() },
                onAddFriendClick = {},
                onAddFriendSubmit = { _, _ -> },
                onAcceptRequest = {},
                onRejectRequest = {},
                onDeleteFriend = {},
                onLocationClick = {},
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

        composable("quack") {
            QuackScreen(
                onBackClick = { navController.popBackStack() },
                onStopClick = { navController.popBackStack() },
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

        composable("report") {
            ReportScreen(
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

        composable("safe_map") {
            SafeMapScreen(
                currentLocationText = "현재 위치 확인 중",
                onBackClick = { navController.popBackStack() },
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

        composable("more") {
            MoreScreen(
                onMenuClick = { menu ->
                    when (menu) {
                        "프로필 설정" -> navController.navigate("profile_setting")
                        "보호자 등록" -> navController.navigate("guardian_register")
                        "공지사항" -> navController.navigate("notice")
                        "문의하기" -> navController.navigate("inquiry")
                        "도움말" -> navController.navigate("help")
                        "서비스 소개" -> navController.navigate("service_intro")
                        "개인정보처리방침" -> navController.navigate("privacy_policy")
                    }
                },
                onSettingsClick = { },
                onTabSelected = { tab ->
                    when (tab) {
                        "홈" -> {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                        "더보기" -> { }
                    }
                },
                onEmergencyClick = { showEmergencyDialog = true }
            )
        }

        composable("notice") {
            NoticeScreen(onBackClick = { navController.popBackStack() })
        }

        composable("inquiry") {
            InquiryScreen(onBackClick = { navController.popBackStack() })
        }

        composable("help") {
            HelpScreen(onBackClick = { navController.popBackStack() })
        }

        composable("service_intro") {
            ServiceIntroScreen(onBackClick = { navController.popBackStack() })
        }

        composable("privacy_policy") {
            PrivacyPolicyScreen(onBackClick = { navController.popBackStack() })
        }

        // ========================================
        // 보호자 관리
        // 실제 API 호출(GuardianApiClient)은
        // GuardianRegisterScreen 내부에서 직접 처리하고 있어
        // 여기 콜백은 비워둡니다.
        // ========================================

        composable("guardian_register") {
            GuardianRegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { name, phone, relation -> },
                onDeleteClick = { guardianId -> }
            )
        }

        composable("profile_setting") {
            ProfileSettingScreen(
                memberId = 3L,
                onBackClick = { navController.popBackStack() }
            )
        }
    }

    if (showEmergencyDialog) {
        EmergencyDialog(
            onDismiss = { showEmergencyDialog = false },
            onEmergencyConfirmed = { },
            onQuackClick = {
                showEmergencyDialog = false
                navController.navigate("quack") { launchSingleTop = true }
            }
        )
    }
}