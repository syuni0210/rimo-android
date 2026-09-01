package com.example.clouddx_team4_project.network

import retrofit2.http.Body
import retrofit2.http.POST


interface AiSafeRouteApi {

    @POST("api/routes/ai-safe")
    suspend fun getAiSafeRoute(
        @Body request: AiSafeRouteRequest
    ): AiSafeRouteResponse
}


data class AiSafeRouteRequest(

    val startLatitude: Double,

    val startLongitude: Double,

    val destinationLatitude: Double,

    val destinationLongitude: Double
)


data class AiSafeRouteResponse(

    val routeMode: String,

    val distanceMeter: Int,

    val timeSecond: Int,

    val safetyScore: Double,

    val cctvCount: Int,

    val emergencyBellCount: Int,

    val policeCount: Int,

    val safeHouseCount: Int,

    val securityLightCount: Int,

    val smartLightCount: Int,

    val recommendationReason: String,

    val path: List<AiRoutePoint>,

    val candidates: List<AiRouteCandidate>
)


data class AiRoutePoint(

    val latitude: Double,

    val longitude: Double
)


data class AiRouteCandidate(

    val routeMode: String,

    val distanceMeter: Int,

    val timeSecond: Int,

    val path: List<AiRoutePoint>,

    val safetyScore: Double,

    val facilities: AiSafetyFacilities
)


data class AiSafetyFacilities(

    val cctvCount: Int,

    val emergencyBellCount: Int,

    val policeCount: Int,

    val safeHouseCount: Int,

    val securityLightCount: Int,

    val smartLightCount: Int
)