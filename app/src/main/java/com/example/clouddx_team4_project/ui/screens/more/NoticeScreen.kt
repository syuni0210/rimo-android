package com.example.clouddx_team4_project.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val NoticeBackground =
    Color(0xFFF8F9FC)

private val NoticeTextBlack =
    Color(0xFF222222)

private val NoticeTextGray =
    Color(0xFF888888)

private val NoticeBlue =
    Color(0xFF6A92FE)


// ========================================
// 공지사항 데이터
// 나중에 API 응답 DTO로 변경하면 됨
// ========================================

data class NoticeItem(
    val id: Int,
    val title: String,
    val date: String,
    val content: String
)


// ========================================
// 공지사항 화면
// ========================================

@Composable
fun NoticeScreen(

    onBackClick: () -> Unit = {}

) {

    val notices =
        remember {

            listOf(

                NoticeItem(
                    id = 1,
                    title = "안온 서비스 이용 안내",
                    date = "2026.08.26",
                    content =
                        "안온 서비스를 이용해 주셔서 감사합니다.\n\n" +
                                "안심경로, 안심지도, 안심친구 등 주요 기능을 통해 " +
                                "보다 안전한 귀가를 지원합니다."
                ),

                NoticeItem(
                    id = 2,
                    title = "안심지도 기능 업데이트 안내",
                    date = "2026.08.25",
                    content =
                        "안심지도 기능이 추가되었습니다.\n\n" +
                                "지도에서 CCTV, 가로등, 지킴이집, 지구대, " +
                                "비상벨, 보안등 등의 안전시설 정보를 확인할 수 있습니다."
                ),

                NoticeItem(
                    id = 3,
                    title = "안심경로 이용 안내",
                    date = "2026.08.24",
                    content =
                        "안심경로에서 목적지를 검색한 후 원하는 경로를 선택할 수 있습니다.\n\n" +
                                "지도 우측 하단의 현재 위치 버튼을 누르면 " +
                                "현재 위치로 지도를 다시 이동할 수 있습니다."
                )
            )
        }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                NoticeBackground
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
                    NoticeTextBlack,

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
                    "공지사항",

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    NoticeTextBlack,

                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )
        }


        // ========================================
        // 안내 문구
        // ========================================

        Text(
            text =
                "안온의 새로운 소식을 확인해보세요.",

            fontSize =
                14.sp,

            color =
                NoticeTextGray,

            modifier =
                Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 16.dp
                )
        )


        // ========================================
        // 공지사항 목록
        // ========================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp
                )
                .background(
                    color =
                        Color.White,

                    shape =
                        RoundedCornerShape(
                            18.dp
                        )
                )
        ) {

            notices.forEachIndexed {
                    index,
                    notice ->

                NoticeRow(
                    notice =
                        notice
                )


                if (
                    index != notices.lastIndex
                ) {

                    Divider(
                        modifier =
                            Modifier.padding(
                                horizontal = 18.dp
                            ),

                        color =
                            Color(
                                0xFFF0F0F0
                            )
                    )
                }
            }
        }
    }
}


// ========================================
// 공지사항 한 줄
// ========================================

@Composable
private fun NoticeRow(

    notice: NoticeItem

) {

    var expanded by remember {
        mutableStateOf(
            false
        )
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                expanded =
                    !expanded
            }
            .padding(
                horizontal = 18.dp,
                vertical = 18.dp
            )
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(
                    text =
                        notice.title,

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        NoticeTextBlack
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            5.dp
                        )
                )


                Text(
                    text =
                        notice.date,

                    fontSize =
                        12.sp,

                    color =
                        NoticeTextGray
                )
            }


            Icon(
                imageVector =
                    if (
                        expanded
                    ) {

                        Icons.Filled.KeyboardArrowUp

                    } else {

                        Icons.Filled.KeyboardArrowDown
                    },

                contentDescription =
                    null,

                tint =
                    NoticeTextGray,

                modifier =
                    Modifier.size(
                        22.dp
                    )
            )
        }


        // ========================================
        // 펼쳐진 내용
        // ========================================

        if (
            expanded
        ) {

            Spacer(
                modifier =
                    Modifier.height(
                        15.dp
                    )
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color =
                            Color(
                                0xFFF6F7FA
                            ),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            )
                    )
                    .padding(
                        15.dp
                    )
            ) {

                Text(
                    text =
                        notice.content,

                    fontSize =
                        14.sp,

                    lineHeight =
                        21.sp,

                    color =
                        Color(
                            0xFF555555
                        )
                )
            }
        }
    }
}