package com.example.clouddx_team4_project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.clouddx_team4_project.ui.navigation.AppNavigation
import com.kakao.sdk.common.util.Utility
import com.kakao.vectormap.MapView


class MainActivity : ComponentActivity() {

    companion object {

        var mapView: MapView? = null
    }


    // ========================================
    // 위치 권한 요청 Launcher
    // ========================================

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true


            val coarseLocationGranted =
                permissions[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] == true


            if (
                fineLocationGranted ||
                coarseLocationGranted
            ) {

                Log.d(
                    "LOCATION_PERMISSION",
                    "위치 권한 허용됨"
                )

            } else {

                Log.e(
                    "LOCATION_PERMISSION",
                    "위치 권한 거부됨"
                )
            }
        }


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        // ========================================
        // Kakao Key Hash 확인
        // ========================================

        val keyHash =
            Utility.getKeyHash(
                this
            )


        Log.e(
            "KAKAO_KEY_HASH",
            "KEY_HASH = $keyHash"
        )


        // ========================================
        // 위치 권한 확인 및 요청
        // ========================================

        requestLocationPermission()


        // ========================================
        // Compose 실행
        // ========================================

        setContent {

            AppNavigation()
        }
    }


    // ========================================
    // 위치 권한 요청
    // ========================================

    private fun requestLocationPermission() {

        val fineLocationGranted =

            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED


        val coarseLocationGranted =

            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED


        // ========================================
        // 둘 중 하나라도 허용되어 있으면
        // 다시 요청하지 않음
        // ========================================

        if (
            fineLocationGranted ||
            coarseLocationGranted
        ) {

            Log.d(
                "LOCATION_PERMISSION",
                "이미 위치 권한 있음"
            )

            return
        }


        // ========================================
        // 위치 권한 요청
        // ========================================

        locationPermissionLauncher.launch(

            arrayOf(

                Manifest.permission.ACCESS_FINE_LOCATION,

                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }


    // ========================================
    // KakaoMap resume
    // ========================================

    override fun onResume() {

        super.onResume()

        mapView?.resume()
    }


    // ========================================
    // KakaoMap pause
    // ========================================

    override fun onPause() {

        mapView?.pause()

        super.onPause()
    }
}