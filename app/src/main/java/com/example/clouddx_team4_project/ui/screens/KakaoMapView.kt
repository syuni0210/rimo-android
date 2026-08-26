package com.example.clouddx_team4_project.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.clouddx_team4_project.MainActivity
import com.example.clouddx_team4_project.R
import com.example.clouddx_team4_project.data.KakaoDirectionsClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles


@Composable
fun KakaoMapView(

    modifier: Modifier = Modifier,

    destinationName: String = "",

    destinationLatitude: Double? = null,

    destinationLongitude: Double? = null,

    routeMode: String = "BROAD_FIRST",

    showRoute: Boolean = false,

    // ActiveRouteScreen에서 실시간 GPS 전달
    currentLatitude: Double? = null,

    currentLongitude: Double? = null,

    // 지도 클릭으로 목적지 선택
    onDestinationSelected: (Double, Double) -> Unit = { _, _ -> }

) {

    val context = LocalContext.current


    // ========================================
    // 내부 현재 위치
    // ========================================

    var internalLatitude by remember {
        mutableStateOf<Double?>(null)
    }

    var internalLongitude by remember {
        mutableStateOf<Double?>(null)
    }


    val realCurrentLatitude =
        currentLatitude ?: internalLatitude

    val realCurrentLongitude =
        currentLongitude ?: internalLongitude


    // ========================================
    // Fused Location
    // ========================================

    val fusedLocationClient = remember {

        LocationServices
            .getFusedLocationProviderClient(context)
    }


    // ========================================
    // Kakao Map
    // ========================================

    var kakaoMap by remember {
        mutableStateOf<KakaoMap?>(null)
    }

    var routeLayer by remember {
        mutableStateOf<RouteLineLayer?>(null)
    }


    // ========================================
    // 위치 권한 상태
    // ========================================

    var locationPermissionGranted by remember {

        mutableStateOf(

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||

                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }


    // ========================================
    // 위치 권한 요청
    // ========================================

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(

            contract =
                ActivityResultContracts.RequestMultiplePermissions()

        ) { permissions ->

            val fineGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true

            val coarseGranted =
                permissions[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] == true


            locationPermissionGranted =
                fineGranted || coarseGranted


            Log.d(
                "KAKAO_MAP",
                "위치 권한 결과 = $locationPermissionGranted"
            )
        }


    // 화면 최초 진입 시 권한 요청
    LaunchedEffect(Unit) {

        if (!locationPermissionGranted) {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    // ========================================
    // 현재 위치 가져오기
    // ========================================

    @SuppressLint("MissingPermission")
    fun loadInternalLocation(
        map: KakaoMap
    ) {

        // ActiveRouteScreen에서 현재 위치를 넘기고 있다면
        // 여기서 GPS를 따로 가져올 필요 없음
        if (
            currentLatitude != null &&
            currentLongitude != null
        ) {

            Log.d(
                "KAKAO_MAP",
                "외부 GPS 사용"
            )

            return
        }


        // 위치 권한 없음
        if (!locationPermissionGranted) {

            Log.e(
                "KAKAO_MAP",
                "위치 권한 없음"
            )

            return
        }


        Log.d(
            "KAKAO_MAP",
            "현재 위치 조회 시작"
        )


        // ========================================
        // 현재 GPS 위치 조회
        // ========================================

        val cancellationTokenSource =
            CancellationTokenSource()


        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
            .addOnSuccessListener { location ->


                if (location != null) {

                    internalLatitude =
                        location.latitude

                    internalLongitude =
                        location.longitude


                    Log.d(
                        "KAKAO_MAP",
                        "현재 위치 = ${location.latitude}, ${location.longitude}"
                    )


                    val position =
                        LatLng.from(
                            location.latitude,
                            location.longitude
                        )


                    // 현재 위치로 카메라 이동
                    map.moveCamera(

                        CameraUpdateFactory
                            .newCenterPosition(
                                position,
                                16
                            )
                    )

                } else {

                    Log.e(
                        "KAKAO_MAP",
                        "getCurrentLocation 결과 null"
                    )


                    // ========================================
                    // 현재 위치 실패 시 마지막 위치 사용
                    // ========================================

                    fusedLocationClient
                        .lastLocation
                        .addOnSuccessListener { lastLocation ->


                            if (lastLocation == null) {

                                Log.e(
                                    "KAKAO_MAP",
                                    "lastLocation도 null"
                                )

                                return@addOnSuccessListener
                            }


                            internalLatitude =
                                lastLocation.latitude

                            internalLongitude =
                                lastLocation.longitude


                            val position =
                                LatLng.from(
                                    lastLocation.latitude,
                                    lastLocation.longitude
                                )


                            map.moveCamera(

                                CameraUpdateFactory
                                    .newCenterPosition(
                                        position,
                                        16
                                    )
                            )


                            Log.d(
                                "KAKAO_MAP",
                                "마지막 위치 사용 = " +
                                        "${lastLocation.latitude}, " +
                                        "${lastLocation.longitude}"
                            )
                        }
                }
            }
            .addOnFailureListener { e ->

                Log.e(
                    "KAKAO_MAP",
                    "현재 위치 조회 실패",
                    e
                )
            }
    }


    // ========================================
    // 권한이 허용된 후 위치 다시 가져오기
    // ========================================

    LaunchedEffect(
        kakaoMap,
        locationPermissionGranted
    ) {

        val map =
            kakaoMap ?: return@LaunchedEffect


        if (locationPermissionGranted) {

            loadInternalLocation(map)
        }
    }


    // ========================================
    // 지도 생성
    // ========================================

    AndroidView(

        modifier = modifier,

        factory = { mapContext ->

            MapView(
                mapContext
            ).also { mapView ->


                MainActivity.mapView =
                    mapView


                mapView.start(

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
                            // 권한이 이미 있다면 현재 위치
                            // ========================================

                            if (locationPermissionGranted) {

                                loadInternalLocation(
                                    map
                                )
                            }


                            // ========================================
                            // 지도 클릭 목적지 지정
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
            map.labelManager
                ?.layer


        // 기존 현재 위치 마커 제거
        layer
            ?.getLabel(
                "current_location"
            )
            ?.remove()


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
            map.labelManager
                ?.layer


        layer
            ?.getLabel(
                "destination"
            )
            ?.remove()


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


        // SafeRoute에서 목적지를 선택하면
        // 목적지 쪽으로 카메라 이동
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

        kakaoMap
            ?: return@LaunchedEffect


        routeLayer
            ?.removeAll()


        if (!showRoute) {

            return@LaunchedEffect
        }


        if (routeMode.isBlank()) {

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

            Log.d(
                "WALK_ROUTE",
                "도보 경로 요청"
            )


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


            val routePoints =
                mutableListOf<LatLng>()


            response
                .route
                ?.legs
                ?.forEach { leg ->


                    leg.steps
                        ?.forEach { step ->


                            step.path
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
            // 경로선 그리기
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

            } else {

                Log.e(
                    "WALK_ROUTE",
                    "경로 좌표 없음"
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