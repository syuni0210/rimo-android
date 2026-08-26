package com.example.clouddx_team4_project

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.clouddx_team4_project.ui.navigation.AppNavigation
import com.kakao.sdk.common.util.Utility
import com.kakao.vectormap.MapView

class MainActivity : ComponentActivity() {

    companion object {
        var mapView: MapView? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val keyHash = Utility.getKeyHash(this)

        Log.e(
            "KAKAO_KEY_HASH",
            "KEY_HASH = $keyHash"
        )

        setContent {
            AppNavigation()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.resume()
    }

    override fun onPause() {
        mapView?.pause()
        super.onPause()
    }
}