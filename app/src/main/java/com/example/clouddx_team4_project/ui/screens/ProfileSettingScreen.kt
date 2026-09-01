package com.example.clouddx_team4_project.ui.screens

<<<<<<< HEAD
=======
import android.util.Log
>>>>>>> ldk
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.network.ProfileUpdateRequest
import com.example.clouddx_team4_project.network.RetrofitClient
import kotlinx.coroutines.launch


private val ProfileBlue =
    Color(0xFF6A92FE)

private val ProfileBackground =
    Color(0xFFF8F9FC)

private val ProfileTextBlack =
    Color(0xFF222222)

private val ProfileTextGray =
    Color(0xFF888888)


// ========================================
// 프로필 설정
// ========================================

@Composable
fun ProfileSettingScreen(

<<<<<<< HEAD
    // 로그인 완성 전 테스트 회원
    memberId: Long = 3L,

    onBackClick: () -> Unit = {}

) {
=======
    // 로그인 완성
    onBackClick: () -> Unit = {}

) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = remember { com.example.clouddx_team4_project.data.TokenManager(context) }
>>>>>>> ldk

    var memberName by remember {
        mutableStateOf("")
    }

    var loginId by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var showSuccessDialog by remember {
        mutableStateOf(false)
    }


    val coroutineScope =
        rememberCoroutineScope()


    // ========================================
    // 프로필 조회
    // ========================================

<<<<<<< HEAD
    LaunchedEffect(
        memberId
    ) {
=======
    LaunchedEffect(Unit) {
>>>>>>> ldk

        isLoading =
            true

        errorMessage =
            null


        try {
<<<<<<< HEAD

            val profile =
                RetrofitClient
                    .memberApi
                    .getProfile(
                        memberId
                    )


            memberName =
                profile.memberName

            loginId =
                profile.loginId

            email =
                profile.email

=======
            val currentMemberId = tokenManager.getMemberId()

            if(currentMemberId != null) {
                val profile = RetrofitClient.memberApi.getProfile(currentMemberId)
                memberName = profile.memberName
                loginId = profile.loginId
                email = profile.email
            } else {
                errorMessage = "로그인 정보가 없습니다."
            }
>>>>>>> ldk

        } catch (
            e: Exception
        ) {

            errorMessage =
                "프로필 정보를 불러오지 못했습니다."

            e.printStackTrace()

        } finally {

            isLoading =
                false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ProfileBackground
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
                    ProfileTextBlack,

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
                    "프로필 설정",

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    ProfileTextBlack,

                modifier =
                    Modifier.align(
                        Alignment.Center
                    )
            )
        }


        if (
            isLoading
        ) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color =
                        ProfileBlue
                )
            }


        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 22.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {


                Spacer(
                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )


                // ========================================
                // 프로필 이미지
                // ========================================

                Box(
                    modifier = Modifier
                        .size(
                            88.dp
                        )
                        .background(
                            color =
                                Color(
                                    0xFFE8EEFF
                                ),

                            shape =
                                CircleShape
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
                                70.dp
                            )
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            30.dp
                        )
                )


                // ========================================
                // 이름
                // ========================================

                ProfileLabel(
                    text =
                        "이름"
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                OutlinedTextField(
                    value =
                        memberName,

                    onValueChange = {

                        memberName =
                            it
                    },

                    singleLine =
                        true,

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        profileTextFieldColors(),

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
                // 아이디
                // 수정 불가
                // ========================================

                ProfileLabel(
                    text =
                        "아이디"
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                OutlinedTextField(
                    value =
                        loginId,

                    onValueChange =
                        {},

                    enabled =
                        false,

                    singleLine =
                        true,

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        OutlinedTextFieldDefaults.colors(

                            disabledContainerColor =
                                Color(
                                    0xFFF0F1F4
                                ),

                            disabledTextColor =
                                ProfileTextGray,

                            disabledBorderColor =
                                Color(
                                    0xFFE3E4E8
                                )
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
                // 이메일
                // ========================================

                ProfileLabel(
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

                        email =
                            it
                    },

                    singleLine =
                        true,

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        profileTextFieldColors(),

                    modifier =
                        Modifier.fillMaxWidth()
                )


                // ========================================
                // 오류 메시지
                // ========================================

                errorMessage?.let {

                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    Text(
                        text =
                            it,

                        fontSize =
                            13.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .error,

                        modifier =
                            Modifier.fillMaxWidth()
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            32.dp
                        )
                )


                // ========================================
                // 저장하기
                // ========================================

                Button(
                    onClick = {

                        if (
                            memberName.isBlank() ||
                            email.isBlank()
                        ) {

                            errorMessage =
                                "이름과 이메일을 입력해주세요."

                            return@Button
                        }


                        coroutineScope.launch {

                            isSaving =
                                true

                            errorMessage =
                                null


                            try {
<<<<<<< HEAD

                                val response =
                                    RetrofitClient
                                        .memberApi
                                        .updateProfile(

                                            memberId =
                                                memberId,

                                            request =
                                                ProfileUpdateRequest(
                                                    memberName =
                                                        memberName.trim(),

                                                    email =
                                                        email.trim()
                                                )
                                        )


                                if (
                                    response.isSuccessful
                                ) {

                                    showSuccessDialog =
                                        true

                                } else {

                                    errorMessage =
                                        "프로필 수정에 실패했습니다."
                                }


                            } catch (
                                e: Exception
                            ) {

                                errorMessage =
                                    "서버와 통신할 수 없습니다."

                                e.printStackTrace()

                            } finally {

                                isSaving =
                                    false
                            }
                        }
                    },
=======
                                val currentMemberId = tokenManager.getMemberId()

                                if (currentMemberId != null) {

                                    val response =
                                        RetrofitClient.memberApi.updateProfile(
                                            memberId = currentMemberId,
                                            request = ProfileUpdateRequest(
                                                memberName = memberName.trim(),
                                                email = email.trim()
                                            )
                                        )
                                if (response.isSuccessful) {
                                showSuccessDialog = true
                                } else {
                                    errorMessage = "프로필 수정에 실패했습니다."
                                }
                                } else {
                                    errorMessage = "로그인 정보가 없습니다."
                                }
                            } catch (
                                e: Exception
                            ) {
                                errorMessage = "서버와 통신할 수 없습니다."
                                e.printStackTrace()
                            } finally {
                                isSaving = false
                            }
                                }
                            },
>>>>>>> ldk

                    enabled =
                        !isSaving,

                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    ProfileBlue
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

                    if (
                        isSaving
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(
                                    22.dp
                                ),

                            color =
                                Color.White,

                            strokeWidth =
                                2.dp
                        )

                    } else {

                        Text(
                            text =
                                "저장하기",

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
        }
    }


    // ========================================
    // 저장 성공 팝업
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
                        "프로필 수정",

                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Text(
                    text =
                        "프로필이 수정되었습니다."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showSuccessDialog =
                            false

                        onBackClick()
                    }
                ) {

                    Text(
                        text =
                            "확인",

                        color =
                            ProfileBlue
                    )
                }
            }
        )
    }
}


// ========================================
// 입력 항목 제목
// ========================================

@Composable
private fun ProfileLabel(
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
            ProfileTextBlack,

        modifier =
            Modifier.fillMaxWidth()
    )
}


// ========================================
// 입력창 색상
// ========================================

@Composable
private fun profileTextFieldColors() =
    OutlinedTextFieldDefaults.colors(

        focusedBorderColor =
            ProfileBlue,

        unfocusedBorderColor =
            Color(
                0xFFE3E4E8
            ),

        focusedContainerColor =
            Color.White,

        unfocusedContainerColor =
            Color.White
    )