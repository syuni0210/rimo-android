package com.example.clouddx_team4_project.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.clouddx_team4_project.network.DestinationResponse
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.example.clouddx_team4_project.network.SharingFriendResponse


// ========================================
// 색상
// ========================================

private val AnOnBlue =
    Color(0xFF6A92FE)

private val ScreenBackground =
    Color(0xFFF8F9FC)

private val TextBlack =
    Color(0xFF222222)

private val TextGray =
    Color(0xFF888888)


// ========================================
// 안심경로
// ========================================

@Composable
fun SafeRouteScreen(


    memberId: Long = 3L,
    friendId: Long? = null,
    friendName: String? = null,

    // ========================================
    // 목적지 정보
    // ========================================

    destinationName: String = "",

    destinationLatitude: Double? = null,

    destinationLongitude: Double? = null,


    // ========================================
    // 선택 경로
    // ========================================

    showSelectedRoute: Boolean = false,

    selectedRouteMode: String = "BROAD_FIRST",


    // ========================================
    // 이벤트
    // ========================================

    onBackClick: () -> Unit = {},

    onStartSearchClick: () -> Unit = {},

    onDestinationSearchClick: () -> Unit = {},

    onRouteSearchClick: () -> Unit = {},

    onTabSelected: (String) -> Unit = {},

    onEmergencyClick: () -> Unit = {},


    // ========================================
    // 기본 목적지 선택
    // ========================================

    onDefaultDestinationSelected:
        (
        placeName: String,
        address: String,
        latitude: Double,
        longitude: Double
    ) -> Unit = { _, _, _, _ -> },


    // ========================================
    // 지도 직접 목적지 지정
    // ========================================

    onMapDestinationSelected:
        (Double, Double) -> Unit =
        { _, _ -> }

) {
    android.util.Log.d("SAFE_ROUTE_TEST", "SafeRouteScreen 함수 진입 성공!! memberId: $memberId")

    val context =
        LocalContext.current

    val coroutineScope =
        rememberCoroutineScope()


    // ========================================
    // 현재 위치 복귀 요청값
    // ========================================

    var recenterRequestKey by remember {
        mutableIntStateOf(0)
    }


    // ========================================
    // 기본 목적지 목록
    // ========================================

    var defaultDestinations by remember {

        mutableStateOf(
            emptyList<DestinationResponse>()
        )
    }


    // ========================================
    // 현재 위치 주소
    // ========================================

    var currentLocationAddress by remember {

        mutableStateOf(
            "위치 확인 중..."
        )
    }
    // ========================================
    // 위치 공유 중인 모든 친구 목록 및 3초 주기 폴링
    // ========================================
    var sharingFriends by remember {
        mutableStateOf(emptyList<SharingFriendResponse>())
    }

    LaunchedEffect(memberId) {
        while (true) {
            try {
                val response = RetrofitClient.trackingApi.getSharingFriendsLocations(
                    requesterId = memberId
                )

                android.util.Log.d("FRIEND_DEBUG", "응답 코드: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    android.util.Log.d("FRIEND_DEBUG", "받아온 친구 리스트 바디: $body")

                    if (body != null) {
                        sharingFriends = body
                    }
                } else {
                    android.util.Log.e("FRIEND_DEBUG", "에러 Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("FRIEND_DEBUG", "통신 중 예외 발생", e)
            }
            kotlinx.coroutines.delay(3000)
        }
    }


    // ========================================
    // 기본 목적지 DB 조회
    // ========================================

    LaunchedEffect(
        memberId
    ) {

        try {

            defaultDestinations =
                RetrofitClient
                    .destinationApi
                    .getDestinations(
                        memberId
                    )

        } catch (
            e: Exception
        ) {

            e.printStackTrace()

            defaultDestinations =
                emptyList()
        }
    }


    // ========================================
    // 실제 현재 위치 → 주소 변환
    // ========================================

    LaunchedEffect(
        Unit
    ) {

        val finePermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        val coarsePermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )


        if (
            finePermission !=
            PackageManager.PERMISSION_GRANTED &&
            coarsePermission !=
            PackageManager.PERMISSION_GRANTED
        ) {

            currentLocationAddress =
                "위치 권한이 필요합니다."

            return@LaunchedEffect
        }


        try {

            val fusedLocationClient =
                LocationServices
                    .getFusedLocationProviderClient(
                        context
                    )


            fusedLocationClient
                .lastLocation
                .addOnSuccessListener { location ->

                    if (
                        location == null
                    ) {

                        currentLocationAddress =
                            "현재 위치를 확인할 수 없습니다."

                        return@addOnSuccessListener
                    }


                    coroutineScope.launch {

                        try {

                            val address =
                                withContext(
                                    Dispatchers.IO
                                ) {

                                    val geocoder =
                                        Geocoder(
                                            context,
                                            Locale.KOREA
                                        )


                                    @Suppress("DEPRECATION")
                                    val addresses =
                                        geocoder.getFromLocation(
                                            location.latitude,
                                            location.longitude,
                                            1
                                        )


                                    addresses
                                        ?.firstOrNull()
                                        ?.getAddressLine(0)
                                }


                            currentLocationAddress =
                                address
                                    ?.removePrefix(
                                        "대한민국 "
                                    )
                                    ?: "현재 위치"


                        } catch (
                            e: Exception
                        ) {

                            e.printStackTrace()

                            currentLocationAddress =
                                "현재 위치"
                        }
                    }
                }

        } catch (
            e: Exception
        ) {

            e.printStackTrace()

            currentLocationAddress =
                "현재 위치"
        }
    }


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
                        "안심경로",

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
            // 출발지 / 도착지
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

                    title =
                        "출발지",

                    value =
                        currentLocationAddress,

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

                    title =
                        "도착지",

                    value =
                        if (
                            destinationName.isBlank()
                        ) {

                            "목적지를 검색하세요"

                        } else {

                            destinationName
                        },

                    onClick = {

                        onDestinationSearchClick()
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
            // 지도 직접 지정 안내
            // ========================================

            Text(
                text =
                    "지도를 눌러 도착지 위치를 직접 지정할 수도 있습니다.",

                fontSize =
                    12.sp,

                color =
                    TextGray,

                modifier =
                    Modifier.padding(
                        horizontal = 20.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // ========================================
            // 기본 목적지
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
                    text =
                        "기본 목적지",

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextBlack
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            12.dp
                        )
                )


                // ========================================
                // DB 기본 목적지 목록
                // ========================================

                Row(
                    modifier = Modifier
                        .weight(
                            1f
                        )
                        .horizontalScroll(
                            rememberScrollState()
                        ),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    defaultDestinations.forEach { destination ->

                        DefaultDestinationChip(
                            text = destination.name,

                            onClick = {

                                onDefaultDestinationSelected(
                                    destination.placeName,
                                    destination.address,
                                    destination.latitude,
                                    destination.longitude
                                )
                            }
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


// ========================================
            // 카카오맵
            // ========================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(
                        1f
                    )
            ) {

                KakaoMapView(

                    modifier =
                        Modifier.fillMaxSize(),

                    destinationName =
                        destinationName,

                    destinationLatitude =
                        destinationLatitude,

                    destinationLongitude =
                        destinationLongitude,

                    // ⭐️ 위치 공유 중인 모든 친구 리스트 전달
                    sharingFriends = sharingFriends,

                    showRoute =
                        showSelectedRoute,

                    routeMode =
                        when (
                            selectedRouteMode
                        ) {

                            "SHORTEST" ->
                                "SHORTEST"

                            "BROAD_FIRST" ->
                                "BROAD_FIRST"

                            "BRIGHT" ->
                                ""

                            else ->
                                "BROAD_FIRST"
                        },


                    recenterRequestKey =
                        recenterRequestKey,


                    onDestinationSelected = {
                            latitude,
                            longitude ->

                        onMapDestinationSelected(
                            latitude,
                            longitude
                        )
                    }
                )


                // ========================================
                // 현재 위치 버튼
                // ========================================

                Box(
                    modifier = Modifier
                        .align(
                            Alignment.BottomEnd
                        )
                        .padding(
                            end = 18.dp,
                            bottom = 18.dp
                        )
                        .size(
                            46.dp
                        )
                        .clip(
                            CircleShape
                        )
                        .background(
                            Color.White
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
                            TextBlack,

                        modifier =
                            Modifier.size(
                                23.dp
                            )
                    )
                }
            }
        }


        // ========================================
        // 하단 바
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
// 위치 입력
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
                        title == "출발지"
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
                    TextBlack,

                maxLines =
                    1
            )
        }
    }
}


// ========================================
// 기본 목적지 버튼
// ========================================

@Composable
private fun DefaultDestinationChip(

    text: String,

    onClick: () -> Unit

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
            .clickable {

                onClick()
            }
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
                FontWeight.Medium,

            color =
                TextBlack
        )
    }
}


// ========================================
// Preview
// ========================================

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SafeRouteScreenPreview() {

    SafeRouteScreen(
        destinationName =
            "강남역",

        destinationLatitude =
            37.4979,

        destinationLongitude =
            127.0276
    )
}