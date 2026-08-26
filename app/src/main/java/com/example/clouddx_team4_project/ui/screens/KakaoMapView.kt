package com.example.clouddx_team4_project.ui.screens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.clouddx_team4_project.MainActivity
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.data.KakaoDirectionsClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles


// ========================================
// 카카오맵
// ========================================

@Composable
fun KakaoMapView(

    modifier: Modifier = Modifier,

    destinationName: String = "",

    destinationLatitude: Double? = null,

    destinationLongitude: Double? = null,

    routeMode: String = "BROAD_FIRST",

    showRoute: Boolean = false,


    // ========================================
    // 외부 GPS 좌표
    // ActiveRouteScreen 등에서 사용
    // ========================================

    currentLatitude: Double? = null,

    currentLongitude: Double? = null,


    // ========================================
    // 현재 위치로 다시 이동 요청
    //
    // 값이 변경될 때마다
    // 현재 위치로 카메라 이동
    // ========================================

    recenterRequestKey: Int = 0,


    // ========================================
    // 지도 클릭 목적지 선택
    // ========================================

    onDestinationSelected:
        (Double, Double) -> Unit =
        { _, _ -> }

) {

    val context =
        LocalContext.current


    // ========================================
    // 내부 현재 위치
    // ========================================

    var internalLatitude by remember {

        mutableStateOf<Double?>(
            null
        )
    }


    var internalLongitude by remember {

        mutableStateOf<Double?>(
            null
        )
    }


    // ========================================
    // 실제 사용할 현재 위치
    //
    // 외부 좌표가 있으면 외부 좌표
    // 없으면 내부 GPS 좌표 사용
    // ========================================

    val realCurrentLatitude =
        currentLatitude
            ?: internalLatitude


    val realCurrentLongitude =
        currentLongitude
            ?: internalLongitude


    // ========================================
    // 위치 서비스
    // ========================================

    val fusedLocationClient =
        remember {

            LocationServices
                .getFusedLocationProviderClient(
                    context
                )
        }


    // ========================================
    // KakaoMap 객체
    // ========================================

    var kakaoMap by remember {

        mutableStateOf<KakaoMap?>(
            null
        )
    }


    // ========================================
    // 경로선 Layer
    // ========================================

    var routeLayer by remember {

        mutableStateOf<RouteLineLayer?>(
            null
        )
    }


    // ========================================
    // 위치 권한 확인
    // ========================================

    val hasLocationPermission =

        ContextCompat
            .checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                PackageManager.PERMISSION_GRANTED ||

                ContextCompat
                    .checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) ==
                PackageManager.PERMISSION_GRANTED


    // ========================================
    // 내부 현재 위치 불러오기
    // ========================================

    @SuppressLint(
        "MissingPermission"
    )
    fun loadInternalLocation(
        map: KakaoMap
    ) {


        // ========================================
        // 외부 GPS 좌표가 있으면
        // 내부 GPS 조회 필요 없음
        // ========================================

        if (
            currentLatitude != null &&
            currentLongitude != null
        ) {

            val position =
                LatLng.from(
                    currentLatitude,
                    currentLongitude
                )


            map.moveCamera(

                CameraUpdateFactory
                    .newCenterPosition(
                        position,
                        16
                    )
            )


            return
        }


        // ========================================
        // 위치 권한 확인
        // ========================================

        if (
            !hasLocationPermission
        ) {

            Log.e(
                "KAKAO_MAP",
                "위치 권한 없음"
            )

            return
        }


        // ========================================
        // 마지막 GPS 위치 가져오기
        // ========================================

        fusedLocationClient
            .lastLocation
            .addOnSuccessListener { location ->


                if (
                    location == null
                ) {

                    Log.e(
                        "KAKAO_MAP",
                        "현재 위치 null"
                    )

                    return@addOnSuccessListener
                }


                internalLatitude =
                    location.latitude


                internalLongitude =
                    location.longitude


                val position =
                    LatLng.from(
                        location.latitude,
                        location.longitude
                    )


                // ========================================
                // 현재 위치로 카메라 이동
                // ========================================

                map.moveCamera(

                    CameraUpdateFactory
                        .newCenterPosition(
                            position,
                            16
                        )
                )


                Log.d(
                    "KAKAO_MAP",
                    "현재 위치 가져오기: ${location.latitude}, ${location.longitude}"
                )
            }
    }


    // ========================================
    // 카카오맵 생성
    // ========================================

    AndroidView(

        modifier =
            modifier,

        factory = { mapContext ->


            MapView(
                mapContext
            ).also { mapView ->


                MainActivity.mapView =
                    mapView


                mapView.start(


                    // ========================================
                    // 지도 생명주기
                    // ========================================

                    object :
                        MapLifeCycleCallback() {


                        override fun onMapDestroy() {

                            Log.d(
                                "KAKAO_MAP",
                                "Map Destroy"
                            )
                        }


                        override fun onMapError(
                            error: Exception?
                        ) {

                            Log.e(
                                "KAKAO_MAP",
                                "Map Error",
                                error
                            )
                        }
                    },


                    // ========================================
                    // 지도 준비 완료
                    // ========================================

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


                            routeLayer =
                                map
                                    .routeLineManager
                                    ?.layer


                            // ========================================
                            // 지도 최초 실행 시
                            // 현재 위치로 이동
                            // ========================================

                            loadInternalLocation(
                                map
                            )


                            // ========================================
                            // 지도 클릭 시 좌표 전달
                            // ========================================

                            map.setOnMapClickListener {
                                    _,
                                    position,
                                    _,
                                    _ ->


                                onDestinationSelected(

                                    position.latitude,

                                    position.longitude
                                )
                            }
                        }
                    }
                )
            }
        }
    )


    // ========================================
    // 현재 위치 마커
    // ========================================

    LaunchedEffect(

        kakaoMap,

        realCurrentLatitude,

        realCurrentLongitude

    ) {


        val map =
            kakaoMap
                ?: return@LaunchedEffect


        val latitude =
            realCurrentLatitude
                ?: return@LaunchedEffect


        val longitude =
            realCurrentLongitude
                ?: return@LaunchedEffect


        val currentPosition =
            LatLng.from(
                latitude,
                longitude
            )


        val layer =
            map
                .labelManager
                ?.layer


        // ========================================
        // 기존 현재 위치 마커 제거
        // ========================================

        layer
            ?.getLabel(
                "current_location"
            )
            ?.remove()


        // ========================================
        // 새 현재 위치 마커 생성
        // ========================================

        val currentOptions =

            LabelOptions
                .from(
                    "current_location",
                    currentPosition
                )
                .setStyles(

                    LabelStyle.from(
                        R.drawable.marker_current_location
                    )
                )


        layer?.addLabel(
            currentOptions
        )


        Log.d(
            "KAKAO_MAP",
            "현재 위치 마커 이동: $latitude, $longitude"
        )
    }


    // ========================================
    // 현재 위치 버튼 클릭
    //
    // SafeMapScreen에서
    // recenterRequestKey 값이 바뀌면 실행
    // ========================================

    LaunchedEffect(
        recenterRequestKey
    ) {


        // ========================================
        // 최초 실행 시에는 무시
        // ========================================

        if (
            recenterRequestKey == 0
        ) {

            return@LaunchedEffect
        }


        val map =
            kakaoMap
                ?: return@LaunchedEffect


        // ========================================
        // 현재 위치 좌표가 아직 없는 경우
        // GPS 다시 조회
        // ========================================

        if (
            realCurrentLatitude == null ||
            realCurrentLongitude == null
        ) {

            loadInternalLocation(
                map
            )

            return@LaunchedEffect
        }


        val currentPosition =
            LatLng.from(

                realCurrentLatitude,

                realCurrentLongitude
            )


        // ========================================
        // 현재 위치로 지도 이동
        // ========================================

        map.moveCamera(

            CameraUpdateFactory
                .newCenterPosition(
                    currentPosition,
                    16
                )
        )


        Log.d(
            "KAKAO_MAP",
            "현재 위치 버튼 클릭 -> 지도 중심 이동"
        )
    }


    // ========================================
    // 목적지 마커
    // ========================================

    LaunchedEffect(

        kakaoMap,

        destinationLatitude,

        destinationLongitude

    ) {


        val map =
            kakaoMap
                ?: return@LaunchedEffect


        val latitude =
            destinationLatitude
                ?: return@LaunchedEffect


        val longitude =
            destinationLongitude
                ?: return@LaunchedEffect


        val position =
            LatLng.from(
                latitude,
                longitude
            )


        val layer =
            map
                .labelManager
                ?.layer


        // ========================================
        // 기존 목적지 마커 제거
        // ========================================

        layer
            ?.getLabel(
                "destination"
            )
            ?.remove()


        // ========================================
        // 목적지 마커 생성
        // ========================================

        val destinationOptions =

            LabelOptions
                .from(
                    "destination",
                    position
                )
                .setStyles(

                    LabelStyle.from(
                        R.drawable.marker_current_location
                    )
                )


        layer?.addLabel(
            destinationOptions
        )


        // ========================================
        // SafeRoute 화면에서는
        // 목적지 선택 시 목적지 위치로 카메라 이동
        // ========================================

        if (
            currentLatitude == null &&
            currentLongitude == null
        ) {

            map.moveCamera(

                CameraUpdateFactory
                    .newCenterPosition(
                        position,
                        16
                    )
            )
        }
    }


    // ========================================
    // 경로선
    // ========================================

    LaunchedEffect(

        kakaoMap,

        realCurrentLatitude,

        realCurrentLongitude,

        destinationLatitude,

        destinationLongitude,

        routeMode,

        showRoute

    ) {


        val map =
            kakaoMap
                ?: return@LaunchedEffect


        // ========================================
        // 기존 경로선 제거
        // ========================================

        routeLayer
            ?.removeAll()


        // ========================================
        // 경로 표시 안 하는 화면이면 종료
        // ========================================

        if (
            !showRoute
        ) {

            return@LaunchedEffect
        }


        if (
            routeMode.isBlank()
        ) {

            return@LaunchedEffect
        }


        val startLat =
            realCurrentLatitude
                ?: return@LaunchedEffect


        val startLng =
            realCurrentLongitude
                ?: return@LaunchedEffect


        val endLat =
            destinationLatitude
                ?: return@LaunchedEffect


        val endLng =
            destinationLongitude
                ?: return@LaunchedEffect


        try {


            // ========================================
            // 카카오 도보 길찾기 API
            // ========================================

            val response =

                KakaoDirectionsClient
                    .api
                    .getWalkingRoute(

                        authorization =
                            KakaoDirectionsClient
                                .authorization,

                        startX =
                            startLng.toString(),

                        startY =
                            startLat.toString(),

                        endX =
                            endLng.toString(),

                        endY =
                            endLat.toString(),

                        startName =
                            "현재 위치",

                        endName =

                            if (
                                destinationName.isBlank()
                            ) {

                                "목적지"

                            } else {

                                destinationName
                            },

                        routeMode =
                            routeMode
                    )


            // ========================================
            // 경로 좌표
            // ========================================

            val routePoints =
                mutableListOf<LatLng>()


            response
                .route
                ?.legs
                ?.forEach { leg ->


                    leg
                        .steps
                        ?.forEach { step ->


                            step
                                .path
                                ?.points
                                ?.forEach { point ->


                                    if (
                                        point.size >= 2
                                    ) {


                                        val longitude =
                                            point[0]


                                        val latitude =
                                            point[1]


                                        routePoints.add(

                                            LatLng.from(
                                                latitude,
                                                longitude
                                            )
                                        )
                                    }
                                }
                        }
                }


            // ========================================
            // 지도에 경로선 표시
            // ========================================

            if (
                routePoints.size >= 2
            ) {


                val routeColor =

                    0xFF6A92FE
                        .toInt()


                val routeStyle =

                    RouteLineStyle
                        .from(
                            14f,
                            routeColor
                        )


                val routeStyles =

                    RouteLineStyles
                        .from(
                            routeStyle
                        )


                val routeSegment =

                    RouteLineSegment
                        .from(
                            routePoints
                        )
                        .setStyles(
                            routeStyles
                        )


                val routeOptions =

                    RouteLineOptions
                        .from(
                            routeSegment
                        )


                routeLayer
                    ?.addRouteLine(
                        routeOptions
                    )


                Log.d(
                    "WALK_ROUTE",
                    "경로선 표시 완료"
                )
            }


        } catch (
            e: Exception
        ) {


            Log.e(
                "WALK_ROUTE",
                "도보 경로 오류",
                e
            )
        }
    }


    // ========================================
    // 화면 종료
    // ========================================

    DisposableEffect(
        Unit
    ) {


        onDispose {


            kakaoMap
                ?.setOnMapClickListener(
                    null
                )


            MainActivity
                .mapView
                ?.pause()


            MainActivity.mapView =
                null


            kakaoMap =
                null
        }
    }
}