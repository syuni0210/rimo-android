package com.example.clouddx_team4_project.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface KakaoLocalApi {

    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(

        @Header("Authorization")
        authorization: String,

        @Query("query")
        query: String,

        @Query("x")
        longitude: Double? = null,

        @Query("y")
        latitude: Double? = null,

        @Query("sort")
        sort: String = "accuracy"

    ): KakaoPlaceResponse
}


object KakaoLocalClient {

    val api: KakaoLocalApi by lazy {

        Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(
                KakaoLocalApi::class.java
            )
    }
}


data class KakaoPlaceResponse(

    @SerializedName("documents")
    val documents: List<KakaoPlace>
)


data class KakaoPlace(

    @SerializedName("id")
    val id: String,

    @SerializedName("place_name")
    val placeName: String,

    @SerializedName("address_name")
    val addressName: String,

    @SerializedName("road_address_name")
    val roadAddressName: String,

    // x = 경도
    @SerializedName("x")
    val longitude: String,

    // y = 위도
    @SerializedName("y")
    val latitude: String,

    // 현재 위치(x, y)와의 거리
    @SerializedName("distance")
    val distance: String = "",

    // 카카오 장소 카테고리 그룹 코드
    // SW8 = 지하철역
    @SerializedName("category_group_code")
    val categoryGroupCode: String = "",

    // 예: 지하철역
    @SerializedName("category_group_name")
    val categoryGroupName: String = "",

    // 예:
    // 교통,수송 > 지하철,전철 > 수도권1호선
    @SerializedName("category_name")
    val categoryName: String = ""
)