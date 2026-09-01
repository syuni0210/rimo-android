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
import com.example.clouddx_team4_project.network.FacilityMapDto
import android.graphics.Bitmap
import android.graphics.Canvas

private fun drawableToBitmap(
    context: android.content.Context,
    drawableRes: Int
): Bitmap {

    // ========================================
    // XML drawable 불러오기
    // ========================================

    val drawable =
        ContextCompat.getDrawable(
            context,
            drawableRes
        ) ?: return Bitmap.createBitmap(
            44,
            44,
            Bitmap.Config.ARGB_8888
        )

    // ========================================
    // XML에 지정된 마커 크기 사용
    //
    // intrinsic 크기를 얻을 수 없는 경우
    // 44 x 44를 기본값으로 사용
    // ========================================

    val width =
        if (drawable.intrinsicWidth > 0) {
            drawable.intrinsicWidth
        } else {
            44
        }

    val height =
        if (drawable.intrinsicHeight > 0) {
            drawable.intrinsicHeight
        } else {
            44
        }

    // ========================================
    // 투명 배경 Bitmap 생성
    // ========================================

    val bitmap =
        Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )

    val canvas =
        Canvas(bitmap)

    // ========================================
    // Drawable 크기를 Bitmap 크기에 맞춤
    // ========================================

    drawable.setBounds(
        0,
        0,
        canvas.width,
        canvas.height
    )

    // ========================================
    // XML drawable을 Bitmap에 그림
    // ========================================

    drawable.draw(canvas)

    return bitmap
}

// ========================================
// 카카오맵
// ========================================

@Composable
fun KakaoMapView(

    modifier: Modifier = Modifier,

    selectedFacility: String? = null,

    destinationName: String = "",

    destinationLatitude: Double? = null,

    destinationLongitude: Double? = null,

    routeMode: String = "BROAD_FIRST",

    showRoute: Boolean = false,

    facilities: List<FacilityMapDto> = emptyList(),

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
// 현재 GPS 위치 전달
//
// KakaoMapView 내부에서 얻은 현재 위치를
// SafeMapScreen 등 부모 화면으로 전달합니다.
//
// 첫 번째 Double  = 위도(latitude)
// 두 번째 Double = 경도(longitude)
// ========================================

    onCurrentLocationChanged:
        (Double, Double) -> Unit =
        { _, _ -> },

// ========================================
// 현재 지도 화면의 좌표 범위 전달
//
// swLat = 남서쪽 위도
// swLng = 남서쪽 경도
// neLat = 북동쪽 위도
// neLng = 북동쪽 경도
//
// 사용자가 지도를 이동하거나 확대/축소하면
// 현재 화면에 맞는 새로운 BBOX를
// SafeMapScreen으로 전달합니다.
// ========================================

    onVisibleBoundsChanged:
        (Double, Double, Double, Double) -> Unit =
        { _, _, _, _ -> },

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
// 현재 카카오맵 화면에 실제로 보이는 영역을
// 위도/경도 BBOX로 계산
//
// Viewport의 네 모서리(pixel)를
// Kakao Map의 위도/경도로 변환합니다.
//
// 지도 회전 등의 경우도 고려해서
// 네 좌표 중 최소/최대값으로
// 남서/북동 BBOX를 만듭니다.
// ========================================

    fun sendVisibleBounds(
        map: KakaoMap
    ) {

        // 현재 지도 화면(Viewport)의 픽셀 영역
        val viewport =
            map.getViewport()


        // 화면 영역이 아직 준비되지 않은 경우
        if (
            viewport.width() <= 0 ||
            viewport.height() <= 0
        ) {
            return
        }


        // Rect의 right/bottom은 끝 경계이므로
        // 실제 화면 안쪽 픽셀을 사용하기 위해 -1
        val right =
            viewport.right - 1

        val bottom =
            viewport.bottom - 1


        // ========================================
        // 화면 네 모서리를 실제 위경도로 변환
        // ========================================

        val topLeft =
            map.fromScreenPoint(
                viewport.left,
                viewport.top
            )

        val topRight =
            map.fromScreenPoint(
                right,
                viewport.top
            )

        val bottomLeft =
            map.fromScreenPoint(
                viewport.left,
                bottom
            )

        val bottomRight =
            map.fromScreenPoint(
                right,
                bottom
            )


        // 변환 실패한 좌표가 있으면 제거
        val corners =
            listOfNotNull(
                topLeft,
                topRight,
                bottomLeft,
                bottomRight
            )


        // 4개 좌표를 모두 얻지 못했다면
        // 이번 갱신은 하지 않음
        if (corners.size < 4) {
            return
        }


        // ========================================
        // 네 모서리 중 최소/최대 좌표 계산
        // ========================================

        val swLat =
            corners.minOf {
                it.latitude
            }

        val swLng =
            corners.minOf {
                it.longitude
            }

        val neLat =
            corners.maxOf {
                it.latitude
            }

        val neLng =
            corners.maxOf {
                it.longitude
            }


        // SafeMapScreen으로 화면 범위 전달
        onVisibleBoundsChanged(
            swLat,
            swLng,
            neLat,
            neLng
        )


        Log.d(
            "KAKAO_MAP_BOUNDS",
            "화면 BBOX: $swLat, $swLng ~ $neLat, $neLng"
        )
    }

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
// 현재 GPS 위치를 부모 화면으로 전달
//
// 위도/경도가 준비되면
// SafeMapScreen의 onCurrentLocationChanged로
// 현재 위치를 전달합니다.
//
// 이 좌표는 SafeMapScreen에서
// Kakao Local API를 이용해 주소로 변환합니다.
// ========================================

    LaunchedEffect(
        realCurrentLatitude,
        realCurrentLongitude
    ) {

        val latitude =
            realCurrentLatitude
                ?: return@LaunchedEffect

        val longitude =
            realCurrentLongitude
                ?: return@LaunchedEffect


        onCurrentLocationChanged(
            latitude,
            longitude
        )
    }


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
    // 지도에 표시된 시설 마커 ID
    // ========================================

    val facilityLabelIds =
        remember {
            mutableListOf<String>()
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
// 지도 이동 / 확대 / 축소가 끝날 때마다
// 현재 화면의 BBOX를 다시 계산
//
// 이동 중에는 API를 호출하지 않고
// 손을 떼어 카메라 이동이 끝난 뒤에만
// 조회하므로 불필요한 API 호출을 줄입니다.
// ========================================

                            map.setOnCameraMoveEndListener {
                                    movedMap,
                                    _,
                                    _ ->

                                sendVisibleBounds(
                                    movedMap
                                )
                            }

                            sendVisibleBounds(
                                map
                            )

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
    // 안심지도 시설 마커
    // ========================================

    LaunchedEffect(
        kakaoMap,
        facilities,
        selectedFacility
    ) {

        val map =
            kakaoMap
                ?: return@LaunchedEffect


        val layer =
            map
                .labelManager
                ?.layer
                ?: return@LaunchedEffect


        // ========================================
        // 이전 시설 마커만 제거
        //
        // current_location / destination은
        // 건드리지 않음
        // ========================================

        facilityLabelIds
            .forEach { labelId ->

                layer
                    .getLabel(
                        labelId
                    )
                    ?.remove()
            }


        facilityLabelIds.clear()


        // ========================================
        // 시설 선택이 해제된 경우 종료
        // ========================================

        if (
            selectedFacility == null ||
            facilities.isEmpty()
        ) {

            Log.d(
                "KAKAO_MAP",
                "시설 마커 제거 완료"
            )

            return@LaunchedEffect
        }


        // ========================================
        // 시설 좌표에 마커 생성
        // ========================================

        facilities
            .forEach { facility ->

                // ========================================
                // Kakao Map에서 각 시설 마커를
                // 서로 구분하기 위한 고유 ID
                // ========================================

                val labelId =
                    "facility_${facility.type}_${facility.id}"


                // ========================================
                // DB에서 받아온 시설 위도 / 경도
                // ========================================

                val position =
                    LatLng.from(
                        facility.lat,
                        facility.lng
                    )


                // ========================================
                // 시설 종류에 따라 사용할 XML 마커 선택
                //
                // 1차 테스트:
                // CCTV만 marker_cctv.xml 사용
                //
                // 나머지 시설은 기존 마커를 그대로 사용해서
                // 현재 정상 동작하는 기능에 미치는 영향을 최소화
                // ========================================

                val markerRes =
                    when (facility.type) {

                        // CCTV
                        "CCTV" ->
                            R.drawable.marker_cctv

                        // 스마트 가로등
                        "SMART_LIGHT" ->
                            R.drawable.marker_smart_light

                        // 안심 지킴이집
                        "SAFE_HOUSE" ->
                            R.drawable.marker_safe_house

                        // 지구대 / 파출소
                        "POLICE" ->
                            R.drawable.marker_police

                        // 비상벨
                        "EMERGENCY_BELL" ->
                            R.drawable.marker_emergency_bell

                        // 보안등
                        "SECURITY_LIGHT" ->
                            R.drawable.marker_security_light

                        // 예상하지 못한 타입
                        else ->
                            R.drawable.marker_current_location
                    }


                // ========================================
                // XML drawable -> Bitmap 변환
                //
                // Kakao LabelStyle에는 변환된 Bitmap을 전달
                // ========================================

                val markerBitmap =
                    drawableToBitmap(
                        context,
                        markerRes
                    )


                // ========================================
                // 시설 마커 생성
                // ========================================

                val options =
                    LabelOptions
                        .from(
                            labelId,
                            position
                        )
                        .setStyles(
                            LabelStyle.from(
                                markerBitmap
                            )
                        )


                // ========================================
                // 지도에 시설 마커 추가
                // ========================================

                layer.addLabel(options)

                // 나중에 시설 종류 변경 시
                // 기존 시설 마커만 제거하기 위해 ID 저장
                facilityLabelIds.add(labelId)
            }


        Log.d(
            "KAKAO_MAP",
            "${selectedFacility} 시설 마커 ${facilities.size}개 표시 완료"
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