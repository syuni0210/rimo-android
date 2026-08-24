package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val AnOnBlue = Color(0xFF6A92FE)
private val ScreenBg = Color(0xFFFDFDFD)
private val TextBlack = Color(0xFF222222)
private val TextGray = Color(0xFF8A8A8A)
private val LineGray = Color(0xFFD7D7D7)


@Composable
fun GuardianRegisterScreen(
    onBackClick: () -> Unit = {},
    onRegisterClick: (
        name: String,
        phone: String,
        emergencyMessage: Boolean,
        otherMessage: Boolean
    ) -> Unit = { _, _, _, _ -> }
) {

    var guardianName by remember {
        mutableStateOf("")
    }

    var phoneNumber by remember {
        mutableStateOf("010")
    }

    var emergencyMessageEnabled by remember {
        mutableStateOf(true)
    }

    var otherMessageEnabled by remember {
        mutableStateOf(true)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .statusBarsPadding()
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 22.dp
            )
    ) {

        // ========================================
        // 상단바
        // ========================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
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
                text = "보호자 추가",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // ========================================
        // 보호자 이름
        // ========================================

        Text(
            text = "보호자이름",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        UnderlineTextField(
            value = guardianName,
            onValueChange = {
                guardianName = it
            },
            placeholder = "이름을 입력하세요",
            keyboardType = KeyboardType.Text
        )


        Spacer(
            modifier = Modifier.height(30.dp)
        )


        // ========================================
        // 보호자 연락처
        // ========================================

        Text(
            text = "보호자 연락처",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        UnderlineTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
            },
            placeholder = "010-0000-0000",
            keyboardType = KeyboardType.Phone
        )


        Spacer(
            modifier = Modifier.height(34.dp)
        )


        // ========================================
        // 긴급신고 메시지
        // ========================================

        SettingSwitchRow(
            iconType = 1,
            title = "긴급신고 메시지 발송여부",
            checked = emergencyMessageEnabled,
            onCheckedChange = {
                emergencyMessageEnabled = it
            }
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // ========================================
        // 기타 메시지
        // ========================================

        SettingSwitchRow(
            iconType = 2,
            title = "기타 메시지 발송여부",
            subtitle = "(귀가모니터링, 안심경로)",
            checked = otherMessageEnabled,
            onCheckedChange = {
                otherMessageEnabled = it
            }
        )


        Spacer(
            modifier = Modifier.weight(1f)
        )


        // ========================================
        // 등록 버튼
        // ========================================

        Button(
            onClick = {

                onRegisterClick(
                    guardianName,
                    phoneNumber,
                    emergencyMessageEnabled,
                    otherMessageEnabled
                )
            },

            enabled =
                guardianName.isNotBlank() &&
                        phoneNumber.isNotBlank(),

            colors = ButtonDefaults.buttonColors(
                containerColor = AnOnBlue,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFD2DCFA),
                disabledContentColor = Color.White
            ),

            shape = RoundedCornerShape(11.dp),

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {

            Text(
                text = "등록하기",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ========================================
// 밑줄 입력창
// ========================================

@Composable
private fun UnderlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType
) {

    TextField(
        value = value,

        onValueChange = onValueChange,

        placeholder = {

            Text(
                text = placeholder,
                fontSize = 16.sp,
                color = TextGray
            )
        },

        singleLine = true,

        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),

        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            color = TextBlack
        ),

        colors = TextFieldDefaults.colors(

            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,

            focusedIndicatorColor = AnOnBlue,
            unfocusedIndicatorColor = LineGray,

            disabledIndicatorColor = LineGray,

            cursorColor = AnOnBlue
        ),

        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}


// ========================================
// 토글 한 줄
// ========================================

@Composable
private fun SettingSwitchRow(
    iconType: Int,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector =
                if (iconType == 1) {
                    Icons.Filled.Notifications
                } else {
                    Icons.Filled.Email
                },

            contentDescription = null,

            tint =
                if (iconType == 1) {
                    Color(0xFFFF4A4A)
                } else {
                    Color(0xFFFFB800)
                },

            modifier = Modifier.size(21.dp)
        )


        Spacer(
            modifier = Modifier.width(10.dp)
        )


        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextBlack
            )

            if (subtitle != null) {

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }


        Switch(
            checked = checked,

            onCheckedChange = onCheckedChange,

            colors = SwitchDefaults.colors(

                checkedThumbColor = Color.White,

                checkedTrackColor = AnOnBlue,

                uncheckedThumbColor = Color.White,

                uncheckedTrackColor = Color(0xFFD8D8D8)
            )
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
fun GuardianRegisterScreenPreview() {

    GuardianRegisterScreen()
}