package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar
import java.time.LocalDate
import java.time.YearMonth


// ========================================
// 색상
// ========================================

private val AnOnBlue = Color(0xFF6A92FE)

private val DeepBlue = Color(0xFF3478F6)
private val MiddleBlue = Color(0xFF72A4FF)
private val LightBlue = Color(0xFFB8D0FF)

private val CardBackground = Color(0xFFF8FAFF)
private val BorderColor = Color(0xFFE9EDF5)
private val TextGray = Color(0xFF6F6F6F)


// ========================================
// 데이터 모델
// ========================================

data class RoutePreference(
    val label: String,
    val percent: Int,
    val color: Color
)

data class ReturnRecord(
    val date: LocalDate,
    val startLocation: String,
    val destination: String,
    val startTime: String,
    val arrivalTime: String,
    val duration: String,
    val routeType: String,
    val distance: String
)


// ========================================
// 사용 리포트 메인 화면
// ========================================

@Composable
fun ReportScreen(
    onTabSelected: (String) -> Unit = {},
    onEmergencyClick: () -> Unit = {}
) {

    var selectedReportTab by remember {
        mutableStateOf("대시보드")
    }


    // ========================================
    // 임시 경로 선호도 데이터
    // ========================================

    val routePreferences = listOf(

        RoutePreference(
            label = "빠른길",
            percent = 63,
            color = DeepBlue
        ),

        RoutePreference(
            label = "비추천길",
            percent = 25,
            color = MiddleBlue
        ),

        RoutePreference(
            label = "대로변",
            percent = 12,
            color = LightBlue
        )
    )


    // ========================================
    // 임시 귀가 기록
    // ========================================

    val returnRecords = listOf(

        ReturnRecord(
            date = LocalDate.of(2026, 7, 3),
            startLocation = "강남역 2번 출구",
            destination = "집",
            startTime = "20:32",
            arrivalTime = "20:58",
            duration = "26분",
            routeType = "빠른길",
            distance = "1.8km"
        ),

        ReturnRecord(
            date = LocalDate.of(2026, 7, 7),
            startLocation = "역삼역",
            destination = "집",
            startTime = "21:11",
            arrivalTime = "21:34",
            duration = "23분",
            routeType = "대로변",
            distance = "1.5km"
        ),

        ReturnRecord(
            date = LocalDate.of(2026, 7, 10),
            startLocation = "신논현역",
            destination = "집",
            startTime = "22:03",
            arrivalTime = "22:29",
            duration = "26분",
            routeType = "빠른길",
            distance = "1.7km"
        ),

        ReturnRecord(
            date = LocalDate.of(2026, 7, 14),
            startLocation = "강남역 2번 출구",
            destination = "집",
            startTime = "21:48",
            arrivalTime = "22:12",
            duration = "24분",
            routeType = "빠른길",
            distance = "1.6km"
        ),

        ReturnRecord(
            date = LocalDate.of(2026, 7, 20),
            startLocation = "교대역",
            destination = "집",
            startTime = "20:40",
            arrivalTime = "21:08",
            duration = "28분",
            routeType = "비추천길",
            distance = "2.0km"
        ),

        ReturnRecord(
            date = LocalDate.of(2026, 7, 27),
            startLocation = "강남역",
            destination = "집",
            startTime = "21:20",
            arrivalTime = "21:44",
            duration = "24분",
            routeType = "빠른길",
            distance = "1.6km"
        )
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
        ) {

            // ========================================
            // 화면 제목
            // ========================================

            Text(
                text = "사용 리포트",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(
                        top = 18.dp,
                        bottom = 18.dp
                    )
            )


            // ========================================
            // 대시보드 / 기록 탭
            // ========================================

            ReportTabBar(
                selectedTab = selectedReportTab,
                onTabClick = {
                    selectedReportTab = it
                }
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // ========================================
            // 탭에 따라 화면 변경
            // ========================================

            if (selectedReportTab == "대시보드") {

                DashboardContent(
                    routePreferences = routePreferences
                )

            } else {

                RecordContent(
                    returnRecords = returnRecords
                )
            }
        }


        // ========================================
        // 공용 하단 네비게이션
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
// 대시보드 / 기록 탭
// ========================================

@Composable
private fun ReportTabBar(
    selectedTab: String,
    onTabClick: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp)
            .clip(
                RoundedCornerShape(11.dp)
            )
            .background(
                Color(0xFFF5F6F8)
            )
    ) {

        ReportTabItem(
            text = "대시보드",
            selected = selectedTab == "대시보드",
            modifier = Modifier.weight(1f)
        ) {
            onTabClick("대시보드")
        }


        ReportTabItem(
            text = "기록",
            selected = selectedTab == "기록",
            modifier = Modifier.weight(1f)
        ) {
            onTabClick("기록")
        }
    }
}


@Composable
private fun ReportTabItem(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(3.dp)
            .clip(
                RoundedCornerShape(8.dp)
            )
            .background(
                if (selected)
                    AnOnBlue
                else
                    Color.Transparent
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight =
                if (selected)
                    FontWeight.Bold
                else
                    FontWeight.Medium,
            color =
                if (selected)
                    Color.White
                else
                    TextGray
        )
    }
}


// ========================================
// 대시보드
// ========================================

@Composable
private fun DashboardContent(
    routePreferences: List<RoutePreference>
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(horizontal = 20.dp)
            .padding(bottom = 30.dp)
    ) {

        // ========================================
        // 이번 주 요약
        // ========================================

        SectionTitle(
            title = "이번 주 요약"
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {

            SummaryCard(
                title = "총 귀가 횟수",
                value = "12",
                unit = "회",
                iconType = "home",
                modifier = Modifier.weight(1f)
            )


            SummaryCard(
                title = "평균 소요시간",
                value = "23",
                unit = "분",
                iconType = "time",
                modifier = Modifier.weight(1f)
            )


            SummaryCard(
                title = "야간 귀가 수",
                value = "4",
                unit = "회",
                iconType = "night",
                modifier = Modifier.weight(1f)
            )
        }


        Spacer(
            modifier = Modifier.height(28.dp)
        )


        // ========================================
        // 경로 선호도
        // ========================================

        SectionTitle(
            title = "경로 선호도"
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )


        RoutePreferenceSection(
            preferences = routePreferences
        )


        Spacer(
            modifier = Modifier.height(32.dp)
        )


        // ========================================
        // 친구
        // ========================================

        SectionTitle(
            title = "앱을 가장 많이 사용한 친구"
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        FriendCard()


        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}


// ========================================
// 섹션 제목
// ========================================

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF222222)
    )
}


// ========================================
// 이번 주 요약 카드
// ========================================

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    unit: String,
    iconType: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .height(118.dp)
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
                color = BorderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(
                horizontal = 9.dp,
                vertical = 13.dp
            )
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    when (iconType) {

                        "time" ->
                            Icons.Filled.AccessTime

                        "night" ->
                            Icons.Filled.DarkMode

                        else ->
                            Icons.Filled.Home
                    },

                contentDescription = null,
                tint = AnOnBlue,
                modifier = Modifier.size(20.dp)
            )


            Spacer(
                modifier = Modifier.width(4.dp)
            )


            Text(
                text = title,
                fontSize = 13.sp,
                color = TextGray,
                maxLines = 1
            )
        }


        Spacer(
            modifier = Modifier.weight(1f)
        )


        Row(
            verticalAlignment = Alignment.Bottom
        ) {

            Text(
                text = value,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )


            Spacer(
                modifier = Modifier.width(3.dp)
            )


            Text(
                text = unit,
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(
                    bottom = 5.dp
                )
            )
        }
    }
}


// ========================================
// 경로 선호도
// ========================================

@Composable
private fun RoutePreferenceSection(
    preferences: List<RoutePreference>
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ========================================
        // 도넛 차트
        // ========================================

        Box(
            modifier = Modifier.size(152.dp),
            contentAlignment = Alignment.Center
        ) {

            Canvas(
                modifier = Modifier.size(140.dp)
            ) {

                var startAngle = -90f

                preferences.forEach { item ->

                    val sweepAngle =
                        item.percent / 100f * 360f

                    drawArc(
                        color = item.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(
                            width = 32f,
                            cap = StrokeCap.Butt
                        )
                    )

                    startAngle += sweepAngle
                }
            }


            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Color.White,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = AnOnBlue,
                    modifier = Modifier.size(30.dp)
                )
            }
        }


        Spacer(
            modifier = Modifier.width(18.dp)
        )


        // ========================================
        // 범례
        // ========================================

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(17.dp)
        ) {

            preferences.forEach { item ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                item.color,
                                CircleShape
                            )
                    )


                    Spacer(
                        modifier = Modifier.width(9.dp)
                    )


                    Text(
                        text = item.label,
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        color = Color(0xFF444444)
                    )


                    Text(
                        text = "${item.percent}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }
        }
    }
}


// ========================================
// 친구 카드
// ========================================

@Composable
private fun FriendCard() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .clip(
                RoundedCornerShape(15.dp)
            )
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    Color(0xFFE9F0FF),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = AnOnBlue,
                modifier = Modifier.size(36.dp)
            )
        }


        Spacer(
            modifier = Modifier.width(15.dp)
        )


        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "박민수",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            Text(
                text = "함께 사용한 횟수",
                fontSize = 14.sp,
                color = TextGray
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            Text(
                text = "이번 주 18회",
                color = AnOnBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }


        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(26.dp)
        )
    }
}


// ========================================
// 기록 탭
// ========================================

@Composable
private fun RecordContent(
    returnRecords: List<ReturnRecord>
) {

    var currentMonth by remember {
        mutableStateOf(
            YearMonth.of(
                2026,
                7
            )
        )
    }


    var selectedDate by remember {
        mutableStateOf(
            LocalDate.of(
                2026,
                7,
                14
            )
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        // ========================================
        // 1. 달력
        // 고정 영역
        // ========================================

        CalendarCard(
            currentMonth = currentMonth,
            selectedDate = selectedDate,

            recordDates =
                returnRecords
                    .map { it.date }
                    .toSet(),

            onPreviousMonth = {

                currentMonth =
                    currentMonth.minusMonths(1)
            },

            onNextMonth = {

                currentMonth =
                    currentMonth.plusMonths(1)
            },

            onDateClick = {

                selectedDate = it
            }
        )


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // ========================================
        // 2. 귀가 기록 제목
        // 이것도 고정
        // ========================================

        Text(
            text =
                "${selectedDate.year}.${selectedDate.monthValue}.${selectedDate.dayOfMonth} 귀가 기록",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )


        Spacer(
            modifier = Modifier.height(14.dp)
        )


        val selectedRecord =
            returnRecords.firstOrNull {
                it.date == selectedDate
            }


        // ========================================
        // 3. 아래 기록 상세만 스크롤
        // ========================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(bottom = 24.dp)
        ) {

            if (selectedRecord != null) {

                ReturnRecordCard(
                    record = selectedRecord
                )

            } else {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(CardBackground),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "해당 날짜의 귀가 기록이 없습니다.",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}


// ========================================
// 달력
// ========================================

@Composable
private fun CalendarCard(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    recordDates: Set<LocalDate>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (LocalDate) -> Unit
) {

    val firstDay =
        currentMonth.atDay(1)

    val leadingBlankCount =
        firstDay.dayOfWeek.value % 7

    val daysInMonth =
        currentMonth.lengthOfMonth()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(Color.White)
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 16.dp
            )
    ) {

        // ========================================
        // 월 이동
        // ========================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Icon(
                imageVector =
                    Icons.Filled.ChevronLeft,
                contentDescription = "이전 달",
                tint = Color.Gray,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onPreviousMonth()
                    }
            )


            Text(
                text =
                    "${currentMonth.year}년 ${currentMonth.monthValue}월",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )


            Icon(
                imageVector =
                    Icons.Filled.ChevronRight,
                contentDescription = "다음 달",
                tint = Color.Gray,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onNextMonth()
                    }
            )
        }


        Spacer(
            modifier = Modifier.height(17.dp)
        )


        // ========================================
        // 요일
        // ========================================

        val weekDays =
            listOf(
                "일",
                "월",
                "화",
                "수",
                "목",
                "금",
                "토"
            )


        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            weekDays.forEach { day ->

                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            }
        }


        Spacer(
            modifier = Modifier.height(11.dp)
        )


        // ========================================
        // 날짜 그리드
        // ========================================

        val totalCells =
            leadingBlankCount + daysInMonth

        val rowCount =
            (totalCells + 6) / 7


        repeat(rowCount) { week ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {

                repeat(7) { dayOfWeek ->

                    val cellIndex =
                        week * 7 + dayOfWeek

                    val dayNumber =
                        cellIndex -
                                leadingBlankCount +
                                1


                    Box(
                        modifier =
                            Modifier.weight(1f),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        if (
                            dayNumber in
                            1..daysInMonth
                        ) {

                            val date =
                                currentMonth.atDay(
                                    dayNumber
                                )

                            CalendarDay(
                                date = date,
                                selected =
                                    date == selectedDate,
                                hasRecord =
                                    recordDates.contains(date),
                                onClick = {
                                    onDateClick(date)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


// ========================================
// 달력 날짜 하나
// ========================================

@Composable
private fun CalendarDay(
    date: LocalDate,
    selected: Boolean,
    hasRecord: Boolean,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally,
        modifier = Modifier
            .size(
                width = 42.dp,
                height = 48.dp
            )
            .clickable {
                onClick()
            }
    ) {

        Box(
            modifier = Modifier
                .size(35.dp)
                .background(
                    color =
                        if (selected)
                            AnOnBlue
                        else
                            Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text =
                    date.dayOfMonth.toString(),
                fontSize = 15.sp,
                fontWeight =
                    if (selected)
                        FontWeight.Bold
                    else
                        FontWeight.Medium,
                color =
                    if (selected)
                        Color.White
                    else
                        Color(0xFF333333)
            )
        }


        if (hasRecord) {

            Spacer(
                modifier = Modifier.height(2.dp)
            )


            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(
                        AnOnBlue,
                        CircleShape
                    )
            )
        }
    }
}


// ========================================
// 귀가 기록 카드
// ========================================

@Composable
private fun ReturnRecordCard(
    record: ReturnRecord
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(15.dp)
            )
            .background(Color.White)
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(
                horizontal = 17.dp,
                vertical = 14.dp
            )
    ) {

        RecordRow(
            title = "출발지",
            value = record.startLocation
        )

        RecordRow(
            title = "도착지",
            value = record.destination
        )

        RecordRow(
            title = "출발시간",
            value = record.startTime
        )

        RecordRow(
            title = "도착시간",
            value = record.arrivalTime
        )

        RecordRow(
            title = "소요시간",
            value = record.duration
        )

        RecordRow(
            title = "선택 경로",
            value = record.routeType
        )

        RecordRow(
            title = "이동거리",
            value = record.distance,
            showDivider = false
        )
    }
}


// ========================================
// 기록 한 줄
// ========================================

@Composable
private fun RecordRow(
    title: String,
    value: String,
    showDivider: Boolean = true
) {

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 11.dp
                ),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = title,
                fontSize = 15.sp,
                color = TextGray
            )


            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        }


        if (showDivider) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Color(0xFFF0F0F0)
                    )
            )
        }
    }
}


// ========================================
// Preview
// ========================================

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true
)
@Composable
fun ReportScreenPreview() {

    ReportScreen()
}