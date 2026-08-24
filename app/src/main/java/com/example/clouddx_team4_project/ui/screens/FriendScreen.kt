package com.example.clouddx_team4_project.ui.screens

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar


// ========================================
// 색상
// ========================================

private val AnOnBlue = Color(0xFF6A92FE)
private val ScreenBg = Color(0xFFF8F9FC)
private val CardBorder = Color(0xFFE8EAF0)
private val LightBlue = Color(0xFFEFF4FF)
private val TextGray = Color(0xFF777777)
private val LightGray = Color(0xFFF2F3F5)


// ========================================
// 데이터 모델
// ========================================

data class SafeFriend(
    val name: String,
    val locationSharing: Boolean
)

data class FriendRequest(
    val name: String
)


// ========================================
// 안심친구 화면
// ========================================

@Composable
fun FriendScreen(
    onBackClick: () -> Unit = {},
    onAddFriendClick: () -> Unit = {},
    onAcceptRequest: (String) -> Unit = {},
    onRejectRequest: (String) -> Unit = {},
    onDeleteFriend: (String) -> Unit = {},
    onLocationClick: (String) -> Unit = {},
    onTabSelected: (String) -> Unit = {},
    onEmergencyClick: () -> Unit = {}
) {

    // ========================================
    // UI 확인용 하드코딩 상태
    // true로 바꾸면 받은 요청 카드가 나타남
    // ========================================

    var showFriendRequest by remember {
        mutableStateOf(true)
    }


    var friends by remember {
        mutableStateOf(
            listOf(
                SafeFriend(
                    name = "박민수",
                    locationSharing = true
                ),
                SafeFriend(
                    name = "서윤지",
                    locationSharing = false
                )
            )
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
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
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "뒤로가기",
                    tint = Color(0xFF333333),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable {
                            onBackClick()
                        }
                )


                Text(
                    text = "안심친구",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )


                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "알림",
                    tint = Color(0xFF555555),
                    modifier = Modifier.size(23.dp)
                )
            }


            // ========================================
            // 설명
            // ========================================

            Text(
                text = "안심친구와 함께하면 더욱 안전해요.\n위치 공유와 상황을 실시간으로 나눌 수 있어요.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = TextGray,
                modifier = Modifier.padding(
                    start = 22.dp,
                    end = 22.dp,
                    top = 4.dp
                )
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // ========================================
            // 친구 추가 버튼
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .height(54.dp)
                    .clip(
                        RoundedCornerShape(11.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = AnOnBlue,
                        shape = RoundedCornerShape(11.dp)
                    )
                    .clickable {
                        onAddFriendClick()
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = AnOnBlue,
                    modifier = Modifier.size(22.dp)
                )


                Spacer(
                    modifier = Modifier.width(7.dp)
                )


                Text(
                    text = "친구 추가",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnOnBlue
                )
            }


            // ========================================
            // 받은 요청
            // ========================================

            if (showFriendRequest) {

                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "받은 요청",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )


                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )


                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                AnOnBlue,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "1",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(9.dp)
                )


                FriendRequestCard(
                    request = FriendRequest(
                        name = "이다은"
                    ),

                    onAccept = {

                        showFriendRequest = false

                        onAcceptRequest(
                            "이다은"
                        )
                    },

                    onReject = {

                        showFriendRequest = false

                        onRejectRequest(
                            "이다은"
                        )
                    }
                )
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // ========================================
            // 친구 목록 제목
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "안심친구 목록",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )


                Text(
                    text = "(${friends.size} / 50)",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // ========================================
            // 친구 카드
            // ========================================

            friends.forEachIndexed { index, friend ->

                FriendCard(
                    friend = friend,

                    onLocationSharingChanged = { checked ->

                        friends =
                            friends.toMutableList().also {
                                it[index] =
                                    friend.copy(
                                        locationSharing = checked
                                    )
                            }
                    },

                    onLocationClick = {
                        onLocationClick(
                            friend.name
                        )
                    },

                    onDeleteClick = {

                        friends =
                            friends.filterNot {
                                it.name == friend.name
                            }

                        onDeleteFriend(
                            friend.name
                        )
                    }
                )


                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }


        // ========================================
        // 공용 하단바
        // ========================================

        AnOnBottomBar(
            selectedTab = "",
            onTabSelected = onTabSelected,
            onEmergencyClick = onEmergencyClick,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
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
            .padding(horizontal = 22.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(13.dp)
            )
            .clip(
                RoundedCornerShape(13.dp)
            )
            .background(Color.White)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(13.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 13.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ProfileCircle(
            size = 44
        )


        Spacer(
            modifier = Modifier.width(12.dp)
        )


        Text(
            text = request.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )


        Box(
            modifier = Modifier
                .height(38.dp)
                .padding(horizontal = 3.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(LightGray)
                .clickable {
                    onReject()
                }
                .padding(horizontal = 13.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "거절",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF555555)
            )
        }


        Spacer(
            modifier = Modifier.width(6.dp)
        )


        Box(
            modifier = Modifier
                .height(38.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(AnOnBlue)
                .clickable {
                    onAccept()
                }
                .padding(horizontal = 13.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "수락",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


// ========================================
// 안심친구 한 명 카드
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
            .padding(horizontal = 22.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(14.dp)
            )
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(Color.White)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(14.dp)
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
            verticalAlignment = Alignment.CenterVertically
        ) {

            ProfileCircle(
                size = 46
            )


            Spacer(
                modifier = Modifier.width(12.dp)
            )


            Text(
                text = friend.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
                modifier = Modifier.weight(1f)
            )


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                        onLocationClick()
                    }
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            LightBlue,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "위치",
                        tint = AnOnBlue,
                        modifier = Modifier.size(23.dp)
                    )
                }


                Spacer(
                    modifier = Modifier.height(2.dp)
                )


                Text(
                    text = "위치",
                    fontSize = 10.sp,
                    color = Color(0xFF444444)
                )
            }
        }


        // 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Color(0xFFF0F0F0)
                )
        )


        // ========================================
        // 위치공유 / 삭제
        // ========================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(
                    start = 14.dp,
                    end = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint =
                    if (friend.locationSharing)
                        AnOnBlue
                    else
                        Color(0xFFBEBEBE),
                modifier = Modifier.size(20.dp)
            )


            Spacer(
                modifier = Modifier.width(5.dp)
            )


            Text(
                text = "위치 공유",
                fontSize = 12.sp,
                color =
                    if (friend.locationSharing)
                        Color(0xFF333333)
                    else
                        Color.Gray
            )


            Spacer(
                modifier = Modifier.width(4.dp)
            )


            Switch(
                checked = friend.locationSharing,
                onCheckedChange = {
                    onLocationSharingChanged(it)
                },
                modifier = Modifier.scale(0.75f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AnOnBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFD6D6D6)
                )
            )


            Spacer(
                modifier = Modifier.weight(1f)
            )


            Row(
                modifier = Modifier
                    .clickable {
                        onDeleteClick()
                    }
                    .padding(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "친구 삭제",
                    tint = Color(0xFFB3B3B3),
                    modifier = Modifier.size(18.dp)
                )


                Spacer(
                    modifier = Modifier.width(4.dp)
                )


                Text(
                    text = "친구 삭제",
                    fontSize = 11.sp,
                    color = Color(0xFF999999)
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
            .size(size.dp)
            .background(
                Color(0xFFE9F0FF),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "👤",
            fontSize = if (size >= 46) 23.sp else 21.sp
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