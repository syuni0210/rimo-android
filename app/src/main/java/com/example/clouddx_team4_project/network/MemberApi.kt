package com.example.clouddx_team4_project.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface MemberApi {

    // ========================================
    // 프로필 조회
    // GET /api/member/{memberId}/profile
    // ========================================

    @GET("api/member/{memberId}/profile")
    suspend fun getProfile(
        @Path("memberId") memberId: Long
    ): ProfileResponse


    // ========================================
    // 프로필 수정
    // PUT /api/member/{memberId}/profile
    // ========================================

    @PUT("api/member/{memberId}/profile")
    suspend fun updateProfile(
        @Path("memberId") memberId: Long,
        @Body request: ProfileUpdateRequest
    ): Response<Unit>
}