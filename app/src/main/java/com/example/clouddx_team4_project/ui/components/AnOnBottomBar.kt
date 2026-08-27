package com.example.clouddx_team4_project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AnOnBlue = Color(0xFF6A92FE)
private val EmergencyRed = Color(0xFFE23F3F)

@Composable
fun AnOnBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit = {},
    onEmergencyClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp)
    ) {

        // =========================
        // 하단 네비게이션 본체
        // =========================
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {

            // 상단 구분선
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFEAEAEA))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .height(72.dp)
                    .padding(horizontal = 26.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // 홈
                Box(
                    modifier = Modifier.width(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BottomBarItem(
                        label = "홈",
                        icon = Icons.Filled.Home,
                        selected = selectedTab == "홈"
                    ) {
                        onTabSelected("홈")
                    }
                }

                // 가운데 긴급구조 버튼 공간
                Spacer(modifier = Modifier.width(80.dp))

                // 더보기
                Box(
                    modifier = Modifier.width(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BottomBarItem(
                        label = "더보기",
                        icon = Icons.Filled.MoreHoriz,
                        selected = selectedTab == "더보기"
                    ) {
                        onTabSelected("더보기")
                    }
                }
            }
        }

        // =========================
        // 가운데 긴급구조 버튼
        // =========================
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-8).dp)
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(82.dp)
            ) {

                // 바깥쪽 은은한 빨간 링
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .background(
                            EmergencyRed.copy(alpha = 0.12f),
                            CircleShape
                        )
                )

                // 실제 긴급구조 버튼
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape
                        )
                        .background(
                            EmergencyRed,
                            CircleShape
                        )
                        .clickable {
                            onEmergencyClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "긴급구조",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }

            Text(
                text = "긴급구조",
                fontSize = 13.sp,
                color = EmergencyRed,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@Composable
private fun BottomBarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val itemColor =
        if (selected) AnOnBlue
        else Color(0xFF7D7D7D)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            )
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = itemColor,
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            color = itemColor,
            fontWeight =
                if (selected)
                    FontWeight.SemiBold
                else
                    FontWeight.Medium
        )
    }
}