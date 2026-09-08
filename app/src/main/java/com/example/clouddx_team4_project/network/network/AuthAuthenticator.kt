package com.example.clouddx_team4_project.network

import com.example.clouddx_team4_project.data.TokenManager
import okhttp3.*
import org.json.JSONObject

class AuthAuthenticator(private val tokenManager: TokenManager) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. 괄호 추가: response.request()
        if (response.request().header("Authorization") == null) {
            return null
        }

        val refreshToken = tokenManager.getRefreshToken()

        if (refreshToken.isNullOrEmpty()) {
            tokenManager.clearToken()
            return null
        }

        val newToken = fetchNewToken(refreshToken)

        return if (newToken != null) {
            // 2. 괄호 추가: response.request()
            response.request().newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        } else {
            tokenManager.clearToken()
            null
        }
    }

    private fun fetchNewToken(refreshToken: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://127.0.0.1:8080/api/auth/refresh")
            // 3. OkHttp 3.x 버전에 맞춘 빈 바디 전송 코드
            .post(RequestBody.create(null, ByteArray(0)))
            .addHeader("Authorization", "Bearer $refreshToken")
            .build()

        return try {
            val res = client.newCall(request).execute()
            if (res.isSuccessful) {
                val bodyString = res.body()?.string()
                val jsonObject = JSONObject(bodyString)

                val newAccessToken = jsonObject.getString("accessToken")
                val newRefreshToken = jsonObject.getString("refreshToken")

                tokenManager.saveTokens(newAccessToken, newRefreshToken)
                newAccessToken
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}