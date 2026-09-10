package com.example.clouddx_team4_project.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    // 1. 백엔드의 @RequestMapping("/api/auth")에 맞게 중간의 'v1/'을 모두 제거
    @GET("api/auth/check-id")
    suspend fun checkId(
        @Query("userId") userId: String
    ): Response<CheckIdResponse>

    @POST("api/auth/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 2. 파싱 에러 방지를 위해 반환 타입을 Response<Unit>으로 변경
    // 서버가 주는 텍스트를 파싱하려다 크래시가 나지 않도록 바디 데이터는 무시하고 성공 여부(200 OK)만 확인
    @POST("api/auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<Unit>
}