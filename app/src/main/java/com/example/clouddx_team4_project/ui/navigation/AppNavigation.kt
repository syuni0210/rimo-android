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
import com.example.clouddx_team4_project.ui.screens.SafeRouteScreen
import com.example.clouddx_team4_project.ui.screens.SignUpScreen


@Composable
fun AppNavigation() {

    val navController =
        rememberNavController()


    // ========================================
    // 목적지
    // ========================================

    var selectedDestination by remember {
        mutableStateOf<KakaoPlace?>(null)
    }


    // ========================================
    // 선택한 경로
    // ========================================

    var selectedRouteMode by remember {
        mutableStateOf("BROAD_FIRST")
    }


    // ========================================
    // SafeRoute에서 경로 표시 여부
    // ========================================

    var showSelectedRoute by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 긴급구조
    // ========================================

    var showEmergencyDialog by remember {
        mutableStateOf(false)
    }


    NavHost(
        navController =
            navController,

        startDestination =
            "login"
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
                            )
                        }


                        "꽥꽥이" -> {

                            navController.navigate(
                                "quack"
                            )
                        }


                        "사용 리포트" -> {

                            navController.navigate(
                                "report"
                            )
                        }


                        "더보기" -> {

                            navController.navigate(
                                "more"
                            )
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
                            )
                        }


                        "더보기" -> {

                            navController.navigate(
                                "more"
                            )
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


                    // ========================================
                    // 목적지 선택하자마자
                    // 경로 선택 화면으로
                    // ========================================

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
                // 밝은길
                // 아직 준비 중
                // ========================================

                onBrightRouteClick = {

                    // 현재 비활성화
                },


                // ========================================
                // 대로변
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


                // ========================================
                // 뒤로
                // ========================================

                onBackClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 긴급구조
                // ========================================

                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                },


                // ========================================
                // 꽥꽥이
                // ========================================

                onQuackClick = {

                    navController.navigate(
                        "quack"
                    ) {

                        launchSingleTop =
                            true
                    }
                },


                // ========================================
                // 안내 종료
                // ========================================

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

                        "홈" ->
                            navController.navigate(
                                "home"
                            )

                        "더보기" ->
                            navController.navigate(
                                "more"
                            )
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

                        "홈" ->
                            navController.navigate(
                                "home"
                            )

                        "더보기" ->
                            navController.navigate(
                                "more"
                            )
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 리포트
        // ========================================

        composable(
            "report"
        ) {

            ReportScreen(

                onTabSelected = { tab ->

                    when (tab) {

                        "홈" ->
                            navController.navigate(
                                "home"
                            )

                        "더보기" ->
                            navController.navigate(
                                "more"
                            )
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
                    }
                },


                onSettingsClick = {},


                onTabSelected = { tab ->

                    if (
                        tab == "홈"
                    ) {

                        navController.navigate(
                            "home"
                        )
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog =
                        true
                }
            )
        }


        // ========================================
        // 보호자 관리
        // ========================================

        composable(
            "guardian_register"
        ) {

            GuardianRegisterScreen(

                // ========================================
                // 뒤로가기
                // ========================================

                onBackClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 보호자 등록
                //
                // 현재는 UI만 연결
                //
                // 다음 단계에서
                // POST /api/guardians 연결
                // ========================================

                onRegisterClick = {
                        name,
                        phone,
                        relation ->


                    // TODO:
                    // POST /api/guardians
                    //
                    // 여기에서 API 호출 예정
                },


                // ========================================
                // 보호자 삭제
                //
                // DELETE API 구현되면 연결
                // ========================================

                onDeleteClick = { guardianId ->


                    // TODO:
                    // DELETE /api/guardians/{guardianId}
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

                initialName =
                    "이지연",

                userId =
                    "jiyeon123",

                initialEmail =
                    "jiyeon@example.com",


                onBackClick = {

                    navController.popBackStack()
                },


                onSaveClick = {
                        _,
                        _ ->

                    navController.popBackStack()
                }
            )
        }
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

                // 나중에 실제 긴급신고 API
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