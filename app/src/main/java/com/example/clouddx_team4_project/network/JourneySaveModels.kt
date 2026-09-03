package com.example.clouddx_team4_project.network

data class JourneySaveRequest(
    val memberId: Long,
    val startAddress: String,
    val endAddress: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val pathTypeCode: String,
    val statusCode: String,
    val startDateTime: String,
    val endDateTime: String
)

data class JourneySaveResponse(
    val success: Boolean,
    val jrnyId: Long
)