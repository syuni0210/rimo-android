package com.example.clouddx_team4_project.network

data class DestinationResponse(
    val destinationId: Long,
    val memberId: Long,
    val name: String,
    val placeName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

data class DestinationCreateRequest(
    val name: String,
    val placeName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)