package com.example.clouddx_team4_project.network

import retrofit2.http.Body
import retrofit2.http.POST

interface TrackingApi {

    @POST("api/tracking/emergency/trigger")
    suspend fun triggerEmergency(
        @Body request: EmergencyTriggerRequest
    ): EmergencyTriggerResponse

    // ========================================
    // GPS 위치 갱신 (위치공유용)
    // 3초마다 호출
    // ========================================

    @POST("api/tracking/location")
    suspend fun updateLocation(
        @Body request: LocationUpdateRequest
    ): LocationUpdateResponse
}

data class EmergencyTriggerRequest(
    val memberId: Long,
    val lat: Double,
    val lng: Double
)

data class EmergencyTriggerResponse(
    val success: Boolean,
    val notifiedGuardianCount: Int
)

data class LocationUpdateRequest(
    val memberId: Long,
    val lat: Double,
    val lng: Double
)

data class LocationUpdateResponse(
    val success: Boolean
)