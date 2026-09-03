package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clouddx_team4_project.data.TokenManager

// ========================================
// 색상
// ========================================

private val AnOnBlue =
    Color(0xFF6A92FE)

private val ScreenBg =
    Color(0xFFF8F9FC)

private val CardBorder =
    Color(0xFFE8EAF0)

private val LightBlue =
    Color(0xFFEFF4FF)

private val TextGray =
    Color(0xFF777777)

private val LightGray =
    Color(0xFFF2F3F5)


// ========================================
// 데이터 모델
// ========================================

data class SafeFriend(
    val memberId: Long = 0L,
    val name: String,
    val id: String = "",
    val locationSharing: Boolean
)


data class FriendRequest(
    val friendId: Long,
    val requestMemberId: Long,
    val name: String
)


// ========================================
// 안심친구 화면
// ========================================

@Composable
fun FriendScreen(
    onBackClick: () -> Unit = {},

    onAddFriendClick: () -> Unit = {},

    onAddFriendSubmit: (String, String) -> Unit = { _, _ -> },

    onAcceptRequest: (String) -> Unit = {},
    onRejectRequest: (String) -> Unit = {},
    onDeleteFriend: (String) -> Unit = {},
    onLocationClick: (String) -> Unit = {},
    onTabSelected: (String) -> Unit = {},
    onEmergencyClick: () -> Unit = {},

    friendViewModel: FriendViewModel = viewModel()
) {

    // ========================================
    // 친구 추가 팝업 상태
    // ========================================

    var showAddFriendDialog by remember {
        mutableStateOf(false)
    }


    // ========================================
    // 실제 받은 친구 요청
    // ========================================

    val receivedRequests = friendViewModel.receivedRequests

    val showFriendRequest = receivedRequests.isNotEmpty()

    val sentRequests = friendViewModel.sentRequests

    val showSentRequest = sentRequests.isNotEmpty()


    // ========================================
    // 친구 목록
    // ========================================

    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val currentMemberId = tokenManager.getMemberId() ?: 0L
    var locationSharingStates by remember {
        mutableStateOf<Map<Long, Boolean>>(emptyMap())
    }

    val friends = friendViewModel.friends.map { user ->
        SafeFriend(
            memberId = user.mmbrId,
            name = user.memberName,
            id = user.loginId,
            locationSharing =
                locationSharingStates[user.mmbrId] ?: false
        )
    }

    LaunchedEffect(Unit) {
        friendViewModel.loadAll(currentMemberId)
    }

    LaunchedEffect(friendViewModel.message) {
        friendViewModel.message?.let { message ->

            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()

            friendViewModel.clearMessage()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = 100.dp
                )
                .verticalScroll(
                    rememberScrollState()
                )
        ) {


            // ========================================
            // 상단바
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 15.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.ArrowBackIosNew,

                    contentDescription =
                        "뒤로가기",

                    tint =
                        Color(0xFF333333),

                    modifier = Modifier
                        .size(
                            22.dp
                        )
                        .clickable {

                            onBackClick()
                        }
                )


                Text(
                    text =
                        "안심친구",

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(0xFF222222),

                    textAlign =
                        TextAlign.Center,

                    modifier =
                        Modifier.weight(1f)
                )


                Icon(
                    imageVector =
                        Icons.Filled.Notifications,

                    contentDescription =
                        "알림",

                    tint =
                        Color(0xFF555555),

                    modifier =
                        Modifier.size(
                            23.dp
                        )
                )
            }


            // ========================================
            // 설명
            // ========================================

            Text(
                text =
                    "안심친구와 함께하면 더욱 안전해요.\n" +
                            "위치 공유와 상황을 실시간으로 나눌 수 있어요.",

                fontSize =
                    13.sp,

                lineHeight =
                    19.sp,

                color =
                    TextGray,

                modifier = Modifier.padding(
                    start = 22.dp,
                    end = 22.dp,
                    top = 4.dp
                )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            // ========================================
            // 친구 추가 버튼
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 22.dp
                    )
                    .height(
                        54.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            11.dp
                        )
                    )
                    .border(
                        width =
                            1.5.dp,

                        color =
                            AnOnBlue,

                        shape =
                            RoundedCornerShape(
                                11.dp
                            )
                    )
                    .clickable {

                        // 팝업 열기
                        showAddFriendDialog = true
                    },

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.Add,

                    contentDescription =
                        null,

                    tint =
                        AnOnBlue,

                    modifier =
                        Modifier.size(
                            22.dp
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
                        "친구 추가",

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        AnOnBlue
                )
            }


            // ========================================
            // 받은 요청
            // ========================================

            if (showFriendRequest) {

                Spacer(
                    modifier =
                        Modifier.height(
                            18.dp
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 22.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            "받은 요청",

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF333333)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                6.dp
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(
                                18.dp
                            )
                            .background(
                                AnOnBlue,
                                CircleShape
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                receivedRequests.size.toString(),

                            fontSize =
                                10.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.White
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            9.dp
                        )
                )

                receivedRequests.forEach { request ->

                    FriendRequestCard(
                        request = FriendRequest(
                            friendId = request.friendId,
                            requestMemberId = request.requestMemberId,
                            name = request.requesterName
                                ?: "회원 ${request.requestMemberId}"
                        ),

                        onAccept = {

                            friendViewModel.acceptRequest(
                                currentMemberId = currentMemberId,
                                friendId = request.friendId
                            )

                            onAcceptRequest(
                                request.requesterName
                                    ?: "회원 ${request.requestMemberId}"
                            )
                        },

                        onReject = {

                            friendViewModel.rejectRequest(
                                currentMemberId = currentMemberId,
                                friendId = request.friendId
                            )

                            onRejectRequest(
                                request.requesterName
                                    ?: "회원 ${request.requestMemberId}"
                            )
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                9.dp
                            )
                    )
                }
            }


            // ========================================
            // 보낸 요청
            // ========================================

            if (showSentRequest) {

                Spacer(
                    modifier =
                        Modifier.height(
                            18.dp
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 22.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            "보낸 요청",

                        fontSize =
                            14.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF333333)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                6.dp
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(
                                18.dp
                            )
                            .background(
                                AnOnBlue,
                                CircleShape
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                sentRequests.size.toString(),

                            fontSize =
                                10.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.White
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            9.dp
                        )
                )

                sentRequests.forEach { request ->

                    SentFriendRequestCard(
                        name =
                            request.receiverName
                                ?: "회원 ${request.receiveMemberId}",

                        onCancel = {

                            friendViewModel.cancelSentRequest(
                                currentMemberId = currentMemberId,
                                friendId = request.friendId
                            )
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                9.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        20.dp
                    )
            )


            // ========================================
            // 친구 목록 제목
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 22.dp
                    ),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "안심친구 목록",

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(
                    text =
                        "(${friends.size} / 50)",

                    fontSize =
                        12.sp,

                    color =
                        TextGray
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            // ========================================
            // 친구 카드
            // ========================================

            friends.forEach { friend ->

                FriendCard(
                    friend =
                        friend,

                    onLocationSharingChanged = { checked ->

                        locationSharingStates =
                            locationSharingStates
                                .toMutableMap()
                                .also { states ->

                                    states[friend.memberId] =
                                        checked
                                }
                        // 2. 백엔드로 API 호출 로직 추가
                        friendViewModel.toggleLocationSharing(
                            currentMemberId = currentMemberId,
                            friendMemberId = friend.memberId,
                            isSharing = checked
                        )
                    },

                    onLocationClick = {

                        // 2. 기존 외부 콜백 호출 (필요한 경우)
                        onLocationClick(friend.name)
                    },

                    onDeleteClick = {

                        friendViewModel.deleteFriend(
                            currentMemberId = currentMemberId,
                            friendMemberId = friend.memberId
                        )

                        onDeleteFriend(
                            friend.name
                        )
                    }
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        20.dp
                    )
            )
        }


        // ========================================
        // 공용 하단바
        // ========================================

        AnOnBottomBar(
            selectedTab =
                "",

            onTabSelected =
                onTabSelected,

            onEmergencyClick =
                onEmergencyClick,

            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                )
        )


        // ========================================
        // 친구 추가 팝업
        // ========================================

        if (showAddFriendDialog) {

            AddSafetyFriendDialog(

                onDismiss = {

                    showAddFriendDialog = false
                },

                onAddClick = { name, id ->

                    friendViewModel.sendFriendRequest(
                        currentMemberId = currentMemberId,
                        name = name,
                        loginId = id
                    )

                    onAddFriendSubmit(
                        name,
                        id
                    )

                    onAddFriendClick()

                    showAddFriendDialog = false
                }
            )
        }
    }
}


// ========================================
// 친구 추가 팝업
// ========================================

@Composable
private fun AddSafetyFriendDialog(
    onDismiss: () -> Unit,
    onAddClick: (String, String) -> Unit
) {

    var friendName by remember {
        mutableStateOf("")
    }


    var friendId by remember {
        mutableStateOf("")
    }


    Dialog(
        onDismissRequest = {

            onDismiss()
        }
    ) {

        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    22.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        8.dp
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 25.dp
                    )
            ) {


                // ========================================
                // 팝업 제목
                // ========================================

                Text(
                    text =
                        "안심친구 추가",

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(
                            0xFF222222
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp
                        )
                )


                Text(
                    text =
                        "추가할 안심친구의 정보를 입력해주세요.",

                    fontSize =
                        13.sp,

                    color =
                        Color(
                            0xFF888888
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            25.dp
                        )
                )


                // ========================================
                // 안심친구 이름
                // ========================================

                Text(
                    text =
                        "안심친구 이름",

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        Color(
                            0xFF333333
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                OutlinedTextField(
                    value =
                        friendName,

                    onValueChange = {

                        friendName = it
                    },

                    placeholder = {

                        Text(
                            text =
                                "이름을 입력해주세요",

                            fontSize =
                                14.sp,

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
                                AnOnBlue,

                            unfocusedBorderColor =
                                Color(
                                    0xFFE1E4EA
                                ),

                            focusedContainerColor =
                                Color.White,

                            unfocusedContainerColor =
                                Color.White,

                            cursorColor =
                                AnOnBlue
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            19.dp
                        )
                )


                // ========================================
                // 안심친구 아이디
                // ========================================

                Text(
                    text =
                        "안심친구 아이디",

                    fontSize =
                        14.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        Color(
                            0xFF333333
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                OutlinedTextField(
                    value =
                        friendId,

                    onValueChange = {

                        friendId = it
                    },

                    placeholder = {

                        Text(
                            text =
                                "아이디를 입력해주세요",

                            fontSize =
                                14.sp,

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
                                AnOnBlue,

                            unfocusedBorderColor =
                                Color(
                                    0xFFE1E4EA
                                ),

                            focusedContainerColor =
                                Color.White,

                            unfocusedContainerColor =
                                Color.White,

                            cursorColor =
                                AnOnBlue
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            27.dp
                        )
                )


                // ========================================
                // 취소 / 추가하기 버튼
                // ========================================

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {


                    // ========================================
                    // 취소
                    // ========================================

                    OutlinedButton(
                        onClick = {

                            onDismiss()
                        },

                        modifier = Modifier
                            .weight(
                                1f
                            )
                            .height(
                                50.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        border =
                            BorderStroke(
                                width =
                                    1.dp,

                                color =
                                    Color(
                                        0xFFE1E4EA
                                    )
                            ),

                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                containerColor =
                                    Color.White
                            )
                    ) {

                        Text(
                            text =
                                "취소",

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                Color(
                                    0xFF777777
                                )
                        )
                    }


                    // ========================================
                    // 추가하기
                    // ========================================

                    Button(
                        onClick = {

                            if (
                                friendName.isNotBlank() &&
                                friendId.isNotBlank()
                            ) {

                                onAddClick(
                                    friendName.trim(),
                                    friendId.trim()
                                )
                            }
                        },

                        enabled =
                            friendName.isNotBlank() &&
                                    friendId.isNotBlank(),

                        modifier = Modifier
                            .weight(
                                1f
                            )
                            .height(
                                50.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(

                                containerColor =
                                    AnOnBlue,

                                disabledContainerColor =
                                    Color(
                                        0xFFD4DDF7
                                    ),

                                contentColor =
                                    Color.White,

                                disabledContentColor =
                                    Color.White
                            )
                    ) {

                        Text(
                            text =
                                "추가하기",

                            fontSize =
                                15.sp,

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
// 친구 요청 카드
// ========================================

@Composable
private fun FriendRequestCard(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 22.dp
            )
            .shadow(
                elevation =
                    2.dp,

                shape =
                    RoundedCornerShape(
                        13.dp
                    )
            )
            .clip(
                RoundedCornerShape(
                    13.dp
                )
            )
            .background(
                Color.White
            )
            .border(
                width =
                    1.dp,

                color =
                    CardBorder,

                shape =
                    RoundedCornerShape(
                        13.dp
                    )
            )
            .padding(
                horizontal =
                    14.dp,

                vertical =
                    13.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        ProfileCircle(
            size =
                44
        )


        Spacer(
            modifier =
                Modifier.width(
                    12.dp
                )
        )


        Text(
            text =
                request.name,

            fontSize =
                15.sp,

            fontWeight =
                FontWeight.Bold,

            modifier =
                Modifier.weight(
                    1f
                )
        )


        Box(
            modifier = Modifier
                .height(
                    38.dp
                )
                .padding(
                    horizontal = 3.dp
                )
                .clip(
                    RoundedCornerShape(
                        8.dp
                    )
                )
                .background(
                    LightGray
                )
                .clickable {

                    onReject()
                }
                .padding(
                    horizontal =
                        13.dp
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    "거절",

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Medium,

                color =
                    Color(
                        0xFF555555
                    )
            )
        }


        Spacer(
            modifier =
                Modifier.width(
                    6.dp
                )
        )


        Box(
            modifier = Modifier
                .height(
                    38.dp
                )
                .clip(
                    RoundedCornerShape(
                        8.dp
                    )
                )
                .background(
                    AnOnBlue
                )
                .clickable {

                    onAccept()
                }
                .padding(
                    horizontal =
                        13.dp
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    "수락",

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color.White
            )
        }
    }
}

// ========================================
// 보낸 친구 요청 카드
// ========================================

@Composable
private fun SentFriendRequestCard(
    name: String,
    onCancel: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 22.dp
            )
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(13.dp)
            )
            .clip(
                RoundedCornerShape(13.dp)
            )
            .background(
                Color.White
            )
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(13.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 13.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        ProfileCircle(
            size = 44
        )

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Text(
            text = name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "대기 중",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = AnOnBlue
        )

        Spacer(
            modifier =
                Modifier.width(10.dp)
        )

        Box(
            modifier = Modifier
                .height(38.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(
                    LightGray
                )
                .clickable {
                    onCancel()
                }
                .padding(
                    horizontal = 12.dp
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "요청 취소",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF555555)
            )
        }
    }
}

// ========================================
// 안심친구 카드
// ========================================

@Composable
private fun FriendCard(
    friend: SafeFriend,
    onLocationSharingChanged: (Boolean) -> Unit,
    onLocationClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 22.dp
            )
            .shadow(
                elevation =
                    2.dp,

                shape =
                    RoundedCornerShape(
                        14.dp
                    )
            )
            .clip(
                RoundedCornerShape(
                    14.dp
                )
            )
            .background(
                Color.White
            )
            .border(
                width =
                    1.dp,

                color =
                    CardBorder,

                shape =
                    RoundedCornerShape(
                        14.dp
                    )
            )
    ) {


        // ========================================
        // 친구 기본 정보
        // ========================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 14.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            ProfileCircle(
                size =
                    46
            )


            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )


            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(
                    text =
                        friend.name,

                    fontSize =
                        16.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(
                            0xFF222222
                        )
                )


                // 아이디가 있을 경우에만 표시
                if (friend.id.isNotBlank()) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                2.dp
                            )
                    )


                    Text(
                        text =
                            "@${friend.id}",

                        fontSize =
                            11.sp,

                        color =
                            Color(
                                0xFF999999
                            )
                    )
                }
            }


            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally,

                modifier =
                    Modifier.clickable {

                        onLocationClick()
                    }
            ) {

                Box(
                    modifier = Modifier
                        .size(
                            38.dp
                        )
                        .background(
                            LightBlue,
                            CircleShape
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.LocationOn,

                        contentDescription =
                            "위치",

                        tint =
                            AnOnBlue,

                        modifier =
                            Modifier.size(
                                23.dp
                            )
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )


                Text(
                    text =
                        "위치",

                    fontSize =
                        10.sp,

                    color =
                        Color(
                            0xFF444444
                        )
                )
            }
        }


        // ========================================
        // 구분선
        // ========================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    1.dp
                )
                .background(
                    Color(
                        0xFFF0F0F0
                    )
                )
        )


        // ========================================
        // 위치 공유 / 친구 삭제
        // ========================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    54.dp
                )
                .padding(
                    start =
                        14.dp,

                    end =
                        10.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Filled.LocationOn,

                contentDescription =
                    null,

                tint =
                    if (
                        friend.locationSharing
                    ) {

                        AnOnBlue

                    } else {

                        Color(
                            0xFFBEBEBE
                        )
                    },

                modifier =
                    Modifier.size(
                        20.dp
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
                    "위치 공유",

                fontSize =
                    12.sp,

                color =
                    if (
                        friend.locationSharing
                    ) {

                        Color(
                            0xFF333333
                        )

                    } else {

                        Color.Gray
                    }
            )


            Spacer(
                modifier =
                    Modifier.width(
                        4.dp
                    )
            )


            Switch(
                checked =
                    friend.locationSharing,

                onCheckedChange = {

                    onLocationSharingChanged(
                        it
                    )
                },

                modifier =
                    Modifier.scale(
                        0.75f
                    ),

                colors =
                    SwitchDefaults.colors(

                        checkedThumbColor =
                            Color.White,

                        checkedTrackColor =
                            AnOnBlue,

                        uncheckedThumbColor =
                            Color.White,

                        uncheckedTrackColor =
                            Color(
                                0xFFD6D6D6
                            )
                    )
            )


            Spacer(
                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            Row(
                modifier = Modifier
                    .clickable {

                        onDeleteClick()
                    }
                    .padding(
                        7.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.Delete,

                    contentDescription =
                        "친구 삭제",

                    tint =
                        Color(
                            0xFFB3B3B3
                        ),

                    modifier =
                        Modifier.size(
                            18.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            4.dp
                        )
                )


                Text(
                    text =
                        "친구 삭제",

                    fontSize =
                        11.sp,

                    color =
                        Color(
                            0xFF999999
                        )
                )
            }
        }
    }
}


// ========================================
// 임시 프로필 원
// ========================================

@Composable
private fun ProfileCircle(
    size: Int
) {

    Box(
        modifier = Modifier
            .size(
                size.dp
            )
            .background(
                Color(
                    0xFFE9F0FF
                ),
                CircleShape
            ),

        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                "👤",

            fontSize =
                if (
                    size >= 46
                ) {

                    23.sp

                } else {

                    21.sp
                }
        )
    }
}


// ========================================
// Preview
// ========================================

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true
)
@Composable
fun FriendScreenPreview() {

    FriendScreen()
}