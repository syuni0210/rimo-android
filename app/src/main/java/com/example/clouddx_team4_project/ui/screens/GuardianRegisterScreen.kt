package com.example.clouddx_team4_project.ui.screens
import com.example.clouddx_team4_project.data.GuardianApiClient
import com.example.clouddx_team4_project.data.GuardianRequest
import kotlinx.coroutines.launch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


// ========================================
// 색상
// ========================================

private val GuardianBlue =
    Color(0xFF6A92FE)

private val GuardianScreenBackground =
    Color(0xFFF7F8FC)

private val GuardianTextBlack =
    Color(0xFF222222)

private val GuardianTextGray =
    Color(0xFF858585)

private val GuardianBorderGray =
    Color(0xFFE7E9EF)

private val GuardianLightBlue =
    Color(0xFFF1F5FF)

private val GuardianRed =
    Color(0xFFE85050)


// ========================================
// 화면에서 사용하는 보호자 모델
//
// 실제 API 연결 후에는
// GET /api/guardians 응답을 이 형태로
// 변환해서 사용하면 됩니다.
// ========================================

data class GuardianUiModel(

    // GRDN_ID
    val guardianId: Long,

    // GRDN_NM
    val name: String,

    // PHN_NO
    val phone: String,

    // RL_NM
    val relation: String
)


// ========================================
// 보호자 관리 화면
// ========================================

@Composable
fun GuardianRegisterScreen(

    // 뒤로가기
    onBackClick: () -> Unit = {},


    // ========================================
    // 보호자 등록
    //
    // POST /api/guardians 연결용
    // ========================================

    onRegisterClick: (
        name: String,
        phone: String,
        relation: String
    ) -> Unit = { _, _, _ -> },


    // ========================================
    // 보호자 삭제
    //
    // DELETE API가 생기면 연결
    // ========================================

    onDeleteClick: (
        guardianId: Long
    ) -> Unit = {}
) {


    // ========================================
    // 보호자 등록 팝업 표시 여부
    // ========================================

    var showAddGuardianDialog by remember {

        mutableStateOf(
            false
        )
    }


    // ========================================
    // 삭제할 보호자
    // ========================================

    var guardianToDelete by remember {

        mutableStateOf<GuardianUiModel?>(
            null
        )
    }


    // ========================================
    // 보호자 목록
    //
    // 현재는 UI 동작 확인용입니다.
    //
    // 나중에 GET /api/guardians 연결하면
    // ViewModel에서 가져온 목록으로 교체합니다.
    // ========================================

    var guardians by remember {

        mutableStateOf<List<GuardianUiModel>>(
            emptyList()
        )
    }

    // ========================================
    // API 연동
    // ========================================

    val coroutineScope = rememberCoroutineScope()

    // 테스트용 고정값 (!!!로그인 연동 전까지!!!!)
    val currentMemberId = 1L

    LaunchedEffect(Unit) {
        try {
            val response = GuardianApiClient.api.getGuardians(currentMemberId)
            guardians = response.map {
                GuardianUiModel(
                    guardianId = it.guardianId,
                    name = it.guardianName,
                    phone = it.phoneNumber,
                    relation = it.relationName
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // ========================================
    // 전체 화면
    // ========================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                GuardianScreenBackground
            )
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {


            // ========================================
            // 상단바
            // ========================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        64.dp
                    )
                    .padding(
                        horizontal = 20.dp
                    )
            ) {


                // 뒤로가기
                Icon(
                    imageVector =
                        Icons.Filled.ArrowBackIosNew,

                    contentDescription =
                        "뒤로가기",

                    tint =
                        GuardianTextBlack,

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


                // 제목
                Text(
                    text =
                        "보호자 관리",

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        GuardianTextBlack,

                    modifier =
                        Modifier.align(
                            Alignment.Center
                        )
                )
            }


            // ========================================
            // 목록 영역
            // ========================================

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 20.dp
                    ),

                contentPadding =
                    PaddingValues(
                        bottom = 32.dp
                    )
            ) {


                // ========================================
                // 안내 문구
                // ========================================

                item {

                    Spacer(
                        modifier =
                            Modifier.height(
                                4.dp
                            )
                    )


                    Text(
                        text =
                            "긴급 상황 시 연락받을 보호자를 등록하고 관리할 수 있어요.",

                        fontSize =
                            13.sp,

                        lineHeight =
                            19.sp,

                        color =
                            GuardianTextGray
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                22.dp
                            )
                    )
                }


                // ========================================
                // 보호자 등록 버튼
                // ========================================

                item {

                    Button(
                        onClick = {

                            // ========================================
                            // ★ 버튼 누르면 팝업 표시
                            // ========================================

                            showAddGuardianDialog =
                                true
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                52.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                13.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    GuardianBlue
                            ),

                        contentPadding =
                            PaddingValues(
                                horizontal = 16.dp
                            )
                    ) {


                        Icon(
                            imageVector =
                                Icons.Filled.Add,

                            contentDescription =
                                null,

                            tint =
                                Color.White,

                            modifier =
                                Modifier.size(
                                    21.dp
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    7.dp
                                )
                        )


                        Text(
                            text =
                                "보호자 등록",

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.White
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                28.dp
                            )
                    )
                }


                // ========================================
                // 목록 제목
                // ========================================

                item {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {


                        Text(
                            text =
                                "등록된 보호자",

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                GuardianTextBlack
                        )


                        Text(
                            text =
                                "${guardians.size}명",

                            fontSize =
                                13.sp,

                            color =
                                GuardianTextGray
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )
                }


                // ========================================
                // 등록된 보호자가 없는 경우
                // ========================================

                if (
                    guardians.isEmpty()
                ) {

                    item {

                        EmptyGuardianCard()
                    }

                } else {


                    // ========================================
                    // 등록된 보호자 카드
                    // ========================================

                    items(
                        items =
                            guardians,

                        key = {
                            it.guardianId
                        }
                    ) { guardian ->


                        GuardianCard(

                            guardian =
                                guardian,


                            onDeleteClick = {

                                guardianToDelete =
                                    guardian
                            }
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    12.dp
                                )
                        )
                    }
                }
            }
        }


        // ========================================
        // ★ 보호자 등록 팝업
        // ========================================

        if (
            showAddGuardianDialog
        ) {

            AddGuardianDialog(

                onDismiss = {

                    showAddGuardianDialog =
                        false
                },

                onRegister = {
                        name,
                        phone,
                        relation ->

                    coroutineScope.launch {
                        try {
                            GuardianApiClient.api.registerGuardian(
                                GuardianRequest(
                                    memberId = currentMemberId,
                                    guardianName = name,
                                    phoneNumber = phone,
                                    relationName = relation
                                )
                            )

                            val response = GuardianApiClient.api.getGuardians(currentMemberId)
                            guardians = response.map {
                                GuardianUiModel(
                                    guardianId = it.guardianId,
                                    name = it.guardianName,
                                    phone = it.phoneNumber,
                                    relation = it.relationName
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    onRegisterClick(
                        name,
                        phone,
                        relation
                    )

                    // 팝업 닫기
                    showAddGuardianDialog =
                        false
                }            )
        }


        // ========================================
        // 삭제 확인 팝업
        // ========================================

        guardianToDelete
            ?.let { guardian ->


                DeleteGuardianDialog(

                    guardianName =
                        guardian.name,


                    onDismiss = {

                        guardianToDelete =
                            null
                    },

                    onDelete = {

                        coroutineScope.launch {
                            try {
                                GuardianApiClient.api.deleteGuardian(guardian.guardianId)

                                val response = GuardianApiClient.api.getGuardians(currentMemberId)
                                guardians = response.map {
                                    GuardianUiModel(
                                        guardianId = it.guardianId,
                                        name = it.guardianName,
                                        phone = it.phoneNumber,
                                        relation = it.relationName
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        onDeleteClick(
                            guardian.guardianId
                        )

                        guardianToDelete =
                            null
                    }
                )
            }
    }
}


// ========================================
// 등록된 보호자가 없을 때
// ========================================

@Composable
private fun EmptyGuardianCard() {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        border =
            BorderStroke(
                width =
                    1.dp,

                color =
                    GuardianBorderGray
            )
    ) {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 42.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            // 사람 아이콘
            Box(
                modifier = Modifier
                    .size(
                        56.dp
                    )
                    .background(
                        color =
                            GuardianLightBlue,

                        shape =
                            CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {


                Icon(
                    imageVector =
                        Icons.Filled.Person,

                    contentDescription =
                        null,

                    tint =
                        GuardianBlue,

                    modifier =
                        Modifier.size(
                            29.dp
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        15.dp
                    )
            )


            Text(
                text =
                    "등록된 보호자가 없습니다.",

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.SemiBold,

                color =
                    GuardianTextBlack
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Text(
                text =
                    "보호자를 등록하면 긴급 상황 발생 시\n등록된 연락처로 알림을 전달할 수 있어요.",

                fontSize =
                    12.sp,

                lineHeight =
                    18.sp,

                textAlign =
                    TextAlign.Center,

                color =
                    GuardianTextGray
            )
        }
    }
}


// ========================================
// 보호자 카드
// ========================================

@Composable
private fun GuardianCard(

    guardian: GuardianUiModel,

    onDeleteClick: () -> Unit
) {


    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        border =
            BorderStroke(
                width =
                    1.dp,

                color =
                    GuardianBorderGray
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
            )
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    17.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // ========================================
            // 사람 아이콘
            // ========================================

            Box(
                modifier = Modifier
                    .size(
                        46.dp
                    )
                    .background(
                        color =
                            GuardianLightBlue,

                        shape =
                            CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {


                Icon(
                    imageVector =
                        Icons.Filled.Person,

                    contentDescription =
                        null,

                    tint =
                        GuardianBlue,

                    modifier =
                        Modifier.size(
                            25.dp
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.width(
                        13.dp
                    )
            )


            // ========================================
            // 보호자 정보
            // ========================================

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {


                // 이름 + 관계
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Text(
                        text =
                            guardian.name,

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            GuardianTextBlack
                    )


                    if (
                        guardian.relation
                            .isNotBlank()
                    ) {


                        Spacer(
                            modifier =
                                Modifier.width(
                                    8.dp
                                )
                        )


                        Surface(
                            shape =
                                RoundedCornerShape(
                                    20.dp
                                ),

                            color =
                                GuardianLightBlue
                        ) {


                            Text(
                                text =
                                    guardian.relation,

                                fontSize =
                                    11.sp,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color =
                                    GuardianBlue,

                                modifier =
                                    Modifier.padding(
                                        horizontal = 9.dp,
                                        vertical = 4.dp
                                    )
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                // 전화번호
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Icon(
                        imageVector =
                            Icons.Filled.Phone,

                        contentDescription =
                            null,

                        tint =
                            GuardianTextGray,

                        modifier =
                            Modifier.size(
                                15.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.width(
                                5.dp
                            )
                    )


                    Text(
                        text =
                            guardian.phone,

                        fontSize =
                            13.sp,

                        color =
                            GuardianTextGray
                    )
                }
            }


            // ========================================
            // 삭제
            // ========================================

            IconButton(
                onClick =
                    onDeleteClick
            ) {


                Icon(
                    imageVector =
                        Icons.Filled.DeleteOutline,

                    contentDescription =
                        "보호자 삭제",

                    tint =
                        GuardianRed,

                    modifier =
                        Modifier.size(
                            22.dp
                        )
                )
            }
        }
    }
}


// ========================================
// ★ 보호자 등록 팝업
// ========================================

@Composable
private fun AddGuardianDialog(

    onDismiss: () -> Unit,

    onRegister: (
        name: String,
        phone: String,
        relation: String
    ) -> Unit
) {


    // ========================================
    // 입력값
    // ========================================

    var guardianName by remember {

        mutableStateOf(
            ""
        )
    }


    var phoneNumber by remember {

        mutableStateOf(
            ""
        )
    }


    var relation by remember {

        mutableStateOf(
            ""
        )
    }


    Dialog(
        onDismissRequest =
            onDismiss
    ) {


        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    20.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                )
        ) {


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        22.dp
                    )
            ) {


                // ========================================
                // 제목
                // ========================================

                Text(
                    text =
                        "보호자 등록",

                    fontSize =
                        20.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        GuardianTextBlack
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            6.dp
                        )
                )


                Text(
                    text =
                        "긴급 상황 시 연락받을 보호자 정보를 입력해주세요.",

                    fontSize =
                        12.sp,

                    color =
                        GuardianTextGray
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            24.dp
                        )
                )


                // ========================================
                // 보호자 성함
                // ========================================

                GuardianInputLabel(
                    text =
                        "보호자 성함"
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp
                        )
                )


                GuardianInputField(

                    value =
                        guardianName,

                    onValueChange = {

                        guardianName =
                            it
                    },

                    placeholder =
                        "성함을 입력해주세요.",

                    keyboardType =
                        KeyboardType.Text
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            18.dp
                        )
                )


                // ========================================
                // 연락처
                // ========================================

                GuardianInputLabel(
                    text =
                        "연락처"
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp
                        )
                )


                GuardianInputField(

                    value =
                        phoneNumber,

                    onValueChange = { value ->


                        // 숫자와 - 만 입력 가능
                        if (
                            value.all {

                                it.isDigit() ||
                                        it == '-'
                            }
                        ) {

                            phoneNumber =
                                value
                        }
                    },

                    placeholder =
                        "010-0000-0000",

                    keyboardType =
                        KeyboardType.Phone
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            18.dp
                        )
                )


                // ========================================
                // 관계
                // ========================================

                GuardianInputLabel(
                    text =
                        "관계"
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp
                        )
                )


                GuardianInputField(

                    value =
                        relation,

                    onValueChange = {

                        relation =
                            it
                    },

                    placeholder =
                        "예: 어머니, 아버지, 친구",

                    keyboardType =
                        KeyboardType.Text
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            26.dp
                        )
                )


                // ========================================
                // 하단 버튼
                // ========================================

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {


                    // 취소
                    OutlinedButton(
                        onClick =
                            onDismiss,

                        modifier = Modifier
                            .weight(
                                1f
                            )
                            .height(
                                48.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                11.dp
                            ),

                        border =
                            BorderStroke(
                                width =
                                    1.dp,

                                color =
                                    GuardianBorderGray
                            )
                    ) {


                        Text(
                            text =
                                "취소",

                            color =
                                GuardianTextGray,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }


                    // 등록하기
                    Button(
                        onClick = {


                            onRegister(

                                guardianName
                                    .trim(),

                                phoneNumber
                                    .trim(),

                                relation
                                    .trim()
                            )
                        },

                        // 세 값 모두 입력해야 활성화
                        enabled =
                            guardianName
                                .isNotBlank() &&

                                    phoneNumber
                                        .isNotBlank() &&

                                    relation
                                        .isNotBlank(),

                        modifier = Modifier
                            .weight(
                                1f
                            )
                            .height(
                                48.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                11.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(

                                containerColor =
                                    GuardianBlue,

                                disabledContainerColor =
                                    Color(
                                        0xFFD7DEF0
                                    )
                            )
                    ) {


                        Text(
                            text =
                                "등록하기",

                            color =
                                Color.White,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// ========================================
// 입력 제목
// ========================================

@Composable
private fun GuardianInputLabel(
    text: String
) {


    Text(
        text =
            text,

        fontSize =
            13.sp,

        fontWeight =
            FontWeight.SemiBold,

        color =
            GuardianTextBlack
    )
}


// ========================================
// 입력창
// ========================================

@Composable
private fun GuardianInputField(

    value: String,

    onValueChange: (String) -> Unit,

    placeholder: String,

    keyboardType: KeyboardType
) {


    OutlinedTextField(

        value =
            value,

        onValueChange =
            onValueChange,

        modifier =
            Modifier.fillMaxWidth(),

        singleLine =
            true,

        placeholder = {


            Text(
                text =
                    placeholder,

                fontSize =
                    14.sp,

                color =
                    GuardianTextGray
            )
        },

        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    keyboardType
            ),

        shape =
            RoundedCornerShape(
                11.dp
            ),

        colors =
            OutlinedTextFieldDefaults.colors(

                focusedBorderColor =
                    GuardianBlue,

                unfocusedBorderColor =
                    GuardianBorderGray,

                cursorColor =
                    GuardianBlue,

                focusedContainerColor =
                    Color.White,

                unfocusedContainerColor =
                    Color.White
            )
    )
}


// ========================================
// 삭제 확인 팝업
// ========================================

@Composable
private fun DeleteGuardianDialog(

    guardianName: String,

    onDismiss: () -> Unit,

    onDelete: () -> Unit
) {


    AlertDialog(

        onDismissRequest =
            onDismiss,


        title = {


            Text(
                text =
                    "보호자 삭제",

                fontWeight =
                    FontWeight.Bold,

                color =
                    GuardianTextBlack
            )
        },


        text = {


            Text(
                text =
                    "${guardianName}님을 보호자 목록에서 삭제하시겠습니까?",

                fontSize =
                    14.sp,

                color =
                    GuardianTextGray
            )
        },


        dismissButton = {


            TextButton(
                onClick =
                    onDismiss
            ) {


                Text(
                    text =
                        "취소",

                    color =
                        GuardianTextGray
                )
            }
        },


        confirmButton = {


            TextButton(
                onClick =
                    onDelete
            ) {


                Text(
                    text =
                        "삭제",

                    color =
                        GuardianRed,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        },


        containerColor =
            Color.White
    )
}


// ========================================
// Preview
// ========================================

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun GuardianRegisterScreenPreview() {


    GuardianRegisterScreen()
}