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
import com.example.clouddx_team4_project.ui.viewmodel.ReportViewModel
import com.example.clouddx_team4_project.ui.theme.rememberResponsiveDimens
import androidx.lifecycle.viewmodel.compose.viewModel
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
    onEmergencyClick: () -> Unit = {},
    viewModel: ReportViewModel = viewModel()
) {

    val dimens = rememberResponsiveDimens()

    var selectedReportTab by remember {
        mutableStateOf("대시보드")
    }

    // ========================================
    // Spring Boot API 데이터
    // ========================================

    val summary by viewModel.summary.collectAsState()
    val apiRoutePreferences by viewModel.routePreferences.collectAsState()
    val apiRecords by viewModel.records.collectAsState()
    val topFriend by viewModel.topFriend.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadReport()
    }

    // 서버의 경로 선호도 DTO -> 화면용 모델
    val routePreferences = apiRoutePreferences.mapIndexed { index, item ->

        val color = when (item.code) {
            "R001" -> DeepBlue
            "R002" -> MiddleBlue
            "R003" -> LightBlue
            else -> when (index % 3) {
                0 -> DeepBlue
                1 -> MiddleBlue
                else -> LightBlue
            }
        }

        RoutePreference(
            label = item.label,
            percent = item.percent.toInt(),
            color = color
        )
    }

    // 서버의 귀가 기록 DTO -> 화면용 모델
    val returnRecords = apiRecords.mapNotNull { item ->

        val parsedDate = runCatching {
            LocalDate.parse(item.date)
        }.getOrNull()

        if (parsedDate == null) {
            null
        } else {
            ReturnRecord(
                date = parsedDate,
                startLocation = item.startLocation,
                destination = item.destination,
                startTime = item.startTime,
                arrivalTime = item.arrivalTime,
                duration = item.duration,
                routeType = item.routeType,
                distance = item.distance
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 92.dp)
        ) {

            // ========================================
            // 화면 제목
            // ========================================

            Text(
                text = "사용 리포트",
                fontSize = dimens.titleSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(
                        top = dimens.screenVerticalPadding,
                        bottom = dimens.screenVerticalPadding
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
                modifier = Modifier.height(dimens.mediumSpacing)
            )


            // ========================================
            // 탭에 따라 화면 변경
            // ========================================

            if (selectedReportTab == "대시보드") {

                DashboardContent(
                    totalCount = summary?.totalCount ?: 0L,
                    avgDurationMin = summary?.avgDurationMin ?: 0.0,
                    routePreferences = routePreferences,
                    topFriendName = topFriend?.name ?: "-",
                    topFriendCount = topFriend?.count ?: 0L
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

    val dimens = rememberResponsiveDimens()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.screenHorizontalPadding)
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

    val dimens = rememberResponsiveDimens()

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
            fontSize = dimens.bodySize,
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
    totalCount: Long,
    avgDurationMin: Double,
    routePreferences: List<RoutePreference>,
    topFriendName: String,
    topFriendCount: Long
) {

    val dimens = rememberResponsiveDimens()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(horizontal = dimens.screenHorizontalPadding)
            .padding(bottom = dimens.largeSpacing)
    ) {

        // ========================================
        // 이번 주 요약
        // ========================================

        SectionTitle(
            title = "귀가 요약"
        )


        Spacer(
            modifier = Modifier.height(dimens.mediumSpacing)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(dimens.mediumSpacing)
        ) {

            SummaryCard(
                title = "총 귀가 횟수",
                value = totalCount.toString(),
                unit = "회",
                iconType = "home",
                modifier = Modifier.weight(1f)
            )


            SummaryCard(
                title = "평균 소요시간",
                value = avgDurationMin.toInt().toString(),
                unit = "분",
                iconType = "time",
                modifier = Modifier.weight(1f)
            )
        }


        Spacer(
            modifier = Modifier.height(dimens.largeSpacing)
        )


        // ========================================
        // 경로 선호도
        // ========================================

        SectionTitle(
            title = "경로 선호도"
        )


        Spacer(
            modifier = Modifier.height(dimens.mediumSpacing)
        )


        RoutePreferenceSection(
            preferences = routePreferences
        )


        Spacer(
            modifier = Modifier.height(dimens.largeSpacing)
        )


        // ========================================
        // 친구
        // ========================================

        SectionTitle(
            title = "앱을 가장 많이 사용한 친구"
        )


        Spacer(
            modifier = Modifier.height(dimens.mediumSpacing)
        )


        FriendCard(
            name = topFriendName,
            count = topFriendCount
        )


        Spacer(
            modifier = Modifier.height(dimens.largeSpacing)
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

    val dimens = rememberResponsiveDimens()

    Text(
        text = title,
        fontSize = dimens.sectionTitleSize,
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

    val dimens = rememberResponsiveDimens()

    Column(
        modifier = modifier
            .heightIn(min = 108.dp)
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
                horizontal = dimens.cardPadding,
                vertical = dimens.cardPadding
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
                modifier = Modifier.size(dimens.smallIconSize)
            )


            Spacer(
                modifier = Modifier.width(4.dp)
            )


            Text(
                text = title,
                fontSize = dimens.captionSize,
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
                fontSize = dimens.bodySize,
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

    val dimens = rememberResponsiveDimens()

    val chartOuterSize = if (dimens.screenHorizontalPadding <= 12.dp) 128.dp else 152.dp
    val chartSize = if (dimens.screenHorizontalPadding <= 12.dp) 118.dp else 140.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ========================================
        // 도넛 차트
        // ========================================

        Box(
            modifier = Modifier.size(chartOuterSize),
            contentAlignment = Alignment.Center
        ) {

            Canvas(
                modifier = Modifier.size(chartSize)
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
                    modifier = Modifier.size(dimens.mediumIconSize)
                )
            }
        }


        Spacer(
            modifier = Modifier.width(dimens.mediumSpacing)
        )


        // ========================================
        // 범례
        // ========================================

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(dimens.mediumSpacing)
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
                        fontSize = dimens.bodySize,
                        color = Color(0xFF444444)
                    )


                    Text(
                        text = "${item.percent}%",
                        fontSize = dimens.sectionTitleSize,
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
private fun FriendCard(
    name: String,
    count: Long
) {

    val dimens = rememberResponsiveDimens()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .clip(
                RoundedCornerShape(15.dp)
            )
            .background(CardBackground)
            .border(
                width = 1.dp,
                color = BorderColor,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(
                horizontal = dimens.cardPadding
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // 친구 아이콘
        Box(
            modifier = Modifier
                .size(if (dimens.screenHorizontalPadding <= 12.dp) 52.dp else 60.dp)
                .background(
                    Color(0xFFE9F0FF),
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
                    AnOnBlue,

                modifier =
                    Modifier.size(dimens.largeIconSize)
            )
        }


        Spacer(
            modifier =
                Modifier.width(dimens.mediumSpacing)
        )


        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            // ========================================
            // 친구 이름
            // ========================================

            Text(
                text = name,

                fontSize =
                    dimens.sectionTitleSize,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color(0xFF222222)
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            Text(
                text =
                    "앱 사용 횟수",

                fontSize =
                    dimens.bodySize,

                color =
                    TextGray
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            // ========================================
            // 이번 주 사용 횟수
            // ========================================

            Text(
                text =
                    "이번 주 ${count}회",

                color =
                    AnOnBlue,

                fontSize =
                    dimens.bodySize,

                fontWeight =
                    FontWeight.Bold
            )
        }


        Icon(
            imageVector =
                Icons.Filled.ChevronRight,

            contentDescription =
                null,

            tint =
                Color.Gray,

            modifier =
                Modifier.size(dimens.mediumIconSize)
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

    val dimens = rememberResponsiveDimens()

    var currentMonth by remember {
        mutableStateOf(YearMonth.now())
    }

    var selectedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var initializedFromRecords by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(returnRecords) {
        if (!initializedFromRecords && returnRecords.isNotEmpty()) {
            val latestDate = returnRecords.maxByOrNull { it.date }!!.date

            currentMonth = YearMonth.from(latestDate)
            selectedDate = latestDate
            initializedFromRecords = true
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimens.screenHorizontalPadding)
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
            modifier = Modifier.height(dimens.mediumSpacing)
        )


        // ========================================
        // 2. 귀가 기록 제목
        // 이것도 고정
        // ========================================

        Text(
            text =
                "${selectedDate.year}.${selectedDate.monthValue}.${selectedDate.dayOfMonth} 귀가 기록",
            fontSize = dimens.sectionTitleSize,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )


        Spacer(
            modifier = Modifier.height(dimens.mediumSpacing)
        )


        val selectedRecords =
            returnRecords.filter {
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
                .padding(bottom = dimens.largeSpacing)
        ) {

            if (selectedRecords.isNotEmpty()) {

                selectedRecords.forEachIndexed { index, record ->

                    ReturnRecordCard(
                        record = record
                    )

                    if (index != selectedRecords.lastIndex) {
                        Spacer(
                            modifier = Modifier.height(dimens.mediumSpacing)
                        )
                    }
                }

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
                modifier = Modifier.height(dimens.largeSpacing)
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

    val dimens = rememberResponsiveDimens()

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
                horizontal = dimens.cardPadding,
                vertical = dimens.cardPadding
            )
    ) {

        // ========================================
        // 월 이동
        // ========================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.calendarHeaderHeight),
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
                    .size(dimens.mediumIconSize)
                    .clickable {
                        onPreviousMonth()
                    }
            )


            Text(
                text =
                    "${currentMonth.year}년 ${currentMonth.monthValue}월",
                fontSize = dimens.sectionTitleSize,
                fontWeight = FontWeight.Bold
            )


            Icon(
                imageVector =
                    Icons.Filled.ChevronRight,
                contentDescription = "다음 달",
                tint = Color.Gray,
                modifier = Modifier
                    .size(dimens.mediumIconSize)
                    .clickable {
                        onNextMonth()
                    }
            )
        }


        Spacer(
            modifier = Modifier.height(dimens.calendarSpacing)
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
                    fontSize = dimens.bodySize,
                    fontWeight = FontWeight.Medium,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            }
        }


        Spacer(
            modifier = Modifier.height(dimens.calendarSpacing)
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
                    .height(dimens.calendarCellHeight)
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

    val dimens = rememberResponsiveDimens()
    val dayCircleSize = dimens.calendarCellHeight - 8.dp

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.calendarCellHeight)
            .clickable {
                onClick()
            }
    ) {

        Box(
            modifier = Modifier
                .size(dayCircleSize)
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
                fontSize = dimens.bodySize,
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
                modifier = Modifier.height(1.dp)
            )


            Box(
                modifier = Modifier
                    .size(if (dimens.calendarCellHeight <= 34.dp) 4.dp else 5.dp)
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

    val dimens = rememberResponsiveDimens()

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
                horizontal = dimens.cardPadding,
                vertical = dimens.cardPadding
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

    val dimens = rememberResponsiveDimens()

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = dimens.mediumSpacing
                ),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = title,
                fontSize = dimens.bodySize,
                color = TextGray
            )


            Text(
                text = value,
                fontSize = dimens.bodySize,
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