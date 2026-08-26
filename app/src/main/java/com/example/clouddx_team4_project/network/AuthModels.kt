package com.example.clouddx_team4_project.network

data class SignupRequest(
    val email: String,
    val password: String,
    val name: String
)

data class SignupResponse(
    val message: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String
)