package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WbIncandescent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar
import com.example.clouddx_team4_project.ui.theme.rememberResponsiveDimens
import androidx.compose.ui.unit.sp

// ========================================
// 색상
// ========================================

private val RimoBlue =
    Color(0xFF6A92FE)

private val ScreenBackground =
    Color(0xFFF7F8FC)

private val TextBlack =
    Color(0xFF222222)

private val TextGray =
    Color(0xFF888888)


// ========================================
// 시설 데이터
// ========================================

data class SafeMapFacility(
    val name: String,
    val icon: ImageVector,
    val color: Color
)


// ========================================
// 안심지도
// ========================================

@Composable
fun SafeMapScreen(

    currentLocationText: String =
        "현재 위치 확인 중",

    onBackClick: () -> Unit = {},

    onTabSelected: (String) -> Unit = {},

    onEmergencyClick: () -> Unit = {}
) {

    val dimens =
        rememberResponsiveDimens()


    // ========================================
    // 선택된 시설
    // ========================================

    var selectedFacility by remember {
        mutableStateOf<String?>(null)
    }


    // ========================================
    // 현재 위치 재이동 요청값
    //
    // 값이 변경될 때마다 KakaoMapView가
    // 현재 위치로 카메라를 다시 이동
    // ========================================

    var recenterRequestKey by remember {
        mutableIntStateOf(0)
    }


    // ========================================
    // 시설 목록
    // ========================================

    val facilities =
        listOf(

            SafeMapFacility(
                name = "CCTV",
                icon = Icons.Filled.Videocam,
                color = Color(0xFF4D8DFF)
            ),

            SafeMapFacility(
                name = "가로등",
                icon = Icons.Filled.WbIncandescent,
                color = Color(0xFFFFA63D)
            ),

            SafeMapFacility(
                name = "지킴이집",
                icon = Icons.Filled.Home,
                color = Color(0xFFFF6F9E)
            ),

            SafeMapFacility(
                name = "지구대",
                icon = Icons.Filled.Security,
                color = Color(0xFF36B873)
            ),

            SafeMapFacility(
                name = "비상벨",
                icon = Icons.Filled.Notifications,
                color = Color(0xFFFF5D5D)
            ),

            SafeMapFacility(
                name = "보안등",
                icon = Icons.Filled.Campaign,
                color = Color(0xFF9675F5)
            )
        )


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
                    bottom = 92.dp
                )
        ) {


            // ========================================
// 상단
// 안심경로와 동일한 스타일
// ========================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(
                        62.dp
                    )
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
                    text =
                        "안심지도",

                    fontSize =
                        22.sp,

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
            // 현재 위치 카드
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
                            1.dp
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal =
                                dimens.cardPadding,

                            vertical =
                                dimens.mediumSpacing
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
                            RimoBlue,

                        modifier =
                            Modifier.size(
                                dimens.smallIconSize
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
                            "현재 위치",

                        fontSize =
                            dimens.bodySize,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            TextBlack
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                dimens.mediumSpacing
                            )
                    )


                    Text(
                        text =
                            currentLocationText,

                        fontSize =
                            dimens.captionSize,

                        color =
                            TextGray,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis,

                        modifier =
                            Modifier.weight(1f)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.smallSpacing
                    )
            )


            // ========================================
            // 지도 영역
            // ========================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {


                // ========================================
                // 실제 카카오맵
                // ========================================

                KakaoMapView(

                    modifier =
                        Modifier.fillMaxSize(),

                    showRoute =
                        false,

                    recenterRequestKey =
                        recenterRequestKey
                )


                // ========================================
                // 현재 위치로 돌아가기 버튼
                // ========================================

                Box(
                    modifier = Modifier
                        .align(
                            Alignment.BottomEnd
                        )
                        .padding(
                            end =
                                dimens.screenHorizontalPadding,

                            bottom =
                                175.dp
                        )
                        .size(
                            44.dp
                        )
                        .clip(
                            CircleShape
                        )
                        .background(
                            Color.White
                        )
                        .border(
                            width =
                                1.dp,

                            color =
                                Color(
                                    0xFFE8E8E8
                                ),

                            shape =
                                CircleShape
                        )
                        .clickable {

                            recenterRequestKey++
                        },

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.MyLocation,

                        contentDescription =
                            "현재 위치로 이동",

                        tint =
                            Color(0xFF333333),

                        modifier =
                            Modifier.size(
                                22.dp
                            )
                    )
                }


                // ========================================
                // 시설 선택 패널
                // ========================================

                FacilityPanel(

                    facilities =
                        facilities,

                    selectedFacility =
                        selectedFacility,

                    onFacilitySelected = { name ->

                        selectedFacility =
                            if (
                                selectedFacility ==
                                name
                            ) {

                                null

                            } else {

                                name
                            }
                    },

                    modifier =
                        Modifier.align(
                            Alignment.BottomCenter
                        )
                )
            }
        }


        // ========================================
        // 하단바
        // ========================================

        AnOnBottomBar(

            selectedTab =
                "",

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
// 시설 선택 패널
// ========================================

@Composable
private fun FacilityPanel(

    facilities:
    List<SafeMapFacility>,

    selectedFacility:
    String?,

    onFacilitySelected:
        (String) -> Unit,

    modifier:
    Modifier = Modifier
) {

    val dimens =
        rememberResponsiveDimens()


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal =
                    dimens.screenHorizontalPadding,

                vertical =
                    dimens.smallSpacing
            ),

        shape =
            RoundedCornerShape(
                dimens.cardRadius + 4.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    4.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    dimens.cardPadding
                )
        ) {


            Text(
                text =
                    "지도에 표시할 항목을 선택해주세요",

                fontSize =
                    dimens.captionSize,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    Color(0xFF555555)
            )


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.mediumSpacing
                    )
            )


            // ========================================
            // 1행
            // ========================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        dimens.smallSpacing
                    )
            ) {

                FacilityButton(
                    facility =
                        facilities[0],

                    selected =
                        selectedFacility ==
                                facilities[0].name,

                    modifier =
                        Modifier.weight(1f),

                    onClick =
                        onFacilitySelected
                )


                FacilityButton(
                    facility =
                        facilities[1],

                    selected =
                        selectedFacility ==
                                facilities[1].name,

                    modifier =
                        Modifier.weight(1f),

                    onClick =
                        onFacilitySelected
                )


                FacilityButton(
                    facility =
                        facilities[2],

                    selected =
                        selectedFacility ==
                                facilities[2].name,

                    modifier =
                        Modifier.weight(1f),

                    onClick =
                        onFacilitySelected
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        dimens.smallSpacing
                    )
            )


            // ========================================
            // 2행
            // ========================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        dimens.smallSpacing
                    )
            ) {

                FacilityButton(
                    facility =
                        facilities[3],

                    selected =
                        selectedFacility ==
                                facilities[3].name,

                    modifier =
                        Modifier.weight(1f),

                    onClick =
                        onFacilitySelected
                )


                FacilityButton(
                    facility =
                        facilities[4],

                    selected =
                        selectedFacility ==
                                facilities[4].name,

                    modifier =
                        Modifier.weight(1f),

                    onClick =
                        onFacilitySelected
                )


                FacilityButton(
                    facility =
                        facilities[5],

                    selected =
                        selectedFacility ==
                                facilities[5].name,

                    modifier =
                        Modifier.weight(1f),

                    onClick =
                        onFacilitySelected
                )
            }
        }
    }
}


// ========================================
// 시설 버튼
// ========================================

@Composable
private fun FacilityButton(

    facility:
    SafeMapFacility,

    selected:
    Boolean,

    modifier:
    Modifier = Modifier,

    onClick:
        (String) -> Unit
) {

    val dimens =
        rememberResponsiveDimens()


    val backgroundColor =

        if (selected) {

            facility.color.copy(
                alpha = 0.10f
            )

        } else {

            Color.White
        }


    val borderColor =

        if (selected) {

            facility.color

        } else {

            Color(0xFFEBEDF3)
        }


    Column(
        modifier = modifier
            .height(
                dimens.homeServiceCardHeight *
                        0.63f
            )
            .clip(
                RoundedCornerShape(
                    dimens.cardRadius
                )
            )
            .background(
                backgroundColor
            )
            .border(
                width =
                    1.dp,

                color =
                    borderColor,

                shape =
                    RoundedCornerShape(
                        dimens.cardRadius
                    )
            )
            .clickable {

                onClick(
                    facility.name
                )
            },

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {


        Icon(
            imageVector =
                facility.icon,

            contentDescription =
                facility.name,

            tint =
                facility.color,

            modifier =
                Modifier.size(
                    dimens.mediumIconSize
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
                facility.name,

            fontSize =
                dimens.captionSize,

            fontWeight =
                if (selected) {

                    FontWeight.Bold

                } else {

                    FontWeight.Medium
                },

            color =
                if (selected) {

                    facility.color

                } else {

                    Color(0xFF555555)
                }
        )
    }
}