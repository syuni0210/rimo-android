package com.example.clouddx_team4_project.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


// ============================================================
// Kakao Local API - 현재 좌표를 주소로 변환
//
// 예:
// 위도  37.57186
// 경도 126.98723
//
// ↓
//
// "서울특별시 종로구 ..."
// 또는 건물명이 존재하면 "OO빌딩" 등으로 변환
//
// 주의:
// Kakao API에서는
// x = 경도(longitude)
// y = 위도(latitude)
// ============================================================

interface KakaoReverseGeocodeApi {

    @GET("v2/local/geo/coord2address.json")
    suspend fun getAddressFromCoordinate(

        // Kakao REST API 인증
        // 형식: KakaoAK {REST_API_KEY}
        @Header("Authorization")
        authorization: String,

        // x = 경도
        @Query("x")
        longitude: Double,

        // y = 위도
        @Query("y")
        latitude: Double,

        // 스마트폰 GPS 좌표는 WGS84 사용
        @Query("input_coord")
        inputCoord: String = "WGS84"

    ): KakaoCoord2AddressResponse
}


// ============================================================
// Kakao API Response
// ============================================================

data class KakaoCoord2AddressResponse(

    val documents:
    List<KakaoAddressDocument>
)


data class KakaoAddressDocument(

    // 도로명 주소
    @SerializedName("road_address")
    val roadAddress:
    KakaoRoadAddress?,

    // 지번 주소
    val address:
    KakaoJibunAddress?
)


data class KakaoRoadAddress(

    // 전체 도로명 주소
    // 예: 서울특별시 종로구 종로 123
    @SerializedName("address_name")
    val addressName:
    String?,

    // 건물 이름
    // 예: 서울시청, OO빌딩 등
    @SerializedName("building_name")
    val buildingName:
    String?
)


data class KakaoJibunAddress(

    // 전체 지번 주소
    @SerializedName("address_name")
    val addressName:
    String?
)


// ============================================================
// Kakao Local API Retrofit Client
//
// 기존 백엔드 RetrofitClient와 분리합니다.
// 이유:
// 우리 Backend = localhost:8080
// Kakao Local API = https://dapi.kakao.com/
// ============================================================

object KakaoReverseGeocodeClient {

    val api: KakaoReverseGeocodeApi by lazy {

        Retrofit.Builder()

            .baseUrl(
                "https://dapi.kakao.com/"
            )

            .addConverterFactory(
                GsonConverterFactory.create()
            )

            .build()

            .create(
                KakaoReverseGeocodeApi::class.java
            )
    }
}