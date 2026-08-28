package com.example.clouddx_team4_project.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ResponsiveDimens(

    // ========================================
    // 화면 기본 여백
    // ========================================

    val screenHorizontalPadding: Dp,
    val screenVerticalPadding: Dp,


    // ========================================
    // 공통 간격
    // ========================================

    val smallSpacing: Dp,
    val mediumSpacing: Dp,
    val largeSpacing: Dp,


    // ========================================
    // 글씨 크기
    // ========================================

    val titleSize: TextUnit,
    val sectionTitleSize: TextUnit,
    val bodySize: TextUnit,
    val captionSize: TextUnit,


    // ========================================
    // 공통 아이콘 크기
    // ========================================

    val smallIconSize: Dp,
    val mediumIconSize: Dp,
    val largeIconSize: Dp,


    // ========================================
    // 카드
    // ========================================

    val cardPadding: Dp,
    val cardRadius: Dp,


    // ========================================
    // 사용 리포트 - 달력
    // ========================================

    val calendarCellHeight: Dp,
    val calendarHeaderHeight: Dp,
    val calendarSpacing: Dp,


    // ========================================
    // 홈 화면
    // ========================================

    val homeLogoSize: Dp,
    val homeNotificationSize: Dp,

    val homeProfileIconSize: Dp,
    val homeProfilePersonIconSize: Dp,

    val homeBannerHeight: Dp,

    val homeServiceCardHeight: Dp,
    val homeServiceIconSize: Dp
)


@Composable
fun rememberResponsiveDimens(): ResponsiveDimens {

    val configuration =
        LocalConfiguration.current

    val width =
        configuration.screenWidthDp

    val height =
        configuration.screenHeightDp


    // ========================================
    // 작은 화면 판별
    // ========================================

    val isSmallWidth =
        width < 360

    val isSmallHeight =
        height < 700


    return when {


        // ========================================
        // 작은 폰
        //
        // 목표:
        // 홈 화면이 최대한 한눈에 보이도록
        // 전체 높이를 많이 압축
        // ========================================

        isSmallWidth || isSmallHeight -> {

            ResponsiveDimens(

                screenHorizontalPadding = 12.dp,
                screenVerticalPadding = 6.dp,


                smallSpacing = 3.dp,
                mediumSpacing = 6.dp,
                largeSpacing = 10.dp,


                titleSize = 20.sp,
                sectionTitleSize = 16.sp,
                bodySize = 13.sp,
                captionSize = 11.sp,


                smallIconSize = 18.dp,
                mediumIconSize = 22.dp,
                largeIconSize = 30.dp,


                cardPadding = 10.dp,
                cardRadius = 14.dp,


                // ------------------------------
                // 사용 리포트 달력
                // ------------------------------

                calendarCellHeight = 34.dp,
                calendarHeaderHeight = 38.dp,
                calendarSpacing = 4.dp,


                // ------------------------------
                // 홈
                // ------------------------------

                homeLogoSize = 22.dp,

                homeNotificationSize = 24.dp,

                homeProfileIconSize = 44.dp,

                homeProfilePersonIconSize = 24.dp,

                homeBannerHeight = 120.dp,

                homeServiceCardHeight = 82.dp,

                homeServiceIconSize = 26.dp
            )
        }


        // ========================================
        // 일반 폰
        // ========================================

        width < 400 || height < 820 -> {

            ResponsiveDimens(

                screenHorizontalPadding = 16.dp,
                screenVerticalPadding = 10.dp,


                smallSpacing = 5.dp,
                mediumSpacing = 8.dp,
                largeSpacing = 14.dp,


                titleSize = 22.sp,
                sectionTitleSize = 17.sp,
                bodySize = 14.sp,
                captionSize = 12.sp,


                smallIconSize = 20.dp,
                mediumIconSize = 24.dp,
                largeIconSize = 34.dp,


                cardPadding = 13.dp,
                cardRadius = 15.dp,


                // ------------------------------
                // 사용 리포트 달력
                // ------------------------------

                calendarCellHeight = 40.dp,
                calendarHeaderHeight = 44.dp,
                calendarSpacing = 6.dp,


                // ------------------------------
                // 홈
                // ------------------------------

                homeLogoSize = 25.dp,

                homeNotificationSize = 27.dp,

                homeProfileIconSize = 50.dp,

                homeProfilePersonIconSize = 27.dp,

                homeBannerHeight = 145.dp,

                homeServiceCardHeight = 94.dp,

                homeServiceIconSize = 29.dp
            )
        }


        // ========================================
        // 큰 폰
        // ========================================

        else -> {

            ResponsiveDimens(

                screenHorizontalPadding = 20.dp,
                screenVerticalPadding = 16.dp,


                smallSpacing = 8.dp,
                mediumSpacing = 12.dp,
                largeSpacing = 20.dp,


                titleSize = 24.sp,
                sectionTitleSize = 18.sp,
                bodySize = 15.sp,
                captionSize = 13.sp,


                smallIconSize = 22.dp,
                mediumIconSize = 26.dp,
                largeIconSize = 38.dp,


                cardPadding = 16.dp,
                cardRadius = 16.dp,


                // ------------------------------
                // 사용 리포트 달력
                // ------------------------------

                calendarCellHeight = 46.dp,
                calendarHeaderHeight = 48.dp,
                calendarSpacing = 8.dp,


                // ------------------------------
                // 홈
                // ------------------------------

                homeLogoSize = 28.dp,

                homeNotificationSize = 30.dp,

                homeProfileIconSize = 58.dp,

                homeProfilePersonIconSize = 31.dp,

                homeBannerHeight = 190.dp,

                homeServiceCardHeight = 118.dp,

                homeServiceIconSize = 33.dp
            )
        }
    }
}