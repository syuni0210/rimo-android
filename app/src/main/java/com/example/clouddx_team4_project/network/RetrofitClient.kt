package com.example.clouddx_team4_project.network

import com.example.clouddx_team4_project.data.GuardianApi
import com.example.clouddx_team4_project.data.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Nginx 통합 서버
    private const val BASE_URL =
        "http://127.0.0.1:8080/"

    // Ubuntu Spring Boot 서버 (GPS/위치공유/긴급구조) - 게이트웨이 미포함 직접 연결
    private const val TRACKING_API_BASE_URL =
        "http://127.0.0.1:8082/"

    var tokenManager: TokenManager? = null

    // ========================================
    // JWT 토큰 자동 주입
    // ========================================
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = tokenManager?.getToken()
        if (!token.isNullOrEmpty()) {
            val newRequest = originalRequest
                .newBuilder()
                .header(
                    "Authorization",
                    "Bearer $token"
                )
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    // ========================================
    // 공용 Retrofit
    // ========================================
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    // ========================================
    // 로그인 / 회원가입
    // ========================================
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val trackingApi: TrackingApi by lazy {
        Retrofit.Builder()
            .baseUrl(TRACKING_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackingApi::class.java)
    }

    // ========================================
    // 리포트
    // ========================================
    val reportApi: ReportApi by lazy {
        retrofit.create(ReportApi::class.java)
    }

    // ========================================
    // 회원 / 프로필
    // ========================================
    val memberApi: MemberApi by lazy {
        retrofit.create(MemberApi::class.java)
    }

    // ========================================
    // 기본 목적지
    // ========================================
    val destinationApi: DestinationApi by lazy {
        retrofit.create(DestinationApi::class.java)
    }

    // ========================================
    // 친구
    // ========================================
    val friendApi: FriendApi by lazy {
        retrofit.create(FriendApi::class.java)
    }

    // ========================================
    // 보호자
    // ========================================
    val guardianApi: GuardianApi by lazy {
        retrofit.create(GuardianApi::class.java)
    }

    // ========================================
    // AI 안전경로
    // ========================================
    val aiSafeRouteApi: AiSafeRouteApi by lazy {
        retrofit.create(AiSafeRouteApi::class.java)
    }
}