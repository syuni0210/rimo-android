package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.BuildConfig
import com.example.clouddx_team4_project.data.KakaoLocalClient
import com.example.clouddx_team4_project.data.KakaoPlace
import kotlinx.coroutines.launch


private val MainBlue =
    Color(0xFF6A92FE)


@Composable
fun DestinationSearchScreen(

    currentLatitude: Double? = null,

    currentLongitude: Double? = null,

    onBackClick: () -> Unit = {},

    onPlaceSelected: (KakaoPlace) -> Unit = {}

) {

    var keyword by remember {
        mutableStateOf("")
    }

    var searchResults by remember {
        mutableStateOf<List<KakaoPlace>>(
            emptyList()
        )
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val coroutineScope =
        rememberCoroutineScope()

    val keyboardController =
        LocalSoftwareKeyboardController.current


    // ========================================
    // 검색 결과 정렬
    //
    // 일반 네비게이션처럼
    // "종각역" 검색 시 실제 지하철역을 먼저 표시
    //
    // 1순위: "종각역" + SW8 지하철역 + 역 이름 일치
    // 2순위: 검색어와 장소명 완전 일치
    // 3순위: 역 검색 + SW8 지하철역 + 검색어로 시작
    // 4순위: 검색어로 시작
    // 5순위: 검색어 포함
    //
    // 같은 순위에서는 가까운 순
    // ========================================

    fun sortPlaces(
        places: List<KakaoPlace>,
        searchKeyword: String
    ): List<KakaoPlace> {

        fun normalize(
            value: String
        ): String {

            return value
                .trim()
                .replace(" ", "")
                .lowercase()
        }


        val normalizedKeyword =
            normalize(
                searchKeyword
            )


        // "종각역", "강남역" 같이
        // '역'으로 끝나는 검색어인지 확인
        val isStationSearch =
            normalizedKeyword
                .endsWith("역")


        fun relevanceRank(
            place: KakaoPlace
        ): Int {

            val placeName =
                normalize(
                    place.placeName
                )


            // 예:
            // "종각역1호선"
            // → 끝의 "1호선" 제거
            // → "종각역"
            val stationBaseName =
                placeName.replace(
                    Regex("\\d+호선$"),
                    ""
                )


            // Kakao 카테고리
            // SW8 = 지하철역
            val isSubwayStation =
                place.categoryGroupCode ==
                        "SW8"


            return when {

                // ========================================
                // 1순위
                //
                // 검색: 종각역
                // 결과: 종각역 1호선
                // ========================================
                isStationSearch &&
                        isSubwayStation &&
                        stationBaseName ==
                        normalizedKeyword -> {

                    0
                }


                // ========================================
                // 2순위
                // 장소명 완전 일치
                // ========================================
                placeName ==
                        normalizedKeyword -> {

                    1
                }


                // ========================================
                // 3순위
                // 역 검색 + 실제 지하철역
                // ========================================
                isStationSearch &&
                        isSubwayStation &&
                        placeName.startsWith(
                            normalizedKeyword
                        ) -> {

                    2
                }


                // ========================================
                // 4순위
                // 검색어로 시작
                // ========================================
                placeName.startsWith(
                    normalizedKeyword
                ) -> {

                    3
                }


                // ========================================
                // 5순위
                // 검색어 포함
                // ========================================
                placeName.contains(
                    normalizedKeyword
                ) -> {

                    4
                }


                else -> {

                    5
                }
            }
        }


        return places.sortedWith(

            compareBy<KakaoPlace> {

                relevanceRank(
                    it
                )

            }.thenBy {

                it.distance
                    .toIntOrNull()
                    ?: Int.MAX_VALUE
            }
        )
    }


    // ========================================
    // 장소 검색
    // ========================================

    fun searchPlace() {

        val trimmedKeyword =
            keyword.trim()


        if (
            trimmedKeyword.isBlank()
        ) {

            return
        }


        coroutineScope.launch {

            isLoading =
                true

            errorMessage =
                null


            try {

                val response =
                    KakaoLocalClient
                        .api
                        .searchKeyword(

                            authorization =
                                "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",

                            query =
                                trimmedKeyword,

                            // 현재 위치는 거리 계산용
                            longitude =
                                currentLongitude,

                            latitude =
                                currentLatitude,

                            // Kakao 검색 자체는 정확도 우선
                            sort =
                                "accuracy"
                        )


                searchResults =
                    sortPlaces(

                        places =
                            response.documents,

                        searchKeyword =
                            trimmedKeyword
                    )


            } catch (e: Exception) {

                errorMessage =
                    "장소 검색 중 오류가 발생했습니다."

                e.printStackTrace()


            } finally {

                isLoading =
                    false
            }
        }
    }


    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    horizontal =
                        20.dp
                )

    ) {


        // ========================================
        // 상단 제목
        // ========================================

        Box(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        60.dp
                    )

        ) {

            Icon(

                imageVector =
                    Icons.Filled
                        .ArrowBackIosNew,

                contentDescription =
                    "뒤로가기",

                modifier =
                    Modifier
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
                    "목적지 검색",

                fontSize =
                    21.sp,

                fontWeight =
                    FontWeight.Bold,

                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )
        }


        Spacer(

            modifier =
                Modifier.height(
                    12.dp
                )
        )


        // ========================================
        // 검색창
        // ========================================

        OutlinedTextField(

            value =
                keyword,

            onValueChange = {

                keyword =
                    it
            },

            modifier =
                Modifier.fillMaxWidth(),

            placeholder = {

                Text(

                    text =
                        "장소 또는 주소를 검색하세요"
                )
            },


            leadingIcon = {

                Icon(

                    imageVector =
                        Icons.Filled.Search,

                    contentDescription =
                        null,

                    tint =
                        MainBlue
                )
            },


            trailingIcon = {

                TextButton(

                    onClick = {

                        searchPlace()

                        keyboardController
                            ?.hide()
                    }

                ) {

                    Text(

                        text =
                            "검색",

                        color =
                            MainBlue,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },


            // 키보드 오른쪽 아래 버튼을
            // Search로 표시
            keyboardOptions =
                KeyboardOptions(

                    imeAction =
                        ImeAction.Search
                ),


            // 키보드 검색 버튼을 눌러도
            // 화면 검색 버튼과 같은 함수 실행
            keyboardActions =
                KeyboardActions(

                    onSearch = {

                        searchPlace()

                        keyboardController
                            ?.hide()
                    }
                ),


            singleLine =
                true,


            shape =
                RoundedCornerShape(
                    14.dp
                )
        )


        // ========================================
        // 로딩
        // ========================================

        if (
            isLoading
        ) {

            Spacer(

                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            LinearProgressIndicator(

                modifier =
                    Modifier.fillMaxWidth(),

                color =
                    MainBlue
            )
        }


        // ========================================
        // 에러
        // ========================================

        errorMessage?.let {

            Spacer(

                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            Text(

                text =
                    it,

                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }


        Spacer(

            modifier =
                Modifier.height(
                    10.dp
                )
        )


        // ========================================
        // 검색 결과
        // ========================================

        LazyColumn(

            modifier =
                Modifier.fillMaxSize()

        ) {

            items(

                items =
                    searchResults,

                key = {
                    it.id
                }

            ) { place ->


                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {

                                onPlaceSelected(
                                    place
                                )
                            }
                            .padding(
                                vertical =
                                    16.dp
                            ),

                    verticalAlignment =
                        Alignment.Top

                ) {


                    Icon(

                        imageVector =
                            Icons.Filled
                                .LocationOn,

                        contentDescription =
                            null,

                        tint =
                            MainBlue,

                        modifier =
                            Modifier.size(
                                23.dp
                            )
                    )


                    Spacer(

                        modifier =
                            Modifier.width(
                                12.dp
                            )
                    )


                    Column(

                        modifier =
                            Modifier.weight(
                                1f
                            )

                    ) {


                        Text(

                            text =
                                place.placeName,

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(

                            modifier =
                                Modifier.height(
                                    4.dp
                                )
                        )


                        Text(

                            text =

                                if (
                                    place
                                        .roadAddressName
                                        .isNotBlank()
                                ) {

                                    place
                                        .roadAddressName

                                } else {

                                    place
                                        .addressName
                                },

                            fontSize =
                                13.sp,

                            color =
                                Color.Gray
                        )


                        // ========================================
                        // 현재 위치와 거리 표시
                        // ========================================

                        val distanceMeter =
                            place.distance
                                .toIntOrNull()


                        if (
                            distanceMeter != null
                        ) {

                            Spacer(

                                modifier =
                                    Modifier.height(
                                        3.dp
                                    )
                            )


                            val distanceText =

                                if (
                                    distanceMeter <
                                    1000
                                ) {

                                    "${distanceMeter}m"

                                } else {

                                    String.format(

                                        "%.1fkm",

                                        distanceMeter /
                                                1000.0
                                    )
                                }


                            Text(

                                text =
                                    distanceText,

                                fontSize =
                                    12.sp,

                                color =
                                    MainBlue
                            )
                        }


                        // 카테고리도 있으면 작게 표시
                        if (
                            place.categoryName
                                .isNotBlank()
                        ) {

                            Spacer(

                                modifier =
                                    Modifier.height(
                                        2.dp
                                    )
                            )


                            Text(

                                text =
                                    place.categoryName,

                                fontSize =
                                    11.sp,

                                color =
                                    Color(
                                        0xFF999999
                                    )
                            )
                        }
                    }
                }


                HorizontalDivider(

                    color =
                        Color(
                            0xFFEEEEEE
                        )
                )
            }
        }
    }
}