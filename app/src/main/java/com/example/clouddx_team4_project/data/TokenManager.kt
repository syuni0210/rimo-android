package com.example.clouddx_team4_project.data

import android.R
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext
import com.example.clouddx_team4_project.data.TokenManager

class TokenManager(context: Context) {
    // rimo_prefs 라는 이름의 로컬 저장소 생성
    private val prefs: SharedPreferences = context.getSharedPreferences("rimo_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "jwt_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_MEMBER_ID = "MEMBER_ID"
    }

    // 토큰 저장 (로그인 성공 시 호출)
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }
    // Refresh Token 저장
    fun saveRefreshToken(token: String) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }
    // 두 토큰을 한번에 저장하는 편의 함수
    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    // 사용자 ID 저장 (로그인 성공 직후 호출)
    fun saveMemberId(memberId: Long) {
        prefs.edit().putLong(KEY_MEMBER_ID, memberId).apply()
    }

    // 사용자 ID 불러오기 (리포트 화면 등에서 호출)
    fun getMemberId(): Long? {
        val id = prefs.getLong(KEY_MEMBER_ID, -1L)
        return if (id != -1L) id else null
    }

    // 토큰 불러오기 (API 요청 시 Header에 넣기 위해 사용)
    fun getToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    // 토큰 삭제 (로그아웃 시 호출)
    fun clearToken() {
        prefs.edit().remove(KEY_ACCESS_TOKEN).remove(KEY_REFRESH_TOKEN).remove(KEY_MEMBER_ID).apply()
    }

    // 자동 로그인을 위한 토큰 유무 확인
    fun hasValidToken(): Boolean {
        val accessToken = getToken()
        val refreshToken = getRefreshToken()
        return !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()
    }
}