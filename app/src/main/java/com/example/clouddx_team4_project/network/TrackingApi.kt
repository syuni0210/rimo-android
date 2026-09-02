package com.example.clouddx_team4_project.network

import retrofit2.http.Body
import retrofit2.http.POST

interface TrackingApi {

    @POST("api/tracking/emergency/trigger")
    suspend fun triggerEmergency(
        @Body request: EmergencyTriggerRequest
    ): EmergencyTriggerResponse
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