package com.example.clouddx_team4_project.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    // 아이디 중복 검사 (추가된 부분)
    @GET("api/v1/auth/check-id")
    suspend fun checkId(
        @Query("userId") userId: String
    ): Response<CheckIdResponse>

    @POST("api/v1/auth/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}