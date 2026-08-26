package com.example.clouddx_team4_project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay


// ========================================
// 색상
// ========================================

private val AnOnBlue = Color(0xFF6A92FE)
private val EmergencyRed = Color(0xFFE23F3F)
private val LightRed = Color(0xFFFFE8E8)
private val TextGray = Color(0xFF777777)


// ========================================
// 긴급구조 Dialog 상태
// ========================================

private enum class EmergencyDialogState {
    COUNTDOWN,
    COMPLETE
}


// ========================================
// 긴급구조 공용 Dialog
// ========================================

@Composable
fun EmergencyDialog(
    onDismiss: () -> Unit,

    // 실제 문자/긴급신고 API 연결할 자리
    onEmergencyConfirmed: () -> Unit = {},

    // 꽥꽥이 화면 이동
    onQuackClick: () -> Unit = {}
) {

    var dialogState by remember {
        mutableStateOf(EmergencyDialogState.COUNTDOWN)
    }


    var countdown by remember {
        mutableIntStateOf(5)
    }


    // ========================================
    // 5초 자동 카운트다운
    // ========================================

    LaunchedEffect(dialogState) {

        if (dialogState == EmergencyDialogState.COUNTDOWN) {

            while (countdown > 0) {

                delay(1000)

                countdown--
            }


            // 0초가 되면 자동 긴급신고 처리
            if (dialogState == EmergencyDialogState.COUNTDOWN) {

                // ========================================
                // 나중에 여기서 백엔드 API 실행
                // ========================================

                onEmergencyConfirmed()


                // 접수 완료 UI로 변경
                dialogState =
                    EmergencyDialogState.COMPLETE
            }
        }
    }


    // ========================================
    // Dialog
    // ========================================

    Dialog(
        // 바깥을 눌러도 닫히지 않음
        onDismissRequest = {},

        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            when (dialogState) {

                // ========================================
                // 1. 5초 카운트다운
                // ========================================

                EmergencyDialogState.COUNTDOWN -> {

                    CountdownEmergencyCard(
                        countdown = countdown,

                        onCancel = {
                            onDismiss()
                        },

                        onImmediateReport = {

                            // ========================================
                            // 나중에 문자/긴급신고 API 호출
                            // ========================================

                            onEmergencyConfirmed()


                            dialogState =
                                EmergencyDialogState.COMPLETE
                        },

                        onQuackClick = {
                            onQuackClick()
                        }
                    )
                }


                // ========================================
                // 2. 신고 접수 완료
                // ========================================

                EmergencyDialogState.COMPLETE -> {

                    EmergencyCompleteCard(

                        onClose = {
                            onDismiss()
                        },

                        onQuackClick = {
                            onQuackClick()
                        }
                    )
                }
            }
        }
    }
}


// ========================================
// 첫 번째 화면
// 5초 자동 신고
// ========================================

@Composable
private fun CountdownEmergencyCard(
    countdown: Int,
    onCancel: () -> Unit,
    onImmediateReport: () -> Unit,
    onQuackClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 38.dp)
            .clip(
                RoundedCornerShape(22.dp)
            )
            .background(Color.White)
            .padding(
                horizontal = 22.dp,
                vertical = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ========================================
        // 경고 아이콘
        // ========================================

        Box(
            modifier = Modifier
                .size(66.dp)
                .background(
                    LightRed,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Filled.NotificationsActive,
                contentDescription = null,
                tint = EmergencyRed,
                modifier = Modifier.size(38.dp)
            )
        }


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // ========================================
        // 제목
        // ========================================

        Text(
            text = "긴급상황이 발생하였습니다.",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222),
            textAlign = TextAlign.Center
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = "잘못 누르신 경우 5초 안에 취소해주세요.",
            fontSize = 13.sp,
            color = TextGray,
            textAlign = TextAlign.Center
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        // ========================================
        // 카운트다운 숫자
        // ========================================

        Text(
            text = "${countdown}초",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            color = EmergencyRed
        )


        Spacer(
            modifier = Modifier.height(2.dp)
        )


        Text(
            text = "후 자동 신고됩니다.",
            fontSize = 14.sp,
            color = Color(0xFF444444),
            fontWeight = FontWeight.Medium
        )


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // ========================================
        // 취소 / 즉시신고
        // ========================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            // 취소
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .clip(
                        RoundedCornerShape(11.dp)
                    )
                    .background(
                        Color(0xFF202020)
                    )
                    .clickable {
                        onCancel()
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "취소",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            // 즉시신고
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .clip(
                        RoundedCornerShape(11.dp)
                    )
                    .background(
                        EmergencyRed
                    )
                    .clickable {
                        onImmediateReport()
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "즉시신고",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ========================================
        // 꽥꽥이 사용
        // ========================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(
                    RoundedCornerShape(11.dp)
                )
                .background(
                    Color(0xFFF5F8FF)
                )
                .clickable {
                    onQuackClick()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Filled.Campaign,
                contentDescription = null,
                tint = AnOnBlue,
                modifier = Modifier.size(24.dp)
            )


            Spacer(
                modifier = Modifier.width(8.dp)
            )


            Text(
                text = "꽥꽥이 사용",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AnOnBlue
            )
        }
    }
}


// ========================================
// 두 번째 화면
// 신고 접수 완료
// ========================================

@Composable
private fun EmergencyCompleteCard(
    onClose: () -> Unit,
    onQuackClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 34.dp)
            .clip(
                RoundedCornerShape(22.dp)
            )
            .background(Color.White)
    ) {

        // ========================================
        // 닫기 버튼
        // ========================================

        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "닫기",
            tint = Color.Gray,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp
                )
                .size(24.dp)
                .clickable {
                    onClose()
                }
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 28.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // ========================================
            // 접수 완료 아이콘
            // ========================================

            Box(
                modifier = Modifier.size(72.dp),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .background(
                            LightRed,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Filled.NotificationsActive,
                        contentDescription = null,
                        tint = EmergencyRed,
                        modifier = Modifier.size(38.dp)
                    )
                }


                // 체크 뱃지
                Box(
                    modifier = Modifier
                        .align(
                            Alignment.BottomEnd
                        )
                        .size(24.dp)
                        .background(
                            EmergencyRed,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // ========================================
            // 제목
            // ========================================

            Text(
                text = "긴급 신고가 접수되었습니다.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Text(
                text = "등록한 보호자에게 문자 알림이 전송되었습니다.",
                fontSize = 13.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            Text(
                text = "필요하면 꽥꽥이를 바로 사용할 수 있어요.",
                fontSize = 13.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // ========================================
            // 꽥꽥이 사용
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(AnOnBlue)
                    .clickable {
                        onQuackClick()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.Campaign,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )


                Spacer(
                    modifier = Modifier.width(9.dp)
                )


                Text(
                    text = "꽥꽥이 사용",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}