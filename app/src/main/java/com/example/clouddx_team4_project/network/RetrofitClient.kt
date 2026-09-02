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

// ========================================
// 💡 재발급용 DTO 및 API (백엔드 응답 형태에 맞게 수정 필요)
// ========================================
data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String
)

interface RefreshApi {
    // 백엔드의 토큰 재발급 엔드포인트 주소
    @POST("api/v1/auth/refresh")
    fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Call<TokenRefreshResponse> // 💡 비동기(suspend)가 아닌 동기(Call) 방식을 사용해야 합니다.
}


object RetrofitClient {

    private const val BASE_URL = "http://127.0.0.1:8080/"

    var tokenManager: TokenManager? = null

    // ========================================
    // 1. 기존: Access Token을 매 요청마다 달아주는 인터셉터
    // ========================================
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = tokenManager?.getToken() // Access Token

        if (!token.isNullOrEmpty()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }

    // ========================================
    // 2. 신규: 401 에러 시 자동으로 Refresh Token을 쏘는 Authenticator
    // ========================================
    private val tokenAuthenticator = object : Authenticator {

        // 💡 에러 해결 1: 무한 루프(401 에러 반복)를 세는 헬퍼 함수 직접 구현
        private fun getResponseCount(response: Response?): Int {
            var count = 1
            var priorResponse = response?.priorResponse() // OkHttp 3.x 맞춤 함수 호출
            while (priorResponse != null) {
                count++
                priorResponse = priorResponse.priorResponse()
            }
            return count
        }

        override fun authenticate(route: Route?, response: Response): Request? {
            // 무한 루프 방지: 재발급 시도 후 또 401이 떨어지면 로그아웃 처리
            if (getResponseCount(response) > 1) {
                tokenManager?.clearToken()
                return null
            }

            val refreshToken = tokenManager?.getRefreshToken()
            if (refreshToken.isNullOrEmpty()) {
                tokenManager?.clearToken()
                return null
            }

            try {
                // 순환 참조(무한루프) 방지를 위해 기존 OkHttpClient가 아닌 새 Retrofit 인스턴스를 씁니다.
                val refreshRetrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val refreshApi = refreshRetrofit.create(RefreshApi::class.java)

                // 백엔드로 동기 네트워크 요청 발송
                val refreshResponse = refreshApi.refreshToken("Bearer $refreshToken").execute()

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val newTokens = refreshResponse.body()!!

                    // 1. 발급받은 새 토큰 기기에 덮어쓰기
                    tokenManager?.saveTokens(newTokens.accessToken, newTokens.refreshToken)

                    Log.d("AUTH", "토큰 자동 재발급 성공")

                    // 💡 에러 해결 2: response.request -> response.request() 로 괄호 추가
                    return response.request().newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build()
                } else {
                    // Refresh Token도 만료되었거나 해커로 의심되어 블랙리스트 처리된 경우
                    Log.e("AUTH", "Refresh Token 만료. 강제 로그아웃 처리")
                    tokenManager?.clearToken()
                }
            } catch (e: Exception) {
                Log.e("AUTH", "토큰 재발급 통신 에러", e)
            }
            return null
        }
    }

    // ========================================
    // 3. OkHttp 클라이언트에 Interceptor와 Authenticator 동시 장착
    // ========================================
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .authenticator(tokenAuthenticator) // 💡 새로 만든 자동 재발급기 장착
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
    // API 객체 생성
    // ========================================
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val reportApi: ReportApi by lazy { retrofit.create(ReportApi::class.java) }
    val memberApi: MemberApi by lazy { retrofit.create(MemberApi::class.java) }
    val destinationApi: DestinationApi by lazy { retrofit.create(DestinationApi::class.java) }
    val friendApi: FriendApi by lazy { retrofit.create(FriendApi::class.java) }
    val guardianApi: GuardianApi by lazy { retrofit.create(GuardianApi::class.java) }
}