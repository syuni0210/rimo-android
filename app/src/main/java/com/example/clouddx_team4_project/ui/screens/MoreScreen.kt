package com.example.clouddx_team4_project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clouddx_team4_project.ui.components.AnOnBottomBar


// ========================================
// 색상
// ========================================

private val AnOnBlue = Color(0xFF6A92FE)

private val ScreenBackground = Color(0xFFF4F5F8)

private val CardBackground = Color.White

private val IconGray = Color(0xFFB8B8B8)

private val TextGray = Color(0xFF8B8B8B)


// ========================================
// 메뉴 데이터
// ========================================

data class MoreMenuItem(
    val title: String,
    val icon: ImageVector
)


// ========================================
// 더보기 화면
// ========================================

@Composable
fun MoreScreen(
    userName: String = "이지연",
    region: String = "영등포구",

    onMenuClick: (String) -> Unit = {},

    onSettingsClick: () -> Unit = {},

    onTabSelected: (String) -> Unit = {},

    onEmergencyClick: () -> Unit = {}
) {

    // ========================================
    // 첫 번째 그룹
    // ========================================

    val firstMenuGroup = listOf(

        MoreMenuItem(
            title = "기본 목적지 설정",
            icon = Icons.Filled.LocationOn
        ),

        MoreMenuItem(
            title = "보호자 등록",
            icon = Icons.Filled.Person
        )
    )


    // ========================================
    // 두 번째 그룹
    // ========================================

    val secondMenuGroup = listOf(

        MoreMenuItem(
            title = "공지사항 및 문의하기",
            icon = Icons.Filled.Notifications
        ),

        MoreMenuItem(
            title = "도움말",
            icon = Icons.Filled.Help
        ),

        MoreMenuItem(
            title = "서비스 소개",
            icon = Icons.Filled.Info
        ),

        MoreMenuItem(
            title = "개인정보처리방침",
            icon = Icons.Filled.Description
        )
    )


    // ========================================
    // 전체 화면
    // ========================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
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
            // 1. 프로필 영역
            // ========================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 24.dp,
                        bottom = 24.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ========================================
                // 프로필
                // ========================================

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = Color(0xFFE8EEFF),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "프로필",
                        tint = AnOnBlue,
                        modifier = Modifier.size(54.dp)
                    )
                }


                Spacer(
                    modifier = Modifier.width(18.dp)
                )


                // ========================================
                // 사용자 정보
                // ========================================

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "${userName}님",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )


                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )


                    Text(
                        text = region,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextGray
                    )
                }


                // ========================================
                // 설정
                // ========================================

                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "설정",
                    tint = Color(0xFF969696),
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            onSettingsClick()
                        }
                )
            }


            // ========================================
            // 2. 기본 설정 메뉴
            // ========================================

            MenuGroupCard(
                menuItems = firstMenuGroup,
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                onMenuClick = onMenuClick
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            // ========================================
            // 3. 안내 메뉴
            // ========================================

            MenuGroupCard(
                menuItems = secondMenuGroup,
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                onMenuClick = onMenuClick
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }


        // ========================================
        // 하단 네비게이션
        // ========================================

        AnOnBottomBar(

            selectedTab = "더보기",

            onTabSelected = onTabSelected,

            onEmergencyClick = onEmergencyClick,

            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }
}


// ========================================
// 메뉴 그룹 카드
// ========================================

@Composable
private fun MenuGroupCard(
    menuItems: List<MoreMenuItem>,
    modifier: Modifier = Modifier,
    onMenuClick: (String) -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(
                CardBackground
            )
            .padding(
                vertical = 10.dp
            )
    ) {

        menuItems.forEach { item ->

            MoreMenuRow(
                item = item,
                onClick = {

                    onMenuClick(
                        item.title
                    )
                }
            )
        }
    }
}


// ========================================
// 메뉴 한 줄
// ========================================

@Composable
private fun MoreMenuRow(
    item: MoreMenuItem,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 20.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ========================================
        // 아이콘
        // ========================================

        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = IconGray,
                modifier = Modifier.size(28.dp)
            )
        }


        Spacer(
            modifier = Modifier.width(16.dp)
        )


        // ========================================
        // 메뉴 이름
        // ========================================

        Text(
            text = item.title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF222222),
            modifier = Modifier.weight(1f)
        )


        // ========================================
        // 오른쪽 화살표
        // ========================================

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFC4C4C4),
            modifier = Modifier.size(23.dp)
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
fun MoreScreenPreview() {

    MoreScreen()
}