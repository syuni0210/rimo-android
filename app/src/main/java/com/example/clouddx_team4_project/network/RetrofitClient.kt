package com.example.clouddx_team4_project.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 기존 로그인/회원가입 서버
    private const val AUTH_BASE_URL =
        "https://dec046b05962b6.lhr.life/"

    // Ubuntu Spring Boot 서버
    private const val REPORT_BASE_URL =
        "http://127.0.0.1:8080/"

    // 로그인 / 회원가입 API
    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(AuthApi::class.java)
    }

    // 사용 리포트 API
    val reportApi: ReportApi by lazy {
        Retrofit.Builder()
            .baseUrl(REPORT_BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ReportApi::class.java)
    }
}