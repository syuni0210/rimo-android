package com.example.clouddx_team4_project.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 기존 로그인 / 회원가입 서버
    private const val AUTH_BASE_URL = "http://127.0.0.1:8080/"

    // Ubuntu Spring Boot 서버 (회원/프로필 + 보호자 + 친구)
    private const val MEMBER_API_BASE_URL =
        "http://127.0.0.1:8081/"

    // Ubuntu Spring Boot 서버 (GPS/위치공유/긴급구조)
    private const val TRACKING_API_BASE_URL =
        "http://127.0.0.1:8082/"

    // Ubuntu Spring Boot 서버 (기본 목적지 / 경로)
    private const val ROUTE_API_BASE_URL =
        "http://127.0.0.1:8083/"

    // Ubuntu Spring Boot 서버 (사용 리포트 / 통계)
    private const val DATA_API_BASE_URL =
        "http://127.0.0.1:8084/"

    // 친구 API — member-api로 통합됨
    private const val FRIEND_API_BASE_URL =
        MEMBER_API_BASE_URL


    // ========================================
    // 로그인 / 회원가입 API
    // ========================================

    val authApi: AuthApi by lazy {

        Retrofit.Builder()
            .baseUrl(
                AUTH_BASE_URL
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(
                AuthApi::class.java
            )
    }

    val trackingApi: TrackingApi by lazy {
        Retrofit.Builder()
            .baseUrl(TRACKING_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackingApi::class.java)
    }


    // ========================================
    // 사용 리포트 API
    // ========================================

    val reportApi: ReportApi by lazy {

        Retrofit.Builder()
            .baseUrl(
                DATA_API_BASE_URL
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(
                ReportApi::class.java
            )
    }


    // ========================================
    // 회원 / 프로필 API
    // ========================================

    val memberApi: MemberApi by lazy {

        Retrofit.Builder()
            .baseUrl(
                MEMBER_API_BASE_URL
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(
                MemberApi::class.java
            )
    }


    // ========================================
    // 기본 목적지 API
    // ========================================

    val destinationApi: DestinationApi by lazy {

        Retrofit.Builder()
            .baseUrl(
                ROUTE_API_BASE_URL
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(
                DestinationApi::class.java
            )
    }


    // ========================================
    // 친구 API
    // ========================================

    val friendApi: FriendApi by lazy {

        Retrofit.Builder()
            .baseUrl(
                FRIEND_API_BASE_URL
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(
                FriendApi::class.java
            )
    }
}