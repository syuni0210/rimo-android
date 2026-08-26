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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar
import com.example.clouddx_team4_project.ui.theme.rememberResponsiveDimens
import kotlinx.coroutines.delay

private val MainBlue = Color(0xFF6A92FE)
private val BgColor = Color(0xFFF7F8FC)
private val TextBlack = Color(0xFF222222)
private val TextGray = Color(0xFF8D8D8D)
private val BorderGray = Color(0xFFE9ECF3)

data class HomeServiceItem(val title: String, val iconTint: Color)

@Composable
fun HomeScreen(
    userName: String = "이지연",
    currentLocation: String = "서울특별시 영등포구",
    onMenuClick: (String) -> Unit = {},
    onEmergencyClick: () -> Unit = {}
) {
    val dimens = rememberResponsiveDimens()
    val serviceItems = listOf(
        HomeServiceItem("안심경로", Color(0xFF5E86F7)),
        HomeServiceItem("안심친구", Color(0xFFFFA24B)),
        HomeServiceItem("꽥꽥이", Color(0xFFFF6B9D)),
        HomeServiceItem("안심지도", Color(0xFFF1B93B)),
        HomeServiceItem("사용 리포트", Color(0xFF9A82F8))
    )
    val bannerImages = remember {
        listOf(R.drawable.home_banner_1, R.drawable.home_banner_2, R.drawable.home_banner_3)
    }

    Box(Modifier.fillMaxSize().background(BgColor)) {
        Column(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal = dimens.screenHorizontalPadding,
                        vertical = dimens.screenVerticalPadding
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Security,
                        contentDescription = "Rimo",
                        tint = MainBlue,
                        modifier = Modifier.size(dimens.homeLogoSize)
                    )
                    Spacer(Modifier.width(dimens.smallSpacing))
                    Text(
                        "Rimo",
                        fontSize = dimens.titleSize,
                        fontWeight = FontWeight.Bold,
                        color = MainBlue
                    )
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Filled.NotificationsNone,
                    contentDescription = "알림",
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(dimens.homeNotificationSize)
                )
            }

            Spacer(Modifier.height(dimens.smallSpacing))

            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = dimens.screenHorizontalPadding),
                shape = RoundedCornerShape(dimens.cardRadius),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(Modifier.fillMaxWidth().padding(dimens.cardPadding)) {
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd).widthIn(max = 190.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = "현재 위치",
                            tint = MainBlue,
                            modifier = Modifier.size(dimens.smallIconSize)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            currentLocation,
                            fontSize = dimens.captionSize,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF777777),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = dimens.mediumSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(dimens.homeProfileIconSize)
                                .clip(CircleShape)
                                .background(Color(0xFFEAF0FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "프로필",
                                tint = MainBlue,
                                modifier = Modifier.size(dimens.homeProfilePersonIconSize)
                            )
                        }
                        Spacer(Modifier.width(dimens.mediumSpacing))
                        Column {
                            Text("안녕하세요!", fontSize = dimens.captionSize, color = TextGray)
                            Spacer(Modifier.height(dimens.smallSpacing))
                            Text(
                                "${userName}님",
                                fontSize = dimens.sectionTitleSize,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(dimens.largeSpacing))
            HomeBannerSlider(bannerImages)
            Spacer(Modifier.height(dimens.largeSpacing))

            Text(
                "주요 서비스",
                fontSize = dimens.sectionTitleSize,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.padding(horizontal = dimens.screenHorizontalPadding)
            )

            Spacer(Modifier.height(dimens.mediumSpacing))

            Row(
                Modifier.fillMaxWidth().padding(horizontal = dimens.screenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(dimens.mediumSpacing)
            ) {
                HomeServiceCard(Modifier.weight(1f), serviceItems[0].title, serviceItems[0].iconTint, "안심경로") { onMenuClick("안심경로") }
                HomeServiceCard(Modifier.weight(1f), serviceItems[1].title, serviceItems[1].iconTint, "안심친구") { onMenuClick("안심친구") }
                HomeServiceCard(Modifier.weight(1f), serviceItems[2].title, serviceItems[2].iconTint, "꽥꽥이") { onMenuClick("꽥꽥이") }
            }

            Spacer(Modifier.height(dimens.mediumSpacing))

            Row(
                Modifier.fillMaxWidth().padding(horizontal = dimens.screenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(dimens.mediumSpacing)
            ) {
                HomeServiceCard(Modifier.weight(1f), serviceItems[3].title, serviceItems[3].iconTint, "안심지도") { onMenuClick("안심지도") }
                HomeServiceCard(Modifier.weight(1f), serviceItems[4].title, serviceItems[4].iconTint, "사용 리포트") { onMenuClick("사용 리포트") }
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(dimens.largeSpacing))
        }

        AnOnBottomBar(
            selectedTab = "홈",
            onTabSelected = { onMenuClick(it) },
            onEmergencyClick = onEmergencyClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun HomeBannerSlider(bannerImages: List<Int>) {
    val dimens = rememberResponsiveDimens()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { bannerImages.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000L)
            if (!pagerState.isScrollInProgress) {
                val next = if (pagerState.currentPage == bannerImages.lastIndex) 0 else pagerState.currentPage + 1
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(dimens.homeBannerHeight)
        ) { page ->
            Box(
                Modifier.fillMaxSize()
                    .padding(horizontal = dimens.screenHorizontalPadding)
                    .clip(RoundedCornerShape(dimens.cardRadius))
            ) {
                Image(
                    painter = painterResource(bannerImages[page]),
                    contentDescription = "홈 배너 ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.height(dimens.smallSpacing))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bannerImages.indices.forEach { index ->
                val selected = pagerState.currentPage == index
                Box(
                    Modifier.width(if (selected) 18.dp else 6.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (selected) MainBlue else Color(0xFFD8DBE4))
                )
                if (index < bannerImages.lastIndex) Spacer(Modifier.width(6.dp))
            }
        }
    }
}

@Composable
private fun HomeServiceCard(
    modifier: Modifier = Modifier,
    title: String,
    iconTint: Color,
    iconType: String,
    onClick: () -> Unit
) {
    val dimens = rememberResponsiveDimens()

    Card(
        modifier = modifier.height(dimens.homeServiceCardHeight).clickable { onClick() },
        shape = RoundedCornerShape(dimens.cardRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .border(1.dp, BorderGray, RoundedCornerShape(dimens.cardRadius))
                .padding(horizontal = dimens.smallSpacing, vertical = dimens.mediumSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (iconType) {
                    "안심경로" -> Icons.Filled.NearMe
                    "안심친구" -> Icons.Filled.Groups
                    "꽥꽥이" -> Icons.Filled.Campaign
                    "안심지도" -> Icons.Filled.Map
                    else -> Icons.Filled.BarChart
                },
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(dimens.homeServiceIconSize)
            )
            Spacer(Modifier.height(dimens.mediumSpacing))
            Text(
                title,
                fontSize = dimens.bodySize,
                fontWeight = FontWeight.SemiBold,
                color = TextBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}