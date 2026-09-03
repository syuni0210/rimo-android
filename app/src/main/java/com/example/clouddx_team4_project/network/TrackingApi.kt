package com.example.clouddx_team4_project.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TrackingApi {

    @POST("api/tracking/emergency/trigger")
    suspend fun triggerEmergency(
        @Body request: EmergencyTriggerRequest
    ): EmergencyTriggerResponse

    // 친구 위치 조회 API
    @GET("api/tracking/friend/{friendId}")
    suspend fun getFriendLocation(
        @Path("friendId") friendId: Long,
        @Query("requesterId") requesterId: Long
    ): Response<FriendLocationResponse>

    @GET("api/tracking/sharing-friends")
    suspend fun getSharingFriendsLocations(
        @Query("requesterId") requesterId: Long
    ): Response<List<SharingFriendResponse>>
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

//응답 데이터 클래스
data class FriendLocationResponse(
    val success: Boolean,
    val lat: Double,
    val lng: Double,
    val message: String
)

data class SharingFriendResponse(
    val friendId: Long,
    val friendName: String,
    val lat: Double,
    val lng: Double
)