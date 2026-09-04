package com.example.clouddx_team4_project.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay


// ========================================
// 공통 색상
// ========================================

private val AnOnBlue = Color(0xFF6A92FE)
private val EmergencyRed = Color(0xFFE34836)
private val LightRed = Color(0xFFFFEEEE)
private val TextBlack = Color(0xFF222222)
private val TextGray = Color(0xFF777777)
private val BorderGray = Color(0xFFE4E7EC)


// ========================================
// 팝업 종류
// ========================================

enum class SafetyPopupState {
    NONE,
    INACTIVITY_CHECK,        // 30분 움직임 없음
    ROUTE_DEVIATION_CHECK,   // 경로 이탈
    FINAL_CHECK,             // 최종 10초 확인
    GUARDIAN_ALERT_SENT      // 보호자 문자 전송 완료
}


// ========================================
// 팝업 통합 Host
// ActiveRouteScreen에서는 이것 하나만 호출하면 됩니다.
// ========================================

@Composable
fun SafetyCheckDialogHost(

    state: SafetyPopupState,

    onSafeClick: () -> Unit,
    onNeedHelpClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onFinalTimeout: () -> Unit,
    onQuackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {

    when (state) {

        SafetyPopupState.NONE -> {
            // 아무 팝업 없음
        }

        SafetyPopupState.INACTIVITY_CHECK -> {

            InactivityCheckDialog(
                onSafeClick = onSafeClick,
                onNeedHelpClick = onNeedHelpClick,
                // 10분 무응답 시 "도움 필요해요"와 동일하게 최종확인으로 이동
                onTimeout = onNeedHelpClick
            )
        }

        SafetyPopupState.ROUTE_DEVIATION_CHECK -> {

            RouteDeviationCheckDialog(
                onSafeClick = onSafeClick,
                onNeedHelpClick = onNeedHelpClick,
                onTimeout = onNeedHelpClick
            )
        }

        SafetyPopupState.FINAL_CHECK -> {

            FinalSafetyCheckDialog(
                onSafeClick = onSafeClick,
                onEmergencyClick = onEmergencyClick,
                onTimeout = onFinalTimeout
            )
        }

        SafetyPopupState.GUARDIAN_ALERT_SENT -> {

            GuardianAlertSentDialog(
                onQuackClick = onQuackClick,
                onConfirmClick = onConfirmClick
            )
        }
    }
}


// ========================================
// 1. 30분 동안 움직임이 감지되지 않았을 때
// → 10분간 무응답 시 자동으로 onTimeout 호출
// ========================================

@Composable
fun InactivityCheckDialog(

    onSafeClick: () -> Unit,
    onNeedHelpClick: () -> Unit,
    onTimeout: () -> Unit,
    // timeoutSeconds: Int = 600  10분
    timeoutSeconds: Int = 15
) {

    var timeoutHandled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(timeoutSeconds * 1000L)

        if (!timeoutHandled) {
            timeoutHandled = true
            onTimeout()
        }
    }

    SafetyDialogContainer {

        QuestionIcon()

        Spacer(modifier = Modifier.height(17.dp))

        Text(
            text = "상태 확인",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(13.dp))

        Text(
            text = "30분 동안 움직임이 감지되지 않아\n확인이 필요해요.",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "무음 모드가 아니면 알림음,\n진동 모드면 진동으로 안내돼요.\n10분 동안 응답이 없으면 다시 한번 확인해요.",
            fontSize = 13.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedButton(
                onClick = {
                    timeoutHandled = true
                    onSafeClick()
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.4.dp, AnOnBlue)
            ) {
                Text(text = "괜찮아요", color = AnOnBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    timeoutHandled = true
                    onNeedHelpClick()
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnOnBlue)
            ) {
                Text(text = "도움 필요해요", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ========================================
// 2. 예정 경로에서 벗어났을 때
// → 10분간 무응답 시 자동으로 onTimeout 호출
// ========================================

@Composable
fun RouteDeviationCheckDialog(

    onSafeClick: () -> Unit,
    onNeedHelpClick: () -> Unit,
    onTimeout: () -> Unit,
    //timeoutSeconds: Int = 600
    timeoutSeconds: Int = 15
) {

    var timeoutHandled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(timeoutSeconds * 1000L)

        if (!timeoutHandled) {
            timeoutHandled = true
            onTimeout()
        }
    }

    SafetyDialogContainer {

        QuestionIcon()

        Spacer(modifier = Modifier.height(17.dp))

        Text(
            text = "경로 이탈 확인",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(13.dp))

        Text(
            text = "예정된 경로에서 벗어났어요.\n괜찮으신가요?",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "무음 모드가 아니면 알림음,\n진동 모드면 진동으로 안내돼요.\n10분 동안 응답이 없으면 다시 한번 확인해요.",
            fontSize = 13.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedButton(
                onClick = {
                    timeoutHandled = true
                    onSafeClick()
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.4.dp, AnOnBlue)
            ) {
                Text(text = "괜찮아요", color = AnOnBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    timeoutHandled = true
                    onNeedHelpClick()
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnOnBlue)
            ) {
                Text(text = "도움 필요해요", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ========================================
// 3. 10초 최종 확인
// 10초 동안 아무 버튼도 누르지 않으면 onTimeout() 자동 실행
// ========================================

@Composable
fun FinalSafetyCheckDialog(

    onSafeClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onTimeout: () -> Unit,
    initialSeconds: Int = 10
) {

    var countdown by remember { mutableIntStateOf(initialSeconds) }
    var timeoutHandled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        while (countdown > 0) {
            delay(1000L)
            countdown--
        }

        if (!timeoutHandled) {
            timeoutHandled = true
            onTimeout()
        }
    }

    SafetyDialogContainer {

        Box(
            modifier = Modifier
                .size(62.dp)
                .background(color = LightRed, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Filled.WarningAmber,
                contentDescription = null,
                tint = EmergencyRed,
                modifier = Modifier.size(35.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "최종 확인",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(9.dp))

        Text(
            text = "응답이 없어 다시 확인해요.",
            fontSize = 14.sp,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${countdown}초",
            fontSize = 43.sp,
            fontWeight = FontWeight.ExtraBold,
            color = EmergencyRed
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${countdown}초 안에 응답하지 않으면\n등록된 보호자에게 문자가 전송돼요.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedButton(
                onClick = {
                    timeoutHandled = true
                    onSafeClick()
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.4.dp, EmergencyRed)
            ) {
                Text(text = "괜찮아요", color = EmergencyRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    timeoutHandled = true
                    onEmergencyClick()
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
            ) {
                Text(text = "긴급신고", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ========================================
// 4. 보호자 문자 전송 완료
// ========================================

@Composable
fun GuardianAlertSentDialog(

    onQuackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {

    SafetyDialogContainer {

        Box(
            modifier = Modifier
                .size(62.dp)
                .background(color = Color(0xFFFFF3F3), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(color = Color.Transparent, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color(0xFFFF4D4D),
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "보호자 알림 전송",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "10초 동안 응답이 없어\n등록된 보호자에게 문자를 전송했어요.",
            fontSize = 15.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "필요하면 긴급신고 또는 꽥꽥이를\n바로 이용해 주세요.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedButton(
                onClick = onQuackClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.4.dp, AnOnBlue)
            ) {
                Text(text = "꽥꽥이", color = AnOnBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onConfirmClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AnOnBlue)
            ) {
                Text(text = "확인", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ========================================
// 공통 Dialog Container
// ========================================

@Composable
private fun SafetyDialogContainer(
    content: @Composable ColumnScope.() -> Unit
) {

    Dialog(
        onDismissRequest = { },
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

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = content
                )
            }
        }
    }
}


// ========================================
// 파란 물음표 아이콘
// ========================================

@Composable
private fun QuestionIcon() {

    Box(
        modifier = Modifier
            .size(58.dp)
            .background(color = Color(0xFFF4F7FF), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Filled.HelpOutline,
            contentDescription = null,
            tint = AnOnBlue,
            modifier = Modifier.size(42.dp)
        )
    }
}