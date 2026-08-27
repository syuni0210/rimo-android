package com.example.clouddx_team4_project.network

data class UserResponse(
    val mmbrId: Long,
    val loginId: String,
    val memberName: String,
    val profileColor: String?,
    val memberStatusCode: String?,
    val useYn: String?,
    val deleteYn: String?
)

data class FriendResponse(
    val friendId: Long,
    val requestMemberId: Long,
    val receiveMemberId: Long,
    val statusCode: String,
    val requestDate: String?,
    val acceptDate: String?,
    val deleteYn: String?,
    val requesterName: String? = null,
    val requesterLoginId: String? = null,
    val receiverName: String? = null,
    val receiverLoginId: String? = null
)