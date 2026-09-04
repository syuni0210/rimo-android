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

    // ========================================
    // GPS 위치 갱신 (위치공유용)
    // 3초마다 호출
    // ========================================

    @POST("api/tracking/location")
    suspend fun updateLocation(
        @Body request: LocationUpdateRequest
    ): LocationUpdateResponse

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

    @GET("api/tracking/sharing-count")
    suspend fun getSharingCount(
        @Query("memberId") memberId: Long
    ): Int

    @GET("api/tracking/emergency/pending")
    suspend fun getPendingEmergencyPopup(
        @Query("memberId") memberId: Long
    ): Response<EmergencyPopupResponse>

    @POST("api/tracking/emergency/{emergencyId}/ack")
    suspend fun acknowledgeEmergencyPopup(
        @Path("emergencyId") emergencyId: Long,
        @Query("memberId") memberId: Long
    ): Response<Unit>
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

data class EmergencyPopupResponse(
    val hasEmergency: Boolean,
    val emergencyId: Long?,
    val senderId: Long?,
    val senderName: String?
)