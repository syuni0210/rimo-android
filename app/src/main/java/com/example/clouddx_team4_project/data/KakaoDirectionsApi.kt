package com.example.clouddx_team4_project.data

import com.example.clouddx_team4_project.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


// ========================================
// 카카오 도보 경로 API
// ========================================

interface KakaoDirectionsApi {

    @GET("v2/routing/walk")
    suspend fun getWalkingRoute(

        @Header("Authorization")
        authorization: String,

        // 출발지 경도
        @Query("start_x")
        startX: String,

        // 출발지 위도
        @Query("start_y")
        startY: String,

        // 도착지 경도
        @Query("end_x")
        endX: String,

        // 도착지 위도
        @Query("end_y")
        endY: String,

        // 출발지 이름
        @Query("s_name")
        startName: String = "현재 위치",

        // 도착지 이름
        @Query("e_name")
        endName: String = "목적지",

        // 입력 좌표계
        @Query("input_coord")
        inputCoord: String = "WGS84",

        // 출력 좌표계
        @Query("output_coord")
        outputCoord: String = "WGS84",

        // 도보 경로 옵션
        @Query("route_mode")
        routeMode: String = "BROAD_FIRST"

    ): WalkingRouteResponse
}


// ========================================
// Retrofit Client
// ========================================

object KakaoDirectionsClient {

    private const val BASE_URL =
        "https://dapi.kakao.com/"


    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()


    val api: KakaoDirectionsApi =
        retrofit.create(
            KakaoDirectionsApi::class.java
        )


    // REST API 인증 헤더
    val authorization: String
        get() =
            "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}"
}


// ========================================
// 도보 경로 전체 Response
// ========================================

data class WalkingRouteResponse(

    val route: WalkingRoute?,

    val status: String?
)


// ========================================
// Route
// ========================================

data class WalkingRoute(

    val properties: WalkingRouteProperties?,

    val legs: List<WalkingLeg>?
)


// ========================================
// 전체 경로 정보
// ========================================

data class WalkingRouteProperties(

    // 전체 거리 (m)
    val totalDistance: Int?,

    // 전체 소요 시간 (초)
    val totalTime: Int?,

    // 카카오맵 연결 URL
    val landingUrl: String?
)


// ========================================
// Leg
// ========================================

data class WalkingLeg(

    val properties: WalkingLegProperties?,

    val steps: List<WalkingStep>?
)


// ========================================
// Leg 정보
// ========================================

data class WalkingLegProperties(

    // 구간 거리 (m)
    val distance: Int?,

    // 구간 소요 시간 (초)
    val time: Int?
)


// ========================================
// Step
// ========================================

data class WalkingStep(

    val properties: WalkingStepProperties?,

    val path: WalkingPath?
)


// ========================================
// Step 정보
// ========================================

data class WalkingStepProperties(

    // 단계 거리 (m)
    val distance: Int?,

    // 길 안내 문구
    val guidance: String?,

    // 단계 소요 시간 (초)
    val time: Int?,

    // 단계 시작점 경도
    val x: Double?,

    // 단계 시작점 위도
    val y: Double?
)


// ========================================
// 실제 경로 좌표
// ========================================

data class WalkingPath(

    // [
    //   [경도, 위도],
    //   [경도, 위도],
    //   ...
    // ]
    val points: List<List<Double>>?
)