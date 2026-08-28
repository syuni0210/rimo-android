package com.example.clouddx_team4_project.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val InquiryBackground =
    Color(0xFFF8F9FC)

private val InquiryBlue =
    Color(0xFF6A92FE)

private val InquiryTextBlack =
    Color(0xFF222222)

private val InquiryTextGray =
    Color(0xFF888888)


// ========================================
// 문의하기
// ========================================

@Composable
fun InquiryScreen(

    onBackClick: () -> Unit = {}

) {

    var category by remember {
        mutableStateOf(
            "기능 문의"
        )
    }


    var categoryExpanded by remember {
        mutableStateOf(
            false
        )
    }


    var title by remember {
        mutableStateOf(
            ""
        )
    }


    var content by remember {
        mutableStateOf(
            ""
        )
    }


    var showSuccessDialog by remember {
        mutableStateOf(
            false
        )
    }


    val categories =
        listOf(
            "기능 문의",
            "오류 문의",
            "계정 문의",
            "서비스 개선 제안",
            "기타"
        )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                InquiryBackground
            )
            .imePadding()
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
                    InquiryTextBlack,

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
                    "문의하기",

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    InquiryTextBlack,

                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp
                )
        ) {


            Text(
                text =
                    "서비스 이용 중 궁금한 점을 남겨주세요.",

                fontSize =
                    14.sp,

                color =
                    InquiryTextGray
            )


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            // ========================================
            // 문의 유형
            // ========================================

            InquiryLabel(
                text =
                    "문의 유형"
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Box {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            54.dp
                        )
                        .background(
                            color =
                                Color.White,

                            shape =
                                RoundedCornerShape(
                                    12.dp
                                )
                        )
                        .clickable {

                            categoryExpanded =
                                true
                        }
                        .padding(
                            horizontal = 16.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            category,

                        fontSize =
                            14.sp,

                        color =
                            InquiryTextBlack,

                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )


                    Icon(
                        imageVector =
                            Icons.Filled.KeyboardArrowDown,

                        contentDescription =
                            null,

                        tint =
                            InquiryTextGray
                    )
                }


                DropdownMenu(
                    expanded =
                        categoryExpanded,

                    onDismissRequest = {

                        categoryExpanded =
                            false
                    }
                ) {

                    categories.forEach {
                            item ->

                        DropdownMenuItem(

                            text = {

                                Text(
                                    text =
                                        item
                                )
                            },

                            onClick = {

                                category =
                                    item

                                categoryExpanded =
                                    false
                            }
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        20.dp
                    )
            )


            // ========================================
            // 제목
            // ========================================

            InquiryLabel(
                text =
                    "제목"
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            OutlinedTextField(
                value =
                    title,

                onValueChange = {

                    title =
                        it
                },

                placeholder = {

                    Text(
                        text =
                            "문의 제목을 입력해주세요.",

                        color =
                            Color(
                                0xFFAAAAAA
                            )
                    )
                },

                singleLine =
                    true,

                shape =
                    RoundedCornerShape(
                        12.dp
                    ),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            InquiryBlue,

                        unfocusedBorderColor =
                            Color(
                                0xFFE5E5E5
                            ),

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    ),

                modifier =
                    Modifier.fillMaxWidth()
            )


            Spacer(
                modifier =
                    Modifier.height(
                        20.dp
                    )
            )


            // ========================================
            // 문의 내용
            // ========================================

            InquiryLabel(
                text =
                    "문의 내용"
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            OutlinedTextField(
                value =
                    content,

                onValueChange = {

                    content =
                        it
                },

                placeholder = {

                    Text(
                        text =
                            "문의하실 내용을 자세히 입력해주세요.",

                        color =
                            Color(
                                0xFFAAAAAA
                            )
                    )
                },

                minLines =
                    7,

                maxLines =
                    10,

                shape =
                    RoundedCornerShape(
                        12.dp
                    ),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            InquiryBlue,

                        unfocusedBorderColor =
                            Color(
                                0xFFE5E5E5
                            ),

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    ),

                modifier =
                    Modifier.fillMaxWidth()
            )


            Spacer(
                modifier =
                    Modifier.height(
                        28.dp
                    )
            )


            // ========================================
            // 등록 버튼
            // ========================================

            Button(
                onClick = {

                    if (
                        title.isNotBlank() &&
                        content.isNotBlank()
                    ) {

                        showSuccessDialog =
                            true
                    }
                },

                enabled =
                    title.isNotBlank() &&
                            content.isNotBlank(),

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            InquiryBlue,

                        disabledContainerColor =
                            Color(
                                0xFFD9DDE8
                            )
                    ),

                shape =
                    RoundedCornerShape(
                        14.dp
                    ),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        56.dp
                    )
            ) {

                Text(
                    text =
                        "문의 등록",

                    fontSize =
                        16.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color.White
                )
            }
        }
    }


    // ========================================
    // DB 연결 전 임시 완료 팝업
    // ========================================

    if (
        showSuccessDialog
    ) {

        AlertDialog(

            onDismissRequest = {

                showSuccessDialog =
                    false
            },

            title = {

                Text(
                    text =
                        "문의 등록 완료",

                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Text(
                    text =
                        "문의가 등록되었습니다."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showSuccessDialog =
                            false

                        title =
                            ""

                        content =
                            ""
                    }
                ) {

                    Text(
                        text =
                            "확인",

                        color =
                            InquiryBlue
                    )
                }
            }
        )
    }
}


// ========================================
// 입력 제목
// ========================================

@Composable
private fun InquiryLabel(
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
            InquiryTextBlack
    )
}