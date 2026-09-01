package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar
import com.example.clouddx_team4_project.ui.theme.ResponsiveDimens
import com.example.clouddx_team4_project.ui.theme.rememberResponsiveDimens
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.platform.LocalContext
import com.example.clouddx_team4_project.data.TokenManager

// ========================================
// 색상
// ========================================

private val AnOnBlue =
    Color(0xFF6A92FE)

private val ScreenBackground =
    Color(0xFFF4F5F8)

private val CardBackground =
    Color.White

private val IconGray =
    Color(0xFFB8B8B8)

private val TextGray =
    Color(0xFF8B8B8B)


// ========================================
// 메뉴 데이터
// ========================================

data class MoreMenuItem(
    val title: String,
    val icon: ImageVector
)


// ========================================
// 더보기 화면
// ========================================

@Composable
fun MoreScreen(

    // 로그인 완성 전 테스트 회원 ID

    onMenuClick: (String) -> Unit = {},

    onSettingsClick: () -> Unit = {},

    onTabSelected: (String) -> Unit = {},

    onEmergencyClick: () -> Unit = {}

) {

    val dimens =
        rememberResponsiveDimens()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }


    // ========================================
    // 사용자 프로필 정보
    // ========================================

    var userName by remember {
        mutableStateOf("")
    }


    var loginId by remember {
        mutableStateOf("")
    }


    var profileLoadFailed by remember {
        mutableStateOf(false)
    }


    // ========================================
    // DB에서 프로필 조회
    // ========================================

    LaunchedEffect(
        Unit
    ) {

        try {
            val currentMemberId = tokenManager.getMemberId()
            android.util.Log.d("DEBUG_ID", "저장된 내 memberId: $currentMemberId")
            if (currentMemberId == null) {
                userName = "로그인 필요"
                loginId = "정보 없음"
                profileLoadFailed = true
                return@LaunchedEffect
            }


            val profile =
                RetrofitClient
                    .memberApi
                    .getProfile(
                        currentMemberId

                    )


            userName =
                profile.memberName


            loginId =
                profile.loginId


            profileLoadFailed =
                false


        } catch (
            e: Exception
        ) {
            android.util.Log.e("DEBUG_ID", "프로필 조회 중 에러 발생", e)
            e.printStackTrace()


            userName =
                "사용자"


            loginId =
                "정보를 불러올 수 없습니다"


            profileLoadFailed =
                true
        }
    }


    // ========================================
    // 첫 번째 메뉴 그룹
    // ========================================

    val firstMenuGroup =
        listOf(

            MoreMenuItem(
                title = "프로필 설정",
                icon = Icons.Filled.AccountCircle
            ),

            MoreMenuItem(
                title = "기본 목적지 설정",
                icon = Icons.Filled.LocationOn
            ),

            MoreMenuItem(
                title = "보호자 등록",
                icon = Icons.Filled.Person
            )
        )


    // ========================================
    // 두 번째 메뉴 그룹
    // ========================================

    val secondMenuGroup =
        listOf(

            MoreMenuItem(
                title = "공지사항",
                icon = Icons.Filled.Notifications
            ),

            MoreMenuItem(
                title = "문의하기",
                icon = Icons.Filled.Chat
            ),

            MoreMenuItem(
                title = "도움말",
                icon = Icons.Filled.Help
            ),

            MoreMenuItem(
                title = "서비스 소개",
                icon = Icons.Filled.Info
            ),

            MoreMenuItem(
                title = "개인정보처리방침",
                icon = Icons.Filled.Description
            ),

            MoreMenuItem(
                title = "로그아웃",
                icon = Icons.Filled.Logout
            )
        )


    // ========================================
    // 전체 화면
    // ========================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ScreenBackground
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    bottom = 100.dp
                )
        ) {


            // ========================================
            // 1. 상단 Rimo / 설정
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal =
                            dimens.screenHorizontalPadding,

                        vertical =
                            dimens.screenVerticalPadding
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                // ========================================
                // Rimo 로고
                // ========================================

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.Security,

                        contentDescription =
                            "Rimo",

                        tint =
                            AnOnBlue,

                        modifier =
                            Modifier.size(
                                dimens.homeLogoSize
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                dimens.smallSpacing
                            )
                    )


                    Text(
                        text =
                            "Rimo",

                        fontSize =
                            dimens.titleSize,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            AnOnBlue
                    )
                }


                Spacer(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                // ========================================
                // 설정 버튼
                // ========================================

                Icon(
                    imageVector =
                        Icons.Filled.Settings,

                    contentDescription =
                        "설정",

                    tint =
                        Color(
                            0xFF969696
                        ),

                    modifier = Modifier
                        .size(
                            dimens.mediumIconSize
                        )
                        .clickable {

                            onSettingsClick()
                        }
                )
            }


            // ========================================
            // 2. 사용자 프로필 영역
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start =
                            dimens.screenHorizontalPadding,

                        end =
                            dimens.screenHorizontalPadding,

                        top =
                            dimens.smallSpacing,

                        bottom =
                            dimens.largeSpacing
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                // ========================================
                // 프로필 아이콘
                // ========================================

                Box(
                    modifier = Modifier
                        .size(
                            dimens.largeIconSize + 30.dp
                        )
                        .background(
                            color =
                                Color(
                                    0xFFE8EEFF
                                ),

                            shape =
                                RoundedCornerShape(
                                    dimens.cardRadius
                                )
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.AccountCircle,

                        contentDescription =
                            "프로필",

                        tint =
                            AnOnBlue,

                        modifier =
                            Modifier.size(
                                dimens.largeIconSize + 10.dp
                            )
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            dimens.mediumSpacing
                        )
                )


                // ========================================
                // 사용자 정보
                // ========================================

                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            if (
                                userName.isBlank()
                            ) {

                                "불러오는 중..."

                            } else {

                                "${userName}님"
                            },

                        fontSize =
                            dimens.titleSize,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(
                                0xFF222222
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                dimens.smallSpacing
                            )
                    )


                    Text(
                        text =
                            if (
                                loginId.isBlank()
                            ) {

                                "사용자 정보 확인 중"

                            } else {

                                loginId
                            },

                        fontSize =
                            dimens.bodySize,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            if (
                                profileLoadFailed
                            ) {

                                Color(
                                    0xFFE57373
                                )

                            } else {

                                TextGray
                            }
                    )
                }
            }


            // ========================================
            // 3. 기본 설정 메뉴
            // ========================================

            MenuGroupCard(

                menuItems =
                    firstMenuGroup,

                dimens =
                    dimens,

                modifier =
                    Modifier.padding(
                        horizontal =
                            dimens.screenHorizontalPadding
                    ),

                onMenuClick =
                    onMenuClick
            )


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.largeSpacing
                    )
            )


            // ========================================
            // 4. 안내 메뉴
            // ========================================

            MenuGroupCard(

                menuItems =
                    secondMenuGroup,

                dimens =
                    dimens,

                modifier =
                    Modifier.padding(
                        horizontal =
                            dimens.screenHorizontalPadding
                    ),

                onMenuClick =
                    onMenuClick
            )


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.largeSpacing
                    )
            )
        }


        // ========================================
        // 하단 네비게이션
        // ========================================

        AnOnBottomBar(

            selectedTab =
                "더보기",

            onTabSelected =
                onTabSelected,

            onEmergencyClick =
                onEmergencyClick,

            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                )
        )
    }
}


// ========================================
// 메뉴 그룹 카드
// ========================================

@Composable
private fun MenuGroupCard(

    menuItems:
    List<MoreMenuItem>,

    dimens:
    ResponsiveDimens,

    modifier:
    Modifier = Modifier,

    onMenuClick:
        (String) -> Unit

) {

    Column(
        modifier = modifier
            .fillMaxWidth()

            .clip(
                RoundedCornerShape(
                    dimens.cardRadius
                )
            )

            .background(
                CardBackground
            )

            .padding(
                vertical =
                    dimens.smallSpacing
            )
    ) {

        menuItems.forEach { item ->

            MoreMenuRow(

                item =
                    item,

                dimens =
                    dimens,

                onClick = {

                    onMenuClick(
                        item.title
                    )
                }
            )
        }
    }
}


// ========================================
// 메뉴 한 줄
// ========================================

@Composable
private fun MoreMenuRow(

    item:
    MoreMenuItem,

    dimens:
    ResponsiveDimens,

    onClick:
        () -> Unit

) {

    Row(
        modifier = Modifier
            .fillMaxWidth()

            .height(
                dimens.largeIconSize + 34.dp
            )

            .clickable {

                onClick()
            }

            .padding(
                horizontal =
                    dimens.cardPadding
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        // ========================================
        // 메뉴 아이콘
        // ========================================

        Box(
            modifier =
                Modifier.size(
                    dimens.largeIconSize
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    item.icon,

                contentDescription =
                    item.title,

                tint =
                    IconGray,

                modifier =
                    Modifier.size(
                        dimens.mediumIconSize
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.width(
                    dimens.mediumSpacing
                )
        )


        // ========================================
        // 메뉴 이름
        // ========================================

        Text(
            text =
                item.title,

            fontSize =
                dimens.bodySize,

            fontWeight =
                FontWeight.Medium,

            color =
                Color(
                    0xFF222222
                ),

            modifier =
                Modifier.weight(
                    1f
                )
        )


        // ========================================
        // 오른쪽 화살표
        // ========================================

        Icon(
            imageVector =
                Icons.Filled.ChevronRight,

            contentDescription =
                null,

            tint =
                Color(
                    0xFFC4C4C4
                ),

            modifier =
                Modifier.size(
                    dimens.smallIconSize
                )
        )
    }
}


// ========================================
// Preview
// ========================================

@Preview(
    showBackground = true
)
@Composable
fun MoreScreenPreview() {

    MoreScreen()
}