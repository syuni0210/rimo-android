package com.example.clouddx_team4_project.network

<<<<<<< HEAD
=======
import com.example.clouddx_team4_project.data.GuardianApi
import com.example.clouddx_team4_project.data.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
>>>>>>> ldk
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

<<<<<<< HEAD
    // 기존 로그인 / 회원가입 서버
    private const val AUTH_BASE_URL = "http://127.0.0.1:8080/"

    // Ubuntu Spring Boot 서버 (회원/프로필 + 보호자 + 친구)
    private const val MEMBER_API_BASE_URL =
        "http://127.0.0.1:8081/"

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
=======
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
>>>>>>> ldk
    }
}