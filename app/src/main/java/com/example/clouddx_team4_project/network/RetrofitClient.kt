package com.example.clouddx_team4_project.network


import com.example.clouddx_team4_project.data.GuardianApi
import com.example.clouddx_team4_project.data.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {


    // 💡 Nginx가 열려있는 우분투 서버의 IP와 8080 포트로 완전히 통일합니다.
    private const val BASE_URL = "http://127.0.0.1:8080/"

    var tokenManager: TokenManager? = null

    // 모든 API 요청에 JWT 토큰을 자동으로 주입하는 인터셉터
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = tokenManager?.getToken() // TokenManager의 실제 함수명에 맞게 수정

        if (!token.isNullOrEmpty()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    // 통합된 BASE_URL과 인터셉터가 적용된 단일 Retrofit 객체
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ========================================
    // API 객체 생성 (기존 변수명 유지)
    // ========================================

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val reportApi: ReportApi by lazy {
        retrofit.create(ReportApi::class.java)
    }

    val memberApi: MemberApi by lazy {
        retrofit.create(MemberApi::class.java)
    }

    val destinationApi: DestinationApi by lazy {
        retrofit.create(DestinationApi::class.java)
    }

    val friendApi: FriendApi by lazy {
        retrofit.create(FriendApi::class.java)
    }

    val guardianApi: GuardianApi by lazy {
        retrofit.create(GuardianApi::class.java)
    }
}