package com.example.clouddx_team4_project.network

// 회원가입 요청 DTO[cite: 1]
data class SignupRequest(
    val userId: String,
    val email: String,
    val password: String,
    val name: String
)

// 로그인 요청 DTO[cite: 1]
data class LoginRequest(
    val userId: String,
    val password: String
)

// 로그인 응답 DTO[cite: 1]
data class LoginResponse(
    val message: String,
    val token: String,
    val memberId: Long,
    val name: String
)

// 아이디 중복 확인 응답 DTO[cite: 1]
data class CheckIdResponse(
    val available: Boolean,
    val message: String
)
//회원가입 완료 요청
data class SignupResponse(
    val message: String
)