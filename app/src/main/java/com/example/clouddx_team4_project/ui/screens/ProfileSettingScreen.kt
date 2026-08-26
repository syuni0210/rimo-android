package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ========================================
// 색상
// ========================================

private val ProfileBlue =
    Color(0xFF6A92FE)

private val ProfileScreenBackground =
    Color(0xFFF8F9FC)

private val ProfileTextBlack =
    Color(0xFF222222)

private val ProfileTextGray =
    Color(0xFF888888)

private val ProfileBorderGray =
    Color(0xFFE4E7EE)


// ========================================
// 프로필 설정 화면
// ========================================

@Composable
fun ProfileSettingScreen(

    // ========================================
    // 초기 사용자 정보
    // ========================================

    initialName: String = "이지연",

    userId: String = "jiyeon123",

    initialEmail: String = "jiyeon@example.com",


    // ========================================
    // 뒤로가기
    // ========================================

    onBackClick: () -> Unit = {},


    // ========================================
    // 저장
    // ========================================

    onSaveClick: (
        name: String,
        email: String
    ) -> Unit = { _, _ -> }
) {

    // ========================================
    // 입력 상태
    // ========================================

    var name by remember {
        mutableStateOf(
            initialName
        )
    }


    var email by remember {
        mutableStateOf(
            initialEmail
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ProfileScreenBackground
            )
    ) {


        // ========================================
        // 상단바
        // ========================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(
                    64.dp
                )
                .padding(
                    horizontal = 20.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // ========================================
            // 뒤로가기
            // ========================================

            Icon(
                imageVector =
                    Icons.Filled.ArrowBackIosNew,

                contentDescription =
                    "뒤로가기",

                tint =
                    ProfileTextBlack,

                modifier = Modifier
                    .size(
                        22.dp
                    )
                    .clickable {

                        onBackClick()
                    }
            )


            // ========================================
            // 제목
            // ========================================

            Text(
                text =
                    "프로필 설정",

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    ProfileTextBlack,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            // 제목 중앙 정렬용
            Spacer(
                modifier =
                    Modifier.size(
                        22.dp
                    )
            )
        }


        // ========================================
        // 프로필 영역
        // ========================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 26.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            // ========================================
            // 프로필 이미지
            // ========================================

            Box(
                contentAlignment =
                    Alignment.BottomEnd
            ) {

                Box(
                    modifier = Modifier
                        .size(
                            104.dp
                        )
                        .clip(
                            CircleShape
                        )
                        .background(
                            Color(
                                0xFFE8EEFF
                            )
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.AccountCircle,

                        contentDescription =
                            "프로필",

                        tint =
                            ProfileBlue,

                        modifier =
                            Modifier.size(
                                82.dp
                            )
                    )
                }


                // ========================================
                // 프로필 수정 아이콘
                // ========================================

                Box(
                    modifier = Modifier
                        .size(
                            34.dp
                        )
                        .clip(
                            CircleShape
                        )
                        .background(
                            ProfileBlue
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.Edit,

                        contentDescription =
                            "프로필 수정",

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(
                                17.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        13.dp
                    )
            )


            Text(
                text =
                    "${name}님",

                fontSize =
                    21.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    ProfileTextBlack
            )


            Spacer(
                modifier =
                    Modifier.height(
                        5.dp
                    )
            )


            Text(
                text =
                    "@$userId",

                fontSize =
                    14.sp,

                color =
                    ProfileTextGray
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    32.dp
                )
        )


        // ========================================
        // 프로필 정보 카드
        // ========================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp
                )
                .clip(
                    RoundedCornerShape(
                        20.dp
                    )
                )
                .background(
                    Color.White
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                )
        ) {


            // ========================================
            // 이름
            // ========================================

            ProfileSettingLabel(
                text = "이름"
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            OutlinedTextField(
                value =
                    name,

                onValueChange = {

                    name = it
                },

                placeholder = {

                    Text(
                        text =
                            "이름을 입력해주세요",

                        color =
                            Color(
                                0xFFAAAAAA
                            )
                    )
                },

                singleLine =
                    true,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        12.dp
                    ),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            ProfileBlue,

                        unfocusedBorderColor =
                            ProfileBorderGray,

                        cursorColor =
                            ProfileBlue,

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        22.dp
                    )
            )


            // ========================================
            // 아이디
            // ========================================

            ProfileSettingLabel(
                text = "아이디"
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            OutlinedTextField(
                value =
                    userId,

                onValueChange = {},

                readOnly =
                    true,

                singleLine =
                    true,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        12.dp
                    ),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            ProfileBorderGray,

                        unfocusedBorderColor =
                            ProfileBorderGray,

                        cursorColor =
                            Color.Transparent,

                        focusedContainerColor =
                            Color(
                                0xFFF5F6F8
                            ),

                        unfocusedContainerColor =
                            Color(
                                0xFFF5F6F8
                            ),

                        focusedTextColor =
                            Color(
                                0xFF777777
                            ),

                        unfocusedTextColor =
                            Color(
                                0xFF777777
                            )
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Text(
                text =
                    "아이디는 변경할 수 없어요.",

                fontSize =
                    12.sp,

                color =
                    Color(
                        0xFFAAAAAA
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        22.dp
                    )
            )


            // ========================================
            // 이메일
            // ========================================

            ProfileSettingLabel(
                text =
                    "이메일"
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            OutlinedTextField(
                value =
                    email,

                onValueChange = {

                    email = it
                },

                placeholder = {

                    Text(
                        text =
                            "이메일을 입력해주세요",

                        color =
                            Color(
                                0xFFAAAAAA
                            )
                    )
                },

                singleLine =
                    true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Email
                    ),

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        12.dp
                    ),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            ProfileBlue,

                        unfocusedBorderColor =
                            ProfileBorderGray,

                        cursorColor =
                            ProfileBlue,

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    )
            )
        }


        // ========================================
        // 아래 공간
        // ========================================

        Spacer(
            modifier =
                Modifier.weight(
                    1f
                )
        )


        // ========================================
        // 저장 버튼
        // ========================================

        Button(
            onClick = {

                if (
                    name.isNotBlank() &&
                    email.isNotBlank()
                ) {

                    onSaveClick(
                        name.trim(),
                        email.trim()
                    )
                }
            },

            enabled =
                name.isNotBlank() &&
                        email.isNotBlank(),

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp
                )
                .height(
                    56.dp
                ),

            shape =
                RoundedCornerShape(
                    14.dp
                ),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        ProfileBlue,

                    disabledContainerColor =
                        Color(
                            0xFFD2DCF6
                        )
                )
        ) {

            Text(
                text =
                    "저장하기",

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color.White
            )
        }


        Spacer(
            modifier = Modifier
                .navigationBarsPadding()
                .height(
                    20.dp
                )
        )
    }
}


// ========================================
// 입력 항목 제목
// ========================================

@Composable
private fun ProfileSettingLabel(
    text: String
) {

    Text(
        text =
            text,

        fontSize =
            14.sp,

        fontWeight =
            FontWeight.SemiBold,

        color =
            Color(
                0xFF333333
            )
    )
}


// ========================================
// Preview
// ========================================

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true
)
@Composable
fun ProfileSettingScreenPreview() {

    ProfileSettingScreen()
}