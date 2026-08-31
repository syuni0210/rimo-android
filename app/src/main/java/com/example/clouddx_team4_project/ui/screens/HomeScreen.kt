package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.data.TokenManager
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar
import com.example.clouddx_team4_project.ui.theme.rememberResponsiveDimens
import kotlinx.coroutines.delay


private val MainBlue =
    Color(0xFF6A92FE)

private val BgColor =
    Color(0xFFF7F8FC)

private val TextBlack =
    Color(0xFF222222)

private val TextGray =
    Color(0xFF8D8D8D)

private val BorderGray =
    Color(0xFFE9ECF3)


data class HomeServiceItem(
    val title: String,
    val iconTint: Color
)


// ========================================
// 홈 화면
// ========================================

@Composable
fun HomeScreen(

    // 로그인 기능 완성 전 테스트 회원

    onMenuClick: (String) -> Unit = {},

    onEmergencyClick: () -> Unit = {}

) {

    val dimens =
        rememberResponsiveDimens()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }


    // ========================================
    // 사용자 이름
    // ========================================

    var userName by remember {
        mutableStateOf("")
    }


    var profileLoadFailed by remember {
        mutableStateOf(false)
    }


    // ========================================
    // DB에서 사용자 프로필 조회
    // ========================================

    LaunchedEffect(
        Unit
    ) {

        try {

            val currentMemberId = tokenManager.getMemberId()
            android.util.Log.d("DEBUG_ID", "저장된 내 memberId: $currentMemberId")
            if (currentMemberId == null) {
                userName = "로그인 필요"
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


            profileLoadFailed =
                false


        } catch (
            e: Exception
        ) {
            android.util.Log.e("PROFILE_ERROR", "프로필 통신 실패 원인 : ${e.message}", e)

            userName =
                "사용자"


            profileLoadFailed =
                true
        }
    }


    // ========================================
    // 주요 서비스
    // ========================================

    val serviceItems =
        listOf(

            HomeServiceItem(
                "안심경로",
                Color(0xFF5E86F7)
            ),

            HomeServiceItem(
                "안심친구",
                Color(0xFFFFA24B)
            ),

            HomeServiceItem(
                "꽥꽥이",
                Color(0xFFFF6B9D)
            ),

            HomeServiceItem(
                "안심지도",
                Color(0xFFF1B93B)
            ),

            HomeServiceItem(
                "사용 리포트",
                Color(0xFF9A82F8)
            )
        )


    val bannerImages =
        remember {

            listOf(
                R.drawable.home_banner_1,
                R.drawable.home_banner_2,
                R.drawable.home_banner_3
            )
        }


    // ========================================
    // 전체 화면
    // ========================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                BgColor
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
            // 상단 Rimo / 알림
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
                            MainBlue,

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
                            MainBlue
                    )
                }


                Spacer(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                Icon(
                    imageVector =
                        Icons.Filled.NotificationsNone,

                    contentDescription =
                        "알림",

                    tint =
                        Color(
                            0xFF999999
                        ),

                    modifier =
                        Modifier.size(
                            dimens.homeNotificationSize
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.smallSpacing
                    )
            )


            // ========================================
            // 사용자 인사 카드
            // 위치 표시 제거
            // ========================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal =
                            dimens.screenHorizontalPadding
                    ),

                shape =
                    RoundedCornerShape(
                        dimens.cardRadius
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation =
                            2.dp
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            dimens.cardPadding
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
                                dimens.homeProfileIconSize
                            )
                            .clip(
                                CircleShape
                            )
                            .background(
                                Color(
                                    0xFFEAF0FF
                                )
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Filled.Person,

                            contentDescription =
                                "프로필",

                            tint =
                                MainBlue,

                            modifier =
                                Modifier.size(
                                    dimens.homeProfilePersonIconSize
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
                    // 사용자 이름
                    // ========================================

                    Column {

                        Text(
                            text =
                                "안녕하세요!",

                            fontSize =
                                dimens.captionSize,

                            color =
                                TextGray
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
                                    userName.isBlank()
                                ) {

                                    "불러오는 중..."

                                } else {

                                    "${userName}님"
                                },

                            fontSize =
                                dimens.sectionTitleSize,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                if (
                                    profileLoadFailed
                                ) {

                                    TextGray

                                } else {

                                    TextBlack
                                }
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.largeSpacing
                    )
            )


            // ========================================
            // 배너
            // ========================================

            HomeBannerSlider(
                bannerImages
            )


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.largeSpacing
                    )
            )


            // ========================================
            // 주요 서비스 제목
            // ========================================

            Text(
                text =
                    "주요 서비스",

                fontSize =
                    dimens.sectionTitleSize,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextBlack,

                modifier =
                    Modifier.padding(
                        horizontal =
                            dimens.screenHorizontalPadding
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.mediumSpacing
                    )
            )


            // ========================================
            // 첫 번째 서비스 줄
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal =
                            dimens.screenHorizontalPadding
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        dimens.mediumSpacing
                    )
            ) {

                HomeServiceCard(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    title =
                        serviceItems[0].title,

                    iconTint =
                        serviceItems[0].iconTint,

                    iconType =
                        "안심경로"
                ) {

                    onMenuClick(
                        "안심경로"
                    )
                }


                HomeServiceCard(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    title =
                        serviceItems[1].title,

                    iconTint =
                        serviceItems[1].iconTint,

                    iconType =
                        "안심친구"
                ) {

                    onMenuClick(
                        "안심친구"
                    )
                }


                HomeServiceCard(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    title =
                        serviceItems[2].title,

                    iconTint =
                        serviceItems[2].iconTint,

                    iconType =
                        "꽥꽥이"
                ) {

                    onMenuClick(
                        "꽥꽥이"
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.mediumSpacing
                    )
            )


            // ========================================
            // 두 번째 서비스 줄
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal =
                            dimens.screenHorizontalPadding
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        dimens.mediumSpacing
                    )
            ) {

                HomeServiceCard(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    title =
                        serviceItems[3].title,

                    iconTint =
                        serviceItems[3].iconTint,

                    iconType =
                        "안심지도"
                ) {

                    onMenuClick(
                        "안심지도"
                    )
                }


                HomeServiceCard(
                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    title =
                        serviceItems[4].title,

                    iconTint =
                        serviceItems[4].iconTint,

                    iconType =
                        "사용 리포트"
                ) {

                    onMenuClick(
                        "사용 리포트"
                    )
                }


                Spacer(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )
            }


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
                "홈",

            onTabSelected = {

                onMenuClick(
                    it
                )
            },

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
// 홈 배너 슬라이더
// ========================================

@Composable
private fun HomeBannerSlider(
    bannerImages: List<Int>
) {

    val dimens =
        rememberResponsiveDimens()


    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = {
                bannerImages.size
            }
        )


    LaunchedEffect(
        Unit
    ) {

        while (
            true
        ) {

            delay(
                5000L
            )


            if (
                !pagerState.isScrollInProgress
            ) {

                val next =

                    if (
                        pagerState.currentPage ==
                        bannerImages.lastIndex
                    ) {

                        0

                    } else {

                        pagerState.currentPage + 1
                    }


                pagerState.animateScrollToPage(
                    next
                )
            }
        }
    }


    Column {

        HorizontalPager(
            state =
                pagerState,

            modifier = Modifier
                .fillMaxWidth()
                .height(
                    dimens.homeBannerHeight
                )
        ) { page ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal =
                            dimens.screenHorizontalPadding
                    )
                    .clip(
                        RoundedCornerShape(
                            dimens.cardRadius
                        )
                    )
            ) {

                Image(
                    painter =
                        painterResource(
                            bannerImages[page]
                        ),

                    contentDescription =
                        "홈 배너 ${page + 1}",

                    contentScale =
                        ContentScale.Crop,

                    modifier =
                        Modifier.fillMaxSize()
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    dimens.smallSpacing
                )
        )


        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.Center,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            bannerImages.indices.forEach { index ->

                val selected =
                    pagerState.currentPage ==
                            index


                Box(
                    modifier = Modifier
                        .width(
                            if (
                                selected
                            ) {

                                18.dp

                            } else {

                                6.dp
                            }
                        )
                        .height(
                            6.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                50.dp
                            )
                        )
                        .background(
                            if (
                                selected
                            ) {

                                MainBlue

                            } else {

                                Color(
                                    0xFFD8DBE4
                                )
                            }
                        )
                )


                if (
                    index <
                    bannerImages.lastIndex
                ) {

                    Spacer(
                        modifier =
                            Modifier.width(
                                6.dp
                            )
                    )
                }
            }
        }
    }
}


// ========================================
// 주요 서비스 카드
// ========================================

@Composable
private fun HomeServiceCard(

    modifier:
    Modifier = Modifier,

    title:
    String,

    iconTint:
    Color,

    iconType:
    String,

    onClick:
        () -> Unit

) {

    val dimens =
        rememberResponsiveDimens()


    Card(
        modifier = modifier
            .height(
                dimens.homeServiceCardHeight
            )
            .clickable {

                onClick()
            },

        shape =
            RoundedCornerShape(
                dimens.cardRadius
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width =
                        1.dp,

                    color =
                        BorderGray,

                    shape =
                        RoundedCornerShape(
                            dimens.cardRadius
                        )
                )
                .padding(
                    horizontal =
                        dimens.smallSpacing,

                    vertical =
                        dimens.mediumSpacing
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Icon(
                imageVector =
                    when (
                        iconType
                    ) {

                        "안심경로" ->
                            Icons.Filled.NearMe

                        "안심친구" ->
                            Icons.Filled.Groups

                        "꽥꽥이" ->
                            Icons.Filled.Campaign

                        "안심지도" ->
                            Icons.Filled.Map

                        else ->
                            Icons.Filled.BarChart
                    },

                contentDescription =
                    title,

                tint =
                    iconTint,

                modifier =
                    Modifier.size(
                        dimens.homeServiceIconSize
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.mediumSpacing
                    )
            )


            Text(
                text =
                    title,

                fontSize =
                    dimens.bodySize,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    TextBlack,

                maxLines =
                    1,

                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}