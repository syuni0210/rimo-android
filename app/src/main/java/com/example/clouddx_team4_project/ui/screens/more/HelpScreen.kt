package com.example.clouddx_team4_project.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ScreenBackground = Color(0xFFF8F9FC)
private val TextBlack = Color(0xFF222222)
private val TextGray = Color(0xFF777777)

@Composable
fun HelpScreen(
    onBackClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {

        // 상단
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(62.dp)
                .padding(horizontal = 20.dp)
        ) {

            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "뒤로가기",
                tint = TextBlack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(21.dp)
                    .clickable {
                        onBackClick()
                    }
            )

            Text(
                text = "도움말",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {

            HelpCard(
                title = "안심경로는 어떻게 사용하나요?",
                content = "안심경로에서 목적지를 검색한 뒤 원하는 경로를 선택하면 길안내를 시작할 수 있습니다."
            )

            Spacer(modifier = Modifier.height(12.dp))

            HelpCard(
                title = "현재 위치로 돌아가려면 어떻게 하나요?",
                content = "지도 우측 하단의 현재 위치 버튼을 누르면 현재 GPS 위치로 지도가 이동합니다."
            )

            Spacer(modifier = Modifier.height(12.dp))

            HelpCard(
                title = "안심지도에서는 무엇을 볼 수 있나요?",
                content = "CCTV, 가로등, 지킴이집, 지구대, 비상벨, 보안등 등 주변 안전시설을 지도에서 확인할 수 있습니다."
            )

            Spacer(modifier = Modifier.height(12.dp))

            HelpCard(
                title = "꽥꽥이는 무엇인가요?",
                content = "위급하거나 도움이 필요한 상황에서 주변에 큰 소리로 위험 상황을 알릴 수 있는 기능입니다."
            )

            Spacer(modifier = Modifier.height(12.dp))

            HelpCard(
                title = "문의가 필요한 경우",
                content = "더보기 화면의 문의하기 메뉴를 이용해 문의 내용을 남길 수 있습니다."
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun HelpCard(
    title: String,
    content: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(18.dp)
    ) {

        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = content,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = TextGray
        )
    }
}