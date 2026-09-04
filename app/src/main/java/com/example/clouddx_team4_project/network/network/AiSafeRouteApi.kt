package com.example.clouddx_team4_project.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AiSafeRouteApi {

    @POST("api/routes/ai-safe")
    suspend fun getAiSafeRoute(
        @Body request: AiSafeRouteRequest
    ): AiSafeRouteResponse


    // ========================================
    // 선택한 실제 경로 주변 50m 안전시설 조회
    //
    // AI 계산이 끝나기 전에
    // SHORTEST / BROAD_FIRST를 선택한 경우 사용
    // ========================================
    @POST("api/routes/facilities-near-path")
    suspend fun getFacilitiesNearPath(
        @Body request: RouteFacilitiesRequest
    ): List<FacilityMapDto>

    // ========================================
    // 귀가 진행 중 현재 위치 주변 50m 안전시설 조회
    //
    // 현재 GPS 좌표를 Backend로 전달하고,
    // 해당 위치에서 실제 반경 50m 이내 시설만 반환받습니다.
    // ========================================
    @GET("api/routes/facilities-near-location")
    suspend fun getFacilitiesNearLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): List<FacilityMapDto>
}


data class AiSafeRouteRequest(

    val startLatitude: Double,

    val startLongitude: Double,

    val destinationLatitude: Double,

    val destinationLongitude: Double,

    // Android에서 이미 계산한 빠른길
    val shortestCandidate: RouteCandidateRequest? = null,

    // Android에서 이미 계산한 대로변
    val broadCandidate: RouteCandidateRequest? = null
)


// ========================================
// Android에서 계산한 Kakao 후보 경로를
// Backend AI 안전경로 계산에 전달하기 위한 DTO
//
// Backend는 이 값이 있으면
// Kakao API를 다시 호출하지 않습니다.
// ========================================
data class RouteCandidateRequest(

    val routeMode: String,

    val distanceMeter: Int,

    val timeSecond: Int,

    val path: List<AiRoutePoint>
)

// ========================================
// 실제 선택 경로 시설 조회 요청
//
// path의 각 좌표를 Backend로 보내고,
// Backend가 경로 주변 50m 시설을 반환합니다.
// ========================================
data class RouteFacilitiesRequest(

    val path: List<AiRoutePoint>
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

    val facilities: List<FacilityMapDto> = emptyList(),

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

    val facilities: AiSafetyFacilities,

    val mapFacilities: List<FacilityMapDto> = emptyList()
)


data class AiSafetyFacilities(

    val cctvCount: Int,

    val emergencyBellCount: Int,

    val policeCount: Int,

    val safeHouseCount: Int,

    val securityLightCount: Int,

    val smartLightCount: Int
)