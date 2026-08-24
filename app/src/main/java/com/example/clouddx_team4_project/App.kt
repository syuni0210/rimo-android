package com.example.clouddx_team4_project

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.util.Utility
import com.kakao.vectormap.KakaoMapSdk

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // 현재 앱의 패키지명 확인
        Log.d(
            "KAKAO_CHECK",
            "packageName = $packageName"
        )

        // 현재 앱의 Key Hash 확인
        Log.d(
            "KAKAO_CHECK",
            "keyHash = ${Utility.getKeyHash(this)}"
        )

        // 카카오맵 SDK 초기화
        KakaoMapSdk.init(
            this,
            BuildConfig.KAKAO_NATIVE_APP_KEY
        )
    }
}