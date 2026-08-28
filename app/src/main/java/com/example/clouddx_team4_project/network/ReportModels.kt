package com.example.clouddx_team4_project.network

data class SummaryDto(
    val totalCount: Long,
    val avgDurationMin: Double
)

data class RoutePreferenceDto(
    val code: String,
    val label: String,
    val count: Long,
    val percent: Double
)

data class ReportRecordDto(
    val date: String,
    val startLocation: String,
    val destination: String,
    val startTime: String,
    val arrivalTime: String,
    val duration: String,
    val routeType: String,
    val distance: String
)

data class TopFriendDto(
    val memberId: Long,
    val name: String,
    val count: Long
)