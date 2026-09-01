package com.example.clouddx_team4_project.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


// ========================================
// 보호자 API
// ========================================

interface GuardianApi {


    @POST("api/guardians")
    suspend fun registerGuardian(
        @Body request: GuardianRequest
    ): GuardianResponse

    @GET("api/guardians")
    suspend fun getGuardians(
        @Query("memberId") memberId: Long
    ): List<GuardianResponse>

    @DELETE("api/guardians/{guardianId}")
    suspend fun deleteGuardian(
        @Path("guardianId") guardianId: Long
    )
}

// 요청 / 응답 모델
//
// 백엔드 JSON 키(guardianId, guardianName 등)와
// 이름이 정확히 같아서 @SerializedName 없이도
// 자동으로 매핑됩니다. (카카오 API는 snake_case라
// @SerializedName이 필요했던 것과 다른 부분이에요.)
// ========================================

data class GuardianRequest(
    val memberId: Long,
    val guardianName: String,
    val phoneNumber: String,
    val relationName: String
)

data class GuardianResponse(
    val guardianId: Long,
    val memberId: Long,
    val guardianName: String,
    val phoneNumber: String,
    val relationName: String,
    val primaryYn: String,
    val registeredAt: String,
    val useYn: String,
    val deleteYn: String
)