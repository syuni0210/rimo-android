package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar
import kotlinx.coroutines.delay


// ========================================
// 색상
// ========================================

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


// ========================================
// 서비스 데이터
// ========================================

data class HomeServiceItem(
    val title: String,
    val iconTint: Color
)


// ========================================
// 홈 화면
// ========================================

@Composable
fun HomeScreen(
    userName: String = "이지연",

    // 현재 위치
    // 나중에 GPS → 주소 변환 결과를 넣으면 됨
    currentLocation: String = "서울특별시 영등포구",

    onMenuClick: (String) -> Unit = {},
    onEmergencyClick: () -> Unit = {}
) {


    // ========================================
    // 주요 서비스
    // ========================================

    val serviceItems =
        listOf(

            HomeServiceItem(
                title = "안심경로",
                iconTint = Color(0xFF5E86F7)
            ),

            HomeServiceItem(
                title = "안심친구",
                iconTint = Color(0xFFFFA24B)
            ),

            HomeServiceItem(
                title = "꽥꽥이",
                iconTint = Color(0xFFFF6B9D)
            ),

            HomeServiceItem(
                title = "안심지도",
                iconTint = Color(0xFFF1B93B)
            ),

            HomeServiceItem(
                title = "사용 리포트",
                iconTint = Color(0xFF9A82F8)
            )
        )


    // ========================================
    // 홈 배너
    // ========================================

    val bannerImages =
        remember {

            listOf(
                R.drawable.home_banner_1,
                R.drawable.home_banner_2,
                R.drawable.home_banner_3
            )
        }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    bottom = 110.dp
                )
        ) {


            // ========================================
            // 상단 로고 / 알림
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
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
                                26.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )


                    Text(
                        text =
                            "Rimo",

                        fontSize =
                            27.sp,

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
                        Color(0xFF999999),

                    modifier =
                        Modifier.size(
                            30.dp
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            // ========================================
// 프로필 카드
// ========================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp
                    ),

                shape =
                    RoundedCornerShape(
                        20.dp
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 18.dp,
                            vertical = 18.dp
                        )
                ) {


                    // ========================================
                    // 오른쪽 위 현재 위치
                    // ========================================

                    Row(
                        modifier =
                            Modifier.align(
                                Alignment.TopEnd
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Filled.LocationOn,

                            contentDescription =
                                "현재 위치",

                            tint =
                                MainBlue,

                            modifier =
                                Modifier.size(
                                    15.dp
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    3.dp
                                )
                        )


                        Text(
                            text =
                                currentLocation,

                            fontSize =
                                12.sp,

                            fontWeight =
                                FontWeight.Medium,

                            color =
                                Color(
                                    0xFF777777
                                )
                        )
                    }


                    // ========================================
                    // 프로필 정보
                    // ========================================

                    Row(
                        modifier =
                            Modifier.padding(
                                top = 10.dp
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
                                    58.dp
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
                                        31.dp
                                    )
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    15.dp
                                )
                        )


                        // ========================================
                        // 인사말 / 이름
                        // ========================================

                        Column {

                            Text(
                                text =
                                    "안녕하세요!",

                                fontSize =
                                    13.sp,

                                color =
                                    TextGray
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        4.dp
                                    )
                            )


                            Text(
                                text =
                                    "${userName}님",

                                fontSize =
                                    22.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextBlack
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            // ========================================
            // 자동 슬라이드 배너
            // ========================================

            HomeBannerSlider(
                bannerImages =
                    bannerImages
            )


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            // ========================================
            // 주요 서비스 제목
            // ========================================

            Text(
                text =
                    "주요 서비스",

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextBlack,

                modifier =
                    Modifier.padding(
                        horizontal = 18.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            // ========================================
            // 서비스 1행
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {

                HomeServiceCard(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        serviceItems[0].title,

                    iconTint =
                        serviceItems[0].iconTint,

                    iconType =
                        "안심경로",

                    onClick = {

                        onMenuClick(
                            "안심경로"
                        )
                    }
                )


                HomeServiceCard(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        serviceItems[1].title,

                    iconTint =
                        serviceItems[1].iconTint,

                    iconType =
                        "안심친구",

                    onClick = {

                        onMenuClick(
                            "안심친구"
                        )
                    }
                )


                HomeServiceCard(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        serviceItems[2].title,

                    iconTint =
                        serviceItems[2].iconTint,

                    iconType =
                        "꽥꽥이",

                    onClick = {

                        onMenuClick(
                            "꽥꽥이"
                        )
                    }
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // ========================================
            // 서비스 2행
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {

                HomeServiceCard(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        serviceItems[3].title,

                    iconTint =
                        serviceItems[3].iconTint,

                    iconType =
                        "안심지도",

                    onClick = {

                        onMenuClick(
                            "안심지도"
                        )
                    }
                )


                HomeServiceCard(
                    modifier =
                        Modifier.weight(1f),

                    title =
                        serviceItems[4].title,

                    iconTint =
                        serviceItems[4].iconTint,

                    iconType =
                        "사용 리포트",

                    onClick = {

                        onMenuClick(
                            "사용 리포트"
                        )
                    }
                )


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
                        30.dp
                    )
            )
        }


        // ========================================
        // 하단바
        // ========================================

        AnOnBottomBar(
            selectedTab =
                "홈",

            onTabSelected = { tab ->

                onMenuClick(
                    tab
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

    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = {
                bannerImages.size
            }
        )


    // ========================================
    // 5초마다 자동 슬라이드
    // ========================================

    LaunchedEffect(Unit) {

        while (true) {

            delay(
                5000L
            )


            if (
                !pagerState.isScrollInProgress
            ) {

                val nextPage =
                    if (
                        pagerState.currentPage ==
                        bannerImages.lastIndex
                    ) {

                        0

                    } else {

                        pagerState.currentPage + 1
                    }


                pagerState.animateScrollToPage(
                    nextPage
                )
            }
        }
    }


    Column {

        // ========================================
        // 배너 이미지
        // ========================================

        HorizontalPager(
            state =
                pagerState,

            modifier = Modifier
                .fillMaxWidth()
                .height(
                    190.dp
                )
        ) { page ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 18.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            22.dp
                        )
                    )
            ) {

                Image(
                    painter =
                        painterResource(
                            id =
                                bannerImages[
                                    page
                                ]
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
                    10.dp
                )
        )


        // ========================================
        // 페이지 표시
        // ========================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.Center,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            bannerImages.indices.forEach { index ->

                val isSelected =
                    pagerState.currentPage ==
                            index


                Box(
                    modifier = Modifier
                        .width(
                            if (
                                isSelected
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
                                isSelected
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
    modifier: Modifier = Modifier,
    title: String,
    iconTint: Color,
    iconType: String,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(
                118.dp
            )
            .clickable {

                onClick()
            },

        shape =
            RoundedCornerShape(
                18.dp
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
                            18.dp
                        )
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 18.dp
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
                        33.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        13.dp
                    )
            )


            Text(
                text =
                    title,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    TextBlack
            )
        }
    }
}