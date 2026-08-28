package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.BuildConfig
import com.example.clouddx_team4_project.data.KakaoLocalClient
import com.example.clouddx_team4_project.data.KakaoPlace
import kotlinx.coroutines.launch


private val MainBlue = Color(0xFF6A92FE)


@Composable
fun DestinationSearchScreen(
    onBackClick: () -> Unit = {},
    onPlaceSelected: (KakaoPlace) -> Unit = {}
) {

    var keyword by remember {
        mutableStateOf("")
    }

    var searchResults by remember {
        mutableStateOf<List<KakaoPlace>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val coroutineScope = rememberCoroutineScope()


    fun searchPlace() {

        if (keyword.isBlank()) {
            return
        }

        coroutineScope.launch {

            isLoading = true
            errorMessage = null

            try {

                val response =
                    KakaoLocalClient.api.searchKeyword(
                        authorization =
                            "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",

                        query = keyword
                    )

                searchResults =
                    response.documents

            } catch (e: Exception) {

                errorMessage =
                    "장소 검색 중 오류가 발생했습니다."

                e.printStackTrace()

            } finally {

                isLoading = false
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {

        // 상단 제목
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {

            Icon(
                imageVector =
                    Icons.Filled.ArrowBackIosNew,

                contentDescription =
                    "뒤로가기",

                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(21.dp)
                    .clickable {
                        onBackClick()
                    }
            )

            Text(
                text = "목적지 검색",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,

                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // 검색창
        OutlinedTextField(
            value = keyword,

            onValueChange = {
                keyword = it
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
                    }
                ) {

                    Text(
                        text = "검색",
                        color = MainBlue,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },

            singleLine = true,

            shape =
                RoundedCornerShape(14.dp)
        )


        if (isLoading) {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            LinearProgressIndicator(
                modifier =
                    Modifier.fillMaxWidth(),

                color =
                    MainBlue
            )
        }


        errorMessage?.let {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text = it,

                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        // 검색 결과
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            onPlaceSelected(
                                place
                            )
                        }
                        .padding(
                            vertical = 16.dp
                        ),

                    verticalAlignment =
                        Alignment.Top
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.LocationOn,

                        contentDescription =
                            null,

                        tint =
                            MainBlue,

                        modifier =
                            Modifier.size(23.dp)
                    )


                    Spacer(
                        modifier =
                            Modifier.width(12.dp)
                    )


                    Column {

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
                                Modifier.height(4.dp)
                        )


                        Text(
                            text =
                                if (
                                    place.roadAddressName
                                        .isNotBlank()
                                ) {
                                    place.roadAddressName
                                } else {
                                    place.addressName
                                },

                            fontSize =
                                13.sp,

                            color =
                                Color.Gray
                        )
                    }
                }


                HorizontalDivider(
                    color =
                        Color(0xFFEEEEEE)
                )
            }
        }
    }
}