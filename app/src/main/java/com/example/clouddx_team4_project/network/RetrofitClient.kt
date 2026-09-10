package com.example.clouddx_team4_project.network

import android.util.Log
import com.example.clouddx_team4_project.data.GuardianApi
import com.example.clouddx_team4_project.data.TokenManager
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface RefreshApi {
    @POST("api/auth/refresh")
    fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Call<LoginResponse>
}

object RetrofitClient {

    private const val BASE_URL = "https://api.rimo-app.com/"

    var tokenManager: TokenManager? = null

    // ========================================
    // JWT 토큰 자동 주입 인터셉터
    // ========================================
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val path = originalRequest.url().encodedPath()

        // 로그인, 회원가입 등은 토큰 부착 없이 그대로 통과
        if (path.contains("/auth/login") || path.contains("/auth/signup") || path.contains("/auth/check-id")) {
            return@Interceptor chain.proceed(originalRequest)
        }

        // ⭐️ 수정됨: TokenManager의 getToken() 메서드 사용
        val token = tokenManager?.getToken()

        if (!token.isNullOrEmpty()) {
            val newRequest = originalRequest
                .newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }

    // ========================================
    // 401 에러 시 Refresh Token으로 자동 재발급하는 Authenticator
    // ========================================
    private val tokenAuthenticator = object : Authenticator {
        private fun getResponseCount(response: Response?): Int {
            var count = 1
            var priorResponse = response?.priorResponse()
            while (priorResponse != null) {
                count++
                priorResponse = priorResponse.priorResponse()
            }
            return count
        }

        override fun authenticate(route: Route?, response: Response): Request? {
            if (getResponseCount(response) > 1) {
                // ⭐️ 수정됨: clearToken() 사용
                tokenManager?.clearToken()
                return null
            }

            val refreshToken = tokenManager?.getRefreshToken()
            if (refreshToken.isNullOrEmpty()) {
                // ⭐️ 수정됨: clearToken() 사용
                tokenManager?.clearToken()
                return null
            }

            try {
                val refreshRetrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val refreshApi = refreshRetrofit.create(RefreshApi::class.java)
                val refreshResponse = refreshApi.refreshToken("Bearer $refreshToken").execute()

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val newTokens = refreshResponse.body()!!

                    tokenManager?.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                    Log.d("AUTH", "토큰 자동 재발급 성공")

                    return response.request().newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build()
                } else {
                    Log.e("AUTH", "Refresh Token 만료. 강제 로그아웃 처리")
                    // ⭐️ 수정됨: clearToken() 사용
                    tokenManager?.clearToken()
                }
            } catch (e: Exception) {
                Log.e("AUTH", "토큰 재발급 통신 에러", e)
            }
            return null
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .authenticator(tokenAuthenticator)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val trackingApi: TrackingApi by lazy { retrofit.create(TrackingApi::class.java) }
    val reportApi: ReportApi by lazy { retrofit.create(ReportApi::class.java) }
    val memberApi: MemberApi by lazy { retrofit.create(MemberApi::class.java) }
    val destinationApi: DestinationApi by lazy { retrofit.create(DestinationApi::class.java) }
    val friendApi: FriendApi by lazy { retrofit.create(FriendApi::class.java) }
    val guardianApi: GuardianApi by lazy { retrofit.create(GuardianApi::class.java) }
    val aiSafeRouteApi: AiSafeRouteApi by lazy { retrofit.create(AiSafeRouteApi::class.java) }
}