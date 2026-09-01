package com.example.clouddx_team4_project.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext
import com.example.clouddx_team4_project.data.TokenManager


class TokenManager(context: Context) {
    // rimo_prefs 라는 이름의 로컬 저장소 생성
    private val prefs: SharedPreferences = context.getSharedPreferences("rimo_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_TOKEN = "jwt_token"
    }

    // 토큰 저장 (로그인 성공 시 호출)
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_USER_TOKEN, token).apply()
    }

    // 사용자 ID 저장 (로그인 성공 직후 호출)
    fun saveMemberId(memberId: Long) {
        prefs.edit().putLong("MEMBER_ID", memberId).apply()
    }

    // 사용자 ID 불러오기 (리포트 화면 등에서 호출)
    fun getMemberId(): Long? {
        val id = prefs.getLong("MEMBER_ID", -1L)
        return if (id != -1L) id else null
    }

    // 토큰 불러오기 (API 요청 시 Header에 넣기 위해 사용)
    fun getToken(): String? {
        return prefs.getString(KEY_USER_TOKEN, null)
    }

    // 토큰 삭제 (로그아웃 시 호출)
    fun clearToken() {
        prefs.edit().remove(KEY_USER_TOKEN).apply()
    }

    // 자동 로그인을 위한 토큰 유무 확인
    fun hasValidToken(): Boolean {
        val token = getToken()
        return !token.isNullOrBlank()
    }
}