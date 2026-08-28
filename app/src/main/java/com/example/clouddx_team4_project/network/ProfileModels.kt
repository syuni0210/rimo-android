package com.example.clouddx_team4_project.network

data class ProfileResponse(
    val memberId: Long,
    val loginId: String,
    val memberName: String,
    val email: String
)

data class ProfileUpdateRequest(
    val memberName: String,
    val email: String
)