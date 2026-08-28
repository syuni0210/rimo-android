package com.example.clouddx_team4_project.network

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendApi {

    @GET("api/users/search/id")
    suspend fun searchUserById(
        @Query("loginId") loginId: String
    ): Response<UserResponse>

    @GET("api/users/search/name")
    suspend fun searchUserByName(
        @Query("name") name: String
    ): Response<List<UserResponse>>

    @POST("api/friends/request")
    suspend fun sendFriendRequest(
        @Query("requesterId") requesterId: Long,
        @Query("receiverId") receiverId: Long
    ): Response<FriendResponse>

    @GET("api/friends/pending/sent")
    suspend fun getSentPendingRequests(
        @Query("memberId") memberId: Long
    ): Response<List<FriendResponse>>

    @GET("api/friends/pending/received")
    suspend fun getReceivedPendingRequests(
        @Query("memberId") memberId: Long
    ): Response<List<FriendResponse>>

    @PATCH("api/friends/{friendId}/accept")
    suspend fun acceptFriendRequest(
        @Path("friendId") friendId: Long,
        @Query("memberId") memberId: Long
    ): Response<FriendResponse>

    @PATCH("api/friends/{friendId}/reject")
    suspend fun rejectFriendRequest(
        @Path("friendId") friendId: Long,
        @Query("memberId") memberId: Long
    ): Response<FriendResponse>

    @PATCH("api/friends/{friendId}/cancel")
    suspend fun cancelFriendRequest(
        @Path("friendId") friendId: Long,
        @Query("memberId") memberId: Long
    ): Response<FriendResponse>

    @GET("api/friends")
    suspend fun getFriendList(
        @Query("memberId") memberId: Long
    ): Response<List<UserResponse>>

    @DELETE("api/friends")
    suspend fun deleteFriend(
        @Query("memberId") memberId: Long,
        @Query("friendMemberId") friendMemberId: Long
    ): Response<FriendResponse>
}