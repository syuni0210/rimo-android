package com.example.clouddx_team4_project.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 기존 로그인/회원가입 서버
    private const val AUTH_BASE_URL =
        "https://dec046b05962b6.lhr.life/"

    // Ubuntu Spring Boot 서버
    // 사용 리포트 + 프로필 + 기본 목적지 API 공용
    private const val RIMO_API_BASE_URL =
      //  "http://127.0.0.1:8080/"
        "http://15.165.159.41:8080/"


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


    // ========================================
    // 사용 리포트 API
    // ========================================

    val reportApi: ReportApi by lazy {

        Retrofit.Builder()
            .baseUrl(
                RIMO_API_BASE_URL
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
                RIMO_API_BASE_URL
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
                RIMO_API_BASE_URL
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(
                DestinationApi::class.java
            )
    }
}