package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar


// ========================================
// 색상
// ========================================

private val AnOnBlue = Color(0xFF6A92FE)

private val CardBg = Color(0xFFF3F5FA)

private val ColorRoute = AnOnBlue
private val ColorFriend = Color(0xFFFFA645)
private val ColorQuack = Color(0xFFFF7AA8)
private val ColorMap = Color(0xFFFFC94D)
private val ColorReport = Color(0xFFB39DFF)


// ========================================
// 주요 서비스 데이터
// ========================================

data class HomeMenuItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)


// ========================================
// 배너 데이터
// ========================================

data class BannerItem(
    val title: String,
    val gradient: List<Color>
)


// ========================================
// 홈 화면
// ========================================

@Composable
fun HomeScreen(
    userName: String = "이지연",
    onMenuClick: (String) -> Unit = {},
    onEmergencyClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ========================================
            // 1. 상단바
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(
                        top = 12.dp,
                        bottom = 8.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = AnOnBlue,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "안온",
                        color = AnOnBlue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "알림",
                    tint = Color.Gray,
                    modifier = Modifier.size(23.dp)
                )
            }


            // ========================================
            // 2. 본문
            // ========================================

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Top
            ) {

                // 상단바와 인사카드 사이 여백
                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // ========================================
                // 2-1. 사용자 인사 카드
                // ========================================

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        )
                ) {

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = CardBg,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(23.dp)
                        )
                    }


                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )


                    Column {

                        Text(
                            text = "안녕하세요,",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = "${userName}님",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                // 인사카드 아래 여백
                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // ========================================
                // 2-2. 배너
                // ========================================

                val banners = listOf(

                    BannerItem(
                        title = "언제 어디서나\n당신의 안전을 지켜드려요",
                        gradient = listOf(
                            Color(0xFF3E6BFF),
                            Color(0xFF6A92FE),
                            Color(0xFF9BB6FF)
                        )
                    ),

                    BannerItem(
                        title = "밤길도 안심하고\n걸을 수 있어요",
                        gradient = listOf(
                            Color(0xFFFF8FB1),
                            Color(0xFFFF7AA8),
                            Color(0xFFFFB4CB)
                        )
                    ),

                    BannerItem(
                        title = "소중한 사람과\n안심 경로를 공유해요",
                        gradient = listOf(
                            Color(0xFFFFC155),
                            Color(0xFFFFA645),
                            Color(0xFFFFD08A)
                        )
                    )
                )


                val pagerState = rememberPagerState(
                    pageCount = { banners.size }
                )


                Column {

                    // 배너 좌우 여백 추가
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .height(150.dp)
                    ) { page ->

                        val banner = banners[page]

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = banner.gradient,
                                        start = Offset(0f, 0f),
                                        end = Offset(600f, 400f)
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                )
                        ) {

                            Text(
                                text = banner.title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 18.dp)
                            )
                        }
                    }


                    // 배너와 페이지 점 사이
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    // ========================================
                    // 배너 페이지 점
                    // ========================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        repeat(banners.size) { index ->

                            val isSelected =
                                pagerState.currentPage == index

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(
                                        if (isSelected) 7.dp
                                        else 6.dp
                                    )
                                    .background(
                                        color =
                                            if (isSelected)
                                                AnOnBlue
                                            else
                                                Color.LightGray,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }


                // 페이지 점과 주요서비스 사이
                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                // ========================================
                // 2-3. 주요 서비스
                // ========================================

                Text(
                    text = "주요 서비스",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                val menuItems = listOf(

                    HomeMenuItem(
                        label = "안심경로",
                        icon = Icons.Filled.Navigation,
                        color = ColorRoute
                    ),

                    HomeMenuItem(
                        label = "안심친구",
                        icon = Icons.Filled.People,
                        color = ColorFriend
                    ),

                    HomeMenuItem(
                        label = "꽥꽥이",
                        icon = Icons.Filled.Campaign,
                        color = ColorQuack
                    ),

                    HomeMenuItem(
                        label = "안심지도",
                        icon = Icons.Filled.Map,
                        color = ColorMap
                    ),

                    HomeMenuItem(
                        label = "사용 리포트",
                        icon = Icons.Filled.BarChart,
                        color = ColorReport
                    )
                )


                // ========================================
                // 서비스 카드 영역
                // ========================================

                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    val gap = 12.dp

                    val cardWidth =
                        (maxWidth - gap * 2) / 3

                    // 카드 높이 다시 조금 키움
                    val cardHeight = 120.dp


                    Column {

                        // ========================================
                        // 첫 번째 줄
                        // ========================================

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(gap)
                        ) {

                            menuItems
                                .take(3)
                                .forEach { item ->

                                    MenuCard(
                                        item = item,
                                        modifier = Modifier
                                            .width(cardWidth)
                                            .height(cardHeight)
                                    ) {
                                        onMenuClick(item.label)
                                    }
                                }
                        }


                        Spacer(
                            modifier = Modifier.height(gap)
                        )


                        // ========================================
                        // 두 번째 줄
                        // ========================================

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.Center
                        ) {

                            menuItems
                                .drop(3)
                                .forEachIndexed { index, item ->

                                    if (index > 0) {

                                        Spacer(
                                            modifier =
                                                Modifier.width(gap)
                                        )
                                    }


                                    MenuCard(
                                        item = item,
                                        modifier = Modifier
                                            .width(cardWidth)
                                            .height(cardHeight)
                                    ) {
                                        onMenuClick(item.label)
                                    }
                                }
                        }
                    }
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }


        // ========================================
        // 3. 하단 네비게이션
        // ========================================

        AnOnBottomBar(
            selectedTab = "홈",

            onTabSelected = { tab ->
                onMenuClick(tab)
            },

            onEmergencyClick = onEmergencyClick,

            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}


// ========================================
// 주요 서비스 카드
// ========================================

@Composable
private fun MenuCard(
    item: HomeMenuItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val shape =
        RoundedCornerShape(16.dp)


    Box(
        modifier = modifier
    ) {

        // ========================================
        // 그림자
        // ========================================

        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(
                    x = 3.dp,
                    y = 3.dp
                )
                .background(
                    color =
                        Color.Black.copy(
                            alpha = 0.10f
                        ),
                    shape = shape
                )
        )


        // 카드 배경색
        val cardBgColor =
            lerp(
                start = Color.White,
                stop = item.color,
                fraction = 0.18f
            )


        // ========================================
        // 실제 카드
        // ========================================

        Column(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(
                    color = cardBgColor,
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    color =
                        item.color.copy(
                            alpha = 0.35f
                        ),
                    shape = shape
                )
                .clickable {
                    onClick()
                }
                .padding(
                    vertical = 14.dp,
                    horizontal = 6.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = item.color,
                modifier = Modifier.size(34.dp)
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Text(
                text = item.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}


// ========================================
// Preview
// ========================================

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true
)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}