package com.example.clouddx_team4_project.network

import com.example.clouddx_team4_project.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 1. 로그인, 회원가입 등 토큰이 아예 필요 없는 경로는 헤더 없이 그냥 통과
        val path = originalRequest.url().encodedPath()
        if (path.contains("/auth/login") || path.contains("/auth/signup") || path.contains("/auth/check-id")) {
            return chain.proceed(originalRequest)
        }

        // 2. 저장된 Access Token 꺼내기
        val accessToken = tokenManager.getToken() ?: ""
        val requestBuilder = originalRequest.newBuilder()

        // 3. 토큰이 있다면 Authorization 헤더에 부착
        if (accessToken.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}