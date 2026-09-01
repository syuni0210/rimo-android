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
import android.util.Log
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.network.FacilityMapDto
import com.example.clouddx_team4_project.BuildConfig
import com.example.clouddx_team4_project.data.KakaoReverseGeocodeClient
import androidx.compose.ui.zIndex

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

    // ============================================================
// 실제 현재 위치
//
// KakaoMapView에서 GPS 좌표를 받아 저장합니다.
// 좌표가 들어오면 아래 LaunchedEffect에서
// Kakao Local API를 이용하여 주소로 변환합니다.
// ============================================================

    var currentLatitude by remember {
        mutableStateOf<Double?>(null)
    }

    var currentLongitude by remember {
        mutableStateOf<Double?>(null)
    }

    // ========================================
    // 현재 카카오맵 화면에 보이는 좌표 범위
    //
    // KakaoMapView에서 화면이 이동/확대/축소될 때
    // 새로운 값이 전달됩니다.
    // ========================================

    var visibleSwLat by remember {
        mutableStateOf<Double?>(null)
    }

    var visibleSwLng by remember {
        mutableStateOf<Double?>(null)
    }

    var visibleNeLat by remember {
        mutableStateOf<Double?>(null)
    }

    var visibleNeLng by remember {
        mutableStateOf<Double?>(null)
    }

// ============================================================
// 화면에 표시할 현재 위치 이름
//
// 처음에는 기존 문구:
// "현재 위치 확인 중"
//
// GPS + Kakao API 응답을 받으면
// 실제 주소/건물명으로 변경됩니다.
// ============================================================

    var currentLocationDisplay by remember(
        currentLocationText
    ) {
        mutableStateOf(
            currentLocationText
        )
    }

    // ============================================================
// 현재 좌표 → 주소 변환
//
// currentLatitude / currentLongitude가 변경되면 실행됩니다.
//
// Kakao Local API:
// /v2/local/geo/coord2address.json
//
// 표시 우선순위:
// 1. 건물명
// 2. 도로명 주소
// 3. 지번 주소
// ============================================================

    LaunchedEffect(
        currentLatitude,
        currentLongitude
    ) {

        val latitude =
            currentLatitude
                ?: return@LaunchedEffect

        val longitude =
            currentLongitude
                ?: return@LaunchedEffect


        try {

            val response =
                KakaoReverseGeocodeClient
                    .api
                    .getAddressFromCoordinate(

                        // REST API Key
                        authorization =
                            "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",

                        // Kakao API:
                        // x = 경도
                        longitude =
                            longitude,

                        // Kakao API:
                        // y = 위도
                        latitude =
                            latitude
                    )


            val document =
                response
                    .documents
                    .firstOrNull()


            // ----------------------------------------
            // 가능한 경우 건물명을 우선 표시
            // ----------------------------------------

            val buildingName =
                document
                    ?.roadAddress
                    ?.buildingName
                    ?.takeIf {
                        it.isNotBlank()
                    }


            // ----------------------------------------
            // 그 다음 도로명 주소
            // ----------------------------------------

            val roadAddress =
                document
                    ?.roadAddress
                    ?.addressName
                    ?.takeIf {
                        it.isNotBlank()
                    }


            // ----------------------------------------
            // 도로명 주소가 없으면 지번 주소
            // ----------------------------------------

            val jibunAddress =
                document
                    ?.address
                    ?.addressName
                    ?.takeIf {
                        it.isNotBlank()
                    }


            // ----------------------------------------
            // 최종적으로 화면에 표시
            // ----------------------------------------

            currentLocationDisplay =
                buildingName
                    ?: roadAddress
                            ?: jibunAddress
                            ?: "현재 위치"


            Log.d(
                "SAFE_MAP_LOCATION",
                "현재 위치: $currentLocationDisplay"
            )

        } catch (
            e: Exception
        ) {

            Log.e(
                "SAFE_MAP_LOCATION",
                "현재 위치 주소 변환 실패",
                e
            )

            currentLocationDisplay =
                "현재 위치 확인 실패"
        }
    }

    // ========================================
    // 선택된 시설
    // ========================================

    var selectedFacility by remember {
        mutableStateOf<String?>(null)
    }

    var mapFacilities by remember {
        mutableStateOf(
            emptyList<FacilityMapDto>()
        )
    }

    LaunchedEffect(
        selectedFacility,
        visibleSwLat,
        visibleSwLng,
        visibleNeLat,
        visibleNeLng
    ) {

        if (selectedFacility == null) {

            mapFacilities =
                emptyList()

            return@LaunchedEffect
        }


        try {

// ============================================================
// 현재 카카오맵 화면에 보이는 범위를 사용
//
// 기존:
// 현재 위치 기준 반경 50m
//
// 변경:
// 사용자가 현재 보고 있는 지도 화면 전체
//
// KakaoMapView에서 받은 화면의
// 남서쪽(SW) / 북동쪽(NE) 좌표를 사용합니다.
// ============================================================

            val swLat =
                visibleSwLat
                    ?: return@LaunchedEffect

            val swLng =
                visibleSwLng
                    ?: return@LaunchedEffect

            val neLat =
                visibleNeLat
                    ?: return@LaunchedEffect

            val neLng =
                visibleNeLng
                    ?: return@LaunchedEffect


            val result =
                when (selectedFacility) {

                    "CCTV" ->

                        RetrofitClient
                            .reportApi
                            .getCctv(
                                swLat = swLat,
                                swLng = swLng,
                                neLat = neLat,
                                neLng = neLng
                            )


                    "가로등" ->

                        RetrofitClient
                            .reportApi
                            .getSmartLight(
                                swLat = swLat,
                                swLng = swLng,
                                neLat = neLat,
                                neLng = neLng
                            )


                    "지킴이집" ->

                        RetrofitClient
                            .reportApi
                            .getSafeHouse(
                                swLat = swLat,
                                swLng = swLng,
                                neLat = neLat,
                                neLng = neLng
                            )


                    "지구대" ->

                        RetrofitClient
                            .reportApi
                            .getPolice(
                                swLat = swLat,
                                swLng = swLng,
                                neLat = neLat,
                                neLng = neLng
                            )


                    "비상벨" ->

                        RetrofitClient
                            .reportApi
                            .getEmergencyBell(
                                swLat = swLat,
                                swLng = swLng,
                                neLat = neLat,
                                neLng = neLng
                            )


                    "보안등" ->

                        RetrofitClient
                            .reportApi
                            .getSecurityLight(
                                swLat = swLat,
                                swLng = swLng,
                                neLat = neLat,
                                neLng = neLng
                            )


                    else ->
                        emptyList()
                }


            mapFacilities =
                result


            Log.d(
                "SAFE_MAP",
                "${selectedFacility} 조회 성공: ${result.size}개"
            )


            result
                .take(3)
                .forEach {

                    Log.d(
                        "SAFE_MAP",
                        "${it.type}: ${it.id}, ${it.name}, ${it.address}, ${it.lat}, ${it.lng}"
                    )
                }


        } catch (
            e: Exception
        ) {

            mapFacilities =
                emptyList()


            Log.e(
                "SAFE_MAP",
                "${selectedFacility} 조회 실패",
                e
            )
        }
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
                            currentLocationDisplay,

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

                    selectedFacility =
                        selectedFacility,

                    facilities =
                        mapFacilities,

                    showRoute =
                        false,

                    recenterRequestKey =
                        recenterRequestKey,


                    // ====================================================
                    // 현재 GPS 위치
                    //
                    // 주소 표시와 "내 위치로 돌아가기" 기능에서 사용
                    // ====================================================

                    onCurrentLocationChanged = {
                            latitude,
                            longitude ->

                        currentLatitude =
                            latitude

                        currentLongitude =
                            longitude
                    },


                    // ====================================================
                    // 현재 지도 화면 범위
                    //
                    // 사용자가 보고 있는 지도 화면의
                    // 남서쪽 / 북동쪽 좌표를 받아옵니다.
                    //
                    // 시설 조회는 이제 이 좌표를 기준으로 합니다.
                    // ====================================================

                    onVisibleBoundsChanged = {
                            swLat,
                            swLng,
                            neLat,
                            neLng ->

                        visibleSwLat =
                            swLat

                        visibleSwLng =
                            swLng

                        visibleNeLat =
                            neLat

                        visibleNeLng =
                            neLng
                    }
                )

                // ========================================
// 현재 위치로 돌아가기 버튼
//
// 시설 선택 패널보다 위쪽에 표시합니다.
// zIndex를 주어 지도나 시설 패널 뒤로
// 버튼이 가려지지 않도록 합니다.
// ========================================

                Box(
                    modifier = Modifier

                        // 지도 화면 오른쪽 아래에 배치
                        .align(
                            Alignment.BottomEnd
                        )

                        // 시설 선택 패널 위로 올림
                        .padding(
                            end =
                                dimens.screenHorizontalPadding,

                            // 기존 175.dp에서는 시설 패널에
                            // 가려질 수 있어서 더 위로 배치
                            bottom =
                                240.dp
                        )

                        // 다른 요소보다 위에 표시
                        .zIndex(
                            10f
                        )

                        // 버튼 크기
                        .size(
                            48.dp
                        )

                        // 원형 버튼
                        .clip(
                            CircleShape
                        )

                        // 흰색 배경
                        .background(
                            Color.White
                        )

                        // 버튼 테두리
                        .border(
                            width =
                                1.dp,

                            color =
                                Color(
                                    0xFFE0E0E0
                                ),

                            shape =
                                CircleShape
                        )

                        // 버튼 클릭 시
                        // 현재 위치로 카메라 이동 요청
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
                            RimoBlue,

                        modifier =
                            Modifier.size(
                                24.dp
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