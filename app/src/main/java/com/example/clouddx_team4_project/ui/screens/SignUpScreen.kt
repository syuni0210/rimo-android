package com.example.clouddx_team4_project.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.network.RetrofitClient
import com.example.clouddx_team4_project.network.SignupRequest
import kotlinx.coroutines.launch


// ========================================
// 색상
// ========================================

private val AnOnBlue =
    Color(0xFF6A92FE)

private val ScreenBackground =
    Color(0xFFFAFBFD)

private val TextBlack =
    Color(0xFF222222)

private val TextGray =
    Color(0xFF8B8B8B)

private val BorderGray =
    Color(0xFFE7E9EE)

private val LightBlue =
    Color(0xFFF0F4FF)


// ========================================
// 회원가입 화면
// ========================================

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit = {},
    onSignUpComplete: () -> Unit = {}
) {

    // ========================================
    // 현재 회원가입 단계
    // ========================================

    var currentStep by remember {
        mutableIntStateOf(1)
    }


    // ========================================
    // 입력 정보
    // ========================================

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var userId by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordConfirm by remember {
        mutableStateOf("")
    }


    // ========================================
    // 비밀번호 보이기
    // ========================================

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var passwordConfirmVisible by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 아이디 중복검사 상태
    // ========================================

    var isIdChecked by remember {
        mutableStateOf(false)
    }

    var idCheckMessage by remember {
        mutableStateOf<String?>(null)
    }


    // ========================================
    // API 상태
    // ========================================

    val coroutineScope =
        rememberCoroutineScope()

    var isSigningUp by remember {
        mutableStateOf(false)
    }

    var signupError by remember {
        mutableStateOf<String?>(null)
    }


    // ========================================
    // 약관 체크 상태
    // ========================================

    var serviceAgree by remember {
        mutableStateOf(false)
    }

    var privacyAgree by remember {
        mutableStateOf(false)
    }

    var locationAgree by remember {
        mutableStateOf(false)
    }

    var marketingAgree by remember {
        mutableStateOf(false)
    }

    var emergencyAgree by remember {
        mutableStateOf(false)
    }


    val requiredAgreed =
        serviceAgree &&
                privacyAgree &&
                locationAgree


    val allAgreed =
        serviceAgree &&
                privacyAgree &&
                locationAgree &&
                marketingAgree &&
                emergencyAgree


    // ========================================
    // 전체 화면
    // ========================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                ScreenBackground
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {


            // ========================================
            // 상단 헤더
            // ========================================

            SignUpHeader(
                currentStep = currentStep,

                onBackClick = {

                    if (currentStep > 1) {

                        currentStep--

                    } else {

                        onBackClick()
                    }
                }
            )


            // ========================================
            // 단계 표시
            // ========================================

            SignUpStepIndicator(
                currentStep = currentStep
            )


            // ========================================
            // 내용
            // ========================================

            when (currentStep) {


                // ========================================
                // 1단계 - 약관
                // ========================================

                1 -> {

                    TermsStep(
                        serviceAgree = serviceAgree,
                        privacyAgree = privacyAgree,
                        locationAgree = locationAgree,
                        marketingAgree = marketingAgree,
                        emergencyAgree = emergencyAgree,
                        allAgreed = allAgreed,

                        onAllAgreeChange = { checked ->

                            serviceAgree = checked
                            privacyAgree = checked
                            locationAgree = checked
                            marketingAgree = checked
                            emergencyAgree = checked
                        },

                        onServiceAgreeChange = {

                            serviceAgree = it
                        },

                        onPrivacyAgreeChange = {

                            privacyAgree = it
                        },

                        onLocationAgreeChange = {

                            locationAgree = it
                        },

                        onMarketingAgreeChange = {

                            marketingAgree = it
                        },

                        onEmergencyAgreeChange = {

                            emergencyAgree = it
                        },

                        onNextClick = {

                            if (requiredAgreed) {

                                currentStep = 2
                            }
                        }
                    )
                }


                // ========================================
                // 2단계 - 회원정보 입력
                // ========================================

                2 -> {

                    UserInfoStep(

                        name = name,

                        email = email,

                        userId = userId,

                        password = password,

                        passwordConfirm = passwordConfirm,

                        passwordVisible = passwordVisible,

                        passwordConfirmVisible =
                            passwordConfirmVisible,

                        isIdChecked = isIdChecked,

                        idCheckMessage = idCheckMessage,


                        onNameChange = {

                            name = it
                        },


                        onEmailChange = {

                            email = it
                        },


                        onUserIdChange = {

                            userId = it

                            // 아이디를 수정하면 다시 중복검사 필요
                            isIdChecked = false

                            idCheckMessage = null
                        },


                        onPasswordChange = {

                            password = it
                        },


                        onPasswordConfirmChange = {

                            passwordConfirm = it
                        },


                        onPasswordVisibleChange = {

                            passwordVisible =
                                !passwordVisible
                        },


                        onPasswordConfirmVisibleChange = {

                            passwordConfirmVisible =
                                !passwordConfirmVisible
                        },


                        // ========================================
                        // 아이디 중복검사 실제 API 연동
                        // ========================================
                        onIdCheckClick = {
                            if (userId.isBlank()) {
                                isIdChecked = false
                                idCheckMessage = "아이디를 입력해주세요."
                            } else {
                                coroutineScope.launch {
                                    try {
                                        val response = RetrofitClient.authApi.checkId(userId.trim())
                                        if (response.isSuccessful && response.body() != null) {
                                            isIdChecked = response.body()!!.available
                                            idCheckMessage = response.body()!!.message
                                        } else {
                                            isIdChecked = false
                                            idCheckMessage = "중복 검사에 실패했습니다."
                                        }
                                    } catch (e: Exception) {
                                        Log.e("SIGNUP_API", "중복 검사 통신 실패", e)
                                        isIdChecked = false
                                        idCheckMessage = "서버에 연결할 수 없습니다."
                                    }
                                }
                            }
                        },


                        onNextClick = {

                            if (
                                name.isNotBlank() &&
                                email.isNotBlank() &&
                                userId.isNotBlank() &&
                                password.isNotBlank() &&
                                passwordConfirm.isNotBlank() &&
                                password == passwordConfirm &&
                                isIdChecked
                            ) {

                                signupError = null

                                currentStep = 3
                            }
                        }
                    )
                }


                // ========================================
                // 3단계 - 입력 정보 확인 + API 호출
                // ========================================

                3 -> {

                    ConfirmInfoStep(

                        name = name,

                        email = email,

                        userId = userId,

                        isLoading = isSigningUp,

                        errorMessage = signupError,


                        onNextClick = {

                            if (isSigningUp) {
                                return@ConfirmInfoStep
                            }


                            coroutineScope.launch {

                                isSigningUp = true

                                signupError = null


                                try {

                                    // ========================================
                                    // 백엔드 회원가입 요청
                                    //
                                    // POST /api/v1/auth/signup
                                    // ========================================

                                    val response =
                                        RetrofitClient
                                            .authApi
                                            .signup(

                                                SignupRequest(
                                                    userId =
                                                        userId.trim(),

                                                    email =
                                                        email.trim(),

                                                    password =
                                                        password,

                                                    name =
                                                        name.trim()
                                                )
                                            )


                                    // ========================================
                                    // 회원가입 성공
                                    // ========================================

                                    if (
                                        response.isSuccessful
                                    ) {

                                        val message =
                                            response
                                                .body()
                                                ?.message


                                        Log.d(
                                            "SIGNUP_API",
                                            "회원가입 성공: $message"
                                        )


                                        currentStep = 4


                                    } else {

                                        // ========================================
                                        // HTTP 오류
                                        // ========================================

                                        val errorBody =
                                            response
                                                .errorBody()
                                                ?.string()


                                        Log.e(
                                            "SIGNUP_API",
                                            "회원가입 실패 code=${response.code()}, body=$errorBody"
                                        )


                                        signupError =
                                            when (
                                                response.code()
                                            ) {

                                                400 -> {

                                                    "입력한 회원정보를 확인해주세요."
                                                }


                                                409 -> {

                                                    "이미 가입된 이메일입니다."
                                                }


                                                500 -> {

                                                    "서버 오류가 발생했습니다."
                                                }


                                                else -> {

                                                    "회원가입에 실패했습니다. (${response.code()})"
                                                }
                                            }
                                    }


                                } catch (
                                    e: Exception
                                ) {

                                    Log.e(
                                        "SIGNUP_API",
                                        "서버 통신 실패",
                                        e
                                    )


                                    signupError =
                                        "서버에 연결할 수 없습니다."


                                } finally {

                                    isSigningUp = false
                                }
                            }
                        }
                    )
                }


                // ========================================
                // 4단계 - 완료
                // ========================================

                4 -> {

                    CompleteStep(

                        onCompleteClick = {

                            onSignUpComplete()
                        }
                    )
                }
            }
        }
    }
}


// ========================================
// 상단 헤더
// ========================================

@Composable
private fun SignUpHeader(
    currentStep: Int,
    onBackClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                62.dp
            )
            .padding(
                horizontal = 20.dp
            )
    ) {

        if (
            currentStep != 4
        ) {

            Icon(
                imageVector =
                    Icons.Filled.ArrowBackIosNew,

                contentDescription =
                    "뒤로가기",

                tint =
                    Color(
                        0xFF333333
                    ),

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
        }


        Text(
            text =
                "회원가입",

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextBlack,

            modifier =
                Modifier.align(
                    Alignment.Center
                )
        )
    }
}


// ========================================
// 상단 단계 표시
// ========================================

@Composable
private fun SignUpStepIndicator(
    currentStep: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 55.dp,
                vertical = 8.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        for (
        step in 1..4
        ) {

            StepCircle(
                step = step,
                currentStep = currentStep
            )


            if (
                step != 4
            ) {

                Box(
                    modifier = Modifier
                        .weight(
                            1f
                        )
                        .height(
                            2.dp
                        )
                        .background(

                            if (
                                currentStep > step
                            ) {

                                AnOnBlue.copy(
                                    alpha = 0.4f
                                )

                            } else {

                                Color(
                                    0xFFE7E9EF
                                )
                            }
                        )
                )
            }
        }
    }
}


// ========================================
// 단계 원
// ========================================

@Composable
private fun StepCircle(
    step: Int,
    currentStep: Int
) {

    val isCurrent =
        currentStep == step


    Box(
        modifier = Modifier
            .size(
                27.dp
            )
            .background(

                color =
                    if (
                        isCurrent
                    ) {

                        AnOnBlue

                    } else {

                        Color(
                            0xFFF0F1F5
                        )
                    },

                shape = CircleShape
            ),

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                step.toString(),

            fontSize =
                12.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                if (
                    isCurrent
                ) {

                    Color.White

                } else {

                    Color(
                        0xFF9197A5
                    )
                }
        )
    }
}


// ========================================
// 1단계 : 약관 동의
// ========================================

@Composable
private fun TermsStep(
    serviceAgree: Boolean,
    privacyAgree: Boolean,
    locationAgree: Boolean,
    marketingAgree: Boolean,
    emergencyAgree: Boolean,
    allAgreed: Boolean,

    onAllAgreeChange: (Boolean) -> Unit,
    onServiceAgreeChange: (Boolean) -> Unit,
    onPrivacyAgreeChange: (Boolean) -> Unit,
    onLocationAgreeChange: (Boolean) -> Unit,
    onMarketingAgreeChange: (Boolean) -> Unit,
    onEmergencyAgreeChange: (Boolean) -> Unit,

    onNextClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 30.dp,
                bottom = 25.dp
            )
    ) {

        Text(
            text =
                "약관 동의",

            fontSize =
                23.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextBlack,

            modifier =
                Modifier.align(
                    Alignment.CenterHorizontally
                )
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Text(
            text =
                "서비스 이용을 위해 약관에 동의해주세요.",

            fontSize =
                13.sp,

            color =
                TextGray,

            modifier =
                Modifier.align(
                    Alignment.CenterHorizontally
                )
        )


        Spacer(
            modifier =
                Modifier.height(
                    34.dp
                )
        )


        AgreementRow(
            title =
                "전체 동의합니다.",

            checked =
                allAgreed,

            bold =
                true,

            onCheckedChange = {

                onAllAgreeChange(
                    it
                )
            }
        )


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        HorizontalDivider(
            color =
                BorderGray
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        AgreementRow(
            title =
                "[필수] 서비스 이용약관",

            checked =
                serviceAgree,

            showDetail =
                true,

            onCheckedChange = {

                onServiceAgreeChange(
                    it
                )
            }
        )


        AgreementRow(
            title =
                "[필수] 개인정보 수집 및 이용 동의",

            checked =
                privacyAgree,

            showDetail =
                true,

            onCheckedChange = {

                onPrivacyAgreeChange(
                    it
                )
            }
        )


        AgreementRow(
            title =
                "[필수] 위치정보 수집 및 이용 동의",

            checked =
                locationAgree,

            showDetail =
                true,

            onCheckedChange = {

                onLocationAgreeChange(
                    it
                )
            }
        )


        AgreementRow(
            title =
                "[선택] 마케팅 정보 수신 동의",

            checked =
                marketingAgree,

            showDetail =
                true,

            onCheckedChange = {

                onMarketingAgreeChange(
                    it
                )
            }
        )


        AgreementRow(
            title =
                "[선택] 긴급상황 관련 정보 수신 동의",

            checked =
                emergencyAgree,

            showDetail =
                true,

            onCheckedChange = {

                onEmergencyAgreeChange(
                    it
                )
            }
        )


        Spacer(
            modifier =
                Modifier.weight(
                    1f
                )
        )


        PrimaryButton(
            text =
                "다음",

            enabled =
                serviceAgree &&
                        privacyAgree &&
                        locationAgree,

            onClick =
                onNextClick
        )
    }
}


// ========================================
// 약관 한 줄
// ========================================

@Composable
private fun AgreementRow(
    title: String,
    checked: Boolean,
    bold: Boolean = false,
    showDetail: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                55.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        AgreementCheck(
            checked = checked,

            onClick = {

                onCheckedChange(
                    !checked
                )
            }
        )


        Spacer(
            modifier =
                Modifier.width(
                    10.dp
                )
        )


        Text(
            text =
                title,

            fontSize =
                14.sp,

            fontWeight =
                if (
                    bold
                ) {

                    FontWeight.Bold

                } else {

                    FontWeight.Medium
                },

            color =
                TextBlack,

            modifier =
                Modifier.weight(
                    1f
                )
        )


        if (
            showDetail
        ) {

            Text(
                text =
                    "보기",

                fontSize =
                    12.sp,

                color =
                    TextGray
            )


            Spacer(
                modifier =
                    Modifier.width(
                        3.dp
                    )
            )


            Icon(
                imageVector =
                    Icons.Filled.ChevronRight,

                contentDescription =
                    null,

                tint =
                    TextGray,

                modifier =
                    Modifier.size(
                        16.dp
                    )
            )
        }
    }
}


// ========================================
// 커스텀 체크
// ========================================

@Composable
private fun AgreementCheck(
    checked: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(
                23.dp
            )
            .background(

                color =
                    if (
                        checked
                    ) {

                        AnOnBlue

                    } else {

                        Color.White
                    },

                shape = CircleShape
            )
            .border(

                width =
                    1.5.dp,

                color =
                    if (
                        checked
                    ) {

                        AnOnBlue

                    } else {

                        Color(
                            0xFFD3D5DB
                        )
                    },

                shape = CircleShape
            )
            .clickable {

                onClick()
            },

        contentAlignment =
            Alignment.Center
    ) {

        if (
            checked
        ) {

            Icon(
                imageVector =
                    Icons.Filled.Check,

                contentDescription =
                    null,

                tint =
                    Color.White,

                modifier =
                    Modifier.size(
                        15.dp
                    )
            )
        }
    }
}


// ========================================
// 2단계 : 회원정보 입력
// ========================================

@Composable
private fun UserInfoStep(

    name: String,
    email: String,
    userId: String,
    password: String,
    passwordConfirm: String,

    passwordVisible: Boolean,
    passwordConfirmVisible: Boolean,

    isIdChecked: Boolean,
    idCheckMessage: String?,

    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onUserIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,

    onPasswordVisibleChange: () -> Unit,
    onPasswordConfirmVisibleChange: () -> Unit,

    onIdCheckClick: () -> Unit,

    onNextClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 30.dp,
                bottom = 25.dp
            )
    ) {

        Text(
            text =
                "회원 정보 입력",

            fontSize =
                23.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextBlack,

            modifier =
                Modifier.align(
                    Alignment.CenterHorizontally
                )
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Text(
            text =
                "기본 정보를 입력해주세요.",

            fontSize =
                13.sp,

            color =
                TextGray,

            modifier =
                Modifier.align(
                    Alignment.CenterHorizontally
                )
        )


        Spacer(
            modifier =
                Modifier.height(
                    30.dp
                )
        )


        // ========================================
        // 1. 이름
        // ========================================

        SignUpTextField(
            value =
                name,

            onValueChange = {

                onNameChange(
                    it
                )
            },

            title =
                "이름",

            hint =
                "이름을 입력해주세요",

            icon =
                Icons.Filled.Person
        )


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        // ========================================
        // 2. 이메일
        // ========================================

        SignUpTextField(
            value =
                email,

            onValueChange = {

                onEmailChange(
                    it
                )
            },

            title =
                "이메일",

            hint =
                "example@email.com",

            icon =
                Icons.Filled.Email,

            keyboardType =
                KeyboardType.Email
        )


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        // ========================================
        // 3. 아이디 + 중복검사
        // ========================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            SignUpTextField(
                modifier =
                    Modifier.weight(
                        1f
                    ),

                value =
                    userId,

                onValueChange = {

                    onUserIdChange(
                        it
                    )
                },

                title =
                    "아이디",

                hint =
                    "영문, 숫자 조합 4~16자",

                icon =
                    Icons.Filled.Person
            )


            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )


            Button(
                onClick = {

                    onIdCheckClick()
                },

                modifier = Modifier
                    .width(
                        92.dp
                    )
                    .height(
                        50.dp
                    ),

                shape =
                    RoundedCornerShape(
                        10.dp
                    ),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            AnOnBlue
                    ),

                contentPadding =
                    PaddingValues(
                        horizontal = 6.dp
                    )
            ) {

                Text(
                    text =
                        "중복 검사",

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color.White
                )
            }
        }


        if (
            idCheckMessage != null
        ) {

            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )


            Text(
                text =
                    idCheckMessage,

                fontSize =
                    12.sp,

                color =
                    if (
                        isIdChecked
                    ) {

                        AnOnBlue

                    } else {

                        MaterialTheme
                            .colorScheme
                            .error
                    },

                modifier =
                    Modifier.padding(
                        start = 5.dp
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        // ========================================
        // 4. 비밀번호
        // ========================================

        SignUpTextField(
            value =
                password,

            onValueChange = {

                onPasswordChange(
                    it
                )
            },

            title =
                "비밀번호",

            hint =
                "영문, 숫자, 특수문자 포함 8~16자",

            icon =
                Icons.Filled.Lock,

            password =
                true,

            passwordVisible =
                passwordVisible,

            keyboardType =
                KeyboardType.Password,

            onPasswordVisibleChange =
                onPasswordVisibleChange
        )


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        // ========================================
        // 5. 비밀번호 확인
        // ========================================

        SignUpTextField(
            value =
                passwordConfirm,

            onValueChange = {

                onPasswordConfirmChange(
                    it
                )
            },

            title =
                "비밀번호 확인",

            hint =
                "비밀번호를 다시 입력해주세요",

            icon =
                Icons.Filled.Lock,

            password =
                true,

            passwordVisible =
                passwordConfirmVisible,

            keyboardType =
                KeyboardType.Password,

            onPasswordVisibleChange =
                onPasswordConfirmVisibleChange
        )


        // ========================================
        // 비밀번호 불일치 메시지
        // ========================================

        if (
            passwordConfirm.isNotBlank() &&
            password != passwordConfirm
        ) {

            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )


            Text(
                text =
                    "비밀번호가 일치하지 않습니다.",

                fontSize =
                    12.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .error,

                modifier =
                    Modifier.padding(
                        start = 5.dp
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
        // 다음
        // ========================================

        PrimaryButton(
            text =
                "다음",

            enabled =
                name.isNotBlank() &&
                        email.isNotBlank() &&
                        userId.isNotBlank() &&
                        password.isNotBlank() &&
                        passwordConfirm.isNotBlank() &&
                        password == passwordConfirm &&
                        isIdChecked,

            onClick =
                onNextClick
        )
    }
}


// ========================================
// 입력 필드
// ========================================

@Composable
private fun SignUpTextField(
    modifier: Modifier = Modifier,

    value: String,
    onValueChange: (String) -> Unit,

    title: String,
    hint: String,

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    password: Boolean = false,
    passwordVisible: Boolean = false,

    keyboardType: KeyboardType = KeyboardType.Text,

    onPasswordVisibleChange: () -> Unit = {}
) {

    OutlinedTextField(
        value =
            value,

        onValueChange = {

            onValueChange(
                it
            )
        },

        modifier = modifier
            .fillMaxWidth()
            .height(
                74.dp
            ),

        shape =
            RoundedCornerShape(
                13.dp
            ),

        leadingIcon = {

            Icon(
                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    Color(
                        0xFF9298A8
                    ),

                modifier =
                    Modifier.size(
                        21.dp
                    )
            )
        },


        trailingIcon = {

            if (
                password
            ) {

                Icon(
                    imageVector =
                        if (
                            passwordVisible
                        ) {

                            Icons.Filled.Visibility

                        } else {

                            Icons.Filled.VisibilityOff
                        },

                    contentDescription =
                        null,

                    tint =
                        Color(
                            0xFF9298A8
                        ),

                    modifier = Modifier
                        .size(
                            21.dp
                        )
                        .clickable {

                            onPasswordVisibleChange()
                        }
                )
            }
        },


        label = {

            Text(
                text =
                    title,

                fontSize =
                    13.sp
            )
        },


        placeholder = {

            Text(
                text =
                    hint,

                fontSize =
                    12.sp,

                color =
                    TextGray
            )
        },


        singleLine =
            true,


        visualTransformation =
            if (
                password &&
                !passwordVisible
            ) {

                PasswordVisualTransformation()

            } else {

                VisualTransformation.None
            },


        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    keyboardType
            ),


        colors =
            OutlinedTextFieldDefaults.colors(

                focusedBorderColor =
                    AnOnBlue,

                unfocusedBorderColor =
                    BorderGray,

                focusedLabelColor =
                    AnOnBlue,

                cursorColor =
                    AnOnBlue
            )
    )
}


// ========================================
// 3단계 : 입력 정보 확인
// ========================================

@Composable
private fun ConfirmInfoStep(
    name: String,
    email: String,
    userId: String,

    isLoading: Boolean,
    errorMessage: String?,

    onNextClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 30.dp,
                bottom = 25.dp
            )
    ) {

        Text(
            text =
                "입력 정보 확인",

            fontSize =
                23.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextBlack,

            modifier =
                Modifier.align(
                    Alignment.CenterHorizontally
                )
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Text(
            text =
                "입력한 정보를 확인해주세요.",

            fontSize =
                13.sp,

            color =
                TextGray,

            modifier =
                Modifier.align(
                    Alignment.CenterHorizontally
                )
        )


        Spacer(
            modifier =
                Modifier.height(
                    50.dp
                )
        )


        // ========================================
        // 이름
        // ========================================

        ConfirmRow(
            title =
                "이름",

            value =
                name
        )


        Spacer(
            modifier =
                Modifier.height(
                    30.dp
                )
        )


        // ========================================
        // 이메일
        // ========================================

        ConfirmRow(
            title =
                "이메일",

            value =
                email
        )


        Spacer(
            modifier =
                Modifier.height(
                    30.dp
                )
        )


        // ========================================
        // 아이디
        // ========================================

        ConfirmRow(
            title =
                "아이디",

            value =
                userId
        )


        Spacer(
            modifier =
                Modifier.weight(
                    1f
                )
        )


        Text(
            text =
                "입력한 정보로 회원가입을 진행합니다.",

            fontSize =
                12.sp,

            color =
                TextGray,

            textAlign =
                TextAlign.Center,

            modifier =
                Modifier.fillMaxWidth()
        )


        // ========================================
        // 회원가입 오류
        // ========================================

        if (
            errorMessage != null
        ) {

            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            Text(
                text =
                    errorMessage,

                fontSize =
                    13.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .error,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier.fillMaxWidth()
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    25.dp
                )
        )


        PrimaryButton(
            text =
                if (
                    isLoading
                ) {

                    "회원가입 중..."

                } else {

                    "가입하기"
                },

            enabled =
                !isLoading,

            onClick =
                onNextClick
        )
    }
}


// ========================================
// 입력정보 한 줄
// ========================================

@Composable
private fun ConfirmRow(
    title: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 5.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                title,

            fontSize =
                15.sp,

            color =
                Color(
                    0xFF666666
                ),

            modifier =
                Modifier.weight(
                    1f
                )
        )


        Text(
            text =
                value,

            fontSize =
                15.sp,

            fontWeight =
                FontWeight.SemiBold,

            color =
                TextBlack
        )
    }
}


// ========================================
// 4단계 : 회원가입 완료
// ========================================

@Composable
private fun CompleteStep(
    onCompleteClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 25.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.weight(
                    0.8f
                )
        )


        // ========================================
        // 완료 아이콘
        // ========================================

        Box(
            modifier = Modifier
                .size(
                    105.dp
                )
                .background(

                    color =
                        LightBlue,

                    shape =
                        CircleShape
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(
                        78.dp
                    )
                    .background(

                        color =
                            AnOnBlue,

                        shape =
                            CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.Check,

                    contentDescription =
                        null,

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(
                            48.dp
                        )
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    35.dp
                )
        )


        Text(
            text =
                "회원가입이 완료되었습니다!",

            fontSize =
                22.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                TextBlack
        )


        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )


        Text(
            text =
                "Rimo 서비스 가입을 환영합니다.",

            fontSize =
                13.sp,

            color =
                TextGray
        )


        Spacer(
            modifier =
                Modifier.weight(
                    1f
                )
        )


        PrimaryButton(
            text =
                "완료",

            enabled =
                true,

            onClick =
                onCompleteClick
        )
    }
}


// ========================================
// 공통 하단 버튼
// ========================================

@Composable
private fun PrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick =
            onClick,

        enabled =
            enabled,

        modifier = Modifier
            .fillMaxWidth()
            .height(
                56.dp
            ),

        shape =
            RoundedCornerShape(
                12.dp
            ),

        colors =
            ButtonDefaults.buttonColors(

                containerColor =
                    AnOnBlue,

                contentColor =
                    Color.White,

                disabledContainerColor =
                    Color(
                        0xFFCCD7F6
                    ),

                disabledContentColor =
                    Color.White
            )
    ) {

        Text(
            text =
                text,

            fontSize =
                16.sp,

            fontWeight =
                FontWeight.Bold
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
fun SignUpScreenPreview() {

    SignUpScreen()
}