package com.example.clouddx_team4_project.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.clouddx_team4_project.data.KakaoPlace

import com.example.clouddx_team4_project.ui.components.EmergencyDialog
import com.example.clouddx_team4_project.ui.screens.DestinationSearchScreen
import com.example.clouddx_team4_project.ui.screens.FriendScreen
import com.example.clouddx_team4_project.ui.screens.GuardianRegisterScreen
import com.example.clouddx_team4_project.ui.screens.HomeScreen
import com.example.clouddx_team4_project.ui.screens.LoginScreen
import com.example.clouddx_team4_project.ui.screens.MoreScreen
import com.example.clouddx_team4_project.ui.screens.ProfileSettingScreen
import com.example.clouddx_team4_project.ui.screens.QuackScreen
import com.example.clouddx_team4_project.ui.screens.ReportScreen
import com.example.clouddx_team4_project.ui.screens.SafeRouteScreen
import com.example.clouddx_team4_project.ui.screens.SignUpScreen


@Composable
fun AppNavigation() {

    val navController =
        rememberNavController()


    // ========================================
    // 선택한 목적지
    // ========================================

    var selectedDestination by remember {
        mutableStateOf<KakaoPlace?>(null)
    }


    // ========================================
    // 긴급구조 다이얼로그
    // ========================================

    var showEmergencyDialog by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 전체 네비게이션
    // ========================================

    NavHost(
        navController = navController,

        // 로그인 화면부터 시작
        startDestination = "login"
    ) {


        // ========================================
        // 1. 로그인
        // ========================================

        composable("login") {

            LoginScreen(

                onLoginClick = { id, password ->

                    // ========================================
                    // 현재는 로그인 성공 시 홈 이동
                    // 나중에 Spring Boot 로그인 API 연결
                    // ========================================

                    navController.navigate("home") {

                        popUpTo("login") {
                            inclusive = true
                        }

                        launchSingleTop = true
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
        // 2. 회원가입
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
        // 3. 홈
        // ========================================

        composable("home") {

            HomeScreen(

                onMenuClick = { menu ->

                    when (menu) {


                        // ========================================
                        // 안심경로
                        // ========================================

                        "안심경로" -> {

                            navController.navigate(
                                "safe_route"
                            ) {

                                launchSingleTop = true
                            }
                        }


                        // ========================================
                        // 안심친구
                        // ========================================

                        "안심친구" -> {

                            navController.navigate(
                                "friend"
                            ) {

                                launchSingleTop = true
                            }
                        }


                        // ========================================
                        // 꽥꽥이
                        // ========================================

                        "꽥꽥이" -> {

                            navController.navigate(
                                "quack"
                            ) {

                                launchSingleTop = true
                            }
                        }


                        // ========================================
                        // 안심지도
                        // ========================================

                        "안심지도" -> {

                            // 나중에 별도 지도 화면 연결
                        }


                        // ========================================
                        // 사용 리포트
                        // ========================================

                        "사용 리포트" -> {

                            navController.navigate(
                                "report"
                            ) {

                                launchSingleTop = true
                            }
                        }


                        // ========================================
                        // 더보기
                        // ========================================

                        "더보기" -> {

                            navController.navigate(
                                "more"
                            ) {

                                launchSingleTop = true
                            }
                        }
                    }
                },


                // ========================================
                // 긴급구조
                // ========================================

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 4. 안심경로
        // ========================================

        composable("safe_route") {

            SafeRouteScreen(

                // ========================================
                // 선택된 목적지
                // ========================================

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


                // ========================================
                // 뒤로가기
                // ========================================

                onBackClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 출발지 클릭
                // ========================================

                onStartSearchClick = {

                    // 현재 위치를 기본 출발지로 사용
                    // 나중에 출발지 검색 연결
                },


                // ========================================
                // 목적지 검색
                // ========================================

                onDestinationSearchClick = {

                    navController.navigate(
                        "destination_search"
                    )
                },


                // ========================================
                // 하단 네비게이션
                // ========================================

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


                // ========================================
                // 긴급구조
                // ========================================

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 5. 목적지 검색
        // ========================================

        composable(
            "destination_search"
        ) {

            DestinationSearchScreen(

                // ========================================
                // 뒤로가기
                // ========================================

                onBackClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 장소 선택
                // ========================================

                onPlaceSelected = { place ->

                    selectedDestination = place

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 6. 안심친구
        // ========================================

        composable("friend") {

            FriendScreen(

                // ========================================
                // 뒤로가기
                // ========================================

                onBackClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 기존 친구 추가 콜백
                // ========================================

                onAddFriendClick = {

                    // 팝업에서 추가 완료 후 호출됨
                },


                // ========================================
                // 친구 추가
                // 이름 + 아이디
                // ========================================

                onAddFriendSubmit = { name, id ->

                    // ========================================
                    // TODO
                    // 나중에 Spring Boot 친구 추가 API 연결
                    //
                    // 예:
                    // POST /api/friends
                    //
                    // name
                    // id
                    // ========================================

                    println("친구 추가")
                    println("이름 : $name")
                    println("아이디 : $id")
                },


                // ========================================
                // 친구 요청 수락
                // ========================================

                onAcceptRequest = { name ->

                    // 나중에 Spring Boot API 연결
                },


                // ========================================
                // 친구 요청 거절
                // ========================================

                onRejectRequest = { name ->

                    // 나중에 Spring Boot API 연결
                },


                // ========================================
                // 친구 삭제
                // ========================================

                onDeleteFriend = { name ->

                    // 나중에 Spring Boot API 연결
                },


                // ========================================
                // 친구 위치
                // ========================================

                onLocationClick = { name ->

                    // 나중에 친구 위치 지도 연결
                },


                // ========================================
                // 하단 네비게이션
                // ========================================

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


                // ========================================
                // 긴급구조
                // ========================================

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 7. 꽥꽥이
        // ========================================

        composable("quack") {

            QuackScreen(

                // ========================================
                // 뒤로가기
                // ========================================

                onBackClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 중지
                // ========================================

                onStopClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 하단 네비게이션
                // ========================================

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


                // ========================================
                // 긴급구조
                // ========================================

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 8. 사용 리포트
        // ========================================

        composable("report") {

            ReportScreen(

                // ========================================
                // 하단 네비게이션
                // ========================================

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


                // ========================================
                // 긴급구조
                // ========================================

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 9. 더보기
        // ========================================

        composable("more") {

            MoreScreen(

                // ========================================
                // 더보기 메뉴 클릭
                // ========================================

                onMenuClick = { menu ->

                    when (menu) {


                        // ========================================
                        // 프로필 설정
                        // ========================================

                        "프로필 설정" -> {

                            navController.navigate(
                                "profile_setting"
                            ) {

                                launchSingleTop = true
                            }
                        }


                        // ========================================
                        // 기본 목적지 설정
                        // ========================================

                        "기본 목적지 설정" -> {

                            // 나중에 화면 연결
                        }


                        // ========================================
                        // 보호자 등록
                        // ========================================

                        "보호자 등록" -> {

                            navController.navigate(
                                "guardian_register"
                            ) {

                                launchSingleTop = true
                            }
                        }


                        // ========================================
                        // 공지사항 및 문의하기
                        // ========================================

                        "공지사항 및 문의하기" -> {

                            // 나중에 화면 연결
                        }


                        // ========================================
                        // 도움말
                        // ========================================

                        "도움말" -> {

                            // 나중에 화면 연결
                        }


                        // ========================================
                        // 서비스 소개
                        // ========================================

                        "서비스 소개" -> {

                            // 나중에 화면 연결
                        }


                        // ========================================
                        // 개인정보처리방침
                        // ========================================

                        "개인정보처리방침" -> {

                            // 나중에 화면 연결
                        }
                    }
                },


                // ========================================
                // 우측 상단 설정
                // ========================================

                onSettingsClick = {

                    // 현재는 별도 설정 화면 없음
                },


                // ========================================
                // 하단 네비게이션
                // ========================================

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

                            // 현재 더보기 화면
                            // 아무 동작 없음
                        }
                    }
                },


                // ========================================
                // 긴급구조
                // ========================================

                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 10. 보호자 등록
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
                // ========================================

                onRegisterClick = {
                        name,
                        phone,
                        emergencyMessage,
                        otherMessage ->


                    // ========================================
                    // TODO
                    //
                    // 나중에 Spring Boot 보호자 등록 API
                    //
                    // POST /api/guardians
                    //
                    // 현재는 등록하면 더보기로 복귀
                    // ========================================

                    navController.popBackStack()
                }
            )
        }


        // ========================================
        // 11. 프로필 설정
        // ========================================

        composable(
            "profile_setting"
        ) {

            ProfileSettingScreen(

                // ========================================
                // 현재는 UI 확인용 사용자 정보
                // 나중에 로그인 사용자 정보로 변경
                // ========================================

                initialName =
                    "이지연",

                userId =
                    "jiyeon123",

                initialEmail =
                    "jiyeon@example.com",


                // ========================================
                // 뒤로가기
                // ========================================

                onBackClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 프로필 저장
                // ========================================

                onSaveClick = { name, email ->

                    // ========================================
                    // TODO
                    //
                    // 나중에 Spring Boot
                    // 프로필 수정 API 연결
                    //
                    // 예:
                    // PUT /api/users/profile
                    //
                    // name
                    // email
                    // ========================================

                    println("프로필 수정")
                    println("이름 : $name")
                    println("이메일 : $email")


                    // ========================================
                    // 저장 후 더보기 화면으로 복귀
                    // ========================================

                    navController.popBackStack()
                }
            )
        }
    }


    // ========================================
    // 12. 공용 긴급구조 Dialog
    // ========================================

    if (showEmergencyDialog) {

        EmergencyDialog(

            // ========================================
            // 취소
            // ========================================

            onDismiss = {

                showEmergencyDialog = false
            },


            // ========================================
            // 긴급신고 확정
            // ========================================

            onEmergencyConfirmed = {

                // ========================================
                // TODO
                //
                // 나중에:
                //
                // Android
                // ↓
                // Spring Boot
                // ↓
                // SQS
                // ↓
                // Emergency Worker
                // ↓
                // 보호자 문자
                // ========================================
            },


            // ========================================
            // 꽥꽥이 사용
            // ========================================

            onQuackClick = {

                showEmergencyDialog = false


                navController.navigate(
                    "quack"
                ) {

                    launchSingleTop = true
                }
            }
        )
    }
}