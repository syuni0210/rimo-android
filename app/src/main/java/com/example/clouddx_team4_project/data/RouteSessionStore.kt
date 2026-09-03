package com.example.clouddx_team4_project.data

import kotlin.math.abs
import com.example.clouddx_team4_project.network.FacilityMapDto
/**
 * 경로 선택 화면에서 이미 계산한 경로를
 * 귀가 진행 화면에서 그대로 재사용하기 위한 메모리 캐시입니다.
 *
 * - 같은 경로를 두 번 API 호출하지 않습니다.
 * - 앱 프로세스가 종료되면 자동으로 사라집니다.
 * - 다른 목적지를 선택하면 기존 캐시는 비웁니다.
 */
data class RouteSessionPoint(
    val latitude: Double,
    val longitude: Double
)

data class RouteSessionData(
    val routeMode: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val distanceMeter: Int,
    val timeSecond: Int,
    val points: List<RouteSessionPoint>,
    val facilities: List<FacilityMapDto> = emptyList(),
// ========================================
// 시설 조회 완료 여부
//
// false:
// 아직 시설 데이터를 얻지 못한 상태.
// 필요하면 facilities-near-path API를 호출합니다.
//
// true:
// 시설 조회가 완료된 상태.
// facilities가 emptyList()여도
// "조회했는데 실제 시설이 0개"라는 뜻이므로
// DB를 다시 조회하지 않습니다.
// ========================================
    val facilitiesLoaded: Boolean = false,
    val aiSelectedKakaoRouteMode: String? = null
)

object RouteSessionStore {

    private val routes =
        mutableMapOf<String, RouteSessionData>()

    private var currentDestinationLatitude: Double? = null
    private var currentDestinationLongitude: Double? = null

    @Synchronized
    fun prepareDestination(
        latitude: Double,
        longitude: Double
    ) {
        val oldLat = currentDestinationLatitude
        val oldLng = currentDestinationLongitude

        val sameDestination =
            oldLat != null &&
                    oldLng != null &&
                    abs(oldLat - latitude) < 0.0000001 &&
                    abs(oldLng - longitude) < 0.0000001

        if (!sameDestination) {
            routes.clear()
            currentDestinationLatitude = latitude
            currentDestinationLongitude = longitude
        }
    }

    @Synchronized
    fun put(route: RouteSessionData) {
        prepareDestination(
            route.destinationLatitude,
            route.destinationLongitude
        )
        routes[route.routeMode] = route
    }

    @Synchronized
    fun get(
        routeMode: String,
        destinationLatitude: Double?,
        destinationLongitude: Double?
    ): RouteSessionData? {
        val endLat = destinationLatitude ?: return null
        val endLng = destinationLongitude ?: return null

        val route = routes[routeMode] ?: return null

        val sameDestination =
            abs(route.destinationLatitude - endLat) < 0.0000001 &&
                    abs(route.destinationLongitude - endLng) < 0.0000001

        return if (sameDestination) route else null
    }

    @Synchronized
    fun clear() {
        routes.clear()
        currentDestinationLatitude = null
        currentDestinationLongitude = null
    }
}
