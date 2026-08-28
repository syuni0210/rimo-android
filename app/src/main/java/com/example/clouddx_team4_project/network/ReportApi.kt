package com.example.clouddx_team4_project.network

import retrofit2.http.GET
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
}