package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar


private val AnOnBlue =
    Color(0xFF6A92FE)

private val ScreenBackground =
    Color(0xFFF8F9FC)

private val TextBlack =
    Color(0xFF222222)

private val TextGray =
    Color(0xFF888888)


@Composable
fun SafeRouteScreen(
    onBackClick: () -> Unit = {},
    onStartSearchClick: () -> Unit = {},
    onDestinationSearchClick: () -> Unit = {},
    onTabSelected: (String) -> Unit = {},
    onEmergencyClick: () -> Unit = {}
) {

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
                .padding(
                    bottom = 100.dp
                )
        ) {

            // ========================================
            // 상단
            // ========================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(62.dp)
                    .padding(
                        horizontal = 20.dp
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.ArrowBackIosNew,

                    contentDescription =
                        "뒤로가기",

                    tint =
                        TextBlack,

                    modifier = Modifier
                        .align(
                            Alignment.CenterStart
                        )
                        .size(
                            21.dp
                        )
                        .clickable {
                            onBackClick()
                        }
                )


                Text(
                    text = "안심경로",

                    fontSize = 22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextBlack,

                    modifier =
                        Modifier.align(
                            Alignment.Center
                        )
                )
            }


            // ========================================
            // 출발 / 도착 카드
            // ========================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            18.dp
                        )
                    )
                    .background(
                        Color.White
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 16.dp
                    )
            ) {


                // ========================================
                // 출발지
                // ========================================

                LocationInputRow(
                    title = "출발지",
                    value = "현재 위치",

                    onClick = {
                        onStartSearchClick()
                    }
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )


                // ========================================
                // 도착지
                // ========================================

                LocationInputRow(
                    title = "도착지",
                    value = "목적지를 검색하세요",

                    onClick = {
                        onDestinationSearchClick()
                    }
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            // ========================================
            // 즐겨찾는 장소
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "즐겨찾는 장소",

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextBlack
                )


                Spacer(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                FavoritePlaceChip(
                    text = "집"
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )


                FavoritePlaceChip(
                    text = "학교"
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            // ========================================
            // 실제 카카오맵
            // ========================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                KakaoMapView(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }


        // ========================================
        // 하단 네비게이션
        // ========================================

        AnOnBottomBar(
            selectedTab = "",

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
// 위치 입력 한 줄
// ========================================

@Composable
private fun LocationInputRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                58.dp
            )
            .clip(
                RoundedCornerShape(
                    12.dp
                )
            )
            .background(
                Color(
                    0xFFF6F7FA
                )
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 14.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(
                    35.dp
                )
                .background(
                    color =
                        AnOnBlue.copy(
                            alpha = 0.12f
                        ),

                    shape =
                        CircleShape
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    if (
                        title ==
                        "출발지"
                    ) {
                        Icons.Filled.LocationOn
                    } else {
                        Icons.Filled.Search
                    },

                contentDescription =
                    null,

                tint =
                    AnOnBlue,

                modifier =
                    Modifier.size(
                        21.dp
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.width(
                    12.dp
                )
        )


        Column {

            Text(
                text =
                    title,

                fontSize =
                    12.sp,

                color =
                    TextGray
            )


            Spacer(
                modifier =
                    Modifier.height(
                        2.dp
                    )
            )


            Text(
                text =
                    value,

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.Medium,

                color =
                    TextBlack
            )
        }
    }
}


// ========================================
// 즐겨찾기 버튼
// ========================================

@Composable
private fun FavoritePlaceChip(
    text: String
) {

    Row(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    20.dp
                )
            )
            .background(
                Color.White
            )
            .padding(
                horizontal = 13.dp,
                vertical = 8.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                Icons.Filled.Home,

            contentDescription =
                null,

            tint =
                AnOnBlue,

            modifier =
                Modifier.size(
                    17.dp
                )
        )


        Spacer(
            modifier =
                Modifier.width(
                    5.dp
                )
        )


        Text(
            text =
                text,

            fontSize =
                13.sp,

            fontWeight =
                FontWeight.Medium
        )
    }
}