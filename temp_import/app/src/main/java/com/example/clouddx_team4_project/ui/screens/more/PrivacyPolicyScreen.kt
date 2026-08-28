package com.example.clouddx_team4_project.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

private val PrivacyBackground = Color(0xFFF8F9FC)
private val PrivacyTextBlack = Color(0xFF222222)
private val PrivacyTextGray = Color(0xFF666666)

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrivacyBackground)
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
                tint = PrivacyTextBlack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(21.dp)
                    .clickable {
                        onBackClick()
                    }
            )

            Text(
                text = "개인정보처리방침",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrivacyTextBlack,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 30.dp
                )
        ) {

            Text(
                text = "개인정보처리방침",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrivacyTextBlack
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Rimo는 서비스 제공을 위해 필요한 범위 내에서 개인정보를 처리합니다.",
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = PrivacyTextGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrivacySection(
                title = "1. 수집하는 개인정보",
                content = "회원 가입 및 서비스 이용 과정에서 이름, 아이디, 이메일 등 서비스 제공에 필요한 정보를 수집할 수 있습니다."
            )

            PrivacySection(
                title = "2. 위치정보 이용",
                content = "안심경로, 안심지도 등 위치 기반 기능 제공을 위해 사용자의 현재 위치정보를 이용할 수 있습니다."
            )

            PrivacySection(
                title = "3. 개인정보 이용 목적",
                content = "회원 관리, 안전 기능 제공, 문의 처리 및 서비스 개선을 목적으로 개인정보를 이용합니다."
            )

            PrivacySection(
                title = "4. 개인정보 보관",
                content = "개인정보는 서비스 이용 목적이 달성되거나 회원 탈퇴 시 관련 기준에 따라 삭제 또는 처리됩니다."
            )

            PrivacySection(
                title = "5. 문의",
                content = "개인정보 처리와 관련된 문의는 앱 내 문의하기 기능을 통해 접수할 수 있습니다."
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "※ 본 내용은 프로젝트 시연용 개인정보처리방침입니다.",
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

@Composable
private fun PrivacySection(
    title: String,
    content: String
) {

    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = PrivacyTextBlack
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = content,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        color = PrivacyTextGray
    )

    Spacer(modifier = Modifier.height(22.dp))
}