package com.example.clouddx_team4_project.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.clouddx_team4_project.MainActivity
import com.example.clouddx_team4_project.R
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles


@Composable
fun KakaoMapView(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    var kakaoMap by remember {
        mutableStateOf<KakaoMap?>(null)
    }


    // ========================================
    // 현재 위치로 이동
    // ========================================

    fun moveToCurrentLocation() {

        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        if (
            !fineLocationGranted &&
            !coarseLocationGranted
        ) {
            return
        }


        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(
                context
            )


        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    Log.d(
                        "CURRENT_LOCATION",
                        "lat=${location.latitude}, lng=${location.longitude}"
                    )


                    val currentPosition =
                        LatLng.from(
                            location.latitude,
                            location.longitude
                        )


                    // ========================================
                    // 카메라 이동
                    // ========================================

                    val cameraUpdate =
                        CameraUpdateFactory.newCenterPosition(
                            currentPosition
                        )


                    kakaoMap?.moveCamera(
                        cameraUpdate
                    )


                    val map =
                        kakaoMap
                            ?: return@addOnSuccessListener


                    val labelManager =
                        map.labelManager
                            ?: return@addOnSuccessListener


                    // ========================================
                    // 현재 위치 마커
                    // ========================================

                    val markerStyle =
                        LabelStyle.from(
                            R.drawable.marker_current_location
                        )


                    val markerStyles =
                        LabelStyles.from(
                            markerStyle
                        )


                    val registeredStyles =
                        labelManager.addLabelStyles(
                            markerStyles
                        )


                    val labelOptions =
                        LabelOptions.from(
                            "current_location",
                            currentPosition
                        ).setStyles(
                            registeredStyles
                        )


                    val labelLayer =
                        labelManager.layer
                            ?: return@addOnSuccessListener


                    labelLayer.addLabel(
                        labelOptions
                    )


                    Log.d(
                        "CURRENT_MARKER",
                        "현재 위치 마커 생성 완료"
                    )
                }
            }
    }


    // ========================================
    // 위치 권한 요청
    // ========================================

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
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

                moveToCurrentLocation()
            }
        }


    // ========================================
    // 권한 확인
    // ========================================

    fun checkLocationPermission() {

        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED


        if (
            fineLocationGranted ||
            coarseLocationGranted
        ) {

            moveToCurrentLocation()

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    // ========================================
    // 카카오맵
    // ========================================

    AndroidView(
        modifier = modifier,

        factory = { mapContext ->

            MapView(
                mapContext
            ).also { view ->

                MainActivity.mapView =
                    view


                view.start(

                    object :
                        MapLifeCycleCallback() {

                        override fun onMapDestroy() {

                            Log.d(
                                "KAKAO_MAP",
                                "Map Destroy"
                            )
                        }


                        override fun onMapError(
                            error: Exception
                        ) {

                            Log.e(
                                "KAKAO_MAP",
                                "Map Error",
                                error
                            )
                        }
                    },


                    object :
                        KakaoMapReadyCallback() {

                        override fun onMapReady(
                            map: KakaoMap
                        ) {

                            Log.d(
                                "KAKAO_MAP",
                                "Map Ready"
                            )


                            kakaoMap =
                                map


                            checkLocationPermission()
                        }
                    }
                )
            }
        }
    )


    // ========================================
    // 화면에서 사라질 때 정리
    // ========================================

    DisposableEffect(Unit) {

        onDispose {

            MainActivity.mapView?.pause()

            MainActivity.mapView =
                null

            kakaoMap =
                null
        }
    }
}