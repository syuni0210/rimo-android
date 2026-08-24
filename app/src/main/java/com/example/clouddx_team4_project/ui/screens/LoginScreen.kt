package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ========================================
// 안온 색상
// ========================================

private val AnOnBlue = Color(0xFF6A92FE)

private val InputTextColor = Color(0xFF222222)

private val PlaceholderColor = Color(0xFF8E8E8E)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: (id: String, password: String) -> Unit = { _, _ -> },

    // 회원가입 버튼 클릭
    onSignUpClick: () -> Unit = {}
) {

    var userId by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnOnBlue)
            .statusBarsPadding()
            .padding(horizontal = 26.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.weight(0.9f)
        )


        // ========================================
        // 로고
        // ========================================

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = Color.White.copy(alpha = 0.18f),
                    shape = CircleShape
                ),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "안온 로고",
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )
        }


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // ========================================
        // 안 온
        // ========================================

        Text(
            text = "안 온",
            color = Color.White,

            // 기존보다 크게
            fontSize = 38.sp,

            fontWeight = FontWeight.Bold,
            letterSpacing = 5.sp
        )


        Spacer(
            modifier = Modifier.height(10.dp)
        )


        // ========================================
        // 설명
        // ========================================

        Text(
            text = "안전한 귀가, 안심하고 함께해요",
            color = Color.White.copy(alpha = 0.9f),

            // 기존 14 -> 16
            fontSize = 16.sp,

            fontWeight = FontWeight.Medium
        )


        Spacer(
            modifier = Modifier.height(42.dp)
        )


        // ========================================
        // 아이디 입력
        // ========================================

        OutlinedTextField(
            value = userId,

            onValueChange = {
                userId = it
            },

            placeholder = {

                Text(
                    text = "아이디를 입력하세요",

                    // 기존보다 크게
                    fontSize = 16.sp,

                    color = PlaceholderColor
                )
            },

            leadingIcon = {

                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = AnOnBlue,
                    modifier = Modifier.size(25.dp)
                )
            },

            singleLine = true,

            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                color = InputTextColor
            ),

            shape = RoundedCornerShape(14.dp),

            colors = OutlinedTextFieldDefaults.colors(

                focusedContainerColor = Color.White,

                unfocusedContainerColor = Color.White,

                focusedBorderColor = Color.White,

                unfocusedBorderColor = Color.White,

                cursorColor = AnOnBlue,

                focusedTextColor = InputTextColor,

                unfocusedTextColor = InputTextColor
            ),

            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // ========================================
        // 비밀번호 입력
        // ========================================

        OutlinedTextField(
            value = password,

            onValueChange = {
                password = it
            },

            placeholder = {

                Text(
                    text = "비밀번호를 입력하세요",

                    fontSize = 16.sp,

                    color = PlaceholderColor
                )
            },

            leadingIcon = {

                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = AnOnBlue,
                    modifier = Modifier.size(25.dp)
                )
            },

            trailingIcon = {

                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {

                    Icon(
                        imageVector =
                            if (passwordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            },

                        contentDescription =
                            if (passwordVisible) {
                                "비밀번호 숨기기"
                            } else {
                                "비밀번호 보기"
                            },

                        tint = Color(0xFF999999),

                        modifier = Modifier.size(24.dp)
                    )
                }
            },

            singleLine = true,

            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                color = InputTextColor
            ),

            visualTransformation =
                if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),

            shape = RoundedCornerShape(14.dp),

            colors = OutlinedTextFieldDefaults.colors(

                focusedContainerColor = Color.White,

                unfocusedContainerColor = Color.White,

                focusedBorderColor = Color.White,

                unfocusedBorderColor = Color.White,

                cursorColor = AnOnBlue,

                focusedTextColor = InputTextColor,

                unfocusedTextColor = InputTextColor
            ),

            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        )


        Spacer(
            modifier = Modifier.height(26.dp)
        )


        // ========================================
        // 로그인 버튼
        // ========================================

        Button(
            onClick = {

                onLoginClick(
                    userId,
                    password
                )
            },

            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = AnOnBlue
            ),

            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        ) {

            Text(
                text = "로그인",

                // 기존보다 크게
                fontSize = 18.sp,

                fontWeight = FontWeight.Bold
            )
        }


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        // ========================================
        // 회원가입 버튼
        // ========================================

        OutlinedButton(
            onClick = {

                // AppNavigation에서 signup으로 연결
                onSignUpClick()
            },

            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),

            border = BorderStroke(
                width = 1.5.dp,
                color = Color.White
            ),

            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        ) {

            Text(
                text = "회원가입",

                // 기존보다 크게
                fontSize = 18.sp,

                fontWeight = FontWeight.Bold
            )
        }


        Spacer(
            modifier = Modifier.weight(1.2f)
        )
    }
}


@Preview(
    showBackground = true
)
@Composable
fun LoginScreenPreview() {

    LoginScreen()
}