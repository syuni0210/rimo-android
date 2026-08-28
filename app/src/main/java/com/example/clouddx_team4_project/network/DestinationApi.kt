package com.example.clouddx_team4_project.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DestinationApi {

    @GET("api/member/{memberId}/destinations")
    suspend fun getDestinations(
        @Path("memberId") memberId: Long
    ): List<DestinationResponse>

    @POST("api/member/{memberId}/destinations")
    suspend fun createDestination(
        @Path("memberId") memberId: Long,
        @Body request: DestinationCreateRequest
    ): Response<Unit>

    @DELETE("api/member/{memberId}/destinations/{destinationId}")
    suspend fun deleteDestination(
        @Path("memberId") memberId: Long,
        @Path("destinationId") destinationId: Long
    ): Response<Unit>
}