package com.example.clouddx_team4_project.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ReportApi {

    @GET("api/report/summary")
    suspend fun getSummary(
        @Query("memberId") memberId: Long
    ): SummaryDto

    @GET("api/report/route-preference")
    suspend fun getRoutePreference(
        @Query("memberId") memberId: Long
    ): List<RoutePreferenceDto>

    @GET("api/report/records")
    suspend fun getRecords(
        @Query("memberId") memberId: Long
    ): List<ReportRecordDto>

    @GET("api/report/top-friend")
    suspend fun getTopFriend(
        @Query("memberId") memberId: Long
    ): TopFriendDto

    // ========================================
// 안심지도 - CCTV 조회
// ========================================

    @GET("api/report/cctv")
    suspend fun getCctv(

        @Query("swLat")
        swLat: Double,

        @Query("swLng")
        swLng: Double,

        @Query("neLat")
        neLat: Double,

        @Query("neLng")
        neLng: Double

    ): List<FacilityMapDto>


// ========================================
// 안심지도 - 스마트 가로등 조회
// ========================================

    @GET("api/report/smart-light")
    suspend fun getSmartLight(

        @Query("swLat")
        swLat: Double,

        @Query("swLng")
        swLng: Double,

        @Query("neLat")
        neLat: Double,

        @Query("neLng")
        neLng: Double

    ): List<FacilityMapDto>


// ========================================
// 안심지도 - 지킴이집 조회
// ========================================

    @GET("api/report/safe-house")
    suspend fun getSafeHouse(

        @Query("swLat")
        swLat: Double,

        @Query("swLng")
        swLng: Double,

        @Query("neLat")
        neLat: Double,

        @Query("neLng")
        neLng: Double

    ): List<FacilityMapDto>


// ========================================
// 안심지도 - 지구대 / 파출소 조회
// ========================================

    @GET("api/report/police")
    suspend fun getPolice(

        @Query("swLat")
        swLat: Double,

        @Query("swLng")
        swLng: Double,

        @Query("neLat")
        neLat: Double,

        @Query("neLng")
        neLng: Double

    ): List<FacilityMapDto>


// ========================================
// 안심지도 - 비상벨 조회
// ========================================

    @GET("api/report/emergency-bell")
    suspend fun getEmergencyBell(

        @Query("swLat")
        swLat: Double,

        @Query("swLng")
        swLng: Double,

        @Query("neLat")
        neLat: Double,

        @Query("neLng")
        neLng: Double

    ): List<FacilityMapDto>


// ========================================
// 안심지도 - 보안등 조회
// ========================================

    @GET("api/report/security-light")
    suspend fun getSecurityLight(

        @Query("swLat")
        swLat: Double,

        @Query("swLng")
        swLng: Double,

        @Query("neLat")
        neLat: Double,

        @Query("neLng")
        neLng: Double

    ): List<FacilityMapDto>

    // ========================================
    // 귀가 여정 저장
    // ========================================

    @POST("api/report/journeys")
    suspend fun saveJourney(
        @Body request: JourneySaveRequest
    ): JourneySaveResponse
}