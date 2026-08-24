package com.example.clouddx_team4_project.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.clouddx_team4_project.ui.components.EmergencyDialog
import com.example.clouddx_team4_project.ui.screens.FriendScreen
import com.example.clouddx_team4_project.ui.screens.GuardianRegisterScreen
import com.example.clouddx_team4_project.ui.screens.HomeScreen
import com.example.clouddx_team4_project.ui.screens.LoginScreen
import com.example.clouddx_team4_project.ui.screens.MoreScreen
import com.example.clouddx_team4_project.ui.screens.QuackScreen
import com.example.clouddx_team4_project.ui.screens.ReportScreen
import com.example.clouddx_team4_project.ui.screens.SafeRouteScreen
import com.example.clouddx_team4_project.ui.screens.SignUpScreen


@Composable
fun AppNavigation() {

    val navController = rememberNavController()

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

        // 지금은 UI 개발 중이므로 홈부터 시작
        // 나중에 로그인 완성되면 "login"으로 변경
        startDestination = "home"
    ) {


        // ========================================
        // 1. 로그인
        // ========================================

        composable("login") {

            LoginScreen(

                onLoginClick = { id, password ->

                    // ========================================
                    // 현재는 백엔드 연결 전
                    // 로그인 버튼 누르면 홈 이동
                    // ========================================

                    navController.navigate("home") {

                        popUpTo("login") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },


                onSignUpClick = {

                    navController.navigate("signup")
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

                            // 나중에 별도 지도 화면 만들면 연결
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
                // 뒤로가기
                // ========================================

                onBackClick = {

                    navController.popBackStack()
                },


                // ========================================
                // 출발지 클릭
                // ========================================

                onStartSearchClick = {

                    // 현재 위치를 기본 출발지로 사용 예정
                    // 나중에 출발지 검색 화면을 만들면 연결
                },


                // ========================================
                // 목적지 검색
                // ========================================

                onDestinationSearchClick = {

                    // 나중에 장소 검색 화면 연결
                    //
                    // 예:
                    // navController.navigate("destination_search")
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
        // 5. 안심친구
        // ========================================

        composable("friend") {

            FriendScreen(

                onBackClick = {

                    navController.popBackStack()
                },


                onAddFriendClick = {

                    // 나중에 친구 추가 화면 연결
                },


                onAcceptRequest = { name ->

                    // 나중에 Spring Boot API 연결
                },


                onRejectRequest = { name ->

                    // 나중에 Spring Boot API 연결
                },


                onDeleteFriend = { name ->

                    // 나중에 Spring Boot API 연결
                },


                onLocationClick = { name ->

                    // 나중에 친구 위치 지도 화면 연결
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
        // 6. 꽥꽥이
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
        // 7. 사용 리포트
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
        // 8. 더보기
        // ========================================

        composable("more") {

            MoreScreen(

                onMenuClick = { menu ->

                    when (menu) {


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

                            // 나중에 연결
                        }


                        // ========================================
                        // 도움말
                        // ========================================

                        "도움말" -> {

                            // 나중에 연결
                        }


                        // ========================================
                        // 서비스 소개
                        // ========================================

                        "서비스 소개" -> {

                            // 나중에 연결
                        }


                        // ========================================
                        // 개인정보처리방침
                        // ========================================

                        "개인정보처리방침" -> {

                            // 나중에 연결
                        }
                    }
                },


                // ========================================
                // 우측 상단 설정
                // ========================================

                onSettingsClick = {

                    // 나중에 설정 화면 연결
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

                            // 현재 화면이 더보기이므로
                            // 아무 동작 없음
                        }
                    }
                },


                onEmergencyClick = {

                    showEmergencyDialog = true
                }
            )
        }


        // ========================================
        // 9. 보호자 등록
        // ========================================

        composable("guardian_register") {

            GuardianRegisterScreen(

                onBackClick = {

                    navController.popBackStack()
                },


                onRegisterClick = {
                        name,
                        phone,
                        emergencyMessage,
                        otherMessage ->


                    // ========================================
                    // TODO
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
    }


    // ========================================
    // 10. 공용 긴급구조 Dialog
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