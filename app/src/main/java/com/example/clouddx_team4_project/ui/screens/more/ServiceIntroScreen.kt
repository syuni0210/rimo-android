package com.example.clouddx_team4_project.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val IntroBlue = Color(0xFF6A92FE)
private val IntroBackground = Color(0xFFF8F9FC)
private val IntroTextBlack = Color(0xFF222222)
private val IntroTextGray = Color(0xFF777777)

@Composable
fun ServiceIntroScreen(
    onBackClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IntroBackground)
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
                tint = IntroTextBlack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(21.dp)
                    .clickable {
                        onBackClick()
                    }
            )

            Text(
                text = "서비스 소개",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = IntroTextBlack,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(22.dp))

            Box(
                modifier = Modifier
                    .size(82.dp)
                    .background(
                        color = Color(0xFFE8EEFF),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = IntroBlue,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rimo",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = IntroBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "더 안전한 귀가를 위한 안전 지원 서비스",
                fontSize = 15.sp,
                color = IntroTextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            IntroCard(
                title = "안심경로",
                content = "목적지까지의 이동 경로를 확인하고 안전한 귀가를 지원합니다."
            )

            Spacer(modifier = Modifier.height(12.dp))

            IntroCard(
                title = "안심지도",
                content = "CCTV, 가로등, 지구대, 비상벨 등 안전시설 정보를 지도에서 확인할 수 있습니다."
            )

            Spacer(modifier = Modifier.height(12.dp))

            IntroCard(
                title = "안심친구",
                content = "등록한 안심친구와 귀가 상황을 공유하고 필요한 경우 도움을 요청할 수 있습니다."
            )

            Spacer(modifier = Modifier.height(12.dp))

            IntroCard(
                title = "긴급 기능",
                content = "위급 상황에서 긴급구조 기능과 꽥꽥이를 빠르게 사용할 수 있습니다."
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun IntroCard(
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = IntroTextBlack
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = content,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = IntroTextGray
        )
    }
}