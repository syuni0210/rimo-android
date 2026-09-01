package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.data.TokenManager
import com.example.clouddx_team4_project.network.DestinationCreateRequest
import com.example.clouddx_team4_project.network.DestinationResponse
import com.example.clouddx_team4_project.network.RetrofitClient
import kotlinx.coroutines.launch


private val DestinationBlue =
    Color(0xFF6A92FE)

private val DestinationBackground =
    Color(0xFFF8F9FC)

private val DestinationTextBlack =
    Color(0xFF222222)

private val DestinationTextGray =
    Color(0xFF8B8B8B)

private val DestinationBorder =
    Color(0xFFE7E9EE)


// ========================================
// 기본 목적지 설정 화면
// ========================================

@Composable
fun DefaultDestinationScreen(

    onBackClick: () -> Unit = {},

    // 목적지 등록 버튼 클릭
    // → 장소 검색 화면 이동
    onSearchPlaceClick: () -> Unit = {},

    // 검색 후 선택한 장소 정보
    selectedPlaceName: String? = null,

    selectedAddress: String? = null,

    selectedLatitude: Double? = null,

    selectedLongitude: Double? = null,

    // 저장/취소 후 Navigation에 있는
    // 임시 선택 장소 초기화
    onDestinationSaved: () -> Unit = {}

) {

    val coroutineScope =
        rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val memberId = tokenManager.getMemberId() ?: -1L


    // ========================================
    // DB에서 불러온 목적지 목록
    // ========================================

    var savedDestinations by remember {

        mutableStateOf(
            emptyList<DestinationResponse>()
        )
    }


    // ========================================
    // 로딩 상태
    // ========================================

    var isLoading by remember {

        mutableStateOf(
            true
        )
    }


    // ========================================
    // 에러 메시지
    // ========================================

    var errorMessage by remember {

        mutableStateOf<String?>(null)
    }


    // ========================================
    // 이름 입력 팝업
    // ========================================

    var showNameDialog by remember {

        mutableStateOf(
            false
        )
    }


    var destinationName by remember {

        mutableStateOf(
            ""
        )
    }


    var nameError by remember {

        mutableStateOf(
            false
        )
    }


    // ========================================
    // 삭제 확인 팝업
    // ========================================

    var deleteTarget by remember {

        mutableStateOf<DestinationResponse?>(null)
    }


    // ========================================
    // 기본 목적지 목록 조회 함수
    // ========================================

    fun loadDestinations() {

        coroutineScope.launch {

            try {

                isLoading =
                    true

                errorMessage =
                    null


                savedDestinations =
                    RetrofitClient
                        .destinationApi
                        .getDestinations(
                            memberId
                        )


            } catch (
                e: Exception
            ) {

                errorMessage =
                    "목적지 목록을 불러오지 못했습니다."

                e.printStackTrace()


            } finally {

                isLoading =
                    false
            }
        }
    }


    // ========================================
    // 화면 처음 진입 시 DB 조회
    // ========================================

    LaunchedEffect(
        memberId
    ) {

        loadDestinations()
    }


    // ========================================
    // 장소 검색 후 다시 돌아오면
    // 이름 입력 팝업 자동 표시
    // ========================================

    LaunchedEffect(
        selectedPlaceName,
        selectedLatitude,
        selectedLongitude
    ) {

        if (
            !selectedPlaceName.isNullOrBlank() &&
            selectedLatitude != null &&
            selectedLongitude != null
        ) {

            destinationName =
                ""

            nameError =
                false

            showNameDialog =
                true
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                DestinationBackground
            )
            .statusBarsPadding()
    ) {


        // ========================================
        // 상단바
        // ========================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    60.dp
                )
                .padding(
                    horizontal = 18.dp
                )
        ) {

            Icon(
                imageVector =
                    Icons.Filled.ArrowBackIosNew,

                contentDescription =
                    "뒤로가기",

                tint =
                    DestinationTextBlack,

                modifier = Modifier
                    .size(
                        20.dp
                    )
                    .align(
                        Alignment.CenterStart
                    )
                    .clickable {

                        onBackClick()
                    }
            )


            Text(
                text =
                    "기본 목적지 설정",

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    DestinationTextBlack,

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
                    horizontal = 18.dp
                )
        ) {


            // ========================================
            // 설명
            // ========================================

            Text(
                text =
                    "자주 가는 장소를 등록하고 안심경로에서 빠르게 선택할 수 있어요.",

                fontSize =
                    12.sp,

                color =
                    DestinationTextGray,

                lineHeight =
                    18.sp
            )


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            // ========================================
            // 목적지 등록 버튼
            // ========================================

            Button(
                onClick = {

                    onSearchPlaceClick()
                },

                modifier = Modifier
                    .fillMaxWidth()
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
                            DestinationBlue
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.Add,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(
                            20.dp
                        ),

                    tint =
                        Color.White
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            7.dp
                        )
                )


                Text(
                    text =
                        "목적지 등록",

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
                        24.dp
                    )
            )


            // ========================================
            // 등록된 목적지 제목
            // ========================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "등록된 목적지",

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        DestinationTextBlack
                )


                Spacer(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                Text(
                    text =
                        "${savedDestinations.size}개",

                    fontSize =
                        13.sp,

                    color =
                        DestinationTextGray
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // ========================================
            // 로딩
            // ========================================

            if (
                isLoading
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            160.dp
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color =
                            DestinationBlue,

                        modifier =
                            Modifier.size(
                                32.dp
                            )
                    )
                }


            } else if (
                errorMessage != null
            ) {


                // ========================================
                // 오류
                // ========================================

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),

                    border =
                        BorderStroke(
                            1.dp,
                            DestinationBorder
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
                                24.dp
                            ),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                errorMessage
                                    ?: "",

                            fontSize =
                                13.sp,

                            color =
                                DestinationTextGray
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp
                                )
                        )


                        TextButton(
                            onClick = {

                                loadDestinations()
                            }
                        ) {

                            Text(
                                text =
                                    "다시 시도",

                                color =
                                    DestinationBlue,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }


            } else if (
                savedDestinations.isEmpty()
            ) {


                // ========================================
                // 등록된 목적지 없음
                // ========================================

                EmptyDestinationCard()


            } else {


                // ========================================
                // DB 목적지 목록
                // ========================================

                savedDestinations.forEach { destination ->

                    DestinationCard(

                        destination =
                            destination,

                        onDeleteClick = {

                            deleteTarget =
                                destination
                        }
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                10.dp
                            )
                    )
                }
            }
        }
    }


    // ========================================
    // 장소 선택 후 이름 입력 팝업
    // ========================================

    if (
        showNameDialog
    ) {

        AlertDialog(

            onDismissRequest = {

                showNameDialog =
                    false

                onDestinationSaved()
            },

            title = {

                Text(
                    text =
                        "목적지 이름 설정",

                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Column {


                    // ========================================
                    // 선택한 장소
                    // ========================================

                    Text(
                        text =
                            "선택한 장소",

                        fontSize =
                            13.sp,

                        color =
                            DestinationTextGray
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                6.dp
                            )
                    )


                    Text(
                        text =
                            selectedPlaceName
                                ?: "",

                        fontSize =
                            15.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            DestinationTextBlack
                    )


                    if (
                        !selectedAddress
                            .isNullOrBlank()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    4.dp
                                )
                        )


                        Text(
                            text =
                                selectedAddress,

                            fontSize =
                                12.sp,

                            color =
                                DestinationTextGray
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                20.dp
                            )
                    )


                    // ========================================
                    // 사용자 지정 이름
                    // ========================================

                    Text(
                        text =
                            "이 장소의 이름을 정해주세요.",

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            DestinationTextBlack
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    OutlinedTextField(
                        value =
                            destinationName,

                        onValueChange = {

                            destinationName =
                                it

                            nameError =
                                false
                        },

                        placeholder = {

                            Text(
                                text =
                                    "예: 집, 학교, 회사"
                            )
                        },

                        singleLine =
                            true,

                        isError =
                            nameError,

                        shape =
                            RoundedCornerShape(
                                10.dp
                            ),

                        colors =
                            OutlinedTextFieldDefaults.colors(

                                focusedBorderColor =
                                    DestinationBlue,

                                unfocusedBorderColor =
                                    DestinationBorder
                            ),

                        modifier =
                            Modifier.fillMaxWidth()
                    )


                    if (
                        nameError
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    6.dp
                                )
                        )


                        Text(
                            text =
                                "목적지 이름을 입력해주세요.",

                            fontSize =
                                12.sp,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        )
                    }
                }
            },


            dismissButton = {

                TextButton(
                    onClick = {

                        showNameDialog =
                            false

                        onDestinationSaved()
                    }
                ) {

                    Text(
                        text =
                            "취소",

                        color =
                            DestinationTextGray
                    )
                }
            },


            confirmButton = {

                TextButton(
                    onClick = {

                        val trimmedName =
                            destinationName.trim()


                        if (
                            trimmedName.isBlank()
                        ) {

                            nameError =
                                true

                            return@TextButton
                        }


                        if (
                            selectedPlaceName == null ||
                            selectedLatitude == null ||
                            selectedLongitude == null
                        ) {

                            return@TextButton
                        }


                        coroutineScope.launch {

                            try {

                                val response =
                                    RetrofitClient
                                        .destinationApi
                                        .createDestination(

                                            memberId =
                                                memberId,

                                            request =
                                                DestinationCreateRequest(

                                                    name =
                                                        trimmedName,

                                                    placeName =
                                                        selectedPlaceName,

                                                    address =
                                                        selectedAddress
                                                            ?: "",

                                                    latitude =
                                                        selectedLatitude,

                                                    longitude =
                                                        selectedLongitude
                                                )
                                        )


                                if (
                                    response.isSuccessful
                                ) {

                                    showNameDialog =
                                        false


                                    onDestinationSaved()


                                    // 저장 후 DB 목록 다시 조회
                                    loadDestinations()


                                } else {

                                    errorMessage =
                                        "목적지 저장에 실패했습니다."
                                }


                            } catch (
                                e: Exception
                            ) {

                                errorMessage =
                                    "목적지 저장 중 오류가 발생했습니다."

                                e.printStackTrace()
                            }
                        }
                    }
                ) {

                    Text(
                        text =
                            "저장",

                        color =
                            DestinationBlue,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        )
    }


    // ========================================
    // 삭제 확인 팝업
    // ========================================

    if (
        deleteTarget != null
    ) {

        AlertDialog(

            onDismissRequest = {

                deleteTarget =
                    null
            },

            title = {

                Text(
                    text =
                        "목적지 삭제",

                    fontWeight =
                        FontWeight.Bold
                )
            },

            text = {

                Text(
                    text =
                        "'${deleteTarget?.name}' 목적지를 삭제하시겠습니까?"
                )
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        deleteTarget =
                            null
                    }
                ) {

                    Text(
                        text =
                            "취소",

                        color =
                            DestinationTextGray
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val target =
                            deleteTarget
                                ?: return@TextButton


                        coroutineScope.launch {

                            try {

                                val response =
                                    RetrofitClient
                                        .destinationApi
                                        .deleteDestination(

                                            memberId =
                                                memberId,

                                            destinationId =
                                                target.destinationId
                                        )


                                if (
                                    response.isSuccessful
                                ) {

                                    deleteTarget =
                                        null


                                    loadDestinations()


                                } else {

                                    errorMessage =
                                        "목적지 삭제에 실패했습니다."
                                }


                            } catch (
                                e: Exception
                            ) {

                                errorMessage =
                                    "목적지 삭제 중 오류가 발생했습니다."

                                e.printStackTrace()
                            }
                        }
                    }
                ) {

                    Text(
                        text =
                            "삭제",

                        color =
                            MaterialTheme
                                .colorScheme
                                .error,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        )
    }
}


// ========================================
// 빈 목적지 카드
// ========================================

@Composable
private fun EmptyDestinationCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                180.dp
            ),

        shape =
            RoundedCornerShape(
                14.dp
            ),

        border =
            BorderStroke(
                width =
                    1.dp,

                color =
                    DestinationBorder
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    0.dp
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize(),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(
                        50.dp
                    )
                    .background(
                        color =
                            Color(
                                0xFFF0F4FF
                            ),

                        shape =
                            CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.LocationOn,

                    contentDescription =
                        null,

                    tint =
                        DestinationBlue,

                    modifier =
                        Modifier.size(
                            25.dp
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        17.dp
                    )
            )


            Text(
                text =
                    "등록된 목적지가 없습니다.",

                fontSize =
                    15.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    DestinationTextBlack
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(
                text =
                    "자주 가는 장소를 등록하면\n안심경로에서 빠르게 선택할 수 있어요.",

                fontSize =
                    12.sp,

                color =
                    DestinationTextGray,

                lineHeight =
                    18.sp
            )
        }
    }
}


// ========================================
// 등록된 목적지 카드
// ========================================

@Composable
private fun DestinationCard(

    destination:
    DestinationResponse,

    onDeleteClick:
        () -> Unit

) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                14.dp
            ),

        border =
            BorderStroke(
                width =
                    1.dp,

                color =
                    DestinationBorder
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    0.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 15.dp,
                    vertical = 15.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // ========================================
            // 위치 아이콘
            // ========================================

            Box(
                modifier = Modifier
                    .size(
                        44.dp
                    )
                    .background(
                        color =
                            Color(
                                0xFFF0F4FF
                            ),

                        shape =
                            CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.LocationOn,

                    contentDescription =
                        null,

                    tint =
                        DestinationBlue,

                    modifier =
                        Modifier.size(
                            23.dp
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
            // 목적지 정보
            // ========================================

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {


                // 사용자가 정한 이름
                Text(
                    text =
                        destination.name,

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        DestinationTextBlack
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            4.dp
                        )
                )


                // 검색된 장소명
                Text(
                    text =
                        destination.placeName,

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Medium,

                    color =
                        Color(
                            0xFF555555
                        ),

                    maxLines =
                        1,

                    overflow =
                        TextOverflow.Ellipsis
                )


                // 실제 주소
                if (
                    destination.address
                        .isNotBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                3.dp
                            )
                    )


                    Text(
                        text =
                            destination.address,

                        fontSize =
                            11.sp,

                        color =
                            DestinationTextGray,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }
            }


            // ========================================
            // 삭제 버튼
            // ========================================

            IconButton(
                onClick =
                    onDeleteClick
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.Delete,

                    contentDescription =
                        "삭제",

                    tint =
                        Color(
                            0xFFB5B5B5
                        ),

                    modifier =
                        Modifier.size(
                            20.dp
                        )
                )
            }
        }
    }
}